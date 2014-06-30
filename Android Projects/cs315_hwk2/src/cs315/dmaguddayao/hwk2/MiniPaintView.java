package cs315.dmaguddayao.hwk2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A starter template for an Android scan conversion mini-painter. Includes the logic for doing the scan conversions
 * 
 * This class provides a dedicated thread for drawing, though the actual work is still in the UI thread,
 * because it is user driven.
 * 
 * @author DJ Maguddayao, Adapated from Joel's code from David
 * @version Fall 2013
 */
public class MiniPaintView extends SurfaceView implements SurfaceHolder.Callback
{
	private static final String TAG = "MiniPaintView";

	public static final int POINT_MODE = 0; //mode number (supposedly faster than enums)
	public static final int LINE_MODE = 1;
	public static final int CIRCLE_MODE = 2;
	public static final int POLYLINE_MODE = 3;
	public static final int RECTANGLE_MODE = 4;
	public static final int FLOOD_FILL_MODE = 5;
	public static final int AIRBRUSH_MODE = 6;

	private static final int PIXEL_SIZE = 2; //how "big" to make each pixel; change this for debugging
	private boolean DELAY_MODE = true; //whether to show a delay on the drawing (for debugging)
	private static final int DELAY_TIME = 5; //how long to pause between pixels; delay in ms; 5 is short, 50 is long

	private SurfaceHolder _holder; //basic drawing structure
	private DrawingThread _thread;
	private Context _context;
	private Stack<ArrayList<Integer>> floodStack = new Stack<ArrayList<Integer>>();
	
	private Bitmap _bmp; //frame buffer
	private int _width; //size of the image buffer
	private int _height;
	private Matrix _scaleM; //scale based on pixel size

	private int _mode; //drawing mode
	private int _color; //current painting color
	
	private int _startX; //starting points for multi-click operations
	private int _startY;


	/**
	 * Respond to touch events
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int x = (int)event.getX()/PIXEL_SIZE; //scale event to the size of the frame buffer!
		int y = (int)event.getY()/PIXEL_SIZE;

		switch(_mode) {
		case POINT_MODE:
			drawPoint(x,y);
			break;
			
		case LINE_MODE: 
			if(_startX < 0) { //see if we have a "first click" set of coords
				_startX = x; 
				_startY = y;
			}
			else {
				drawLine(_startX, _startY, x, y);
				_startX = -1;
			}
			break;
		case CIRCLE_MODE:
			if(_startX < 0) { //see if we have a "first click" set of coords
				_startX = x; 
				_startY = y;
			}
			else {
				int radius = (int) Math.sqrt((x - _startX)*(x - _startX) + (y - _startY)*(y - _startY));
				drawCircle(_startX, _startY, radius);
				_startX = -1;
			}
			break;
		case POLYLINE_MODE: 
			if(_startX < 0) { //see if we have a "first click" set of coords
				_startX = x; 
				_startY = y;
			}
			else {
				drawLine(_startX, _startY, x, y);
				_startX = x;
				_startY=y;
			}
			break;
		case RECTANGLE_MODE:
			if(_startX < 0) { //see if we have a "first click" set of coords
				_startX = x; 
				_startY = y;
			}
			else {
				drawRectangle(_startX, _startY, x, y);
				_startX = -1;
			}
			break;
		case FLOOD_FILL_MODE:
			floodFill(x,y);
			
			break;
		case AIRBRUSH_MODE:
			airBrush(x,y);
			break;
		}

		return super.onTouchEvent(event); //pass up the tree, as needed
	}

	/**
	 * Draws a single point on the screen in the current paint color
	 * @param x x-coord of the point
	 * @param y y-coord of the point
	 */
	public void drawPoint(int x, int y)
	{
		setPixel(x, y); //I've done this one for you. You're welcome ;)
	}

