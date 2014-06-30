package cs315.yourname.hwk4;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * A basic activity for displaying a simple OpenGL rendering. This uses a slightly different structure than
 * with a regular Canvas.
 * 
 * @author Joel
 * @version Fall 2013
 */
public class GLDancingRobotActivity extends Activity
{
	private static final String TAG = "GLActivity"; //for logging/debugging

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
	
	//starts the dance
	public void danceOn(View view) 
	{
		Log.d(TAG, "Button was pressed");

		Toast toast = Toast.makeText(getApplicationContext(), "Op op op op op", Toast.LENGTH_SHORT);
		toast.show();
		((GLBasicView) _GLView).robotStartDance();

	}
	
	//returns to neutral on click
	public void danceOff(View view) 
	{
		Log.d(TAG, "Button was pressed");

		Toast toast = Toast.makeText(getApplicationContext(), "Eyyyyy, Sexy Lady", Toast.LENGTH_SHORT);
		toast.show();
		((GLBasicView) _GLView).robotStopDance();

	}
}
