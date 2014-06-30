package cs315.yourname.hwk6;

/**
 * A class to represent a ray
 */
public class Ray
{
	public double[] origin; //origin
	public double[] direction; //direction
	
	public Ray(double[] a, double[] d)
	{
		this.origin = a;
		this.direction = d;
	}
}
