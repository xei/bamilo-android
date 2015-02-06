/**
 * 
 */
package com.mobile.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.framework.objects.RatingStar;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ReviewFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ReviewFragment.class);

    private static ReviewFragment reviewFragment;
    
    private final int RATING_TYPE_BY_LINE = 3;
    
    private LayoutInflater inflater;

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
                KeyboardState.NO_ADJUST_CONTENT);
        
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
        inflater = LayoutInflater.from(getActivity());
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
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
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

        TextView comment = (TextView) getView().findViewById(R.id.review_comment);
        comment.setText(b.getString(ConstantsIntentExtra.REVIEW_COMMENT));

        TextView userName = (TextView) getView().findViewById(R.id.review_username);
        userName.setText(b.getString(ConstantsIntentExtra.REVIEW_NAME) + ",");

        TextView date = (TextView) getView().findViewById(R.id.review_date);
        date.setText(b.getString(ConstantsIntentExtra.REVIEW_DATE));
        
        TextView title = (TextView) getView().findViewById(R.id.title_review);
        title.setText(b.getString(ConstantsIntentExtra.REVIEW_TITLE));
        
        LinearLayout ratingsContainer = (LinearLayout) getView().findViewById(R.id.review_ratings_container);
        
        
        if(ratingsContainer.getChildCount() > 0)
            ratingsContainer.removeAllViews();
        
        
        ArrayList<RatingStar> ratings = b.getParcelableArrayList(ConstantsIntentExtra.REVIEW_RATING);
        
        //used only on seller Review
        int ratingValue = b.getInt(ConstantsIntentExtra.REVIEW_RATING);
        
        insertRatingTypes(ratings,ratingsContainer,false, ratingValue);            

    }
    
    /**
     * insert rate types on the review
     * @param ratingOptionArray
     * @param parent
     */
    private void insertRatingTypes(ArrayList<RatingStar> ratingOptionArray, LinearLayout parent, boolean isBigStar,int ratingValue){
        
        int starsLayout = R.layout.reviews_fragment_rating_samlltype_item;
        
        if(isBigStar)
            starsLayout = R.layout.reviews_fragment_rating_bigtype_item;
        
        if(ratingOptionArray != null && ratingOptionArray.size() > 0){
            
            // calculate how many lines of rate types the review will have, supossing 3 types for line;
            int rateCount = ratingOptionArray.size();
            int rest = rateCount % RATING_TYPE_BY_LINE;
            int numLines =(int) Math.ceil(rateCount / RATING_TYPE_BY_LINE);
            if(rest >= 1)
                numLines = numLines + rest;
            
            int countType = 0;
            

            
            
            for (int i = 0; i < numLines; i++) {
                
                LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,RATING_TYPE_BY_LINE);
                
                typeLine.setOrientation(LinearLayout.HORIZONTAL);
                //#RTL
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if(getResources().getBoolean(R.bool.is_bamilo_specific) && currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
                    typeLine.setLayoutDirection(LayoutDirection.LOCALE);
                }
                typeLine.setLayoutParams(params);
                parent.addView(typeLine);
                
                for (int j = countType; j < countType+RATING_TYPE_BY_LINE; j++) {

                    if(j < ratingOptionArray.size()){
                        final View rateTypeView = inflater.inflate(starsLayout, null, false);
                        
                        rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
                        
                        final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
                        final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);
                          
                        userRating.setRating((float) ratingOptionArray.get(j).getRating());
                        ratingTitle.setText(ratingOptionArray.get(j).getTitle());
                       
                        typeLine.addView(rateTypeView);
                    }

                }
                countType = countType + RATING_TYPE_BY_LINE;
            }
            

        }  else {
            //if rating Options == null then its a seller review
            
            if(parent.getChildCount() > 0){
                parent.removeAllViews();
            }
            
            LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,RATING_TYPE_BY_LINE);
            
            typeLine.setOrientation(LinearLayout.HORIZONTAL);
            
            View rateTypeView = inflater.inflate(starsLayout, null, false);
            
            rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
            
            final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
            final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);
              
            userRating.setRating(ratingValue);
            ratingTitle.setVisibility(View.GONE);
           
            typeLine.addView(rateTypeView);
            parent.addView(typeLine);
        }
    }
}
