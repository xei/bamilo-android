package pt.rocket.testapp.activities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pt.rocket.framework.errormanager.ErrorCode;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import pt.rocket.testapp.R;
import pt.rocket.testapp.helper.GetCategoriesHelper;
import pt.rocket.testapp.helper.GetLoginFormHelper;
import pt.rocket.testapp.utils.DialogGenerator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Example class that deals with the Categories UI and requests
 * 
 * @author josedourado
 * 
 */
public class CategoriesActivity extends BaseActivity {
    private static final String TAG = CategoriesActivity.class.getSimpleName();
    private Context context;
    private View responseCategory;
    private View responseLoginForm;
    private TextView responseTimeCategory;
    private TextView responseTimeLogin;
    private EditText iterations;
    private LinearLayout mainLinear;
    private Button allRequests;
    private float average;
    private static final int ITERATIONS=1;
    private static final int REQUESTS=2;
    private HashMap<String, Long> averages;
    private LinearLayout avgs;
    private RelativeLayout loading;
    private Handler handler=new Handler();
    int counter=0;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        context = this;
        
        responseCategory = (View) findViewById(R.id.result_window_category);
        responseLoginForm = (View) findViewById(R.id.result_window_login);
        
        responseTimeCategory = (TextView) findViewById(R.id.result_time_category);
        responseTimeLogin = (TextView) findViewById(R.id.result_time_login);

