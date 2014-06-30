import java.util.concurrent.locks.ReentrantLock;

class philosophers {
    public static final int STUPID = 0, ATOMIC = 1, WAITER = 2, ALTERNATING = 3;
    public static void main(String[] args) {
    // get type
    int type = STUPID; // default type
    if (args.length >= 1) type = parseType(args[0]);

    // get # philosophers
    int numSeats = 5; // default # philosophers
    if (args.length >= 2) numSeats = Integer.parseInt(args[1]);

    // get # rounds
    int numRounds = 10; // default # rounds
    if (args.length >= 3) numRounds = Integer.parseInt(args[2]);

    // allocate Chopsticks & Philosophers
    Chopstick[] chopsticks = new Chopstick[numSeats];
    for (int i=0; i<numSeats; i++) chopsticks[i] = new Chopstick();

    BasePhilosopher[] philosophers = new BasePhilosopher[numSeats];
    for (int i=0; i<numSeats; i++) {
        if (type == STUPID) philosophers[i] = new StupidPhilosopher(i, numRounds, chopsticks[i], chopsticks[(i+1)%numSeats]);
        else if (type == ATOMIC) philosophers[i] = new AtomicPhilosopher(i, numRounds, chopsticks[i], chopsticks[(i+1)%numSeats]);
        else if (type == WAITER) philosophers[i] = new WaiterPhilosopher(i, numRounds, chopsticks[i], chopsticks[(i+1)%numSeats]);
        else if (type == ALTERNATING) philosophers[i] = new AlternatingPhilosopher(i, numRounds, chopsticks[i], chopsticks[(i+1)%numSeats]);
    }

    // go!
    for (int i=0; i<numSeats; i++) philosophers[i].start();
    }

    // figure out what type of philosopher we have
    private static int parseType(String str) {
    if (str.equalsIgnoreCase("stupid")) return STUPID;
    if (str.equalsIgnoreCase("atomic")) return ATOMIC;
    if (str.equalsIgnoreCase("waiter")) return WAITER;
    if (str.equalsIgnoreCase("alternating")) return ALTERNATING;
    else throw new IllegalArgumentException("\"" +str+ "\" is not a valid philosopher type!");
    }
}

// this is the abstract class that you will extend
abstract class BasePhilosopher extends Thread {
    Chopstick left, right;
    int number;
    int numRounds;

    // constructor
    public BasePhilosopher(int n, int nr, Chopstick l, Chopstick r) {
    number = n;
    left = l;
    right = r;
    numRounds = nr;

    System.out.println("Philosopher " +number+ " has arrived!");
    }

    // think & eat numRounds times
    public void run() {
    try {
        for (int i=0; i<numRounds; i++) {
        // think!
        System.out.println("\tPhilosopher " +number+ " is philosophizing.");
        sleep((long)(Math.random()*3000)); // between 0 sec & 3 sec

        // eat!
        System.out.println("Philosopher " +number+ " is hungry.");

        // here's the call of the function you'll need to modify
        getChopsticks();
        if (!left.isHeldByMe() || !right.isHeldByMe()) throw new IllegalStateException("Philosopher " +number+ " is trying to eat without chopsticks!!!");
        System.out.println("Philosopher " +number+ " is eating.");
        sleep((long)(Math.random()*1000)); // between 0 sec & 1 sec

        // release chopsticks
        left.release();
        right.release();
        }
    }
    catch (InterruptedException e) { return; }
    }

    // this is the method that must be modified
    abstract protected void getChopsticks() throws InterruptedException;
}

class Chopstick {
    ReentrantLock lock;

    // constructor that initializes a lock
    public Chopstick() {
    lock = new ReentrantLock();
    }

    // obtain a Chopstick, wait for 0.2 sec if successful
    public boolean try2Get(BasePhilosopher phil) throws InterruptedException 
    {
        if (lock.tryLock()) 
        { //does this if it was able to get the lock
            phil.sleep(200);
            return true; 
        }
        return false;
    }

    // do I have this Chopstick?
    public boolean isHeldByMe() {
    return lock.isHeldByCurrentThread();
    }

    // Let it go, man.  Let it go.
    public void release() {
    try { lock.unlock(); }
    catch (IllegalMonitorStateException e) {
        System.err.println("A chopstick has been released by a philosopher who didn't own it!");
        e.printStackTrace();
        System.exit(1);
    }
    }
}
