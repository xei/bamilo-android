package pt.rocket.utils.dialogfragments;

import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author sergiopereira
 *
 */
public class DialogProgressFragment extends DialogFragment {
    
    private final static String TAG = LogTagHelper.create( DialogProgressFragment.class );
    
    private LoadingBarView loadingBarView;

    
    /**
     * Empty constructor
     */
    public DialogProgressFragment() {}
    
    /**
     * 
     * @return
     */
    public static DialogProgressFragment newInstance() {
        Log.d(TAG, "NEW INSTANCE");
        DialogProgressFragment dialogProgressFragment = new DialogProgressFragment();
        return dialogProgressFragment;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.Theme_Jumia_Dialog_Progress, R.style.Theme_Jumia_Dialog_Progress);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_progress, container);
        
        loadingBarView = (LoadingBarView) view.findViewById(R.id.loading_bar_view);
        view.findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);

        loadingBarView.startRendering();
        
        return view;
    }
    
    /* (non-Javadoc)
     * @see android.app.Dialog#dismiss()
     */
    @Override
    public void dismiss() {
        super.dismiss();
        loadingBarView.stopRendering();
    }
}
