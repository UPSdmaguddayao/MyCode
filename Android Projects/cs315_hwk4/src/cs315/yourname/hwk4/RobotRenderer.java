package cs315.yourname.hwk4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

/**
 * This class represents a custom OpenGL renderer--all of our "drawing" logic goes here.
 * 
 * @author DJ; code received from Joel who adapted from Google and LearnOpenGLES
 * @version Fall 2013
 */
public class RobotRenderer implements GLSurfaceView.Renderer 
{
	private static final String TAG = "RUBIX Renderer"; //for logging/debugging

	//some constants given our model specifications
	private final int POSITION_DATA_SIZE = 3;	
	private final int NORMAL_DATA_SIZE = 3;
	private final int COLOR_DATA_SIZE = 4; //in case we may want it!
	private final int BYTES_PER_FLOAT = 4;
	
	//Matrix storage
	private float[] mModelMatrix = new float[16]; //to store current model matrix
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVMatrix = new float[16]; //to store the current modelview matrix
	private float[] mMVPMatrix = new float[16]; //combined MVP matrix
	private float[] mTempMatrix = new float[16]; //temporary matrix for transformations, if needed
	
	
	//stores the locations of each parts pivot point
	//private Stack<float[]> stack; //A stack to manage traversal  #Scrapped idea.  Too long to implement now
	
	private float[] mHead = new float[16];
	private float[] mArmLength = new float[16];
	private float[] mRightShoulder = new float[16];
	private float[] mLeftShoulder = new float[16];
	
	private float[] mBody = new float[16];
	private float[] mUpperPants = new float[16];
	private float[] mLowerPants = new float[16];
	private float[] mRightLeg = new float[16];
	private float[] mLeftLeg = new float[16];
	
	//Buffer for model data
	private final FloatBuffer mCubeData;
	private final int mCubeVertexCount; //vertex count for the buffer
	private final FloatBuffer mSphereData;
	private final int mSphereVertexCount;

	private final float[] mColorRed;
	private final float[] mColorBlue;
	private final float[] mColorGrey;

	//axis points (for debugging)
	private final FloatBuffer mAxisBuffer;
	private final int mAxisCount;
	private final float[] lightNormal = {0,0,3};
	
	
	//Handles Movement
	private boolean isDancing; //is it dancing or do we keep it still?
	private boolean armRising; //both arms move simulateneously so alright to have singular call
	private boolean leftLegRising; //keeps track if the leg is rising or falling
	private boolean rightLegRising;
	private boolean rightLegIsMoving; //right leg moves first.  When false, then left leg is moving
	
	//angles for movement.  Keeps track of each joint's angle.  Initialized as 0 for all in the code
	private float shoulderMovement;
	private float forearmMovement;
	private float rightLegMovement;
	private float leftLegMovement;
	/**
	 * OpenGL Handles
	 * These are C-style "pointers" (int representing a memory address) to particular blocks of data.
	 * We pass the pointers around instead of the data to increase efficiency (and because OpenGL is
	 * C-based, and that's how they do things).
	 */
	private int mPerVertexProgramHandle; //our "program" (OpenGL state) for drawing (uses some lighting!)
	private int mMVMatrixHandle; //the combined ModelView matrix
	private int mMVPMatrixHandle; //the combined ModelViewProjection matrix
	private int mPositionHandle; //the position of a vertex
	private int mNormalHandle; //the position of a vertex
	private int mColorHandle; //the color to paint the model
	
	//define the source code for the vertex shader
	private final String perVertexShaderCode = 
		    "uniform mat4 uMVMatrix;" + 	// A constant representing the modelview matrix. Used for calculating lights/shading
			"uniform mat4 uMVPMatrix;" +	// A constant representing the combined modelview/projection matrix. We use this for positioning
			"attribute vec4 aPosition;" +	// Per-vertex position information we will pass in
			"attribute vec3 aNormal;" +		// Per-vertex normal information we will pass in.
			"attribute vec4 aColor;" +		// Per-vertex color information we will pass in.
			"varying vec4 vColor;"  + 		//out : the ultimate color of the vertex
			"vec3 lightPos = vec3(0.0,0.0,3.0);" + //the position of the light
			"void main() {" +
			"  vec3 modelViewVertex = vec3(uMVMatrix * aPosition);" + 			//position modified by modelview
			"  vec3 modelViewNormal = normalize(vec3(uMVMatrix * vec4(aNormal, 0.0)));" +	//normal modified by modelview
			"  vec3 lightVector = normalize(lightPos - modelViewVertex);" +		//the normalized vector between the light and the vertex
			"  float diffuse = max(dot(modelViewNormal, lightVector), 0.1);" +	//the amount of diffuse light to give (based on angle between light and normal)
			"  vColor = aColor * diffuse;"+ 									//scale the color by the light factor and set to output
			"  gl_PointSize = 3.0;" +		//for drawing points
			"  gl_Position = uMVPMatrix * aPosition;" + //gl_Position is built-in variable for the transformed vertex's position.
			"}";