	/**
	 * Draws a line on the screen in the current paint color
	 * @param startX x-coord of starting point
	 * @param startY y-coord of starting point
	 * @param endX x-coord of ending point
	 * @param endY y-coord of ending point
	 */
	public void drawLine(int startX, int startY, int endX, int endY)
	{
	
		//decide which pixel to turn on
		//to do that, iterate either x or y in positive/negative direction
		//then decide if you need to turn on a certain pixel (Bresenham equation)
		int x = startX; //starting place
		int y = startY;
		int dy = endY-startY;
		int dx = endX-startX;//"slope"
		int p =0;
		
		if (dy == 0) //slope is a horizontal line
		{
			int k =0; 
			if(dx > 0) //case where line goes left to right
			{
				while(k < dx)
					{
					 setPixel(x,startY);
					 x++;
					 k++;
					}
			}
			else //goes right to left
			{
				while(k < -dx)
				{
					setPixel(x,startY);
					x--;
					k++;
				}
			}
			return;
		}
		
		if (startX == endX) //slope is a vertical line
		{
			int k =0;
			if (dy > 0)
				{
					while ( k < dy)
					{
						setPixel(startX,y);
						y++;
						k++;
					}
				}
			else
			{
				while (k < -dy)
				{
					setPixel(startX,y);
					y--;
					k++;
				}
			}
			return;
		}
		
		if (dx>0) //righthand side
		{
			if(dy>0) //first quadrant
			{
				if(dx ==dy) //each == is for when slope is 1 in some way
				{
					for(int k=0; k<dx; k++)
					{
						setPixel(x,y);
						x++;
						y++;
					}
				}
				if(dx > dy)
				{
					p =  2*dy - dx; //decision variable 
					for(int k=0; k< dx; k++) //works only down right at y=mx+b where m<=1
					{
						setPixel(x,y);
				
						x = x+1;
						if(p <0) //sets 'lower' pixel
						{
							p = p+2*dy;
							//y=y;keeps it the same.  Pretty useless
						}
						else //set 'upper' pixel
						{
							p = p+ 2*dy -2*dx;
							y= y+1;
						}
					}
				}
		
				//when slope is > 1
				if(dy > dx)
				{
					p = 2*dx- dy; //decision variable
					for(int k=0; k < dy; k++)
					{
						setPixel(x,y);				
						y = y+1;
						if(p <0) //sets 'left' pixel
						{
							p = p+2*dx;
							//x=x;//keeps it the same.  Pretty useless
						}
						else //set 'right' pixel
						{
							p = p+ 2*dx -2*dy;
							x= x+1;
						}
				
					}
				}
			}
			
			if(dy<0) //when in fourth quadrant
			{
				if(dx == -dy)
				{
					for(int k=0; k<dx;k++)
					{
						setPixel(x,y);
						x++;
						y--;
					}
				}
				if(dx > -dy)
				{
					p =  2*-dy - dx; //decision variable 
					for(int k=0; k< dx; k++) //works only down right at y=mx+b where m<=1
					{
						setPixel(x,y);
				
						x = x+1;
						if(p <0) //sets 'lower' pixel
						{
							p = p+2*-dy;
							//y=y;keeps it the same.  Pretty useless
						}
						else //set 'upper' pixel
						{
							p = p+ 2*-dy -2*dx;
							y= y-1;
						}
					}
				}
		
				//when slope is >-1
				if(-dy > dx)
				{
					p = 2*dx- (-dy); //decision variable
					for(int k=0; k < -dy; k++)
					{
						setPixel(x,y);				
						y = y-1;
						if(p <0) //sets 'left' pixel
						{
							p = p+2*dx;
							//x=x;//keeps it the same.  Pretty useless
						}
						else //set 'right' pixel
						{
							p = p+ 2*dx -2*-dy;
							x= x+1;
						}
				
					}
				}
			}
		}
		if (dx<0) //lefthand side
		{
			if(-dx == dy)
			{
				for(int k=0; k<-dx;k++)
				{
					setPixel(x,y);
					x--;
					y++;
				}
			}
			if(dy>0) //second quadrant
			{
				if(-dx > dy)
				{
					p =  2*dy - (-dx); //decision variable 
					for(int k=0; k< -dx; k++)
					{
						setPixel(x,y);
				
						x = x-1; //heads leftward
						if(p <0) //sets 'lower' pixel
						{
							p = p+2*dy;
							//y=y;keeps it the same.  Pretty useless
						}
						else //set 'upper' pixel
						{
							p = p+ 2*dy -2*-dx;
							y= y+1;
						}
					}
				}
		
				//when slope is >-1
				if(dy > -dx)
				{
					p = 2*-dx- dy; //decision variable
					for(int k=0; k < dy; k++)
					{
						setPixel(x,y);				
						y = y+1;
						if(p <0) //sets 'right' pixel
						{
							p = p+2*-dx;
							//x=x;//keeps it the same.  Pretty useless
						}
						else //set 'left' pixel
						{
							p = p+ 2*-dx -2*dy;
							x= x-1;
						}
				
					}
				}
			}
			
			if(dy<0) //when in third quadrant
			{
				if(-dx == -dy)
				{
					for(int k = 0; k<-dx;k++)
					{
						setPixel(x,y);
						x--;
						y--;
					}
				}
				if(-dx > -dy)
				{
					p =  2*-dy - (-dx); //decision variable 
					for(int k=0; k< -dx; k++) 
					{
						setPixel(x,y);
				
						x = x-1;
						if(p <0) //sets 'upper' pixel
						{
							p = p+2*-dy;
							//y=y;keeps it the same.  Pretty useless
						}
						else //set 'lower' pixel
						{
							p = p+ 2*-dy -2*-dx;
							y= y-1;
						}
					}
				}
		
				//when slope is > 1
				if(-dy > -dx)
				{
					p = 2*-dx- (-dy); //decision variable
					for(int k=0; k < -dy; k++)
					{
						setPixel(x,y);				
						y = y-1;
						if(p <0) //sets 'right' pixel
						{
							p = p+2*-dx;
							//x=x;//keeps it the same.  Pretty useless
						}
						else //set 'left' pixel
						{
							p = p+ 2*-dx -2*-dy;
							x= x-1;
						}
				
					}
				}
			}
		}
	}

