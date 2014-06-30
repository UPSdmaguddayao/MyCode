package cs315.yourname.hwk6;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/**
 * A basic starter activity for drawing in Android.
 * 
 * @author Joel
 * @version Fall 2013
 */
public class RayTracerActivity extends Activity
{
	private static final String TAG = "RayTracerActivity"; //for logging/debugging
	
	private RayTraceSurfaceView drawView; //the view we'll be drawing on (for later reference, if needed
	
	/**
	 * Called when the activity is started
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); //use the activity_main layout
		
		drawView = (RayTraceSurfaceView)this.findViewById(R.id.draw_view); //get access to drawView for later
	}

	/**
	 * Auto-generated; provides menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

		
	public static class RayTraceSurfaceView extends SurfaceView implements SurfaceHolder.Callback
	{
		private static final String TAG = "RayTraceSurfaceView";

		private SurfaceHolder holder;
		private RayTracingThread thread;
		
		private RayTracer tracer;
		
		/**
		 * We need to override all the constructors, since different ones are called by the XML
		 */
		public RayTraceSurfaceView(Context context) {
			this(context, null);
		}

		public RayTraceSurfaceView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public RayTraceSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
			super(context, attrs, defaultStyle);

			holder = this.getHolder(); //handles control of the surface
			holder.addCallback(this);
			thread = new RayTracingThread(holder, this);

			tracer = new RayTracer(512,512); //create the ray tracer, with the target size
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) { //initialization stuff
			thread.setRunning(true);
			thread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) { //cleanup
			//Tell the thread to shut down, but wait for it to stop!
			thread.setRunning(false);
			boolean retry = true;
			while(retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					Log.d(TAG, "Ray tracing thread trying to shut down...");
				}
			}
			Log.d(TAG, "Ray tracing thread shut down.");
		}

		//called when the surface changes (like size changes due to rotate). Will need to respond accordingly.
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			tracer.resize(width, height, false);
		}
		
		
		/**
		 * An inner class representing a separate thread that does the rendering. 
		 */
		public class RayTracingThread extends Thread 
		{
			private boolean isRunning; //whether we're running or not (so we can "stop" the thread)
			private SurfaceHolder holder; //the holder we're going to post updates to
			private RayTraceSurfaceView view; //the view that has drawing details

			public RayTracingThread(SurfaceHolder holder, RayTraceSurfaceView view)
			{
				super();
				this.holder = holder;
				this.view = view;
				this.isRunning = false;
			}

			/**
			 * Executed when we call thread.start()
			 */
			@Override
			public void run()
			{
				Canvas canvas;
				while(isRunning)
				{
					canvas = null;
					try {
						canvas = this.holder.lockCanvas();
						synchronized (this.holder) {
							tracer.render(); //render the next frame
							canvas.drawBitmap(tracer.getImage(),0,0,null); //and then draw the BitMap onto the canvas.
							//view.render(canvas);
						}
					} finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
						if (canvas != null) {
							this.holder.unlockCanvasAndPost(canvas);
						}
					}
				}
			}
			
			//toggle whether the thread is running
			public void setRunning(boolean isRunning){
				this.isRunning = isRunning;
			}
		}

	}
	
	
//	//The method that is called when the button is pressed [see the layout xml]
	public void buttonPress(View view) 
	{
		Log.d(TAG, "Button was pressed"); //add a line to the console (debug priority). This is like Sysout.
		
		//toasts are short popup messages; useful for testing!
		Toast toast = Toast.makeText(getApplicationContext(), "Button Pressed!", Toast.LENGTH_SHORT);
		toast.show();
		
		//drawView.invalidate(); //tells our simple view to redraw itself. This is like "repaint()"
	}

	
}
