package cs315.dmaguddayao.hwk1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 * A starter class for doing simple drawing on Android.
 * This class simply overrides View, providing for simple but low-performance drawing
 * (see http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-view)
 * 
 * @author DJ Maguddayao
 * @version Fall 2013
 */
public class DrawingView extends View
{
	private int _viewWidth;
	private int _viewHeight;
	private Paint _blackPaint;
//	private Bitmap _bmp;
	private Paint _redPaint;
	private Paint _greenPaint;
	private Paint _whitePaint; //different paint classes
	private String status= "Mexico"; //default incase no radio dials are pressed yet
	private String size="100%"; //same reason as the status default
	private String canvasColor = "white";//same reason
	
	/**
	 * We need to override all the constructors, since we don't know which will be called
	 * All the constructors eventually call init()
	 */
	public DrawingView(Context context) {
		this(context, null);
	}

	public DrawingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawingView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		init();
	}

	/**
	 * Override method that is called when the size of the display is specified (or changes
	 * due to rotation).
	 */
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		//store new size of the view
		_viewWidth = w;
		_viewHeight = h;
		
		//create a properly-sized bitmap to draw on
		//_bmp = Bitmap.createBitmap(_viewWidth, _viewHeight, Bitmap.Config.ARGB_8888); Not used
	}
	
	/**
	 * "Constructor".  Initializes for different paint classes to use
	 */
	public void init()
	{
		_blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_blackPaint.setColor(Color.BLACK);
		_blackPaint.setStyle(Style.FILL);
		_redPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 	//creating other colors from paint class to use in
		_redPaint.setColor(Color.RED);				  	// drawing
		_redPaint.setStyle(Style.FILL);
		_greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_greenPaint.setColor(Color.GREEN);
		_greenPaint.setStyle(Style.FILL);
		_whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_whitePaint.setColor(Color.WHITE);
		_whitePaint.setStyle(Style.FILL);
	}

	/**
	 * Override this method to specify drawing. It is like our "paintComponent()" method.
	 * Has many different outcomes hardcoded in (sorry).
	 */
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas); //make sure to have the parent do any drawing it is supposed to!
		
		if (canvasColor == "white") //this is for when BC Update is used
			{
				canvas.drawColor(Color.WHITE);
			}
		if (canvasColor == "black")
			{
				canvas.drawColor(Color.BLACK);
			}
		
		//for when a certain status for a flag to be placed  These are possible outcomes
		if (status == "Disney")
		{
			//also taking into account of size differences
			if(size == "25%")
			{
				canvas.drawRect(_viewWidth*3/8, _viewHeight*3/8, _viewWidth*5/8, _viewHeight*5/8, _greenPaint);
				canvas.drawCircle(_viewWidth/2.0f, _viewHeight/2.0f, 25.0f, _blackPaint);
				canvas.drawCircle(_viewWidth/2.0f-20,_viewHeight/2.0f-17.5f,17.5f,_blackPaint);
				canvas.drawCircle(_viewWidth/2.0f+20,_viewHeight/2.0f-17.5f,17.5f,_blackPaint);
			}
			if(size =="50%")
			{
				canvas.drawRect(_viewWidth/4,_viewHeight/4,_viewWidth*3/4,_viewHeight*3/4,_greenPaint);
				canvas.drawCircle(_viewWidth/2.0f, _viewHeight/2.0f, 50.0f, _blackPaint);
				canvas.drawCircle(_viewWidth/2.0f-40, _viewHeight/2.0f-35, 35f, _blackPaint);
				canvas.drawCircle(_viewWidth/2.0f+40, _viewHeight/2.0f-35, 35f, _blackPaint);
			}
			if(size=="100%")
			{	
				canvas.drawRect(0,0,_viewWidth,_viewHeight,_greenPaint);
				canvas.drawCircle(_viewWidth/2.0f, _viewHeight/2.0f, 100.0f, _blackPaint); //we can draw directly onto the canvas
				//see http://developer.android.com/reference/android/graphics/Canvas.html for a list of options.  I barely remember
				//most of the canvas commands, but they pop up when canvas is started out
				canvas.drawCircle(_viewWidth/2.0f-80, _viewHeight/2.0f-70, 70.0f, _blackPaint); 
				canvas.drawCircle(_viewWidth/2.0f+80, _viewHeight/2.0f-70, 70.0f, _blackPaint); 
			}
		}
		if (status == "Japan")
		{
			if (size == "25%")
			{
				canvas.drawRect(_viewWidth*3/8, _viewHeight*3/8, _viewWidth*5/8, _viewHeight*5/8, _whitePaint);
				canvas.drawCircle(_viewWidth/2.0f, _viewHeight/2.0f, 25.0f, _redPaint);
			}
			
			if (size == "50%")
			{
				canvas.drawRect(_viewWidth/4,_viewHeight/4,_viewWidth*3/4,_viewHeight*3/4,_whitePaint);
				canvas.drawCircle(_viewWidth/2.0f, _viewHeight/2.0f, 50.0f, _redPaint);
			}
			if (size == "100%")
			{
			canvas.drawRect(0, 0, _viewWidth, _viewHeight, _whitePaint);
			canvas.drawCircle(_viewWidth/2.0f, _viewHeight/2.0f, 100.0f, _redPaint);
			}

		}
		
		if (status == "Mexico")
		{
			if (size == "25%")
			{
				canvas.drawRect(_viewWidth*3/8,_viewHeight*3/8,_viewWidth*11/24,_viewHeight*5/8,_greenPaint);
				canvas.drawRect(_viewWidth*11/24, _viewHeight*3/8, _viewWidth*13/24, _viewHeight*5/8,_whitePaint );
				canvas.drawRect(_viewWidth*13/24, _viewHeight*3/8, _viewWidth*5/8, _viewHeight*5/8, _redPaint);
			}
			if (size == "50%")
			{
				canvas.drawRect(_viewWidth*1/4, _viewHeight*1/4, _viewWidth*5/12, _viewHeight*3/4,_greenPaint );
				canvas.drawRect(_viewWidth*5/12,_viewHeight*1/4,_viewWidth*7/12,_viewHeight*3/4,_whitePaint);
				canvas.drawRect(_viewWidth*7/12, _viewHeight*1/4, _viewWidth*3/4, _viewHeight*3/4, _redPaint);
			}
			if (size == "100%")
			{
				canvas.drawRect(0,0,_viewWidth/3,_viewHeight,_greenPaint);
				canvas.drawRect(_viewWidth/3,_viewHeight,_viewWidth*2/3,_viewHeight,_whitePaint);
				canvas.drawRect(_viewWidth*2/3, 0, _viewWidth, _viewHeight, _redPaint);
			}
		}
		
		
	}
	
	/**
	 * Updates the status.  Only used when taken from Main Activity
	 * @param stat name of the flag to be used
	 */
	public void updateStatus(String stat)
	{
		status = stat;
	}
	
	/**
	 * Same as status.  Only used in Main Activity for redraws
	 * @param s Name of the size...yeah this one is weird
	 */
	public void updateSize(String s)
	{
		size = s;
	}
	
	/**
	 * Same as before.  Only for updating
	 * @param s Name of the color for the canvas
	 */
	public void updateCanvas(String s)
	{
		canvasColor=s;
	}
}
