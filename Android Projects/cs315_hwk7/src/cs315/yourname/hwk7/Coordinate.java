package cs315.yourname.hwk7;

/**
 * Class to hold "coordinates" consisting of an 'X' and 'Y'.
 * @author djmag93
 *
 */

public class Coordinate {
    public int x;
    public int y;
    
    public Coordinate (int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Compares two coordinates and see if they're the same
     * @param c Coordinate it is compared to
     * @return if they have the same x and y, return true
     */
    public boolean compare (Coordinate c)
    {
        if(c.x == x && c.y == y)
        {
            return true;
        }
        return false;
    }
}

