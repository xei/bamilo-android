package pt.rocket.testapp.activities;

import pt.rocket.framework.errormanager.ErrorCode;
import pt.rocket.framework.utils.Constants;
import pt.rocket.testapp.R;
import pt.rocket.testapp.helper.GetLoginFormHelper;
import pt.rocket.testapp.utils.DialogGenerator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends BaseActivity {
	private static final String TAG = LoginActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Request Button
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getLoginForm();
					}
				});

		// Go to categories button
		findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(LoginActivity.this,
								CategoriesActivity.class);
						startActivity(intent);
					}
				});

		// Go to ratings button
		findViewById(R.id.button3).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(LoginActivity.this,
								RatingActivity.class);
						startActivity(intent);
					}
				});

		// finish activity button
		findViewById(R.id.button4).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	/**
	 * Gets the login form
	 */
	public void getLoginForm() {
		sendRequest(new GetLoginFormHelper(),
				new BaseActivity.IResponseCallback() {
					@Override
					public void onRequestError(Bundle bundle) {
						Log.d(TAG, "Got error");
						Boolean priority = bundle
								.getBoolean(Constants.BUNDLE_PRIORITY_KEY);
						ErrorCode errorCode = (ErrorCode) bundle
								.getSerializable(Constants.BUNDLE_ERROR_KEY);
						if (priority) {
							showErrorDialog(errorCode);
						} else {
							Log.i(TAG,
									"Error handling does not require to be shown to the user");
						}
					}

					@Override
					public void onRequestComplete(Bundle bundle) {
						// TODO Auto-generated method stub
						Log.d(TAG,
								"Got the response => "
										+ bundle.getString(Constants.BUNDLE_RESPONSE_KEY));
					}
				});
	}

	private void showErrorDialog(ErrorCode errorCode) {
		Dialog dialog = DialogGenerator.generateDialog(this, errorCode);
		Button okButton = (Button) dialog
				.findViewById(R.id.generic_dialog_button);
		okButton.setOnClickListener(onClick);
		dialog.show();
	}

	public OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.generic_dialog_button:
				getLoginForm();

				break;
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
