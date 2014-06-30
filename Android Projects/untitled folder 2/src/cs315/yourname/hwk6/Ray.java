package cs315.yourname.hwk6;

/**
 * A class to represent a ray
 */
public class Ray
{
	public double[] origin; //origin to be accessed
	public double[] direction; //direction can be accessed
	
	public Ray(double[] a, double[] d)
	{
		this.origin = a;
		this.direction = d;
	}
}

