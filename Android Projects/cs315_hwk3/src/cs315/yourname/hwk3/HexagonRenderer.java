package cs315.yourname.hwk3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * This class represents a custom OpenGL renderer--all of our "drawing" logic goes here.
 * 
 * @author DJ Maguddayao
 * @version Fall 2013
 */
public class HexagonRenderer implements GLSurfaceView.Renderer 
{
	private static final String TAG = "HexagonRenderer"; //for logging/debugging

	//For screen movement
	public volatile float screenMoveX; //keeps track of how far the screen moved when the 
	public volatile float screenMoveY; //on touch event occurs
	
	//Matrix storage
	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16]; //combined matrix!
	
	//Buffer for model data
	private final FloatBuffer mTriangleVertices;
	private final FloatBuffer mColoredVertices; //float for coloring
	//private final float[] mColor;
	
	//some constants we'll use
	private final int mPositionDataSize = 3;	
	private final int mTrianglePositionLength;
	private final int mColorsLength; //same purpose as mTrianglePositionLength
	
	/**
	 * OpenGL Handles
	 * These are C-style "pointers" (int representing a memory address) to particular blocks of data.
	 * We pass the pointers around instead of the data to increase efficiency (and because OpenGL is
	 * C-based, and that's how they do things).
	 */
	private int mMVPMatrixHandle; //the combined MVPMatrix
	private int mPositionHandle; //the position of a vertex
	private int mColorHandle; //the color to paint the model
	private int mPerVertexProgramHandle; //our "program" (OpenGL state) for drawing a triangle

	//define the source code for the vertex shader
	private final String vertexShaderCode = 
			"uniform mat4 uMVPMatrix;" +	// A constant representing the combined modelview/projection matrix. We use this for positioning
			"attribute vec4 aPosition;" +	// Per-vertex position information we will pass in
			"attribute vec4 aColor;" +		// Per-vertex color information we will pass in.
			"varying vec4 vColor;"  + 		//out : the ultimate color of the vertex
			"void main() {" +
			"  vColor = aColor;"+			//just set the input color to be the output; can get more complex if we want
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
	public HexagonRenderer(Context context)
	{	
		/**
		 * Initialize our model data. Pretty simple
		 */
		//vertices for a 2D equilateral triangle, counterclockwise order
		float[] triangleVertexData = { 
				0.0f, 0.577350269f, 0.0f, //top
				-0.5f, -0.288675135f, 0.0f, //bottom right
				0.5f, -0.288675135f, 0.0f, //bottom left
		};
		
		float[] coloredVertexData = {
				0.6f, 0.1f, 0.1f,  //red
				0.1f, 0.6f, 0.1f,  //green
				0.1f, 0.1f, 0.6f,  //blueish?
				

				0.6f, 0.1f, 0.1f,  //red
				0.1f, 0.1f, 0.6f,  //blueish?
				0.1f, 0.6f, 0.1f, //green
		}; //three colors for changing between the vertices.
		//Two sets for having two "types" of triangles
		//unlike mColor: the float needs 3 floats for RGB.  No transparency needed
		
		mTrianglePositionLength = triangleVertexData.length;
		mColorsLength = coloredVertexData.length; //sets up Color buffer
		
		//Initialize the buffers; uses built-in Android methods.
		mTriangleVertices = 
				ByteBuffer.allocateDirect(mTrianglePositionLength * 4) //allocate memory, 4 bytes per float
				.order(ByteOrder.nativeOrder()) //specify byte order native to hardware (big vs. little endian)
				.asFloatBuffer(); //and convert to a FloatBuffer object
		mTriangleVertices.put(triangleVertexData); //put the float[] into the buffer
		mTriangleVertices.position(0); //move the buffer's "start" pointer to the beginning
		
		//Buffer for the Color Changes
		
		mColoredVertices =
				ByteBuffer.allocateDirect(mColorsLength*4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		mColoredVertices.put(coloredVertexData);
		mColoredVertices.position(0);
		
		//set up the color; nice red!
		//mColor = new float[] {0.6f, 0.1f, 0.1f, 1.0f};
		
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
		// Set the background clear color
		GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f); //Currently a dark grey so we can make sure things are working

		//This is a good place to compile the shaders from Strings into actual executables. We use a helper method for that
		int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode); //get pointers to the executables		
		int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle); //and then we throw them into a program
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
		
		//Set View Matrix
		Matrix.setLookAtM(mViewMatrix, 0, 
				0.0f, 0.0f, 3.0f, //eye's location
				0.0f, 0.0f, -1.0f, //direction we're looking at
				0.0f, 1.0f, 0.0f //direction that is "up" from our head
				); //this gets compiled into the proper matrix automatically
		
		//Set Projection Matrix. We will talk about this more in the future
		final float ratio = (float) width / height; //aspect ratio
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
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
		Matrix.setLookAtM(mViewMatrix, 0, 
				-screenMoveX, screenMoveY, 3.0f,
				-screenMoveX, screenMoveY, -1.0f, 
				0.0f, 1.0f, 0.0f 
				); 
		//For when the "On Touch Event" occurs, begins to move the object/screen around.
		//View changes due to the render request from GLBasicActivity
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //start by clearing the screen for each frame

		GLES20.glUseProgram(mPerVertexProgramHandle); //tell OpenGL to use the shader program we've compiled
		//Get pointers to the program's variables. Instance variables so we can break apart code
		
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "uMVPMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aPosition");
		mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "aColor");
		//Prepare the model matrix!
		
		Matrix.setIdentityM(mModelMatrix, 0); //start modelMatrix as identity (no transformations)  Used as a reset each time
		
		//Matrix.translateM(m, mOffset, x, y, z) Used to translate
		//Matrix.scaleM(m, mOffset, x, y, z) Scaling
		//Matrix.rotateM(m, mOffset, a, x, y, z) Rotating
		
		
		//Triangles only need two types.  Any combination can be made with two triangles different
		//and rotating any one of them  This pattern used alternates between the two triangles
		
		//bottom middle triangle
		drawTriangle1(mModelMatrix); //draw the triangle with the given model matrix
		
		Matrix.setIdentityM(mModelMatrix, 0);
		
		//bottom right triangle
		Matrix.rotateM(mModelMatrix,0,60,0.0f,0.0f,1.0f);
		Matrix.translateM(mModelMatrix,0,0.5f,-0.28867135f,0.0f);
		drawTriangle2(mModelMatrix);
		
		Matrix.setIdentityM(mModelMatrix, 0);
		
		//top right triangle
		Matrix.translateM(mModelMatrix, 0, 0.5f, 0.866021619f, 0.0f);
		Matrix.rotateM(mModelMatrix, 0, 120, 0.0f, 0.0f, 1.0f);
		drawTriangle1(mModelMatrix);
		
		Matrix.setIdentityM(mModelMatrix,0);
		
		//top middle triangle
		Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 0.0f, 1.0f);
		Matrix.translateM(mModelMatrix, 0, 0.0f, -1.154700538f, 0.0f);
		drawTriangle2(mModelMatrix);
		
		Matrix.setIdentityM(mModelMatrix,0);
		
		//top left triangle
		Matrix.translateM(mModelMatrix, 0, -0.5f, 0.866021619f,0.0f);
		Matrix.rotateM(mModelMatrix,0,240,0.0f,0.0f,1.0f);
		drawTriangle1(mModelMatrix);
		
		Matrix.setIdentityM(mModelMatrix,0);
		
		//bottom left triangle
		Matrix.rotateM(mModelMatrix, 0, 300, 0.0f, 0.0f, 1.0f);
		Matrix.translateM(mModelMatrix,0,-0.5f,-0.28867135f,0.0f);
		drawTriangle2(mModelMatrix);
		
		Matrix.setIdentityM(mModelMatrix, 0); //returns matrix to normal just in case
	}				

	/**
	 * Draws a triangle with the given modelMatrix (the view matrix is defined as an instance variable)
	 * This one (Triangle1) draws the top color as red, left color as green, and right color as blue
	 */			
	private void drawTriangle1(float[] modelMatrix)
	{		
		//Calculate MVPMatrix. Note this is MV*P, not M*V*P
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, modelMatrix, 0);  //M * V 
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0); //MV * P

		// Pass in the position information
		mTriangleVertices.position(0); //reset buffer start to 0 (just in case)
		
		mColoredVertices.position(0); //reads colorBuffer from the first three colors
		
		//put position data in the shader variable; really setting up a pointer to an array of data
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mTriangleVertices);//where to find attribute at 
		GLES20.glEnableVertexAttribArray(mPositionHandle); //can make an array of colors to change colors
		//put color data in the shader variable
		
		//GLES20.glVertexAttrib4fv(mColorHandle, mColor, 0);
		GLES20.glVertexAttribPointer(mColorHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mColoredVertices);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		//GLES20.glVertexAttrib4fv(mColorHandle, mColoredVertices);
		//put combined matrix in the shader variable
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		//This the OpenGL command to draw the vertices (as triangles; that is, every 3 vertices)
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mTrianglePositionLength);
	}	

	/**
	 * Same as the previous triangle, but top is red, left is blue, and right is green
	 */
	private void drawTriangle2(float[] modelMatrix)
	{
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

		mTriangleVertices.position(0);
		
		mColoredVertices.position(9); //start the colored buffer for the last three colors
		
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mTriangleVertices); 
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mColorHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mColoredVertices);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mTrianglePositionLength);
	}	

	/** 
	 * Helper function to compile a shader. Reports syntax errors in shader language
	 * 
	 * @param shaderType The shader type.
	 * @param shaderSource The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	private int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) 
		{
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) 
			{
				Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}

		return shaderHandle;
	}	

	/**
	 * Helper function to compile and link a program.
	 * 
	 * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle) 
	{
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);

			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) 
			{				
				Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}

		return programHandle;
	}

	/**
	 * Helper method for debugging OpenGL
	 * From Google
	 */
	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
		
	}

}