	private final String fragmentShaderCode = 
			"precision mediump float;" + 	//don't need high precision
			"varying vec4 vColor;" + 		//color for the fragment; this was output from the vertexShader
			"void main() {" +
			"  gl_FragColor = vColor;" + 	//gl_fragColor is built-in variable for color of fragment
			"}";

	
	/**
	 * Constructor should initialize any data we need, such as model data
	 */
	public RobotRenderer(Context context)
	{	
		/**
		 * Initialize our model data--we fetch it from the factory!
		 */
		ModelFactory models = new ModelFactory();

		//stack = new Stack<float[]>();
		
		float[] cubeData = models.getCubeData();
		mCubeVertexCount = cubeData.length/(POSITION_DATA_SIZE+NORMAL_DATA_SIZE);
		mCubeData = ByteBuffer.allocateDirect(cubeData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer(); //generate buffer
		mCubeData.put(cubeData); //put the float[] into the buffer and set the position

		//more models can go here!
		
		float[] sphereData = models.getSphereData(1);
		mSphereVertexCount = sphereData.length/(POSITION_DATA_SIZE+NORMAL_DATA_SIZE);
		mSphereData = ByteBuffer.allocateDirect(sphereData.length *BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mSphereData.put(sphereData);
		
		//set up some example colors. Can add more as needed!
		mColorRed = new float[] {0.8f, 0.1f, 0.1f, 1.0f};
		mColorBlue = new float[] {0.1f, 0.1f, 0.8f, 1.0f};
		mColorGrey = new float[] {0.8f, 0.8f, 0.8f, 1.0f};

		//axis
		float[] axisData = models.getCoordinateAxis();
		mAxisCount = axisData.length/POSITION_DATA_SIZE;
		mAxisBuffer = ByteBuffer.allocateDirect(axisData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer(); //generate buffer
		mAxisBuffer.put(axisData); //put the float[] into the buffer and set the position

		
		//List of coordinates.  Only some are actually connected to one another
		
		mHead = new float[] { //coordinates 0,-3,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				0.0f,2.0f,0.0f,1.0f			
		};
		mArmLength = new float[] { //coordinates 0,-3,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				0.0f,-3.0f,0.0f,1.0f			
		};
		
		//Transformation needed to get to right shoulder from arm length
		mRightShoulder =new float[]{
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				-6.5f,0.0f,0.0f,1.0f	
		};
		

		
		//Transformation needed to get to left shoulder from arm length
		mLeftShoulder = new float[]{ //6.5,-3,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				6.5f,0.0f,0.0f,1.0f			
		};
		
		mBody = new float[]{ //0,-13,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				0.0f,-13.0f,0.0f,1.0f			
		};


		mUpperPants = new float[]{ //0,-23,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				0.0f,-23.0f,0.0f,1.0f	
				
		};
		

		mLowerPants = new float[]{ //0,-25,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				0.0f,-25.0f,0.0f,1.0f			
		};
		

		mRightLeg = new float[]{ //-3,-27,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				-3.0f,-27.0f,0.0f,1.0f			
		};
		

		mLeftLeg = new float[]{ //3,-27,0
				1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				3.0f,-27.0f,0.0f,1.0f			
		};
		
		//initial values.  This is at stand still (no dancing/movement)
		shoulderMovement = 0.0f;
		forearmMovement= 0.0f;
		rightLegMovement = 0.0f;
		leftLegMovement = 0.0f;		
		isDancing =false;
		armRising = false;
		rightLegIsMoving = false;
		rightLegRising = false;
	}
	
	public void setValue(float shoulder,float forearm, float rightLeg, float leftLeg, boolean dance,boolean arm, boolean rightMovingLeg, boolean rightRisingLeg)
	{
		shoulderMovement = shoulder;
		forearmMovement = forearm;
		rightLegMovement = rightLeg;
		leftLegMovement = leftLeg;
		isDancing = dance;
		armRising = arm;
		rightLegIsMoving = rightMovingLeg;
		rightLegRising = rightRisingLeg;
	}

	/**
	 * This method is called when the rendering surface is first created; more initializing stuff goes here.
	 * I put OpenGL initialization here (with more generic model initialization in the Renderer constructor).
	 * 
	 * Note that the GL10 parameter is unused; this parameter acts sort of like the Graphics2D context for
	 * doing GLES 1.0 operations. But we don't use that class for GLES 2.0+; but in order to keep Android 
	 * working and backwards compatible, the method has the same signature so the unused object is passed in.
	 */
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) 
	{
		//flags to enable depth work
		GLES20.glEnable(GLES20.GL_CULL_FACE); //remove back faces
		GLES20.glEnable(GLES20.GL_DEPTH_TEST); //enable depth testing
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		
		// Set the background clear color
		GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f); //Currently a dark grey so we can make sure things are working

		//This is a good place to compile the shaders from Strings into actual executables. We use a helper method for that
		int vertexShaderHandle = GLUtilities.compileShader(GLES20.GL_VERTEX_SHADER, perVertexShaderCode); //get pointers to the executables		
		int fragmentShaderHandle = GLUtilities.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		mPerVertexProgramHandle = GLUtilities.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle); //and then we throw them into a program

		//Get pointers to the shader's variables (for use elsewhere)
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "uMVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "uMVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aPosition");
		mNormalHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aNormal");
		mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aColor");
	}

