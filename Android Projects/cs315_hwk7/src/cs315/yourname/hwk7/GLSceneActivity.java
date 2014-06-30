package cs315.yourname.hwk7;

import cs315.yourname.hwk5.R;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
	
	public int time = 0;
	private int clicks;
	/**
	 * Called when the activity is started
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		clicks = 0;

		setContentView(R.layout.activity_main);
		_GLView = (GLSurfaceView)this.findViewById(R.id.gl_view);
		
		
		//http://writecodeeasy.blogspot.com/2012/08/androidtutorial-timer-p1.html
		//Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						TextView tv = (TextView) findViewById(R.id.main_timer_text);
						String seconds = String.valueOf(time%60);
						if(time%60 < 10)
						{
							seconds = "0"+seconds;
						}
						tv.setText(String.valueOf(time/60) + ":"+seconds);
						time += 1;
					}
					
				});
			}
        	
        }, 0, 1000);
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

	
	public void moveForward(View view)
	{
		((MyGLSurfaceView) _GLView).moveForward();
		clicks++;
		if(((MyGLSurfaceView) _GLView).hasEnded)
		{
			String times = null;
			String seconds = String.valueOf(time%60);
			if(time%60 < 10)
			{
				seconds = "0"+seconds;
			}
			times = (time/60) + ":"+seconds;
			Toast toast = Toast.makeText(getApplicationContext(), "Time taken to complete: "+ times + ".  Number of clicks of Move Forward: " +clicks + "     Hit Restart to Try Again", Toast.LENGTH_SHORT);
			toast.show();
			clicks = 0;
		}
		
	}
	
	public void lookLeft(View view)
	{
		((MyGLSurfaceView) _GLView).lookLeft();
	}
	
	public void lookRight(View view)
	{
		((MyGLSurfaceView) _GLView).lookRight();
	}
	
	public void reset(View view)
	{
		((MyGLSurfaceView) _GLView).reset();
		time = 0;
		
	}
	
	public void viewOverhead(View view)
	{
		((MyGLSurfaceView) _GLView).viewOverhead();
	}
	/**
	 * The actual view itself, includes as an inner class. Note that this also controls interaction (but not rendering)
	 * We put the OpenGL rendering in a separate class
	 */
	public static class MyGLSurfaceView extends GLSurfaceView
	{
		private Renderer renderer;
		private boolean hasEnded;
		
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
			hasEnded = false;
		}
		//Helpers for Button Presses
		
		public Renderer render()
		{
			return renderer;
		}
		public void moveForward()
		{
			((SceneRenderer) renderer).moveForward();
			
			if(((SceneRenderer) renderer).ended())
			{
				hasEnded = true;
			}
		}
		
		public void lookLeft()
		{
			((SceneRenderer) renderer).lookLeft();
		}
		
		public void lookRight()
		{
			((SceneRenderer) renderer).lookRight();
		}
		
		public void reset()
		{
			((SceneRenderer) renderer).reset();
			hasEnded = false;
			((SceneRenderer) renderer).start();
		}
		
		public void viewOverhead()
		{
			((SceneRenderer) renderer).viewOverhead();
		}
		
		public boolean ended()
		{
			return hasEnded;
		}
		
		public void start()
		{
			hasEnded = false;
		}
	}
}
