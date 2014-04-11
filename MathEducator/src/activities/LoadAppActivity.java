package activities;

import java.io.IOException;

import objects.Constants;

import com.example.matheducator.R;

import database.DBAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class LoadAppActivity extends Activity {

	// Load application button
	private ImageButton startBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_app);
		
		startBtn = (ImageButton) findViewById(R.id.startBtn);
		startBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DBAdapter database = new DBAdapter(getApplicationContext());
				try {
					database.createDataBase();
				} catch (IOException e) {
					Log.i(Constants.LOG_LOADAPP, e.toString());
			    }
				database.close();
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
			} 
		});
	}
}
