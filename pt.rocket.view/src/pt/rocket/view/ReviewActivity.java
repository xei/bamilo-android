package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * <p>
 * This class shows the user a detailed description of a review,allowing him to see the full review
 * statement of a submitted review.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential. Written by josedourado, 19/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author josedourado
 * 
 * @date 19/06/2012
 * 
 * @description
 * 
 */

public class ReviewActivity extends MyActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppContentLayout();
    }

    /**
	 * 
	 */
    public ReviewActivity() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.review, R.layout.reviews);
    }

    /**
     * Sets view layout
     */
    public void setAppContentLayout() {
        Log.i("review", "code1review");
        if (getIntent().getExtras() == null)
            finish();

        Bundle b = getIntent().getExtras();
        TextView title = (TextView) findViewById(R.id.review_title);
        title.setText(b.getString(ConstantsIntentExtra.REVIEW_TITLE));

        TextView comment = (TextView) findViewById(R.id.review_comment);
        comment.setText(b.getString(ConstantsIntentExtra.REVIEW_COMMENT));

        RatingBar ratingBar = (RatingBar) findViewById(R.id.review_rating);
        float rating = Double.valueOf(b.getDouble(ConstantsIntentExtra.REVIEW_RATING)).floatValue();
        ratingBar.setRating(rating);

        TextView userName = (TextView) findViewById(R.id.review_username);
        userName.setText(b.getString(ConstantsIntentExtra.REVIEW_NAME) + ",");

        TextView date = (TextView) findViewById(R.id.review_date);
        date.setText(b.getString(ConstantsIntentExtra.REVIEW_DATE));

    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return true;
    }

}
