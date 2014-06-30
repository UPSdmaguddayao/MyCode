package cs315.yourname.hwk6;

/**
 * A class for creating a triangle.  Triangles can make a lot of shapes.
 * This holds the vertices and the material of any given triangle
 * @author DJ Maguddayao
 *
 */

public class Triangle extends Primitive
{
	private double[] vertex0;
	private double[] vertex1;
	private double[] vertex2;
	private double[] normal;
	private Material material;
	
	public Triangle(double[] v0, double[] v1, double[] v2, double ambience, double specRatio, double shiny, double[] color)
	{
		vertex0 = v0;
		vertex1 = v1;
		vertex2 = v2;
		
		double[] vec1 = Vector3d.sub(v1, v0);
		double[] vec2 = Vector3d.sub(v2, v0);
			
		normal =  Vector3d.normalize(Vector3d.cross(vec1, vec2));
		
		material = new Material(ambience, specRatio, shiny, color);
	}
	
	/**
	 * Needed in the intersection class
	 * @return the vertex point in a float[]
	 */
	public double[] v0()
	{
		return vertex0;
	}
	
	public double[] v1()
	{
		return vertex1;
	}
	
	public double[] v2()
	{
		return vertex2;
	}

	public double[] getNewColor(double[] hitPoint, double[] light, Primitive[] list, Ray eyeDirection, int bounceDepth)
	{
		return material.calculateNewColor(normal, hitPoint, light, list, eyeDirection, bounceDepth);
	}
	
	/**
	 * Normal doesn't change on the triangle
	 */
	public double[] getNormal(double[] hitPoint)
	{
		return normal;
	}
	
	public double getIntersectValue(Ray ray)
	{
		return Intersection.triangleIntersection(ray, this);
	}
}