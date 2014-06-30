/**
 * @author DJ Maguddayao
 * @version 4.4.14
 * The AlternatingPhilosopher may pick up the chopsticks in a different order, depending on that philosopher's
 * ID number (determined by n)
 *
 */
class AlternatingPhilosopher extends BasePhilosopher {
    private int starter; //tells us which to start with.  If odd, then start right.  If even, then start left.
    
    public AlternatingPhilosopher(int n, int nr, Chopstick l, Chopstick r) 
        { 
            super(n, nr, l, r); 
            starter = n%2; //even or odd?
        }
    
    protected void getChopsticks() throws InterruptedException {
        if(starter == 0)
        {
            while (!left.try2Get(this));
            while (!right.try2Get(this));
        }
        else //if it's odd
        {
            while (!right.try2Get(this));
            while (!left.try2Get(this));
        }
    }
}