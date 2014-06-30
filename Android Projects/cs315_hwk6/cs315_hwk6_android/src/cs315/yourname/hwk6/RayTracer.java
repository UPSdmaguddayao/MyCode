package cs315.yourname.hwk6;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

/**
 * A class for doing ray tracing
 * @author DJ Maguddayao
 */
public class RayTracer
{
	private static final String TAG = "RayTracer";

	public static final double EPSILON = 0.00001f; //for error margins

	private Bitmap frameBuffer;
	private int targetWidth;
	private int targetHeight;
	private int width;
	private int height;
	
	private Camera cam; //the camera for our rendering
	
	private Primitive[] primitive;//holds both triangles and spheres
	private Light light;//light source
	
	private int bounces; //number of bounces the recursion will call  Keeping it small

	/**
	 * Creates a new Raytracer object
	 * @param targetWidth the intended with of the render frame
	 * @param targetHeight the intended height of the render frame
	 */
	public RayTracer(int targetWidth, int targetHeight)
	{
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		if(targetWidth > 0)
		{
			width = targetWidth;
			height = targetHeight;
			frameBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //make bitmap
		}
		
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
		
		
		Log.d(TAG, "NEW RAYTRACER CREATED");
	}

	/**
	 * Resizes the frame buffer being filled. Has no effect if RayTracer has a desired target width specified
	 * @param width The new width
	 * @param height The new height
	 * @param force Whether to force a redraw
	 */
	public void resize(int width, int height, boolean force)
	{
		if(targetWidth < 0 || targetHeight < 0 || force) //if we don't have a target width
		{
			this.width = width;
			this.height = height;
			frameBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //make bitmap
		}
	}


	/**
	 * Renders the image via ray tracing
	 */
	public void render()
	{
		if(frameBuffer == null) //in case called 
		{
			Log.d(TAG, "FrameBuffer not initialized");
			return;
		}
		Log.d(TAG,"Starting render...");
		long start = SystemClock.uptimeMillis();


		
		
		//TODO Ray Tracing code (broad) goes here!

		
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
					frameBuffer.setPixel(i, j, Color.GRAY);
					//frameBuffer.setRGB(i,j, Color.gray.getRGB());
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
				
					frameBuffer.setPixel(i, j, rgb);
					//get pixel value
				}		
			}
		}
		
		

		long end = SystemClock.uptimeMillis();
		Log.d(TAG,"...rendered in "+(end-start)+"ms");

	}

	
	/**
	 * Returns the rendered image
	 * @return A Bitmap of the currently rendered image
	 */
	public Bitmap getImage()
	{
		return frameBuffer;
	}


}