	/**
	 * Draws a circle on the screen in the current paint color
	 * @param x x-coord of circle center
	 * @param y y-coord of circle center
	 * @param radius radius of the circle
	 */
	public void drawCircle(int x, int y, int radius)
	{
	//Make a perfect circle.  This works to a degree,but it's imperfect	
	//Update: It works now.  I forgot the 4 in 4*relativeY D:
		int centerX = x;
		int centerY = y;
		int p = 3-2*radius;//sets the decision variable
		int relativeX = radius; //initial point of x
		int relativeY = 0;//initial relative point of y
		
		setPixel(centerX+relativeX,centerY + relativeY); //first point
		
		//first eight of the circle
		while (relativeY < relativeX)// while the Y is smaller than X
			//Y is growing while X is shrinking
		{
			relativeY++; //y increases
			if (p<0)
			{
				//x=x useless.  Stays the same
				p = p+4*relativeY+6;
			}
			else
			{
				relativeX = relativeX-1; //moves leftward
				p = p+4*(relativeY-relativeX)+10;
			}
			setPixel(centerX+relativeX,centerY+relativeY);
		}		
		while (relativeX <= relativeY && relativeX!=0)//bring it to the second eight
									//ensures it goes to highest y point
		{
			relativeX--;//moving backwards
			if(p<0)//same y
			{
				p = p+4*relativeX+6;
				//p = p+relativeX-6;
			}
			
			else
			{
				if(relativeY < radius)//failsafe
				{
				relativeY = relativeY+1;//goes up
				}
				
				p = p+4*(relativeX-relativeY)+10;
				//p = p+4*(relativeX-relativeY)-10;
			}
			setPixel(centerX+relativeX, centerY+relativeY);
		}
		setPixel(centerX+relativeX,centerY+relativeY);//in case the last one was drawn
		while (-relativeX<relativeY)//going down to the X=Y
		{
			relativeX--;
			if(p<0)
			{
				p=p-4*relativeX+6;
			}
			else
			{
				relativeY = relativeY-1; //goes 'down'
				p=p+4*(-relativeX-relativeY)+10;
			}
			setPixel(centerX+relativeX,centerY+relativeY);
		}
		while (relativeY <= -relativeX && relativeY != 0)//heading to Y=0
		{
			relativeY--;
			if(p<0)
			{
				//relativeX=relativeX;
				p=p+4*relativeY+6;
			}
			else
			{
				p = p+4*(relativeY+relativeX)+10;
				if(-relativeX< radius)//failsafe
				{
				relativeX = relativeX-1;
				}
			}
			setPixel(centerX+relativeX,centerY+relativeY);
		}
		while (-relativeY < -relativeX)//moving vertically downwards.  5/8th circle
		{
			relativeY--;
			if(p<0)
			{
				//relativeX = realtiveX;
				p=p-4*relativeY+6;
			}
			else
			{
				p = p+4*(-relativeY + relativeX)+10;
				relativeX =relativeX+1;//moves right
			}
			setPixel(centerX+relativeX,centerY+relativeY);
		}
		while (-relativeX <= -relativeY && -relativeX != 0)//heading to X = 0 on third quadrant
		{
			relativeX++;
			if(p<0)
			{
				p=p-4*relativeX+6;
			}
			else
			{
				p=p+4*(-relativeX+relativeY)+10;
				if (-relativeY < radius)//failsafe
				{
				relativeY = relativeY-1; //moves down
				}
			}
			setPixel(centerX+relativeX, centerY + relativeY);
		}
		while(relativeX < -relativeY)//7/8th of the circle.  Heading to X = -Y
		{
			relativeX++;
			if(p<0)
			{
				p = p+4*relativeX+6;
			}
			else
			{
				p= p+4*(relativeX+relativeY)+10;
				relativeY= relativeY+1;
			}
			setPixel(centerX+relativeX,centerY+relativeY);
		}
		while(-relativeY <= relativeX && relativeY !=0)
		{
			relativeY++;
			if(p<0)
			{
				p=p-4*relativeY+6;
			}
			else
			{
				p = p+4*(-relativeY-relativeX)+10;
				if(relativeX < radius) //fail-safe:  if it overshoots, it drops down
				{
					relativeX++;
				}
			}
			setPixel(centerX+relativeX,centerY+relativeY);
		}
		
	}

