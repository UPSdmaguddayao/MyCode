package cs315.yourname.hwk6;

/**
 * Utility class for vector math. Vectors 3-dimensional with double precision.
 * @author Joel
 * @version Fall 2013
 */
public class Vector3d
{
	public static double dot(double[] p, double[] q)
	{
		return p[0]*q[0]+p[1]*q[1]+p[2]*q[2];
	}
	
	public static double[] cross(double[] u, double[] v)
	{
		double[] cross = {
				u[1]*v[2]-u[2]*v[1],
				u[2]*v[0]-u[0]*v[2],
				u[0]*v[1]-u[1]*v[0],
		};
		
		return cross;
	}
	
	public static double length(double[] v)
	{
		return Math.sqrt((v[0]*v[0])+(v[1]*v[1])+(v[2]*v[2]));
	}

	public static double dotSelf(double[] v)
	{
		return (v[0]*v[0])+(v[1]*v[1])+(v[2]*v[2]);
	}
	
	public static double[] add(double[] p, double[] q)
	{
		return new double[] {p[0]+q[0], p[1]+q[1], p[2]+q[2]};
	}

	public static double[] sub(double[] p, double[] q)
	{
		return new double[] {p[0]-q[0], p[1]-q[1], p[2]-q[2]};
	}
	
	public static double[] normalize(double[] v)
	{
		double length = Math.sqrt((v[0]*v[0])+(v[1]*v[1])+(v[2]*v[2]));
		return new double[] {v[0]/length, v[1]/length, v[2]/length};
	}

	//Added so I can scale all numbers in the double[] by another double
	public static double[] scale(double v, double[] w)
	{
		return new double[] {v*w[0],v*w[1],v*w[2]};
	}
}
