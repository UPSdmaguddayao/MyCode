package cs315.dmaguddayao.hwk1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A starter class for doing basic drawing on Android.
 * This class provides a dedicated thread for drawing, supporting higher performance drawing
 * (see http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview)
 * 
 * @author Joel
 * @version Fall 2013
 */
public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
	private static final String TAG = "DrawingSurfaceView";

	private SurfaceHolder _holder;
	private DrawingThread _thread;
	
	private int _viewWidth;
	private int _viewHeight;
	private Paint _redPaint;
	private Bitmap _bmp;


	/**
	 * We need to override all the constructors, since we don't know which will be called
	 * All the constructors eventually call init()
	 */
	public DrawingSurfaceView(Context context) {
		this(context, null);
	}

	public DrawingSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawingSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		init(context);
	}

	public void init(Context context)
	{
		_holder = this.getHolder(); //handles control of the surface
		_holder.addCallback(this);
		_thread = new DrawingThread(_holder, this);
		
		_redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_redPaint.setColor(Color.RED);
		_redPaint.setStyle(Style.FILL);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) { //initialization stuff
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) { //cleanup
		//Tell the thread to shut down, but wait for it to stop!
		_thread.setRunning(false);
		boolean retry = true;
		while(retry) {
			try {
				_thread.join();
				retry = false;
			} catch (InterruptedException e) {
				//will try again...
			}
		}
		Log.d(TAG, "Drawing thread shut down.");
	}

	//called when the surface changes (like sizes changes due to rotate). Will need to respond accordingly.
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		//store new size of the view
		_viewWidth = width;
		_viewHeight = height;
		
		//create a properly-sized bitmap to draw on
		_bmp = Bitmap.createBitmap(_viewWidth, _viewHeight, Bitmap.Config.ARGB_8888);
	}

	/**
	 * This method does our actual drawing onto a canvas.
	 * This is our "render" function when using something like a game loop	 
	 * * @param canvas The canvas to draw onto.
	 */
	public void render(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK); //black out the background
		canvas.drawCircle(_viewWidth/2.0f, _viewHeight/2.0f, 100.0f, _redPaint); //we can draw directly onto the canvas
		//see http://developer.android.com/reference/android/graphics/Canvas.html for a list of options

		for(int i=20; i<_viewWidth-20; i++)
			_bmp.setPixel(i, 100, Color.BLUE); //we can also set individual pixels in a Bitmap (like a BufferedImage)
		
		canvas.drawBitmap(_bmp,0,0,null); //and then draw the BitMap onto the canvas.
		//Canvas bmc = new Canvas(_bmp); //we can also make a canvas out of a Bitmap to draw on that (like fetching g2d from a BufferedImage)
	}

	
	
	/**
	 * An inner class representing a thread that does the drawing. Animation timing could go in here.
	 * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
	 */
	public class DrawingThread extends Thread 
	{
		private boolean _isRunning; //whether we're running or not (so we can "stop" the thread)
		private SurfaceHolder _holder; //the holder we're going to post updates to
		private DrawingSurfaceView _view; //the view that has drawing details

		/**
		 * Constructor for the Drawing Thread
		 * @param holder
		 * @param view
		 */
		public DrawingThread(SurfaceHolder holder, DrawingSurfaceView view)
		{
			super();
			this._holder = holder;
			this._view = view;
			this._isRunning = false;
		}

		/**
		 * Executed when we call thread.start()
		 */
		@Override
		public void run()
		{
			Canvas canvas;
			while(_isRunning)
			{
				canvas = null;
				try {
					canvas = _holder.lockCanvas();
					synchronized (_holder) {
						_view.render(canvas);
					}
				} finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
					if (canvas != null) {
						_holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
		
		/**
		 * Public toggle for whether the thread is running.
		 * @param isRunning
		 */
		public void setRunning(boolean isRunning){
			this._isRunning = isRunning;
		}
		
		
	}
}
