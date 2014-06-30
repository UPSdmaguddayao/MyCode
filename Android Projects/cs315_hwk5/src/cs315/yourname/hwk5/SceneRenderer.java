package cs315.yourname.hwk5;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

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
 * @author Joel; code adapted from Google and LearnOpenGLES
 * @version Fall 2013
 */
public class SceneRenderer implements GLSurfaceView.Renderer 
{
	private static final String TAG = "Scene Renderer"; //for logging/debugging
	private Context context; //for accessing assets
	
	//some constants given our model specifications
	public final int POSITION_DATA_SIZE = 3;	
	public final int NORMAL_DATA_SIZE = 3;
	public final int COLOR_DATA_SIZE = 4; //in case we may want it!
	public final int BYTES_PER_FLOAT = 4;
	public final int BYTES_PER_SHORT = 2;
	public final int TEXTURE_DATA_SIZE = 2;

	//Matrix storage
	private float[] mModelMatrix = new float[16]; //to store current model matrix
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVMatrix = new float[16]; //to store the current modelview matrix
	private float[] mMVPMatrix = new float[16]; //combined MVP matrix
	private float[] mTempMatrix = new float[16];
	
	private float[] mCloudLocation = new float[16];
	private float[] mTreeLocation = new float [16];
	private float[] mGroundLocation = new float [16];
	private float[] mLegoLocation = new float [16];
	
	private float[] mSunLocation = new float[16]; //stores the location of the "Sun"
	private float[] mInitialDay = new float[16];
	private float[] lightLocation; //keeps track of where the light is.  Just a 3 vector float, but I don't initialize this
	
	//Color storage
	private final float[] mColorRed;
	private final float[] mColorBlue;
	private final float[] mColorGrey;
	private final float[] mColorGreen;
	private final float[] mColorWhite;
	private final float[] mColorYellow;
	private final float[] mColorBrown;

	//ambient light value of different objects
	private final float[] mTreeAmbient;
	private final float[] mLegoAmbient;
	private final float[] mCloudAmbient;
	private final float[] mSunAmbient;
	
	//axis points (for debugging)
	private final FloatBuffer mAxisBuffer;
	private final int mAxisCount;
	private final float[] axislightNormal = {0,0,3};

	//Just for a sphere and cube...heres a buffer
	private final FloatBuffer mCubeData;
	private final int mCubeVertexCount; //vertex count for the buffer
	private final FloatBuffer mCubeTextureBuffer; //for texturing the cube
	private final FloatBuffer mSphereData;
	private final int mSphereVertexCount;
	
	//OpenGL Handles
	private int mPerVertexProgramHandle; //our "program" (OpenGL state) for drawing (uses some lighting!)
	private int mMVMatrixHandle; //the combined ModelView matrix
	private int mMVPMatrixHandle; //the combined ModelViewProjection matrix
	private int mPositionHandle; //the position of a vertex
	private int mNormalHandle; //the position of a vertex
	private int mColorHandle; //the color to paint the model
	private int mAmbientHandle; //handles the different ambient lights between objects
	
	private int mLightHandle; //position of light

	//for textures
	private int mTextureBufferHandle; //memory location of texture buffer (data)
	private int mTextureBufferHandle2; //for a second texture
	private int mTextureHandle; //pointer into shader
	private int mTextureCoordHandle; //pointer into shader
	private int mTextureLightingHandle;
	private int mColorTextureHandle;
	private int mNormalTextureHandle;
	private int mPositionTextureHandle;
	private int mMVPMatrixTextureHandle;
	private int mMVMatrixTextureHandle;
	private int mPerVertexTextureHandle;
	
	//meshes I used
	private final Mesh legoman; //this one was made by Joel
	private final Mesh treeTop;
	private final Mesh treeTrunk;
	
	private boolean isNight; //determines if the initial starting point is night
	private boolean isMoving; //determines if the sun is moving
	
