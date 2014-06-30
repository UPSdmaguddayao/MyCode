package cs315.yourname.hwk7;

/**
 * A 2-D layout (x and y) which holds list of coordinates that you CAN go to.  Can also hold places it can't go to
 * for purposes of generating where the borders are.  Always start each map at 0,0.  Grid goes from -11 to 11 horizontally
 * and 19 forward.  Mark the end point as one of the variables to note when the program should end.  
 * Coordinates for places able to go can be changed here
 * @author djmag93
 *
 */
public class MazeMap 
{
	private Coordinate[] solution; //list of available places to move.
	private Coordinate start;
	private Coordinate end;
	
	public MazeMap()
	{
		//hard coded solution.  If I have time, I will make a method to interpolate a line, but for now, we keep it like this
		solution = new Coordinate[]
		{
			 new Coordinate(0,0),
			 new Coordinate(0,1),
			 new Coordinate(0,2),
			 new Coordinate(-1,2),
			 new Coordinate(-2,2),
			 new Coordinate(-3,2),
			 new Coordinate(-3,3),
			 new Coordinate(-3,4),
			 new Coordinate(-3,5),
			 new Coordinate(-3,6),
			 new Coordinate(-3,7),
			 new Coordinate(-3,8),
			 new Coordinate(-2,8),
			 new Coordinate(-1,8),
			 new Coordinate(0,8),
			 new Coordinate(1,8),
			 new Coordinate(2,8),
			 	new Coordinate(2,7),//uneven coordinates aren't the real path
			 	new Coordinate(3,7),
			 	new Coordinate(4,7),
			 	new Coordinate(5,7),
			 	new Coordinate(5,6),
			 	new Coordinate(5,5),
			 	new Coordinate(5,4),
			 	new Coordinate(5,3),
			 	new Coordinate(4,3),
			 new Coordinate(2,6),
			 new Coordinate(1,6),
			 new Coordinate(0,6),
			 new Coordinate(0,5),
			 new Coordinate(0,4),
			 new Coordinate(1,4),
			 new Coordinate(2,4),
			 new Coordinate(2,3),
			 new Coordinate(2,2),
			 new Coordinate(2,1),
			 new Coordinate(3,1),
			 new Coordinate(4,1),
			 new Coordinate(5,1),
			 new Coordinate(6,1),
			 new Coordinate(7,1),
			 new Coordinate(8,1),
			 	new Coordinate(8,2),
			 	new Coordinate(9,2),
			 	new Coordinate(10,2),
			 	new Coordinate(10,1),
			 new Coordinate(8,3),
			 new Coordinate(8,4),
			 new Coordinate(7,4),
			 new Coordinate(7,5),
			 new Coordinate(7,6),
			 	new Coordinate(7,7),
			 	new Coordinate(8,7),
			 	new Coordinate(9,7),
			 	new Coordinate(10,7),
			 	new Coordinate(10,8),
			 	new Coordinate(10,9),
			 	new Coordinate(10,10),
			 	new Coordinate(10,11),
			 	new Coordinate(10,12),
			 	new Coordinate(10,13),
			 	new Coordinate(10,14),
			 	new Coordinate(10,15),
			 	new Coordinate(10,16),
			 	new Coordinate(10,17),
			 	new Coordinate(10,18),
			 new Coordinate(7,8),
			 new Coordinate(7,9),
			 new Coordinate(7,10),
			 new Coordinate(6,10),
			 new Coordinate(5,10),
			 new Coordinate(4,10),
			 new Coordinate(3,10),
			 new Coordinate(2,10),
			 new Coordinate(1,10),
			 new Coordinate(0,10),
			 new Coordinate(-1,10),
			 new Coordinate(-2,10),
			 new Coordinate(-3,10),
			 new Coordinate(-4,10),
			 new Coordinate(-5,10),
			 new Coordinate(-5,9),
			 new Coordinate(-5,8),
			 new Coordinate(-5,7),
			 new Coordinate(-5,6),
			 new Coordinate(-5,5),
			 new Coordinate(-5,4),
			 new Coordinate(-5,3),
			 new Coordinate(-5,2),
			 new Coordinate(-6,2),
			 new Coordinate(-7,2),
			 new Coordinate(-8,2),
			 new Coordinate(-8,3),
			 new Coordinate(-8,4),
			 new Coordinate(-8,5),	
			 new Coordinate(-8,6),
			 new Coordinate(-8,7),
			 new Coordinate(-8,8),
			 new Coordinate(-8,9),
			 new Coordinate(-8,10),
			 new Coordinate(-8,11),
			 new Coordinate(-8,12),
			 new Coordinate(-8,13),
			 	new Coordinate(-7,13),
				new Coordinate(-7,14),
				new Coordinate(-7,15),
				new Coordinate(-7,16),
				new Coordinate(-7,17),
				new Coordinate(-7,18),
				new Coordinate(-8,18),
			new Coordinate(-6,13),
			new Coordinate(-5,13),
				new Coordinate(-4,13),
				new Coordinate(-4,14),
				new Coordinate(-4,15),
				new Coordinate(-4,16),
				new Coordinate(-4,17),
				new Coordinate(-4,18),
				new Coordinate(-3,18),
			new Coordinate(-3,13),
			new Coordinate(-2,13),
				new Coordinate(-1,13),
				new Coordinate(-1,14),
				new Coordinate(-1,15),
				new Coordinate(-1,16),
				new Coordinate(0,16),
				new Coordinate(1,16),
				new Coordinate(2,16),
				new Coordinate(3,16),
				new Coordinate(4,16),
				new Coordinate(5,16),
				new Coordinate(6,16),
			new Coordinate(0,13),
			new Coordinate(1,13),
				new Coordinate(2,13),
				new Coordinate(2,12),
			new Coordinate(3,13),
			new Coordinate(4,13),
			new Coordinate(5,13),
			new Coordinate(6,13),
			new Coordinate(7,13),
			new Coordinate(8,13),
			new Coordinate(8,14),
			new Coordinate(8,15),
			new Coordinate(8,16),
			new Coordinate(8,17),
			new Coordinate(8,18),
			new Coordinate(7,18),
			new Coordinate(6,18),
			new Coordinate(5,18),
			new Coordinate(4,18),
			new Coordinate(3,18),
			new Coordinate(2,18),
			new Coordinate(1,18),
			new Coordinate(0,18),
			new Coordinate(-1,18),
			new Coordinate(-1,19)
		};

		start = new Coordinate(0,0);
		end = new Coordinate(-1,19);
	}
	
	/**
	 * Checks if someone is able to go to this space.  It's an unfilled space
	 * @param x 'x' coordinate
	 * @param y 'y' coordinate
	 * @return If it is available (true) or not (false)
	 */
	public boolean availableMove(int x, int y)
	{
		Coordinate coordinate = new Coordinate(x,y);
		for (int i = 0; i< solution.length; i++)
		{
			if(coordinate.compare(solution[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * For checks if the coordinate they're on is the end coordinate
	 * @param x 'x' coordinate of the location
	 * @param y 'y' coordinate of the location
	 * @return if it is the end, return true
	 */
	public boolean reachedEnd(int x, int y)
	{
		if (x == end.x && y == end.y)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the endPoint for comparisons
	 * @return The end Coordinate
	 */
	public Coordinate endPoint()
	{
		return end;
	}
}