	/**
	 * Called whenever the surface changes (i.e., size due to rotation). Put initialization stuff that
	 * depends on the size here!
	 */
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height); // Set the OpenGL viewport (basically the canvas) to the same size as the surface.

		/**
		 * Set up the View and Projection matrixes. These matter more for when we're actually constructing
		 * 3D models, rather than 2D models in a 3D world.
		 */
		
		//Set View Matrix.  This is zoomed out
		Matrix.setLookAtM(mViewMatrix, 0, 
				-10.0f, -20.0f, 40.0f, //eye's location 
				0.0f, -20.0f, -1.0f, //direction we're looking at
				0.0f, 1.0f, 0.0f //direction that is "up" from our head
				); //this gets compiled into the proper matrix automatically

		//Set Projection Matrix. We will talk about this more in the future
		final float ratio = (float) width / height; //aspect ratio
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1;
		final float top = 1;
		final float near = 1.0f;
		final float far = 50.0f;
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	/**
	 * This is like our "onDraw" method; it says what to do each frame
	 */
	@Override
	public void onDrawFrame(GL10 unused) 
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT); //start by clearing the screen for each frame
																				//remember to clear
		GLES20.glUseProgram(mPerVertexProgramHandle); //tell OpenGL to use the shader program we've compiled
		
		/*
		// Do a complete rotation every 10 seconds.
		long time = SystemClock.uptimeMillis() % 10000L;        
		float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
		*/
		
		if (isDancing == true) //checks to see if Dance is on.  Else, would stay at neutral
		{
			if (armRising == true) //simple check to make the arms rise or fall
			{
			shoulderMovement -=1.0f;
				if (shoulderMovement == -75) //once arms are at 75 degrees, start to fall
				{
					armRising = false;
				}
			}
			if (!armRising)
			{
				shoulderMovement += 1.0f;
				if (shoulderMovement == -45)
				{
					armRising = true;
				}
			}
			
			if (forearmMovement > -25.0f)//small movement at the beginning to put hands in the right positions from neutral
			{
				forearmMovement -= 0.2f;
			}
			
			//right leg isn't moving.  Hence, left leg is now.  Only occurs when dancing
			if (shoulderMovement<= -45.0f)//starts to rise and fall when arms get to stopping point
			{
			if (rightLegIsMoving == false) //left leg movement
			{
				if (leftLegRising == true)//assumes it starts from 0
				{
					leftLegMovement -= 3.0f; //moves in sync...kinda.  Supposed to move at same pace as arms, but offsyncs after a while
					if (leftLegMovement == -90.0f)
					{
						leftLegRising = false;
					}
				}
				if (leftLegRising == false) //falling
				{
					leftLegMovement += 3.0f;
					if(leftLegMovement == 0.0f) //once back on the ground
					{
						rightLegIsMoving= true; //sets values to right leg can rise as soon as left leg hits the ground
						rightLegRising = true;
					}
				}
				//left leg is done.  Right leg is now moving
			}
	
			if (rightLegIsMoving == true) //same as left leg
			{
				if (rightLegRising == true)
				{
					rightLegMovement -= 3.0f;
					if (rightLegMovement == -90f)
					{
						rightLegRising = false;
					}
				}
				if (rightLegRising == false)
				{
					rightLegMovement += 3.0f;
					if(rightLegMovement == 0f)
					{
						rightLegIsMoving= false;
						leftLegRising = true;
					}
				}
				
			}
			}
		}
		
		Matrix.setIdentityM(mModelMatrix, 0); //start modelMatrix as identity (no transformations)
		
		drawStaticParts();
		
		drawRightArm(shoulderMovement,forearmMovement);//for rotation equations
		drawLeftArm(shoulderMovement,forearmMovement);
		drawRightLeg();
		drawLeftLeg();
		drawAxis(); //so we have guides on coordinate axes, for debugging
	}
	
	private void drawStaticParts()
	{
		//head
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mHead, 0, mModelMatrix, 0);
		Matrix.scaleM(mTempMatrix, 0, 4.0f, 4.0f, 4.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//upper armlength
		
		Matrix.multiplyMM(mTempMatrix, 0, mArmLength, 0, mModelMatrix, 0); //translates to the armlength.
		
		//stack.push(mTempMatrix); //saves the location of arm length
		//printMatrix(stack.peek());
		Matrix.scaleM(mTempMatrix, 0, 4.0f, 1.0f, 5.0f);//scales to fit
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorBlue);
		//printMatrix(stack.pop()); //I noticed the two matrices are different.  I can solve this, but I ran out of time (i.e. noticed this too late) ._.
		
		
		//body
		Matrix.multiplyMM(mTempMatrix, 0, mBody, 0, mModelMatrix, 0);
		Matrix.scaleM(mTempMatrix, 0, 4.0f, 9.0f, 5.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorBlue);
		
		//upperPants
		Matrix.multiplyMM(mTempMatrix, 0, mUpperPants, 0, mModelMatrix, 0);
		Matrix.scaleM(mTempMatrix, 0, 4.0f, 1.0f, 5.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
		
		//lowerPants
		Matrix.multiplyMM(mTempMatrix,0,mLowerPants,0,mModelMatrix,0);
		Matrix.scaleM(mTempMatrix, 0, 4.0f, 1.0f, 5.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
	}
	
	//TODO:  Find out how to make the shifts work with Matrix Multiplication.  If not, this is good enough (misses 20% if not done)
	private void drawRightArm(float backArm,float foreArm)
	{
		//uses a lot of unscales due to not implementing the the Matrix Hierarchy
		
		//draws right shoulder
		Matrix.multiplyMM(mTempMatrix, 0, mRightShoulder, 0, mArmLength, 0);
		Matrix.scaleM(mTempMatrix, 0, 2.5f, 2.5f, 2.5f);
		Matrix.rotateM(mTempMatrix, 0, shoulderMovement, 1.0f, 0.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//draws right upper arm
		
		Matrix.scaleM(mTempMatrix, 0, 0.4f, 0.4f, 0.4f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.5f, 0.0f);
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 5.0f, 1.5f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
		
		//draws right elbow
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 0.2f, 0.67f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix,0,2.0f,2.0f,2.0f);
		Matrix.rotateM(mTempMatrix, 0, -foreArm, 0.0f, 0.0f,1.0f); //Use for transformation stuff.  Simulates movement
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//lower right arm
		
		Matrix.scaleM(mTempMatrix,0,0.5f,0.5f,0.5f);
		Matrix.translateM(mTempMatrix,0,0.0f,-6.0f,0.0f);
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 5.0f, 1.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
		
		//right hand
		
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 0.2f, 1.0f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix, 0, 3.0f, 3.0f, 3.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		

	}
	private void drawLeftArm(float backArm,float foreArm)
	{
		//draws LeftShoulder
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mLeftShoulder, 0, mArmLength, 0);
		Matrix.scaleM(mTempMatrix, 0, 2.5f, 2.5f, 2.5f);
		Matrix.rotateM(mTempMatrix, 0, backArm, 1.0f, 0.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);

		//draws upper arm
		Matrix.scaleM(mTempMatrix, 0, 0.4f, 0.4f, 0.4f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.5f, 0.0f);
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 5.0f, 1.5f);	
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
		
		//draws elbow
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 0.2f, 0.67f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix,0,2.0f,2.0f,2.0f);
		Matrix.rotateM(mTempMatrix, 0, foreArm, 0.0f, 0.0f,1.0f); //Use for transformation stuff.  Simulates movement
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//draws lower arm
		Matrix.scaleM(mTempMatrix,0,0.5f,0.5f,0.5f);
		Matrix.translateM(mTempMatrix,0,0.0f,-6.0f,0.0f);
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 5.0f, 1.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);

		//draws 'hands'
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 0.2f, 1.0f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix, 0, 3.0f, 3.0f, 3.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
	}
	
	private void drawRightLeg()
	{
		//draws right leg joint
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mRightLeg, 0, mModelMatrix, 0);
		Matrix.scaleM(mTempMatrix, 0, 1.5f, 1.5f, 1.5f);
		Matrix.rotateM(mTempMatrix, 0, rightLegMovement, 1.0f, 0.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//draws thigh
		Matrix.scaleM(mTempMatrix, 0, 0.67f, 0.67f, 0.67f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix,0,1.0f,5.0f,3.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
		
		//draws right knee
		Matrix.scaleM(mTempMatrix,0,1.0f,0.2f,0.5f);
		Matrix.translateM(mTempMatrix,0,0.0f,-6.0f,0.0f);
		Matrix.rotateM(mTempMatrix,0,-rightLegMovement,1.0f,0.0f,0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//rotation in opposite ways keeps the last part stationary.  So if I have rotation in
		//the first part and the second part rotating in the opposite way, the second part looks
		//stationary.
		
		//draws calves
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 5.0f, 1.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
		

		
		
	}
	private void drawLeftLeg()
	{
		//draws left leg joint
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mLeftLeg, 0, mModelMatrix, 0);
		Matrix.scaleM(mTempMatrix, 0, 1.5f, 1.5f, 1.5f);
		Matrix.rotateM(mTempMatrix, 0, leftLegMovement, 1.0f, 0.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//draws left thigh
		Matrix.scaleM(mTempMatrix, 0, 0.67f, 0.67f, 0.67f);
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix,0,1.0f,5.0f,3.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
		
		//draws left knee
		Matrix.scaleM(mTempMatrix,0,1.0f,0.2f,0.5f);
		Matrix.translateM(mTempMatrix,0,0.0f,-6.0f,0.0f);
		Matrix.rotateM(mTempMatrix,0,-leftLegMovement,1.0f,0.0f,0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorBlue);
		
		//draws left calf
		Matrix.translateM(mTempMatrix, 0, 0.0f, -6.0f, 0.0f);
		Matrix.scaleM(mTempMatrix, 0, 1.0f, 5.0f, 1.0f);
		drawPackedTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorRed);
	}
	/**
	 * Prints the given 4x4 matrix to LogCat (info priority).
	 * @param array
	 */
	private static void printMatrix(float[] array)
	{
		Log.i("MATRIX","[ " + array[0] + ", "+array[4] + ", "+array[8] + ", "+array[12]);
		Log.i("MATRIX","  " + array[1] + ", "+array[5] + ", "+array[9] + ", "+array[13]);
		Log.i("MATRIX","  " + array[2] + ", "+array[6] + ", "+array[10] + ", "+array[14]);
		Log.i("MATRIX","  " + array[3] + ", "+array[7] + ", "+array[11] + ", "+array[15]+ " ]");		
	}

	/**
	 * Draws a triangle buffer with the given modelMatrix and single color. 
	 * Note the view matrix is defined per program.
	 */			
	private void drawPackedTriangleBuffer(FloatBuffer buffer, int vertexCount, float[] modelMatrix, float[] color)
	{		
		//Calculate MV and MVPMatrix. Note written as MVP, but really P*V*M
		Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, modelMatrix, 0);  //"M * V"
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0); //"MV * P"

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0); //put combined matrixes in the shader variables
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		
		final int stride = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE) * BYTES_PER_FLOAT; //how big of steps we take through the buffer
		
		buffer.position(0); //reset buffer start to 0 (where the position data starts)
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, stride, buffer); //note the stride lets us step over the normal data!
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		buffer.position(POSITION_DATA_SIZE); //shift pointer to where the normal data starts
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, stride, buffer); //note the stride lets us step over the position data!
		GLES20.glEnableVertexAttribArray(mNormalHandle);

		//put color data in the shader variable
		GLES20.glVertexAttrib4fv(mColorHandle, color, 0);

		//This the OpenGL command to draw the specified number of vertices (as triangles; that is, every 3 coordinates). 
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
	}		

	
	//draws the coordinate axis (for debugging)
	private void drawAxis()
	{
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mMVMatrix, 0, mModelMatrix, 0, mViewMatrix, 0);  //M * V
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0); //P * MV 

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		// Pass in the position information
		mAxisBuffer.position(0); //reset buffer start to 0 (just in case)
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, mAxisBuffer); 
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mNormalHandle); //turn off the buffer version of normals
		GLES20.glVertexAttrib3fv(mNormalHandle, lightNormal, 0); //pass particular normal (so points are bright)

		//GLES20.glDisableVertexAttribArray(mColorHandle); //just in case it was enabled earlier
		GLES20.glVertexAttrib4fv(mColorHandle, mColorGrey, 0); //put color in the shader variable
		
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mAxisCount); //draw the axis (as points!)
	}

}