	/**
	 * Constructor should initialize any data we need, such as model data
	 */
	public SceneRenderer(Context context)
	{	
		this.context = context; //so we can fetch from assets
		
		//initialize objects of meshes
		legoman = new Mesh ("legoman.obj",context);
		treeTop = new Mesh("tree_top.obj",context);
		treeTrunk = new Mesh("tree_trunk.obj",context);
		
		//set up some example colors. Can add more as needed!
		mColorRed = new float[] {0.8f, 0.1f, 0.1f, 1.0f};
		mColorBlue = new float[] {0.1f, 0.1f, 0.8f, 1.0f};
		mColorGrey = new float[] {0.8f, 0.8f, 0.8f, 1.0f};
		mColorGreen = new float[] {0.1f,0.8f,0.1f,1.0f};
		mColorWhite = new float[] {1.0f,1.0f,1.0f,1.0f};
		mColorYellow = new float[] {1.0f,1.0f,0.1f,1.0f};
		mColorBrown = new float[] {0.6f,0.25f,0.1f,1.0f};

		//Ambient Values of objects
		mTreeAmbient = new float[] {0.5f, 0.5f, 0.5f, 1.0f};
		mLegoAmbient = new float[] {0.75f, 0.75f, 0.75f, 1.0f};
		mCloudAmbient = new float[] {0.6f, 0.6f, 0.6f, 1.0f};
		mSunAmbient = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
		
		//initializing models
		ModelFactory models = new ModelFactory();

		float[] cubeData = models.getCubeData();
		mCubeVertexCount = cubeData.length/(POSITION_DATA_SIZE+NORMAL_DATA_SIZE);
		mCubeData = ByteBuffer.allocateDirect(cubeData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer(); //generate buffer
		mCubeData.put(cubeData); //put the float[] into the buffer and set the position
		//sets the buffer for the Cube Texture
		float[] cubeTexData = models.getCubeTextureData();
		mCubeTextureBuffer = ByteBuffer.allocateDirect(cubeTexData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer(); //generate buffer
		mCubeTextureBuffer.put(cubeTexData); //put the float[] into the buffer and set the position

		//sets sphere data
		float[] sphereData = models.getSphereData(1);
		mSphereVertexCount = sphereData.length/(POSITION_DATA_SIZE+NORMAL_DATA_SIZE);
		mSphereData = ByteBuffer.allocateDirect(sphereData.length *BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mSphereData.put(sphereData);
		
		//axis
		float[] axisData = models.getCoordinateAxis();
		mAxisCount = axisData.length/POSITION_DATA_SIZE;
		mAxisBuffer = ByteBuffer.allocateDirect(axisData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer(); //generate buffer
		mAxisBuffer.put(axisData); //put the float[] into the buffer and set the position

		
		//Location Matrices.  Keeps in mind where each item is on the map
		mCloudLocation = new float[] 
				{1.0f, 0.0f, 0.0f, 0.0f,
				 0.0f, 1.0f, 0.0f, 0.0f,
				 0.0f, 0.0f, 1.0f, 0.0f,
				 0.0f, 9.0f, 0.0f, 1.0f
		};
		mTreeLocation = new float[] 
				{1.0f, 0.0f, 0.0f, 0.0f,
				 0.0f, 1.0f, 0.0f, 0.0f,
				 0.0f, 0.0f, 1.0f, 0.0f,
				 -5.0f, -5.0f, 0.0f, 1.0f
		};
		
		mLegoLocation = new float[] 
				{1.0f, 0.0f, 0.0f, 0.0f,
				 0.0f, 1.0f, 0.0f, 0.0f,
				 0.0f, 0.0f, 1.0f, 0.0f,
				 -1.5f, -6.0f, 0.0f, 1.0f
				 };
		//top of ground is -7.5
		mGroundLocation = new float[] 
				{1.0f, 0.0f, 0.0f, 0.0f,
				 0.0f, 1.0f, 0.0f, 0.0f,
				 0.0f, 0.0f, 1.0f, 0.0f,
				 0.0f, -27.5f, 0.0f, 1.0f
		};
		

		//initializes the first light location
		lightLocation = new float[] {0.0f,17.0f,4.0f};
		
		//initializes first values.  Starts in the sun and the object is moving
		isNight = false;
		isMoving = true;
		
		//Sun starts off  at 0,9,-7 (negative 7 so we can see it)
		mInitialDay = new float[]
				{1.0f, 0.0f, 0.0f, 0.0f,
				 0.0f, 1.0f, 0.0f, 0.0f,
				 0.0f, 0.0f, 1.0f, 0.0f,
				 0.0f, 9.0f, -7.0f, 1.0f
				};

		Matrix.setIdentityM(mModelMatrix,0);
		Matrix.multiplyMM(mSunLocation, 0, mInitialDay, 0, mModelMatrix, 0); //initialises initial placement.  Helps a lot
		
	}
	

	/**
	 * This method is called when the rendering surface is first created; more initializing stuff goes here.
	 * Initialize OpenGL program components here
	 */
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) 
	{
		//flags to enable depth work
		GLES20.glEnable(GLES20.GL_CULL_FACE); //remove back faces
		GLES20.glEnable(GLES20.GL_DEPTH_TEST); //enable depth testing
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);

		// Set the background clear color
		
		GLES20.glClearColor(0.1f, 0.4f, 0.7f, 1.0f);  //makes a 'sky' blue background

		//This is a good place to compile the shaders from Strings into actual executables. We use a helper method for that
		//int vertexShaderHandle = GLUtilities.compileShader(GLES20.GL_VERTEX_SHADER, perVertexShaderCode); //get pointers to the executables		
		int vertexShaderHandle = GLUtilities.loadShader(GLES20.GL_VERTEX_SHADER, "passVertex.glsl", context);
		int fragmentShaderHandle = GLUtilities.loadShader(GLES20.GL_FRAGMENT_SHADER, "fragment.glsl", context);
		mPerVertexProgramHandle = GLUtilities.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"aPosition","aNormal","aColor","lightPos","ambient"}); //and then we throw them into a program

		int vertexTextureHandle = GLUtilities.loadShader(GLES20.GL_VERTEX_SHADER, "textureVertex.glsl", context);
		int fragmentTextureHandle = GLUtilities.loadShader(GLES20.GL_FRAGMENT_SHADER, 
				"textureFragment.glsl", context);
		mPerVertexTextureHandle = GLUtilities.createAndLinkProgram(vertexTextureHandle, fragmentTextureHandle, 
				new String[] {"TaPosition","TaNormal","TaColor", "aTexCoord"}); 
		
		//Get pointers to the shader's variables (for use elsewhere)
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "uMVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "uMVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aPosition");
		mNormalHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aNormal");
		mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aColor");
		mAmbientHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "ambient");
		mLightHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "lightPos"); //handles the lighting position
		
		//sets up values for textures.  Helps alot
		mMVPMatrixTextureHandle = GLES20.glGetUniformLocation(mPerVertexTextureHandle, "TuMVPMatrix");
		mMVMatrixTextureHandle = GLES20.glGetUniformLocation(mPerVertexTextureHandle, "TuMVMatrix");
		mPositionTextureHandle = GLES20.glGetAttribLocation(mPerVertexTextureHandle, "TaPosition");
		mNormalTextureHandle = GLES20.glGetAttribLocation(mPerVertexTextureHandle, "TaNormal");
		mColorTextureHandle = GLES20.glGetAttribLocation(mPerVertexTextureHandle, "TaColor");
		mTextureBufferHandle = GLUtilities.loadTexture(TEX_FILE, context); //item to load, context
		mTextureBufferHandle2 = GLUtilities.loadTexture(TEX_FILE2, context); //second handle...makes it easier on me
		mTextureHandle = GLES20.glGetUniformLocation(mPerVertexTextureHandle, "uTexture");
		mTextureCoordHandle = GLES20.glGetAttribLocation(mPerVertexTextureHandle, "aTexCoord");
		
		mTextureLightingHandle = GLES20.glGetAttribLocation(mPerVertexTextureHandle, "Tlight");
		
	}

	/**
	 * Called whenever the surface changes (i.e., size due to rotation). Put viewing initialization stuff 
	 * (that depends on the size) here!
	 */
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height); // Set the OpenGL viewport (basically the canvas) to the same size as the surface.

		//Set View Matrix
		Matrix.setLookAtM(mViewMatrix, 0, 
				0.0f, 0.0f, 20.0f, //eye's location
				0.0f, 0.0f, 0.0f, //point we're looking at
				0.0f, 1.0f, 0.0f //direction that is "up" from our head
				);

		//tweak the camera
		Matrix.translateM(mViewMatrix, 0, 0, 0, 3f);
		Matrix.rotateM(mViewMatrix,0, 30, 1f, 0, 0);

		//Set Projection Matrix.
		final float ratio = (float) width / height; //aspect ratio
		//final float left = -ratio;	final float right = ratio;
		//final float bottom = -1; final float top = 1;
		final float near = 1.0f; final float far = 50.0f;
		Matrix.perspectiveM(mProjectionMatrix, 0, 90f, ratio, near, far);
	}

	public static final String TEX_FILE = "grass.png";
	public static final String TEX_FILE2= "mario_brick.png";

	/**
	 * This is like our "onDraw" method; it says what to do each frame
	 */
	@Override
	public void onDrawFrame(GL10 unused) 
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT); //start by clearing the screen for each frame

		GLES20.glUseProgram(mPerVertexProgramHandle); //tell OpenGL to use the shader program we've compiled/are using.  Can enable
													  //different Handles through this method glUseProgram

		if (isNight ==true)
		{
		GLES20.glClearColor(0.1f,0.1f,0.2f,1.0f);
		}
		else
		{
			GLES20.glClearColor(0.1f, 0.4f, 0.7f, 1.0f);
		}
		Matrix.setIdentityM(mModelMatrix, 0);

		if(isNight ==true)
		{
			drawStars();
		}
		
		//draws Sun and keeps track of the location of the light source "Sun"

		if(isMoving == true)
		{
		if(mSunLocation[12] <0 && mSunLocation[13] >= -6) //top left
		{
			Matrix.translateM(mSunLocation, 0, 0.2f, 0.2f, 0.0f);
			isNight=false;
		}
		else if(mSunLocation[12] >= 0 && mSunLocation[13] >= -6) //top right
		{
			Matrix.translateM(mSunLocation, 0, 0.2f, -0.2f, 0.0f);
			isNight = false;
		}
		else if(mSunLocation[12] >= 0 && mSunLocation[13] <-6) //bottom right
		{
			Matrix.translateM(mSunLocation, 0, -0.2f, -0.2f, 0.0f);
			isNight = true;
		}
		else if(mSunLocation[12] < 0 && mSunLocation[13] < -6)//bottom left
		{
			Matrix.translateM(mSunLocation, 0, -0.2f, 0.2f, 0.0f);
			isNight = true;
		}
		}
		//after a while the sun disappears from the distance
		lightLocation = new float[]{mSunLocation[12],mSunLocation[13],-mSunLocation[14]}; //stores the new light location
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mSunLocation, mColorYellow,mSunAmbient);//draw the sun that is moving

		//Uses the helper methods to draw objects on the screen
		drawTree();
		drawCloud();
		drawLego();
		
		drawGround();//texturized ground
		drawMarioBrick();//floating brick
		
		//drawAxis(); //so we have guides on coordinate axes, for debugging
		
	}				

	
	/*
	 * Helper to draw the Legoman
	 */
	private void drawLego()
	{
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mLegoLocation, 0, mModelMatrix, 0);
		Matrix.rotateM(mTempMatrix, 0, -90, 1.0f, 0.0f, 0.0f);
		Matrix.rotateM(mTempMatrix,0,180,0.0f,0.0f,1.0f);
		drawIndexedTriangleBuffer(legoman.getVertexDataBuffer(), legoman.getNormalDataBuffer(),  //draw as an indexed array
				legoman.getIndexDataBuffer(), legoman.getIndexDataBufferLength(), 
				mTempMatrix, mColorGrey, mLegoAmbient);
	}
	
	/*
	 * Helper to draw the ground
	 */
	private void drawGround()
	{
		
		mCubeTextureBuffer.position(0); //reset buffer start to 0 (where data starts)
		GLES20.glUseProgram(mPerVertexTextureHandle);
		//pass in textures to OpenGL
		
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //specify which texture we're going to be using (basically select what will be the "current" texture)
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBufferHandle); //bind the texture to the "current" texture
	    GLES20.glUniform1i(mTextureHandle, 0); //pass the texture into the shader
	    
	    GLES20.glVertexAttribPointer(mTextureCoordHandle, TEXTURE_DATA_SIZE, GLES20.GL_FLOAT, false, 0, mCubeTextureBuffer);
		GLES20.glEnableVertexAttribArray(mTextureCoordHandle); //doing this explicitly/separately
		
		
	    Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mGroundLocation, 0, mModelMatrix, 0);
		Matrix.scaleM(mTempMatrix, 0, 40.0f, 20.0f, 21.0f);
		drawPackedTextureTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorGreen,false);
		
		GLES20.glDisableVertexAttribArray(mTextureCoordHandle);//THIS IS NEEDED APPARENTLY.  Colors were off beforehand
	}
	
	/**
	 * Draws a floating brick that is textured
	 */
	private void drawMarioBrick()
	{
		mCubeTextureBuffer.position(0); //reset buffer start to 0 (where data starts)
		GLES20.glUseProgram(mPerVertexTextureHandle);
		//pass in textures to OpenGL
		
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //specify which texture we're going to be using (basically select what will be the "current" texture)
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBufferHandle2); //bind the texture to the "current" texture
	    GLES20.glUniform1i(mTextureHandle, 0); //pass the texture into the shader
	    
	    GLES20.glVertexAttribPointer(mTextureCoordHandle, TEXTURE_DATA_SIZE, GLES20.GL_FLOAT, false, 0, mCubeTextureBuffer);
		GLES20.glEnableVertexAttribArray(mTextureCoordHandle); //doing this explicitly/separately
		
		
	    Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mGroundLocation, 0, mModelMatrix, 0);
		Matrix.translateM(mTempMatrix, 0, 5.0f, 30.0f, -5.0f);
		drawPackedTextureTriangleBuffer(mCubeData, mCubeVertexCount, mTempMatrix, mColorBrown,false);
		
		GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
	}
	/*
	 * Helper to draw a cloud
	 */
	private void drawCloud()
	{
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mCloudLocation, 0, mModelMatrix, 0);
		Matrix.scaleM(mTempMatrix, 0, 0.5f, 0.5f, 0.5f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorWhite,mCloudAmbient);
		Matrix.translateM(mTempMatrix, 0, 1.0f, 0.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorWhite,mCloudAmbient);
		Matrix.translateM(mTempMatrix, 0, -2.0f, 0.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorWhite,mCloudAmbient);
		Matrix.translateM(mTempMatrix, 0, 0.5f, 1.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorWhite,mCloudAmbient);
		Matrix.translateM(mTempMatrix, 0,1.0f, 0.0f, 0.0f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mTempMatrix, mColorWhite,mCloudAmbient);
	}
	
	/*
	 * Helper for drawing the tree
	 */
	private void drawTree()
	{
		//values of normals of the tree are messed up...to lazy to fix it though
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mTempMatrix,0, mTreeLocation,0,mModelMatrix,0);
		Matrix.scaleM(mTempMatrix, 0, 1.5f, 1.5f, 1.5f);
		drawIndexedTriangleBuffer(treeTop.getVertexDataBuffer(), treeTop.getNormalDataBuffer(),  //draw as an indexed array
				treeTop.getIndexDataBuffer(), treeTop.getIndexDataBufferLength(), 
				mTempMatrix, mColorGreen,mTreeAmbient);
		Matrix.translateM(mTempMatrix, 0, 0.0f,-1.0f, 0.0f);
		drawIndexedTriangleBuffer(treeTrunk.getVertexDataBuffer(), treeTrunk.getNormalDataBuffer(),  //draw as an indexed array
				treeTrunk.getIndexDataBuffer(), treeTrunk.getIndexDataBufferLength(), 
				mTempMatrix, mColorRed,mTreeAmbient);
	}
	
	/**
	 * Draws circles to represent stars.  Has same ambience as the sun
	 */
	private void drawStars()
	{
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 8.0f, 8.0f, 0.0f);
		Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mModelMatrix, mColorWhite,mSunAmbient);
		Matrix.scaleM(mModelMatrix, 0, 5.0f,5.0f, 5.0f);
		Matrix.translateM(mModelMatrix,0,-10.0f,0.0f,0.0f);
		Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mModelMatrix, mColorWhite,mSunAmbient);
		Matrix.scaleM(mModelMatrix, 0, 5.0f,5.0f, 5.0f);
		Matrix.translateM(mModelMatrix, 0, 3.0f, -2.0f, 0.0f);
		Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mModelMatrix, mColorWhite,mSunAmbient);
		Matrix.scaleM(mModelMatrix, 0, 5.0f,5.0f, 5.0f);
		Matrix.translateM(mModelMatrix, 0, 6.0f, 0.0f, 0.0f);
		Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
		drawPackedTriangleBuffer(mSphereData, mSphereVertexCount, mModelMatrix, mColorWhite,mSunAmbient);
		
	}
	
	/**
	 * Draws using float buffer
	 */
	private void drawIndexedTriangleBuffer(FloatBuffer vertexBuffer, FloatBuffer normalBuffer, ShortBuffer indexBuffer, int indexCount,
			float[] modelMatrix, float[] color, float[] ambient)
	{
		//Calculate MV and MVPMatrix. Note written as MVP, but really P*V*M
		Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, modelMatrix, 0);  //"M * V"
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0); //"MV * P"

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0); //put combined matrixes in the shader variables
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		vertexBuffer.position(0); //reset buffer start to 0 (where data starts)
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		normalBuffer.position(0); //reset buffer start to 0 (where data starts)
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 0, normalBuffer);
		GLES20.glEnableVertexAttribArray(mNormalHandle);

		//color data
		GLES20.glVertexAttrib4fv(mColorHandle, color, 0);
		
		//location of light
		GLES20.glVertexAttrib3fv(mLightHandle, lightLocation, 0);
		
		//ambient value
		GLES20.glVertexAttrib4fv(mAmbientHandle, ambient,0);
		
		//setup the index buffer
		indexBuffer.position(0);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer); //draw by indices
		GLUtilities.checkGlError("glDrawElements");
		
		
	}

	/**
	 * Draws a triangle buffer with the given modelMatrix and single color. 
	 * Note the view matrix is defined per program.
	 */			
	private void drawPackedTriangleBuffer(FloatBuffer buffer, int vertexCount, float[] modelMatrix, float[] color, float[] ambient)
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
		
		//ambient value
		GLES20.glVertexAttrib4fv(mAmbientHandle, ambient,0);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount); //draw the vertices
	}		
	/**
	 * This one accounts for Texturing.  Non-cube objects use "True"
	 *
	 */			
	private void drawPackedTextureTriangleBuffer(FloatBuffer buffer, int vertexCount, float[] modelMatrix, float[] color, boolean hasTexture)
	{		
		//Calculate MV and MVPMatrix. Note written as MVP, but really P*V*M
		Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, modelMatrix, 0);  //"M * V"
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0); //"MV * P"

		GLES20.glUniformMatrix4fv(mMVMatrixTextureHandle, 1, false, mMVMatrix, 0); //put combined matrixes in the shader variables
		GLES20.glUniformMatrix4fv(mMVPMatrixTextureHandle, 1, false, mMVPMatrix, 0);


		int stride = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE) * BYTES_PER_FLOAT; //how big of steps we take through the buffer
		if(hasTexture)
			stride += TEXTURE_DATA_SIZE * BYTES_PER_FLOAT;
		
		buffer.position(0); //reset buffer start to 0 (where the position data starts)
		GLES20.glVertexAttribPointer(mPositionTextureHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, stride, buffer); //note the stride lets us step over the normal data!
		GLES20.glEnableVertexAttribArray(mPositionTextureHandle);

		buffer.position(POSITION_DATA_SIZE); //shift pointer to where the normal data starts
		GLES20.glVertexAttribPointer(mNormalTextureHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, stride, buffer); //note the stride lets us step over the position data!
		GLES20.glEnableVertexAttribArray(mNormalTextureHandle);

		if(hasTexture)
		{
			buffer.position(POSITION_DATA_SIZE+NORMAL_DATA_SIZE);
			GLES20.glVertexAttribPointer(mTextureCoordHandle, TEXTURE_DATA_SIZE, GLES20.GL_FLOAT, false, stride, buffer);
			GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
		}
		
		//put color data in the shader variable
		GLES20.glVertexAttrib4fv(mColorTextureHandle, color, 0);

		
		//location of the sun
		GLES20.glVertexAttrib3fv(mTextureLightingHandle, lightLocation, 0); 
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount); //draw the vertices
		
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
		//GLES20.glVertexAttrib3fv(mNormalHandle, lightLocation, 0); //pass particular normal (so points are bright)
		GLES20.glVertexAttrib3fv(mNormalHandle, axislightNormal, 0); //pass particular normal (so points are bright)
		
		//GLES20.glDisableVertexAttribArray(mColorHandle); //just in case it was enabled earlier
		GLES20.glVertexAttrib4fv(mColorHandle, mColorGrey, 0); //put color in the shader variable

		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mAxisCount); //draw the axis (as points!)
	}

	public static void printMatrix(float[] array)
	{
		Log.i("MATRIX","[ " + array[0] + ", "+array[4] + ", "+array[8] + ", "+array[12]);
		Log.i("MATRIX","  " + array[1] + ", "+array[5] + ", "+array[9] + ", "+array[13]);
		Log.i("MATRIX","  " + array[2] + ", "+array[6] + ", "+array[10] + ", "+array[14]);
		Log.i("MATRIX","  " + array[3] + ", "+array[7] + ", "+array[11] + ", "+array[15]+ " ]");
	}	
	
	/*
	 * Creates a new starting place for the sun.  Only used when calling the buttons
	 */
	public void setSunLocation (float[] array)
	{
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.multiplyMM(mSunLocation, 0, array, 0, mModelMatrix, 0);
	}

	
	//For the Helper Methods
	public void doMove()
	{
		isMoving = true;
	}
	
	public void dontMove()
	{
		isMoving = false;
	}
	public void itsNight()
	{
		isNight = true;
	}
	public void itsMorning()
	{
		isNight = false;
	}
}
