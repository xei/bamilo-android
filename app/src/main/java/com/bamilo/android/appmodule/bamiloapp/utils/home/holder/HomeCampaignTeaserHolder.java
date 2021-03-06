package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import android.widget.TextView;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.home.object.BaseTeaserObject;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.DateTimeUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.R;

import java.util.ArrayList;


/**
 * Campaign teaser
 */
public class HomeCampaignTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    private static final int MAIN_POSITION = 0;

    public View container;
    public ImageView image;
    public View progress;
    public TextView title;
    public TextView timer;
    public TextView sub;
    public TextView more;
    private ArrayList<BaseTeaserObject> campaigns;
    private CountDownTimer counter;

    /**
     * Constructor
     */
    public HomeCampaignTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        container = view.findViewById(R.id.home_teaser_campaign_container);
        image = (ImageView) view.findViewById(R.id.home_teaser_item_image);
        progress = view.findViewById(R.id.home_teaser_item_progress);
        title = (TextView) view.findViewById(R.id.home_teaser_campaign_title);
        timer = (TextView) view.findViewById(R.id.home_teaser_campaign_timer);
        sub = (TextView) view.findViewById(R.id.home_teaser_campaign_subtitle);
        more = (TextView) view.findViewById(R.id.home_teaser_campaign_more);

        timer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        // Save campaigns
        campaigns = group.getData();
        // Load first campaign
        loadMainCampaign();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        loadMainCampaign();
    }

    @Override
    public void onDestroy() {
        if(counter != null){
            counter.cancel();
        }
    }

    /**
     * Load next main campaign
     */
    private void loadMainCampaign() {
        // Case empty
        if (!CollectionUtils.isNotEmpty(campaigns)) {
            itemView.setVisibility(View.GONE);
        }
        // Case campaign with timer but is invalid
        else if (campaigns.get(MAIN_POSITION).hasTimer() && !campaigns.get(MAIN_POSITION).hasValidRealTimer()) {
            popOutDatedMainCampaign();
        }
        // Case valid campaign
        else {
            setMainCampaignLayout(campaigns.get(MAIN_POSITION));
        }
    }

    /**
     * Pop the outdated main campaign
     */
    private void popOutDatedMainCampaign() {
        // Remove main campaign
        if (CollectionUtils.isNotEmpty(campaigns)) {
            campaigns.remove(MAIN_POSITION);
        }
        // Case not show new main campaign
        if (CollectionUtils.isNotEmpty(campaigns)) {
            loadMainCampaign();
        }
        // Case empty remove view
        else {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationRepeat(Animation animation) { }
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    itemView.setVisibility(View.GONE);
                }
            });
            itemView.clearAnimation();
            itemView.startAnimation(animation);
        }
    }

    /**
     * Set the main campaign layout
     * @param campaign The main campaign
     */
    private void setMainCampaignLayout(BaseTeaserObject campaign) {
        // More button
        setMoreButton(campaign);
        // Set Image
        ImageManager.getInstance().loadImage(campaign.getImage(), image, progress, R.drawable.no_image_large, false);
        // Set timer
        title.setText(campaign.getTitle());
        // Set sub
        sub.setText(campaign.getSubTitle());
        // Set countdown
        setCountDown(campaign);
        // Set click listener
        TeaserViewFactory.setClickableView(container, campaign, listener, TeaserViewFactory.DEFAULT_POSITION);
    }

    /**
     * Set the more button
     */
    private void setMoreButton(BaseTeaserObject campaign) {
        // Has more campaigns
        if(campaigns.size() > 1) {
            TeaserViewFactory.setClickableView(more, campaign, listener, TeaserViewFactory.DEFAULT_POSITION);
        } else {
            more.setOnClickListener(null);
            more.setVisibility(View.GONE);
        }
    }

    /**
     * Set the count down
     */
    private void setCountDown(BaseTeaserObject campaign) {
        // Validate campaign timer
        if(campaign.hasValidRealTimer()) {
            startCampaignTimer(campaign.getRealTimer());
        } else {
            timer.setVisibility(View.GONE);
        }
    }

    /**
     * Set the click listener
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (counter != null) {
                counter.cancel();
                counter = null;
            }
            mParentClickListener.onClick(v);
        }
    };

    /**
     * Start the campaign timer
     * @param timeInMilliSeconds The remaining time in ms
     */
    private void startCampaignTimer(long timeInMilliSeconds) {
        counter = new CountDownTimer(timeInMilliSeconds, DateTimeUtils.UNIT_SEC_TO_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(changeNumberChar(DateTimeUtils.getTimeFromMillis(millisUntilFinished).toString()));
            }

            @Override
            public void onFinish() {
                popOutDatedMainCampaign();
            }
        };
        counter.start();
    }
    public String changeNumberChar(String enNumber)
    {
        return enNumber.replaceAll("0","۰").replaceAll("1","۱").replaceAll("2","۲").
                replaceAll("3","۳").replaceAll("4","۴").replaceAll("5","۵").
                replaceAll("6","۶").replaceAll("7","۷").replaceAll("8","۸").replaceAll("9","۹");
    }
}