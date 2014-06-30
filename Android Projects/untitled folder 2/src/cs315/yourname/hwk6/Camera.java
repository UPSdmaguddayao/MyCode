package cs315.yourname.hwk6;


/**
 * A class that represents a camera for a ray tracer. Calculates view and perspective transformations
 * @author Joel
 * @version Fall 2013
 */
public class Camera
{
	private double[] eye,D,R,U;
	private double w,h;
	
	/**
	 * Creates a new Camera object
	 * @param eye The position of the eye
	 * @param lookAt The point the camera is looking at
	 * @param up A vector in the "up" direction
	 * @param fovy The vertical field of view angle
	 * @param aspect The aspect ratio for view plane
	 */
	public Camera(double[] eye, double[] lookAt, double[] up, double fovy, double aspect)
	{
		this.eye = eye; //location of eye
		//0,0,1
		
		//our camera's view basis. Math from notes by Wayne Cochran (http://ezekiel.vancouver.wsu.edu/~cs548/notes/viewing/rayview.pdf)
		D = Vector3d.normalize(Vector3d.sub(lookAt, eye)); //D = normal of (0,0,-1 - 0,0,1) = 0,0,-1
		R = Vector3d.normalize(Vector3d.cross(D, up));     //Cross 0,0,-1 with 0,1,0 is 1,0,0
		U = Vector3d.cross(R, D); //cross 1,0,0 and 0,0,-1 which should be 0,1,0 (up)

		//aspect ratio
		h = 2*Math.tan(Math.toRadians(fovy/2.0)); //how high the eye sees
		w = h*aspect; //how wide it sees
	}

	/**
	 * Creates a viewing ray through the given pixel
	 * @param i x-coordinate of the pixel
	 * @param j y-coordinate of the pixel
	 * @param imageWidth the width the image (for relative position x pixel)
	 * @param imageHeight the height of the image (for relative position of y pixel)
	 * @return A view ray form the eye through the given pixel
	 */
	public Ray makeViewRay(int i, int j, int imageWidth, int imageHeight)
	{
		//calculate transformation
		double x = (w*i/(imageWidth-1))-(w/2);
		double y = (-h*j/(imageHeight-1))+(h/2);

		//calculate direction
		double[] dir = {
				x*R[0]+y*U[0]+D[0], //x*1 + y*0 + 0
				x*R[1]+y*U[1]+D[1], //x*0 + y*1 + 0
				x*R[2]+y*U[2]+D[2], //x*0 + y*0  -1?
		};//x,y,z directions

		return new Ray(eye, dir); //make a new ray with the calculated eye and direction
		//each origin (from the eye) has a direction associated with it
	}

}
