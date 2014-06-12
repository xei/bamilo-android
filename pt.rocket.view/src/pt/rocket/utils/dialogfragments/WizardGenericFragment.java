package pt.rocket.utils.dialogfragments;

import org.holoeverywhere.drawable.ColorDrawable;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.dialogfragments.WizardPreferences.WizardType;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class WizardGenericFragment extends DialogFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(WizardGenericFragment.class);

    private static WizardGenericFragment sWizardGenericFragment;

    private int mWizardLayout;

    private WizardType mType;

    private int mWizardDismiss;

    
    public static WizardGenericFragment getInstance(WizardType type, int wizardLayout, int wizardDismiss) {
        sWizardGenericFragment = new WizardGenericFragment();
        sWizardGenericFragment.mType = type;
        sWizardGenericFragment.mWizardLayout = wizardLayout;
        sWizardGenericFragment.mWizardDismiss = wizardDismiss;
        return sWizardGenericFragment;
    }

    /**
     * Empty constructor
     */
    public WizardGenericFragment() {
        // ...
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Retain this fragment across configuration changes.
        //setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(mWizardLayout, container);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set dismiss button
        view.findViewById(mWizardDismiss).setOnClickListener(this);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "ON CREATE DIALOG");
        // The content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        // Creating the full screen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);        
        return dialog;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        dismissAllowingStateLoss();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == mWizardDismiss) {
            WizardPreferences.changeState(getActivity(), mType);
            dismiss();
        }
    }

}
