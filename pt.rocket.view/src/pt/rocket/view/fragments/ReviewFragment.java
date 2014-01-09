/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class), EnumSet.noneOf(MyMenuItem.class), NavigationAction.Products,  R.string.review);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.reviews, container, false);
        return view;
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
