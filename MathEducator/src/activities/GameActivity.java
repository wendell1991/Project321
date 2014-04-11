package activities;

import com.example.matheducator.R;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		android.util.Log.e("test", "test");
		
	}
}
