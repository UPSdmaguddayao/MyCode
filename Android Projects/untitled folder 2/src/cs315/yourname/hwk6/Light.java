package cs315.yourname.hwk6;

public class Light 
{
	private double x;
	private double y; //location of the light source
	private double z;
	private double[] lightLocation;
	
	//refernce for all objects to point a ray toward this light source
	//For right now, direction is emanating from all around the light source instead of a focus point.  Maybe 
	//give a direction vector in the future?
	
	public Light(double newX, double newY, double newZ)
	{
		x = newX;
		y = newY;
		z = newZ;
		
		lightLocation = new double[]{x,y,z};
		
		
	}
	
	/**
	 * Method of getting the light location in x,y,z paired form.  Vector point
	 * @return the location of the light source. 
	 */
	public double[] lightLocation()
	{
		return lightLocation; //shows x,y, and z
	}
}
