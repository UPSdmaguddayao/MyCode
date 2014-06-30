package cs315.yourname.hwk3;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A basic activity for displaying a simple OpenGL rendering. This uses a slightly different structure than
 * with a regular Canvas.
 * 
 * @author Joel with extra parts by DJ Maguddayao
 * @version Fall 2013
 */
public class GLBasicActivity extends Activity
{
	private static final String TAG = "GLBasic"; //for logging/debugging

	private GLSurfaceView _GLView; //the view that we're actually drawing

	/**
	 * Called when the activity is started
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_GLView = new GLBasicView(this); //we set the GLView programmatically rather than with the XML
		setContentView(_GLView);

		//we can build layout systems and add them in here

	}

	protected void onPause() {
		super.onPause();
		_GLView.onPause(); //tell the view to pause
	}

	protected void onResume() {
		super.onResume();
		_GLView.onResume(); //tell the view to resume
	}


	/**
	 * The actual view itself, includes as an inner class. Note that this also controls interaction (but not rendering)
	 * We put the OpenGL rendering in a separate class
	 */
	public class GLBasicView extends GLSurfaceView
	{
		private static final float TOUCH_SCALE_FACTOR = 0.01f; //factor for shortening movement.  Makes it easier to see than a blinding light
		private HexagonRenderer _hexRenderer;
		private float initialX=0.0f;//starts at (0,0) (middle of screen)
		private float initialY=0.0f;
		public GLBasicView(Context context) {
			super(context);

			setEGLContextClientVersion(2); //specify OpenGL ES 2.0
			super.setEGLConfigChooser(8, 8, 8, 8, 16, 0); //may be needed for some targets; specifies 24bit color

			_hexRenderer = new HexagonRenderer(context);
			setRenderer(_hexRenderer); //set the renderer

			/* 
			 * Render the view only when there is a change in the drawing data.
			 * We comment this out when we don't have UI (just animation)
			 */
			setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}
		
		@Override
		/**
		 * Only touch event is movement of the screen (hold down and dragging finger along)
		 */
		public boolean onTouchEvent(MotionEvent e)
		{
		    // MotionEvent reports input details from the touch screen
		    // and other input controls. In this case, you are only

		    float x = e.getX();
		    float y = e.getY();

		    switch (e.getAction()) {
		        case MotionEvent.ACTION_MOVE:

		            float dx = (x-initialX);
		            float dy = y-initialY ;
		            
		            _hexRenderer.screenMoveX +=dx * TOUCH_SCALE_FACTOR; //makes it so that the object doesn't move too fast
		            _hexRenderer.screenMoveY +=dy * TOUCH_SCALE_FACTOR;
		            requestRender(); //re-renders the object
		    }

		    initialX = x; //keeps tracks of where the X/Y is moving
		    initialY = y;
		    return true;

		}
	}
}
