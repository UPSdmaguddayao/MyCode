package cs315.yourname.hwk5;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

/**
 * A basic activity for displaying a simple OpenGL rendering. This uses a slightly different structure than
 * with a regular Canvas.
 * 
 * @author Joel
 * @version Fall 2013
 */
public class GLSceneActivity extends Activity
{
	private static final String TAG = "GLScene"; //for logging/debugging

	private GLSurfaceView _GLView; //the view that we're actually drawing

	/**
	 * Called when the activity is started
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		_GLView = (GLSurfaceView)this.findViewById(R.id.gl_view);
	}

	protected void onPause() {
		super.onPause();
		_GLView.onPause(); //tell the view to pause
	}

	protected void onResume() {
		super.onResume();
		_GLView.onResume(); //tell the view to resume
	}

	//Button Presses
	public void dayStart(View view)
	{
		((MyGLSurfaceView) _GLView).dayStart();
	}
	
	public void nightStart(View view)
	{
		((MyGLSurfaceView) _GLView).nightStart();
	}
	
	public void sunMove(View view)
	{
		((MyGLSurfaceView) _GLView).sunMove();
	}
	
	public void sunStop(View view)
	{
		((MyGLSurfaceView) _GLView).sunStop();
	}

	/**
	 * The actual view itself, includes as an inner class. Note that this also controls interaction (but not rendering)
	 * We put the OpenGL rendering in a separate class
	 */
	public static class MyGLSurfaceView extends GLSurfaceView
	{
		private Renderer renderer;
		
		public MyGLSurfaceView(Context context) {
			this(context,null);
		}
		
		public MyGLSurfaceView(Context context, AttributeSet attrs)
		{
			super(context, attrs);

			setEGLContextClientVersion(2); //specify OpenGL ES 2.0
			super.setEGLConfigChooser(8, 8, 8, 8, 16, 0); //may be needed for some targets; specifies 24bit color

			renderer = new SceneRenderer(context);
			setRenderer(renderer); //set the renderer

			//render continuously (like for animation). Set to WHEN_DIRTY to manually control redraws (via GLSurfaceView.requestRender())
			setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//WHEN_DIRTY);
		}
		//Helpers for Button Presses
		public void dayStart()
		{
			((SceneRenderer) renderer).setSunLocation(new float[]
					{1.0f, 0.0f, 0.0f, 0.0f,
					 0.0f, 1.0f, 0.0f, 0.0f,
					 0.0f, 0.0f, 1.0f, 0.0f,
					 0.0f, 9.0f, -7.0f, 1.0f
					});
			((SceneRenderer) renderer).itsMorning();
			sunStop();
		}
		public void nightStart()
		{
			((SceneRenderer) renderer).setSunLocation(new float[]
					{1.0f, 0.0f, 0.0f, 0.0f,
					 0.0f, 1.0f, 0.0f, 0.0f,
					 0.0f, 0.0f, 1.0f, 0.0f,
					 0.0f, -23.0f, -7.0f, 1.0f
					});
			((SceneRenderer) renderer).itsNight();
			sunStop();
		}
		
		public void sunMove()
		{
			((SceneRenderer) renderer).doMove();
		}
		
		public void sunStop()
		{
			((SceneRenderer) renderer).dontMove();
		}
	}
}