        // String encripted = Utils.encrypt("password");
        // Log.i(TAG, "password => " + encripted);
        // Log.i(TAG, encripted + " => " + Utils.decrypt(encripted));

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            /*
             * (non-Javadoc)
             * 
             * @see android.view.View.OnClickListener#onClick(android.view
             * .View)
             */
            @Override
            public void onClick(View v) {
                getCategories();
            }
        });
        
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            /*
             * (non-Javadoc)
             * 
             * @see android.view.View.OnClickListener#onClick(android.view
             * .View)
             */
            @Override
            public void onClick(View v) {
                getLoginForm();
            }
        });
        
        mainLinear	= (LinearLayout) findViewById(R.id.all_request_lin);
        allRequests	= (Button) findViewById(R.id.all_requests);
        iterations = (EditText) findViewById(R.id.iterations);
        avgs = (LinearLayout) findViewById(R.id.avg);
        loading = (RelativeLayout) findViewById(R.id.loading);
        averages = new HashMap<String, Long>();
        allRequests.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loading.setVisibility(View.VISIBLE);
				if(mainLinear!=null && mainLinear.getChildCount()>0){
					mainLinear.removeAllViews();
				}
				
				if(avgs!=null && avgs.getChildCount()>0){
					avgs.removeAllViews();
				}
				averages.clear();
				counter=0;
				if(iterations.getText().toString().trim().length()>0){
					for (int i = 0; i < Integer.parseInt(iterations.getText().toString()); i++) {

						 getCategories();
						 getLoginForm();
					}
					
//					handler.post(r);
					
				}else{
					for (int i = 0; i < ITERATIONS; i++) {

						 getCategories();
						 getLoginForm();	
				
					}
				}
				

				

			}
		});
    }
    
	final Runnable r = new Runnable()
	{
	    public void run() 
	    {
	    	counter++;
	    	Log.e("TRACK", "RUNNABLE: "+counter);
	    
			 if(counter<=Integer.parseInt(iterations.getText().toString())){
				 getCategories();
				 getLoginForm();
				 
			        handler.postDelayed(this, 2000);
			 }else{
				 Log.e("TRACK", "RUNNABLE: REMOVE");
				 handler.removeCallbacks(r);
			 }

	    }
	};


    @Override
    public void onStop() {
        super.onStop();
        context = null;
    }

    /**
     * Example method to getCategories
     */
    public void getCategories() {
        // for(int i=0;i<10;i++){
    	final long startTime= System.nanoTime();
    	Log.d("TRACK", "getCategories");
        sendRequest(new GetCategoriesHelper(), new BaseActivity.IResponseCallback() {
            @Override
            public void onRequestError(Bundle bundle) {
            	Log.d("TRACK", "onRequestError getCategories");
                Log.d(TAG, "Got error");
                Boolean priority = bundle.getBoolean(Constants.BUNDLE_PRIORITY_KEY);
                ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
                if (priority) {
                    Dialog dialog = DialogGenerator.generateDialog(context, errorCode);
                    Button okButton = (Button) dialog.findViewById(R.id.generic_dialog_button);
                    okButton.setOnClickListener(onClick);
                    dialog.show();
                } else {
                    Log.i(TAG, "Error handling does not require to be shown to the user");
                }
                
                loading.setVisibility(View.GONE);
                Toast.makeText(context, "Error on "+bundle.getString(Constants.BUNDLE_URL_KEY), Toast.LENGTH_SHORT).show();
            }

            @Override	
            public void onRequestComplete(Bundle bundle) {
                // TODO Auto-generated method stub
            	Log.d("TRACK", "onRequestComplete getCategories");
                Log.d(TAG, "GetCategoriesHelper : got the response => ");
                responseCategory.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.color_green));
                responseTimeCategory.setText(""+bundle.getLong(Constants.BUNDLE_ELAPSED_REQUEST_TIME)+" ms");
                
                long endTime= System.nanoTime();
                long ellapseTime = (endTime-startTime)/1000000;

                dynamicForm(bundle.getString(Constants.BUNDLE_URL_KEY),ellapseTime,bundle.getLong(Constants.BUNDLE_ELAPSED_REQUEST_TIME));
            }
        });

        // }
    }
    
    

    public void getLoginForm(){
    	final long startTime= System.nanoTime();
    	Log.d("TRACK", "getLoginForm");
    	
        sendRequest(new GetLoginFormHelper(), new BaseActivity.IResponseCallback() {
            @Override
            public void onRequestError(Bundle bundle) {
            	Log.d("TRACK", "onRequestError getLoginForm");
                Log.d(TAG, "Got error");
                Boolean priority = bundle.getBoolean(Constants.BUNDLE_PRIORITY_KEY);
                ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
                if (priority) {
                    Dialog dialog = DialogGenerator.generateDialog(context, errorCode);
                    Button okButton = (Button) dialog.findViewById(R.id.generic_dialog_button);
                    okButton.setOnClickListener(onClick);
                    dialog.show();
                } else {
                    Log.i(TAG, "Error handling does not require to be shown to the user");
                }
                loading.setVisibility(View.GONE);
                Toast.makeText(context, "Error on "+bundle.getString(Constants.BUNDLE_URL_KEY), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                // TODO Auto-generated method stub
            	Log.d("TRACK", "onRequestComplete getLoginForm");
                Log.d(TAG, "GetLoginFormHelper: got the response => " + bundle.describeContents());
                responseLoginForm.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.color_green));
                responseTimeLogin.setText(""+bundle.getLong(Constants.BUNDLE_ELAPSED_REQUEST_TIME)+" ms");
                
                long endTime= System.nanoTime();
                long ellapseTime = (endTime-startTime)/1000000;
                Log.d("ELLAPSE TIME", ":"+ ellapseTime);
                dynamicForm(bundle.getString(Constants.BUNDLE_URL_KEY),ellapseTime,bundle.getLong(Constants.BUNDLE_ELAPSED_REQUEST_TIME));
            }
        });
    }

    public OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
            case R.id.generic_dialog_button:
                getCategories();
                break;
            }

        }
    };

    private TextView generateView(Context context, String label) {
        TextView labelView = new TextView(context);
        labelView.setText(label);

        return labelView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void dynamicForm(String requestName,long apptime,long fwtime){
    	
		

    	
		if(averages.containsKey(requestName)){
			// add value to long of this key
			long previousValue = averages.get(requestName);
			long newValue = previousValue+fwtime;
			averages.put(requestName, newValue);
			
		}else{
			averages.put(requestName, fwtime);
		}


    	LinearLayout cell = new LinearLayout(this);
    	cell.setOrientation(LinearLayout.VERTICAL);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		        LayoutParams.MATCH_PARENT,      
		        LayoutParams.WRAP_CONTENT
		);
	 
		params.setMargins(0, 50, 0, 0);
		
    	
    	
    	 TextView serviceName = new TextView(this);
    	 serviceName.setText("Service: "+requestName+"   ");
    	 serviceName.setId(5);
    	 serviceName.setLayoutParams(new LayoutParams(
    	            LayoutParams.WRAP_CONTENT,
    	            LayoutParams.WRAP_CONTENT));
    	 serviceName.setGravity(Gravity.CENTER_VERTICAL);
    	 serviceName.setTextColor(getResources().getColor(R.color.color_green));
    	 serviceName.setTextSize(22);
    	 cell.addView(serviceName);
    	 
    	 TextView timeEllapse = new TextView(this);
    	 timeEllapse.setText("FW Time: "+fwtime+" ms    "+"APP Time: "+apptime+" ms ");
    	 timeEllapse.setId(6);
    		timeEllapse.setGravity(Gravity.CENTER_VERTICAL);
    	 timeEllapse.setLayoutParams(new LayoutParams(
    	            LayoutParams.WRAP_CONTENT,
    	            LayoutParams.WRAP_CONTENT));
    	    
    	 cell.addView(timeEllapse);

        	mainLinear.addView(cell,params);
        	Log.e("TRACK", "CHILDCOUNT:"+mainLinear.getChildCount());
        	Log.e("TRACK", "ITERATIONS:"+iterations.getText().toString());
        	if(iterations.getText().toString().trim().length()>0){
        		if(mainLinear.getChildCount()==(Integer.parseInt(iterations.getText().toString())*REQUESTS)){
        			dynamicAvg(averages);
            		
            	}
        	}else{
        		if(mainLinear.getChildCount()==(ITERATIONS*REQUESTS)){
        			dynamicAvg(averages);
            		
            	}	
        	}
        	
    	
    }
    
    
    public void dynamicAvg(HashMap<String, Long> mp){
    	
    	loading.setVisibility(View.GONE);
    	
      	for (Map.Entry entry : mp.entrySet()) { 
    		Log.d("HASHMAP", ":"+"key,val: " + entry.getKey() + "," + entry.getValue());


        	avgs= (LinearLayout) findViewById(R.id.avg);


          	LinearLayout cell = new LinearLayout(this);
          	cell.setOrientation(LinearLayout.VERTICAL);
          	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
      		        LayoutParams.MATCH_PARENT,      
      		        LayoutParams.WRAP_CONTENT
      		);
      	 
      		params.setMargins(0, 20, 0, 0);
      		
          	
          	
          	 TextView serviceName = new TextView(this);
          	 serviceName.setText("Service: "+entry.getKey()+"   ");
          	 serviceName.setId(5);
          	 serviceName.setLayoutParams(new LayoutParams(
          	            LayoutParams.WRAP_CONTENT,
          	            LayoutParams.WRAP_CONTENT));
          	 serviceName.setGravity(Gravity.CENTER_VERTICAL);
          	 serviceName.setTextColor(getResources().getColor(R.color.color_green));
          	 serviceName.setTextSize(18);
          	 cell.addView(serviceName);
          	 
          	 TextView timeEllapse = new TextView(this);
          	long avg=0;
         	if(iterations.getText().toString().trim().length()>0){
         		avg =Long.parseLong(entry.getValue().toString())/Integer.parseInt(iterations.getText().toString());
         	}else{
         		avg=Long.parseLong(entry.getValue().toString()) /ITERATIONS;
         	}
          	 
          	 timeEllapse.setText("AVG FW Time: "+avg+" ms ");
          	 timeEllapse.setId(6);
          		timeEllapse.setGravity(Gravity.CENTER_VERTICAL);
          	 timeEllapse.setLayoutParams(new LayoutParams(
          	            LayoutParams.WRAP_CONTENT,
          	            LayoutParams.WRAP_CONTENT));
          	    
          	 cell.addView(timeEllapse);

          	avgs.addView(cell,params);
      	
      	}


          	         	
      	
      }

}
