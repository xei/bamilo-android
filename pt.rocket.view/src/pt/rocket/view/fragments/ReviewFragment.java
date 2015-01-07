/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ReviewFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ReviewFragment.class);

    private static ReviewFragment reviewFragment;

    /**
     * Get instance
     * 
     * @return
     */
    public static ReviewFragment getInstance() {
        if (reviewFragment == null)
            reviewFragment = new ReviewFragment();
        return reviewFragment;
    }

    /**
     * Empty constructor
     */
    public ReviewFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.review_fragment,
                0,
                KeyboardState.ADJUST_CONTENT);
        
        this.setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        setAppContentLayout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
    }
    
    
    /**
     * Sets view layout
     */
    public void setAppContentLayout() {

        Bundle b = getArguments();
        TextView title = (TextView) getView().findViewById(R.id.review_title);
        title.setText(b.getString(ConstantsIntentExtra.REVIEW_TITLE));

        TextView comment = (TextView) getView().findViewById(R.id.review_comment);
        comment.setText(b.getString(ConstantsIntentExtra.REVIEW_COMMENT));

        RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.review_rating);
        float rating = Double.valueOf(b.getDouble(ConstantsIntentExtra.REVIEW_RATING)).floatValue();
        ratingBar.setRating(rating);

        TextView userName = (TextView) getView().findViewById(R.id.review_username);
        userName.setText(b.getString(ConstantsIntentExtra.REVIEW_NAME) + ",");

        TextView date = (TextView) getView().findViewById(R.id.review_date);
        date.setText(b.getString(ConstantsIntentExtra.REVIEW_DATE));

    }
}