	/**
	 * Draws a rectangle on the screen in the current paint color
	 * @param startX x-coord of first corner (i.e., upper left)
	 * @param startY y-coord of first corner
	 * @param endX x-coord of second corner (i.e., lower right)
	 * @param endY y-coord of second corner
	 */
	public void drawRectangle(int startX, int startY, int endX, int endY)
	{ 
		// Note that you can reuse your drawLine() method, but you shouldn't reuse the floodFill() method!
		
		//find the other two points.  The starting points and ending points are just two corners
		//calculate the points using math.  
		
		//should be that x1,y1 should be (rightmost x, lowest y)
		//x2,y2 should be (lowest x, highest y)
		//draw lines from start,(x1,y1),end,(x2,y2),start
		
		//how to fill...I don't know yet.  Since we know that all the end points
		//and this isn't slanted.  We can make a method that will fill each row
		//by x*y pixels
		int currentY;
		if (endY > startY) //if the 'end point' is 'higher'
		{
		currentY = startY;
		while(currentY != endY)
			{
				drawLine(startX,currentY,endX,currentY);//draw bottom up
				currentY++;
			}
		}
		else//if the 'starting point' is 'higher'
		{
		currentY = endY;
		while(currentY != startY)
			{
				drawLine(startX,currentY,endX,currentY);
				currentY++;
			}
		}
	}

