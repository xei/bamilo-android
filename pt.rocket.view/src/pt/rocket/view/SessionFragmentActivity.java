/**
 * 
 */
package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.ForgotPasswordFragment;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.LoginFragment;
import pt.rocket.view.fragments.RegisterFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionFragmentActivity extends BaseActivity {

    private static final String TAG = LogTagHelper.create(SessionFragmentActivity.class);

    private Fragment fragment;

    /**
     * 
     */
    public SessionFragmentActivity() {
        super(NavigationAction.LoginOut,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.login_title, R.layout.session_fragments);

    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // setContentView(R.layout.main_menu_frame);
        // Add the first fragment
        onSwitchFragment(FragmentType.LOGIN, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        //System.gc();
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onSwitchFragment(pt.rocket.view.fragments.FragmentType, java.lang.Boolean)
     */
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        Log.i(TAG, "ON SWITCH FRAGMENT");
        switch (type) {
        case LOGIN:
            fragment = LoginFragment.getInstance();
            AnalyticsGoogle.get().trackPage(R.string.glogin);
            break;
        case REGISTER:
            fragment = RegisterFragment.getInstance();
            AnalyticsGoogle.get().trackPage( R.string.gregister);
            break;
        case FORGOT_PASSWORD:
            fragment = ForgotPasswordFragment.getInstance();
            break;
        default:
            break;
        }
        fragmentManagerTransition(R.id.content_frame, fragment, addToBackStack, true);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Log.i(TAG, "ON BACK PRESSED");
        fragmentManagerBackPressed();
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent)
     */
    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        Log.d(TAG, "ON SUCCESS EVENT");
        return false;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        Log.d(TAG, "ON ERROR EVENT");
        return false;
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
//     * android.content.Intent)
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == maskRequestCodeId(R.id.request_register)) {
//            if (resultCode == RESULT_OK) {
//                requestStore(data.getExtras());
//                setResult(RESULT_OK);
//                finish();
//            }
//        }
//    }
//
//    private void requestStore(Bundle bundle) {
//        Log.d(TAG, "requestLogin: trigger LogInEvent for store only");
//        if (dynamicForm == null) {
//            return;
//        }
//
//        ContentValues values = new ContentValues();
//        for (DynamicFormItem item : dynamicForm) {
//            String value = bundle.getString(item.getKey());
//            values.put(item.getName(), value);
//        }
//        values.put(CustomerAccountService.INTERNAL_AUTOLOGIN_FLAG, true);
//
//        EventManager.getSingleton().triggerRequestEvent(
//                new StoreEvent(EventType.STORE_LOGIN, values));
//    }

}
