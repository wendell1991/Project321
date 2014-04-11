package activities;

import com.example.matheducator.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TutorialActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		android.util.Log.e("test", "test");
		TextView tv = (TextView) findViewById (R.id.timerText);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String tutorial = extras.getString("Tutorial");
		    tv.setText("Tutorial "+tutorial);
		}
	}
}