	/**
	 * Flood-fills a space on the canvas in the current paint color
	 * @param x x-coord to start filling from
	 * @param y y-coord to start filling from
	 */
	public void floodFill(int x, int y)
	{
		// TODO: 
		// Note that you should implement this iteratively (using a Stack), not recursively!
		//from starting point fill all the way right.
		
		//for some reason, it is really selective in the direction it wants to go when deciding
		//a way to fill D:  The stacks aren't iterating properly for some reason.  
		
		floodStack.clear(); //clears the floodStack just in case
		int oldColor = getPixel(x,y); //stores what the old color was
		int currentX = x; //keeps track of where it is at the moment in time
		boolean addTopStack = true;
		boolean addBottomStack= true; //for adding the bottom to the stack
		ArrayList<Integer> location = new ArrayList<Integer>(2); //holds coordinates	
		
		if (oldColor == _color)
		{
			return; //if you chose the same color as your are changing, do nothing
		}
		
		while(getPixel(currentX,y) == oldColor)//heads to the right
		{
			currentX++;
		} //ends when it hits a "wall"
		currentX--; //moves away from the wall
		location.add(0,currentX);
		location.add(1,y);
		floodStack.push(location);
		//holds the first location just in case the top or bottom is missed somehow
		
		//now check the space above and below and see if it needs to be filled.
		//if so, add to stack.
		
		while(getPixel(currentX,y) == oldColor && currentX > 0) //failsafe for if it goes to the border
		{
			setPixel(currentX,y); //sets the pixels along the way
			if(getPixel(currentX,y+1) == oldColor)//checks above.  If it is the same as the old color, runs
				{
					if(addTopStack==true)//if there wasn't anything else that blocked it
					{
						location.add(0,currentX);//we set the coordinates for the stack
						location.add(1,y+1);//holds coordinates
						floodStack.push(location); //
						addTopStack=false;//makes it so that the next colors in the same row aren't added
					}
				}
			
			if(getPixel(currentX,y+1) != oldColor) //if it isn't the same color as mentioned
				{
					addTopStack=true; //sets the counter to add so that it will add "same color" due to 
									//not being in the same row
				}
			if(getPixel(currentX,y-1) == oldColor) //checks the bottom now
				{
					if(addBottomStack==true) //same as for +1, do the same for -1
					{
						location.add(0,currentX);
						location.add(1,y-1); //stores the below location
						floodStack.push(location);
						addBottomStack=false;
					}
				}
				
			if(getPixel(currentX,y-1) != oldColor)
				{
					addBottomStack=true;
				}
			currentX--;//goes left
		}
			//bottom is pushed on stack last
		
		 	//one iteration.  Now use the stack to get other iterations
		
		while (!floodStack.empty())//as long as the stack isn't empty
		{
			location = floodStack.pop();
			fillChecker(location.get(0),location.get(1),oldColor); //in theory, should
			//be able to put more stacks until the entire thing is filled  But for some reason,
			//it's not reading some of parts of the stack
		}

	}
	
