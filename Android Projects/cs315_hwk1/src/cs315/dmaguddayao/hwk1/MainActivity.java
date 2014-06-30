package cs315.dmaguddayao.hwk1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioButton;
import android.widget.Toast;
import cs315.jross.hwk1.R;

/**
 * A basic starter activity for drawing in Android.
 * 
 * @author DJ Maguddayao
 * @version Fall 2013
 */
public class MainActivity extends Activity
{
	private static final String TAG = "DrawingActivity"; //for logging/debugging
	
	private DrawingView drawView; //the view we'll be drawing on (for later reference). Cast the variable to make more specific
	
	private String status = "Disney"; //default is Disney for first display when made.  When updated, will be used to create a different art
	
	private String size ="100%"; //default size is 100% (fullscreen)
	
	private String canvasColor = "white"; //two canvas colors.  White and Black.  Used for background
	/**
	 * Called when the activity is started
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); //use the activity_main layout xml
		
		drawView = (DrawingView)this.findViewById(R.id.draw_view); //get access to drawView for later
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

	
	//The method that is called when the button is pressed [see the layout xml]
	public void buttonPress(View view) 
	{
		Log.d(TAG, "Button was pressed"); //add a line to the console (debug priority). This is like Sysout.
		
		//check the status of the "state" the program is in.  Used to update the 'flag'
		drawView.updateSize(size); //forces the size in DrawingView to change
		drawView.updateStatus(status); //changes status state so different flag is drawn
		drawView.invalidate();

		//toasts are short popup messages; useful for testing!
		Toast toast = Toast.makeText(getApplicationContext(), "Flag changed to " + status, Toast.LENGTH_SHORT);
		toast.show(); //popup status to tell what was updated
		//drawView.invalidate(); //tells our simple view to redraw itself. This is like "repaint()"
	}
	
	/**
	 * Changes the background/canvas color when the button is pressed
	 * @param view
	 */
	public void backgroundUpdate(View view)
	{
		drawView.updateCanvas(canvasColor);
		drawView.invalidate();
	}
	
	/**
	 * If you found this button somehow, good. You destroyed the world
	 */
	public void buttonDestroy(View view) 
	{
		Log.d(TAG, "Button was pressed");

		Toast toast = Toast.makeText(getApplicationContext(), "You destroyed the world somehow.  Good job", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * Changes the 'status' to what country it is currently on
	 * @param view  The view
	 */
	public void onRadioButtonClicked(View view) 
	{
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radioDIS:
	            if (checked)
	                status = "Disney";
	            break;
	        case R.id.radioMEX:
	            if (checked)
	                status= "Mexico";
	            break;
	        case R.id.radioJapan:
	            if (checked)
	                status = "Japan";
	            break;
	    }

	}
	
	/**
	 * Same as the country, this one is for sizes
	 * @param view
	 */
		public void onSizeButtonClicked(View view) {
		    // Is the button now checked?
		    boolean checked = ((RadioButton) view).isChecked();
		    
		    // Check which radio button was clicked
		    switch(view.getId()) {
		        case R.id.smallSize:
		            if (checked)
		                size = "25%";
		            break;
		        case R.id.midSize:
		            if (checked)
		                size= "50%";
		            break;
		        case R.id.bigSize:
		            if (checked)
		                size = "100%";
		            break;
		    }
	}
		
		/**
		 * When radio dial is pressed, hold the status until needed
		 * @param view
		 */
		public void onColorButtonClicked(View view) {
		    // Is the button now checked?
		    boolean checked = ((RadioButton) view).isChecked();
		    
		    // Check which radio button was clicked
		    switch(view.getId()) {
		        case R.id.whiteColor:
		            if (checked)
		                canvasColor = "white";
		            break;
		        case R.id.blackColor:
		            if (checked)
		                canvasColor = "black";
		            break;
		    }
		    //all radio buttons are from http://developer.android.com/guide/topics/ui/controls/radiobutton.html
		}
}
