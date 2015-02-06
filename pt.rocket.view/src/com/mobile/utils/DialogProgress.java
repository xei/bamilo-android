package com.mobile.utils;
//package com.mobile.utils;
//
//import com.mobile.framework.utils.LoadingBarView;
//import com.mobile.view.R;
//import android.app.Activity;
//import android.app.Dialog;
//import android.graphics.drawable.ColorDrawable;
//import android.view.View;
//
//public class DialogProgress extends Dialog {
//    
//    private LoadingBarView loadingBarView;
//
//    public DialogProgress(final Activity activity) {
//        super(activity, R.style.Theme_Jumia_Dialog_Progress);
//        setContentView(R.layout.dialog_progress);
//        loadingBarView = (LoadingBarView) findViewById(R.id.fragment_root_loading_gif);
//        findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
////     * @see android.app.Dialog#show()
//     */
//    @Override
//    public void show() {
//        super.show();
//        loadingBarView.startRendering();
//    }
//    
//    /* (non-Javadoc)
//     * @see android.app.Dialog#dismiss()
//     */
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        loadingBarView.stopRendering();
//    }
//}
