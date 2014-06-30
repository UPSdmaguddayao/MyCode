package cs315.yourname.hwk4;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;

/**
 * The actual view itself, includes as an inner class. Note that this also controls interaction (but not rendering)
 * We put the OpenGL rendering in a separate class
 */
public class GLBasicView extends GLSurfaceView
{
	private Renderer renderer;
	
	public GLBasicView(Context context) {
		super(context);

		setEGLContextClientVersion(2); //specify OpenGL ES 2.0
		super.setEGLConfigChooser(8, 8, 8, 8, 16, 0); //may be needed for some targets; specifies 24bit color

		renderer = new RobotRenderer(context);
		setRenderer(renderer); //set the renderer

		/* 
		 * Render the view only when there is a change in the drawing data.
		 * We comment this out when we don't have UI (just animation)
		 */
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//WHEN_DIRTY);
	}

	public GLBasicView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		setEGLContextClientVersion(2); //specify OpenGL ES 2.0
		super.setEGLConfigChooser(8, 8, 8, 8, 16, 0); //may be needed for some targets; specifies 24bit color

		renderer = new RobotRenderer(context);
		setRenderer(renderer); //set the renderer

		/* 
		 * Render the view only when there is a change in the drawing data.
		 * We comment this out when we don't have UI (just animation)
		 */
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//WHEN_DIRTY);
	}
	
	//starts dancing
	public void robotStartDance()
	{
		((RobotRenderer) renderer).setValue(0.0f,0.0f,0.0f,0.0f,true,true,true,true);

	}
	
	//stops dancing
	public void robotStopDance()
	{
		((RobotRenderer) renderer).setValue(0.0f,0.0f,0.0f,0.0f,false,false,false,false);
	}
}
