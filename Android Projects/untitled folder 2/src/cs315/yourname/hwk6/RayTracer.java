package cs315.yourname.hwk6;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;
/**
 * A class for doing ray tracing!
 * @author DJ Maguddayao
 */
public class RayTracer
{
	public static final double EPSILON = 0.00001f; //for error margins

	private BufferedImage frameBuffer;
	private int width; //how wide the frame is
	private int height; //how tall the frame is

	private Camera cam; //location of the camera
	
	private Primitive[] primitive;//holds both triangles and spheres
	private Light light;//light source
	
	private int bounces; //number of bounces the recursion will call  Keeping it small

	/**
	 * Creates a new Raytracer object
	 * @param width the intended with of the render frame
	 * @param height the intended height of the render frame
	 */
	public RayTracer(int width, int height)
	{
		this.width = width;
		this.height = height;
		frameBuffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB); //make bitmap

		cam = new Camera(
				new double[] {0,0,1}, //eye pos
				new double[] {0,0,-1}, //look at
				new double[] {0,1,0}, //up
				45,1); //fovy & aspect

		//camera is set up to be outside of screen looking in...and y axis is up
		
		primitive = new Primitive[10];
		light = new Light(3,0.0,5);
		
		//list of objects that are around
		primitive[0] = new Sphere(-2,0,-10,1, 0.1, 1.0, 200, new double[]{1.0,0.1,0.1});
		primitive[1] = new Sphere(10,-10,-40,5, 0.3, 1.0, 100, new double[]{0.1,0.1,1.0});
		primitive[2] = new Triangle(new double[]{-10,0,-25},new double[]{10,0,-25},new double[]{0,10,-25}, 0.1, 0.5,200, new double[]{1.0,1.0,1.0});
		primitive[3] = new Triangle(new double[]{10,0,-25},new double[]{-10,0,-25},new double[]{0,-10,-25}, 0.1, 0.5,200, new double[]{1.0,1.0,1.0});
		primitive[4] = new Sphere(2,0,-10 ,1, 0.1, 1.0, 200, new double[]{0.1,1.0,0.1});
		primitive[5] = new Sphere(0,2,-10 ,1, 0.1, 1.0, 200, new double[]{0.1,0.1,1.0});
		primitive[6] = new Sphere(0,-2,-10 ,1, 0.1, 1.0, 200, new double[]{1.0,1.0,0.1});
		
		bounces = 2;
	}

	/**
	 * Renders the image via ray tracing
	 */
	public void render()
	{
		if(frameBuffer == null) //in case called 
		{
			System.out.println("FrameBuffer not initialized");
			return;
		}
		System.out.println("Starting render...");
		long start = System.currentTimeMillis();
		
		//goes across every pixel y wise
		for (int j= 0; j < height; j++)
		{
			for(int i=0; i < width;i++)//goes across every pixel x wise
			{
				Ray viewRay = cam.makeViewRay(i, j, width, height);//compute viewing ray
				//see if the ray hit anything
				Primitive hitPrimitive = Intersection.findIntersection(viewRay,primitive); //compares all the primitives.
				//the one with the smallest t value that is above zero will be drawn.  If t is 0 or less, nothing is drawn
				
				if (hitPrimitive == null)
				{
					frameBuffer.setRGB(i,j, Color.gray.getRGB());
				}
				else
				{
					double[] hitPoint = Intersection.intersectionPoint(viewRay, hitPrimitive);
					double[] pixelColor = hitPrimitive.getNewColor(hitPoint, light.lightLocation(), primitive, viewRay,bounces);
				
					double r = pixelColor[0];
					double g = pixelColor[1];
					double b = pixelColor[2];
					//change the pixel using the color here.  Transform from float [] to integer
				
					int rgb = (int)(255) <<24| (int)(255*r) <<16|(int)(255*g) <<8| (int)(255*b);
				
					frameBuffer.setRGB(i, j, rgb);
					//get pixel value
				}		
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("...rendered in "+(end-start)+"ms");

	}


	/**
	 * Returns the rendered image
	 * @return A Image representation of the currently rendered image
	 */
	public Image getImage()
	{
		return frameBuffer;
	}
}