	/**
	 * Most of it is the same code above.  It just goes past the first iteration
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param oldColor the Color we are changing
	 */
	private void fillChecker(int x, int y,int oldColor)
	{
		boolean addTopStack = true;
		boolean addBottomStack=true;
		ArrayList<Integer> location = new ArrayList<Integer>(2);
		
		int currentX = x;
		
		while(getPixel(currentX,y) == oldColor)//oldColor is the original color
		{
			currentX++;
		} 
		//ends when it hits a "wall".  Goes all the way to the right  This is incase we didn't
		//hit the furthest right pixel the first time
		
		currentX--; //moves away from the wall
		
		while(getPixel(currentX,y) == oldColor && currentX > 0) //failsafe again
		{ 							//if it hits an unfamiliar color, then it stops
			
			setPixel(currentX,y); //sets the pixel to the current color selected
			
			if(getPixel(currentX,y+1) == oldColor) //checks above if the color needs to be changed
			{ 										//this is the case where it does need to
				if(addTopStack==true)
				{
					location.add(0,currentX);
					location.add(1,y+1);//holds coordinates
					floodStack.push(location); //puts it on the stack
					addTopStack=false; //for checking if the pixels next to it are also the same color
				}
			}
			
			if(getPixel(currentX,y+1)!=oldColor) //if it isn't the same color as mentioned
			{
				addTopStack=true; //sets the counter to add so that it will add next above
								//thing checked
			}
			
			if(getPixel(currentX,y-1) == oldColor) //checks bottom now
			{
				if(addBottomStack==true) 
				{
					location.add(0,currentX);
					location.add(1,y-1); //stores the location
					floodStack.push(location);
					addBottomStack=false;
				}
			}
			if(getPixel(currentX,y-1)!=oldColor) 
				{
					addBottomStack=true;
				}
			currentX--;//goes left after each time
		}
	}
		
	/**
	 * Draws an airbrushed blob in the current paint color (blending with existing colors)
	 * @param x x-coord to center the airbrush
	 * @param y y-coord to center the airbrush
	 */
	public void airBrush(int x, int y)
	{
		
		int radius =5; //how many pixels out this reaches out
		int multiplier = 2*radius; //how much to multiply the values for averaging
		int currentMult = multiplier;
		boolean XPen= true; //as it gets closer to the middle/origin, lower the multiplier
		boolean YPen= true;//becomes false as it passes the midpoint
		
		int multiplyXPen = 0;//multiplier penalty
		int multiplyYPen = 0;
		
		int startX = x-radius; //left
		int endX = x+radius; //right
		int startY = y-radius; //top
		int endY = y+radius; //bottom
		
		while (startY <= endY) //just looping.  From bottom to top
		{
			while (startX <= endX) //from left to right
			{
				newColorMaker(startX,startY,currentMult-multiplyXPen); //utilizes another method
																		//to calculate
				if (multiplyXPen ==5)//when it is directly above midpoint and heading right
				{
					XPen = false;
				}
				
				if(XPen ==true)
				{
					multiplyXPen++; //makes it closer to original color
				}
				else
				{
					multiplyXPen--;
				}
				
				startX++;//heads right
			}
			XPen = true;//resets after all the loops are done
			multiplyXPen = 0; //reset in case math was improper (failsafe)
			if (multiplyYPen ==5)//when it is on the midpoint/x-axis
			{
				YPen = false;
			}
			
			if(YPen ==true)
			{
				multiplyYPen++;
			}
			else
			{
				multiplyYPen--;
			}
			
			currentMult=multiplier-multiplyYPen;
			
			startY++; //moves down by one
			startX = x-radius;
		}
	}
	
	/**
	 * New color maker.  Averages the RGB of the point at the time
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param multiplier Multiplier for averaging
	 */
	private void newColorMaker(int x, int y, int multiplier)
	{
		int newRed = ((11-multiplier)*Color.red(_color)+multiplier*Color.red(getPixel(x,y)))/11;
		int newGreen=((11-multiplier)*Color.green(_color)+multiplier*Color.green(getPixel(x,y)))/11;
		int newBlue=((11-multiplier)*Color.blue(_color)+multiplier*Color.blue(getPixel(x,y)))/11;
		setPixel(x,y,Color.rgb(newRed, newGreen, newBlue));
	}


	/*********
	 * You shouldn't need to modify anything below this point!
	 ********/
	
	/**
	 * We need to override all the constructors, since we don't know which will be called
	 * All the constructors eventually call init()
	 */
	public MiniPaintView(Context context) {
		this(context, null);
	}

