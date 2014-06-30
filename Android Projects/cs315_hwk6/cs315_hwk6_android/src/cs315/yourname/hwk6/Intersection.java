package cs315.yourname.hwk6;
/**
 * A class that represents an intersection between a ray and a surface. This mostly contains helper methods for now
 * and can be optimized in the near future.  The methods can be accessed by all classes to help find different intersections
 * @author DJ Maguddayao
 *
 */
public class Intersection {
	
	/**
	 * With a list of primitives, find if the ray given intersect with them and find the closest one
	 * @param ray The ray which holds the origin and direction of it
	 * @param primitive List of primitives to test
	 * @return the lowest t valued primitive above 0
	 */
	public static Primitive findIntersection(Ray ray, Primitive[] primitive) //can be accessed anywhere
	{
		double min_t = 1000000000; //make minimum t as close to infinity as possible
		double t = 0;
		Primitive min_primitive = null;
		
		
		for (int i = 0; i < primitive.length; i++)
		{
			if (primitive[i] == null) //skips the primitive slot if not available
			{
				 //immediately look at next value
			}
			else
			{
				t = primitive[i].getIntersectValue(ray);
				if (t> 0.0 && t < min_t) //checks of the primitive isn't behind the viewing ray
				{
					min_t = t;
					min_primitive = primitive[i];
				}
			}
		}
		return min_primitive;
		 //Takes all the objects and scans for the smallest t value for the ray
	}
	
	/**
	 * Finds the intersection point in x,y,z of the a ray and a primitive
	 * @param ray A ray to see where it intersects the primitive
	 * @return
	 */
	public static double[] intersectionPoint(Ray ray, Primitive primitive)
	{
		return Vector3d.add(ray.origin, Vector3d.scale(primitive.getIntersectValue(ray),ray.direction)); //gets the t value to multiply	
	}
	

	/**
	 * Used to find if the ray intersects with a specified sphere 
	 * @param ray viewRay
	 * @param s sphere to be intersected(?) with
	 * @return The t value for the ray.  If it doesn't intersect, returns -1 
	 */
	public static double sphereIntersection(Ray ray, Sphere s)
	{
		
		//Can be optimized by creating a box around the sphere and seeing if it intersects inside the box
		//Can implement later
		
		double t = -1;
		
		//stores direction of the ray
		double dx = ray.direction[0];
		double dy = ray.direction[1];
		double dz = ray.direction[2];
		
		//stores values of the sphere's center
		double cx = s.getX();
		double cy = s.getY();
		double cz = s.getZ();
		
		//stores origin value...should be x,y,z
		double originX = ray.origin[0];
		double originY = ray.origin[1];
		double originZ = ray.origin[2];
		
		double a = dx*dx + dy*dy + dz*dz;
		double b = 2*dx*(originX-cx) +  2*dy*(originY-cy) +  2*dz*(originZ-cz);
		double c = cx*cx + cy*cy + cz*cz + originX*originX + originY*originY + originZ*originZ + -2*(cx*originX + cy*originY + cz*originZ) - (s.getRadius()*s.getRadius());
		
		double deter = b*b - 4*a*c;
		
		if (deter < 0)
		{
			return t; //no intersection
		}
		
		else
		{
			t = ((-b - Math.sqrt(deter))/(2*a));
		}
		return t;
	}
	
	/**
	 * Calculates the t value if a triangle is intersected by a ray
	 * @param ray ray being used to 'intersect'
	 * @param tri Triangle with possibility to be intersected
	 * @return A t value
	 */
	public static double triangleIntersection (Ray ray, Triangle tri)
	{
		
		double[] e1 = Vector3d.sub(tri.v1(), tri.v0());
		double[] e2 = Vector3d.sub(tri.v2(), tri.v0());
		double[] T = Vector3d.sub(ray.origin, tri.v0());
		double[] P = Vector3d.cross(ray.direction, e2);
		double[] Q = Vector3d.cross(T,e1);
		double divisor = Vector3d.dot(P, e1);
		
		double u = Vector3d.dot(P, T) / divisor; //beta
		if (u <0 || u > 1)
		{
			return -1;
		}
		double v = Vector3d.dot(Q, ray.direction) / divisor; //gamma
		if ( v < 0 || u+v > 1)
		{
			return -1;
		}
		double t = Vector3d.dot(Q, e2) /divisor;//t value
		if (t < 0)
		{
			return -1;
		}
		return t;
	}
	
	public static boolean inShadow(double[]hitPoint, double[] light, Primitive[] list)
	{
		double[] lightVector = Vector3d.normalize(Vector3d.sub(light, hitPoint)); //vector direction from point to light source normalized
		Ray toLightRay = new Ray(Vector3d.add(hitPoint, Vector3d.scale(0.001, lightVector)), lightVector);
		Primitive hit = Intersection.findIntersection(toLightRay, list); //assuming that if the hitpoint value returned will be 0 if it is the same primitive
		if(hit != null)
		{
			return true;
		}
		
		return false;
	}
	
public static double[] reflection(Ray view, double[] light,double[] viewDirection, double[] hitPoint, double[] norm, Primitive[] list, int bounceDepth, double[] color)
	{
		//grab hitPoint's color each time and lower bounceDepth
		if(bounceDepth == 0)
		{
			return color;
		}
		
		else
		{
			double c1 = -Vector3d.dot(Vector3d.normalize(viewDirection), norm);
			double[] bounce = Vector3d.normalize(Vector3d.add(viewDirection, Vector3d.scale(2*c1,norm))); //direction it bounces to
			
			double[] newStart = Vector3d.add(hitPoint, Vector3d.scale(0.0001, bounce)); //adds a small offset to the starting point to avoid self bounce?
			Ray bounceRay = new Ray(newStart,bounce); 
		
			Primitive hitPrimitive = findIntersection(bounceRay, list);
			if (hitPrimitive != null)//bounce actually hit something 
			{	
				//get t value and add that to the ray to get the hit point
		
				double[] newHitPoint = intersectionPoint(bounceRay,hitPrimitive);
		
				double[] hitColor= hitPrimitive.getNewColor(newHitPoint, light, list, view,0); //grabs the color of this object.  No bounces will be calculated in this
		
				//Calculate added colors
				double newR = Math.max(0, color[0]+hitColor[0]);
				newR = Math.min(1,newR);
		
				double newG = Math.max(0, color[1]+hitColor[1]);
				newG = Math.min(1,newG);
		
				double newB = Math.max(0, color[2]+hitColor[2]);
				newB = Math.min(1,newB);
	
				bounceDepth--;//reduce the bounce
		
				return reflection(view, light, bounce, newHitPoint, hitPrimitive.getNormal(newHitPoint),list, bounceDepth, new double[]{newR,newG,newB});
			}
			else
			{
				return color;
			}
		}
	
	}
}