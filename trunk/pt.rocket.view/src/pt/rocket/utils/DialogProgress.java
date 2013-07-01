package pt.rocket.utils;

import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.view.R;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;

public class DialogProgress extends Dialog {
    
    private LoadingBarView loadingBarView;

    public DialogProgress(final Activity activity) {
        super(activity, R.style.Theme_Lazada_Dialog_Progress);
        setContentView(R.layout.dialog_progress);
        loadingBarView = (LoadingBarView) findViewById(R.id.loading_bar_view);
        findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Dialog#show()
     */
    @Override
    public void show() {
        super.show();
        loadingBarView.startRendering();
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