	public MiniPaintView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MiniPaintView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		init(context);
	}

	/**
	 * Our initialization method (called from constructors)
	 */
	public void init(Context context)
	{
		_context = context;
		_holder = this.getHolder(); //handles control of the surface
		_holder.addCallback(this);
		_thread = new DrawingThread(_holder, this);

		_scaleM = new Matrix();
		_scaleM.setScale(PIXEL_SIZE, PIXEL_SIZE);
		DELAY_MODE = true;
		
		_mode = POINT_MODE;
		_color = Color.WHITE;

		_startX = -1; //initialize as invalid
		_startY = -1;

		setFocusable(true); //just in case for touch events
	}
	
	
	/**
	 * Sets the drawing mode (UI method)
	 */
	public void setMode(int mode)
	{
		_mode = mode;
		_startX = -1;
		_startY = -1;
		//Toast toast = Toast.makeText(_context, "Mode set: "+_mode, Toast.LENGTH_SHORT);
		//toast.show();
	}

	/**
	 * Sets the painting color (UI method)
	 */
	public void setColor(int color)
	{
		_color = color;
		//Toast toast = Toast.makeText(_context, "Color set: "+_color, Toast.LENGTH_SHORT);
		//toast.show();
	}

	/**
	 * Clears the drawing (resets all pixels to Black)
	 */
	public void clearDrawing()
	{
		for(int i=0; i<_width; i++)
			for(int j=0; j<_height; j++)
				_bmp.setPixel(i, j, Color.BLACK);
	}

	/**
	 * Helper method to set a single pixel to a given color.
	 * Performed clipping, and includes debug settings to introduce a delay in pixel drawing
	 * @param x x-coord of pixel
	 * @param y y-coord of pixel
	 * @param color color to apply to pixel
	 */
	public void setPixel(int x, int y, int color)
	{
		/*Can comment out this block to make things go even faster*/
		if(DELAY_MODE) //if we're in delay mode, then pause while drawing
		{
			try{
				Thread.sleep(DELAY_TIME);
			} catch (InterruptedException e){}
		}
		
		if(x >= 0 && x < _width && y >= 0 && y < _height) //clipping for generated shapes (so we don't try and draw outside the bmp)
			_bmp.setPixel(x, y, color);
	}

	/**
	 * Helper method to set a single pixel to the current paint color.
	 * Performed clipping, and includes debug settings to introduce a delay in pixel drawing
	 * @param x x-coord of pixel
	 * @param y y-coord of pixel
	 */
	public void setPixel(int x, int y)
	{
		setPixel(x,y,_color);
	}
	
	/**
	 * Convenience method to get the color of a specific pixel
	 * @param x x-coord of pixel
	 * @param y y-coord of pixel
	 * @return The color of the pixel
	 */
	public int getPixel(int x, int y)
	{
		return _bmp.getPixel(x,y);
	}
	
	//called when the surface changes (like sizes changes due to rotate). Will need to respond accordingly.
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		//store new size for our BitMap
		_width = width/2;
		_height = height/2;

		//create a properly-sized bitmap to draw on
		_bmp = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
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

	/**
	 * An inner class representing a thread that does the drawing. Animation timing could go in here.
	 * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
	 */
	public class DrawingThread extends Thread 
	{
		private boolean _isRunning; //whether we're running or not (so we can "stop" the thread)
		private SurfaceHolder _holder; //the holder we're going to post updates to
		private MiniPaintView _view; //the view that has drawing details

		/**
		 * Constructor for the Drawing Thread
		 * @param holder
		 * @param view
		 */
		public DrawingThread(SurfaceHolder holder, MiniPaintView view)
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
						canvas.drawBitmap(_bmp,_scaleM,null); //draw the _bitmap onto the canvas. Note that filling the bitmap occurs elsewhere
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
