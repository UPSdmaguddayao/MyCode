import java.util.concurrent.Semaphore;
/**
 * @author DJ Maguddayao
 * @version 4.4.14
 * The WaiterPhilosopher is controlled by a virtual waiter (a semaphore) that only allows N-1 philosophers to
 *eat at once.
 */
class WaiterPhilosopher extends BasePhilosopher {
    private static int starter=0;
    private static Semaphore waiter; //only allows n-1 Philosophers to access at a time
    //private int in; //used for debugging to see which philosopher is doing what

    /**
     * Have a semaphore initialized to (n-1), and all philosophers must wait on it in order to pick up chopsticks.
     * In the metaphor, this semaphore is a “waiter” who can control who eats.
     * Reasoning is similar to the above: some philosophers have exclusive chopsticks.
     */
    public WaiterPhilosopher(int n, int nr, Chopstick l, Chopstick r) 
    { 
        super(n, nr, l, r);
        //in = n;
        starter = starter + 1;
        waiter = new Semaphore(starter-1); //replaces the current semaphore with a new one if the number increases
    }
    
    protected void getChopsticks() throws InterruptedException 
    {
        //once you get both chopsticks, you'll eat after this is done executing
        if(waiter.tryAcquire()) //if it has the permit from the waiter, will attempt to eat
        {
            //System.out.println("            Philosopher number " + in + " has permission");
            while (!left.try2Get(this));
            while (!right.try2Get(this));
            waiter.release(); //only release one it has already gotten it.  Any other time and there' will be more permits than there actually should be (which can lead to deadlock)
        }
        else
        {
            sleep((long)(Math.random()*100));
            getChopsticks(); //try to get permission again
        }
        //    System.out.println("            Philosopher number " + in + " has released permission");  //helps with debuggin
    }
}