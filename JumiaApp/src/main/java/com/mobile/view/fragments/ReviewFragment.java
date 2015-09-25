/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.product.RatingStar;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * @author sergiopereira
 * 
 */
public class ReviewFragment extends BaseFragment {

    private static final String TAG = ReviewFragment.class.getSimpleName();
    
    private static final int RATING_TYPE_BY_LINE = 3;
    
    private LayoutInflater inflater;

    /**
     * Get instance
     * 
     * @return
     */
    public static ReviewFragment getInstance(Bundle bundle) {
        ReviewFragment fragment = new ReviewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ReviewFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.review_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
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
        Print.i(TAG, "ON VIEW CREATED");
        inflater = LayoutInflater.from(getActivity());
        setAppContentLayout(view);

        //clean form from write Review form
        JumiaApplication.cleanRatingReviewValues();
        JumiaApplication.cleanSellerReviewValues();
        JumiaApplication.INSTANCE.setFormReviewValues(null);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY");
    }
    
    
    /**
     * Sets view layout
     */
    public void setAppContentLayout(View view) {

        Bundle b = getArguments();

        TextView comment = (TextView) view.findViewById(R.id.review_comment);
        comment.setText(b.getString(ConstantsIntentExtra.REVIEW_COMMENT));

        TextView userName = (TextView) view.findViewById(R.id.review_username);
        userName.setText(b.getString(ConstantsIntentExtra.REVIEW_NAME) + ",");

        TextView date = (TextView) view.findViewById(R.id.review_date);
        date.setText(b.getString(ConstantsIntentExtra.REVIEW_DATE));
        
        TextView title = (TextView) view.findViewById(R.id.title_review);
        title.setText(b.getString(ConstantsIntentExtra.REVIEW_TITLE));
        
        LinearLayout ratingsContainer = (LinearLayout) view.findViewById(R.id.review_ratings_container);
        
        
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
                if(ShopSelector.isRtl() && currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
                    typeLine.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
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
