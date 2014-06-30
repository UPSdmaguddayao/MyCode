package cs315.yourname.hwk6;


public class Sphere extends Primitive {

	private double radius; // size of the sphere
	private double centerX;
	private double centerY; //use this to know the location of an instance of the sphere
	private double centerZ;
	//private double[] center;
	
	/*private double minX;
	private double minY;
	private double minZ;
	
	private double maxX;
	private double maxY;
	private double maxZ;
	*/
	private Material material; //stores the object's ambience, specular ratio, how shiny it is, and its color.
	
	public Sphere(double x, double y, double z, double newRadius, double ambience, double specRatio, double shiny, double[] color) 
	{
		radius = newRadius;
		centerX = x;
		centerY = y;
		centerZ = z;
		//this.color=color;
		
		//center = new double[]{x,y,z};
		
		//Helps for creating a bounding volume for later if I have time
		/*
		minX = x-radius;
		minY = y-radius;
		minZ = z-radius;
		
		maxX = x+radius;
		maxY = y+radius;
		maxZ = z+radius;
		*/
		material = new Material(ambience, specRatio, shiny, color);
	}

	/**
	 * Helper to get the radius
	 * @return Radius of the sphere
	 */
	public double getRadius () 
	{ 
		return radius;
	} 
	
	/**
	 * Helper methods for getting values for calculations
	 * @return Returns the center X of the Sphere
	 */
	public double getX()
	{
		return centerX;
	}
	public double getY()
	{
		return centerY;
	}
	public double getZ()
	{
		return centerZ;
	}
	
	/**
	 * Helper method for other methods to access the classes's material class
	 * @return a Material class of this instance of an object
	 */
	public Material getMaterial()
	{
		return material;
	}
	
	/**
	 * Calculates how the color is at a point
	 * @param hitPoint Place on the sphere we're calculating the color for
	 * @param light The light source direction
	 * @param eyeDirection Location of the eye.  Needed for specular
	 * @return a color in RGB form
	 */
	public double[] getNewColor(double[] hitPoint, double[] light, Primitive[] list, Ray eyeDirection, int bounceDepth)
	{

		return material.calculateNewColor(getNormal(hitPoint), hitPoint, light, list, eyeDirection, bounceDepth);
	}
	
	/**
	 * Normal changes depending on the point it hits the sphere
	 */
	public double[] getNormal(double[] hitPoint)
	{
		return Vector3d.normalize(new double[] {(hitPoint[0] - centerX)/radius, (hitPoint[1]- centerY)/radius, hitPoint[2] - centerZ/radius});
	}
	
	/**
	 * Helper method for getting the t value when the method is needed outside of Intersection
	 */
	public double getIntersectValue(Ray ray)
	{
		return Intersection.sphereIntersection(ray, this);
		
	}
	
}