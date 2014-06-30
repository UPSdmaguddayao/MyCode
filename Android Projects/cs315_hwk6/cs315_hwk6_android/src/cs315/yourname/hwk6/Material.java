package cs315.yourname.hwk6;


public class Material {

	private double ambient; //reflection of ambience
	private double specularRatio; //This is the 'material' for the object
	private double shiny;
	private double[] color; //stores the color for an object.  in RGB (no alpha needed)
	
	
	/**
	 * Creates a class to store the material of an object
	 * @param ambience How much ambient light affects it
	 * @param specRatio How much light pointing at it affects it
	 * @param shiny How 'shiny' it appears
	 * @param color The color of the object in float form {r,g,b}
	 */
	public Material (double ambience,  double specRatio, double shiny, double[] color)
	{
		ambient = ambience; //saves the value of the 'ambient lighting' of an object
		this.shiny = shiny; //saves the shine value of the object.  This is for specular lighting
		this.color = color;
		specularRatio = specRatio;
	}
	
	/**
	 * Calculates the diffuse and specular (maybe shadow and reflection soon)
	 * @param normal The calculated normal at that point
	 * @param hitPoint Where the ray hits the object on the canvas
	 * @param light The light location
	 * @param eyeDirection The direction the eye is 'hitting' the object at
	 * @param bounceDepth how many bounces it will use
	 * @return The color of a point
	 */
	public double[] calculateNewColor(double[] normal, double[] hitPoint, double [] light, Primitive[] list, Ray eyeDirection, int bounceDepth)
	{
		
		double[] norm = Vector3d.normalize(normal);  //normalizes the normal to a unit vector
		
		//find the unit vector from the x,y,z to the Light Hitpoint is the x,y,z for the point on sphere/triangle
		//double [] lightVector = Vector3d.normalize(Vector3d.sub(hitPoint, light)); 
		double [] lightVector = Vector3d.normalize(Vector3d.sub(light,hitPoint)); 
		double diffuseFactor = Vector3d.dot(norm, lightVector); 
		
		//Reflect is I - 2N (N dot I) where I is incident light vector hitting the surface and N is the normal
		double[] lightReflect = Vector3d.normalize(Vector3d.sub(lightVector, Vector3d.scale(2,(Vector3d.scale(-diffuseFactor, norm)))));// lightVector - (2.0 * N dot I * norm);
		
		
		
		double specular = Math.max(0, Vector3d.dot(lightReflect, Vector3d.normalize(Vector3d.scale(-1, eyeDirection.direction)))); //direction the eye had hit the backwards "to eye"
		double calcSpecular = Math.pow(specular, shiny);
		
		if (Intersection.inShadow(hitPoint, light, list))
		{
			diffuseFactor = 0;//relies on the low ambient light value
			calcSpecular = 0; //light shouldn't be hitting something blocked in the first place
		}
		
		//adds the specular, ambience, and diffuse together.  Makes sure it doesn't exceed 1 or lower than 0
		double r = Math.max(0, calcSpecular*specularRatio+ (ambient+ diffuseFactor)*color[0]);
		r = Math.min(1,r);
		
		double g = Math.max(0, calcSpecular*specularRatio+ (ambient+ diffuseFactor)*color[1]);
		g = Math.min(1,g);
		
		double b = Math.max(0, calcSpecular*specularRatio+ (ambient+ diffuseFactor)*color[2]);
		b = Math.min(1,b);
		
		double[] newColor = new double[]{r,g,b};
		
		return Intersection.reflection(eyeDirection, light, eyeDirection.direction, hitPoint, norm, list, bounceDepth, newColor); //grabs reflected color if any
	}
}