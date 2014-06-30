// These philosophers are *not* bright.  To see this, set up a table 
// with only 2 of them.  Odds are, you'll get deadlock.
class StupidPhilosopher extends BasePhilosopher {
    // just do root-class's constructor
    public StupidPhilosopher(int n, int nr, Chopstick l, Chopstick r) { super(n, nr, l, r); }

    // the problem's here
    protected void getChopsticks() throws InterruptedException 
    {
        while (!left.try2Get(this))
        {
            //attempts to get the left chopstick.  Will wait until it is able to
        }
        while (!right.try2Get(this))
        {
            //attempts to get the right chopstick.  Will wait here until it is able to
        }
        //if they both get hungry, this deadlocks
    }
}
