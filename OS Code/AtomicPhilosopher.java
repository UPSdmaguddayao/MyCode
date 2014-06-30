/** 
 * @author DJ Maguddayao
 * @version 4.4.14
 * The AtomicPhilosopher picks up both chopsticks as an atomic action.
 */

class AtomicPhilosopher extends BasePhilosopher {
    
    public AtomicPhilosopher(int n, int nr, Chopstick l, Chopstick r) { super(n, nr, l, r); }
    
    protected void getChopsticks() throws InterruptedException {
        while (!left.try2Get(this))
        {
            
        }
        if (!right.try2Get(this))
        {
            left.release();
            //System.out.println("Letting go of left.  Not ready");
            sleep((long)(Math.random()*100)); // stall before trying to grab chopsticks again.  Avoids infinite loop if it waits a bit
            getChopsticks(); //attempt to grab chopsticks again
        }
    }
}
