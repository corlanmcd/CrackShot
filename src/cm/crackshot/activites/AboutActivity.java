package cm.crackshot.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cm.crackshot.R;

public class AboutActivity extends Activity implements OnClickListener 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		registerViewListeners();
	}

	private void registerViewListeners() 
	{
		((Button) findViewById(R.id.AboutActivity_backButton)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_about, menu);
		return true;
	}

	@Override
	public void onClick(View view) 
	{
		if (view.getId() == R.id.AboutActivity_backButton)
		{
			startMainMenuActivityFromIntent();
		}
	}

	private void startMainMenuActivityFromIntent() {
		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		startActivity(mainMenuIntent);
		finish();
	}

}
