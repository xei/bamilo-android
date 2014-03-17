package pt.rocket.testapp.activities;

import pt.rocket.framework.utils.Constants;
import pt.rocket.testapp.R;
import pt.rocket.testapp.helper.GetRatingsFormHelper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.View;

/**
 * Example ratings activity
 * @author Guilherme Silva
 *
 */
public class RatingActivity extends BaseActivity {
    private static final String TAG = RatingActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);
		
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {
					/* (non-Javadoc)
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						getRatings();
					}
				});
		
		findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener() {
					/* (non-Javadoc)
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(RatingActivity.this, CategoriesActivity.class);
						startActivity(intent);
					}
				});
		
		findViewById(R.id.button3).setOnClickListener(
				new View.OnClickListener() {
					/* (non-Javadoc)
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(RatingActivity.this, LoginActivity.class);
						startActivity(intent);
					}
				});
		
		findViewById(R.id.button4).setOnClickListener(
				new View.OnClickListener() {
					/* (non-Javadoc)
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		
	}
	
	@Override
	protected void onServiceActivation() {
		super.onServiceActivation();

		getRatings();
	}
	
	public void getRatings() {
		sendRequest(new GetRatingsFormHelper(), new BaseActivity.IResponseCallback() {
			@Override
			public void onRequestError(Bundle bundle) {
				Log.d(TAG, "Got error");
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Got the response => "+bundle.getString(Constants.BUNDLE_RESPONSE_KEY));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
