package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.DividerItemDecoration;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.about.AboutItem;
import com.mobile.newFramework.objects.catalog.ITargeting;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author shahrooz
 * 
 */
public class MyNewAccountFragment extends BaseFragment implements  IResponseCallback{

    private static final String TAG = MyNewAccountFragment.class.getSimpleName();
    private static final String TARGETS_TAG = MobileAbout.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private static List<AboutItem> movieList = new ArrayList<>();
    private static MoviesAdapter mAdapter;
    private ArrayList<TargetHelper> targets;

    public final static int POSITION_USER_DATA = 0;

    public final static int POSITION_MY_ADDRESSES = 1;

    public final static int NOTIFICATION_STATUS = 2;

    public final static int POSITION_EMAIL = 3;

    public final static int EMAIL_NOTIFICATION_STATUS = 4;

    public final static int POSITION_SHARE_APP = 5;

    public final static int POSITION_RATE_APP =6;


    /**
     * Empty constructor
     */
    public MyNewAccountFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_NEW_ACCOUNT,
                R.layout.my_new_account_fragment,
                R.string.new_account_name,
                NO_ADJUST_CONTENT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.my_new_account_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE");
        outState.putParcelableArrayList(TARGETS_TAG, targets);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        if(savedInstanceState != null){
            targets = savedInstanceState.getParcelableArrayList(TARGETS_TAG);
        } else {
            setTargets(CountryPersistentConfigs.getMoreInfo(this.getContext()));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       // super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter( prepareMovieData(getBaseActivity()));
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    public RecyclerView.Adapter prepareMovieData(BaseActivity baseActivity) {
        movieList.clear();
            AboutItem movie = new AboutItem(getResources().getString(R.string.myaccount_userdata).toString(),R.drawable.user_information_icons,"","");
            movieList.add(movie);

        movie = new AboutItem(getResources().getString(R.string.my_addresses).toString(),R.drawable.my_address_icon,"","");
        movieList.add(movie);

        movie = new AboutItem(getResources().getString(R.string.notifications).toString(),R.drawable.announcements_icon,"","");
        movieList.add(movie);

        movie = new AboutItem(getResources().getString(R.string.newsletter_label).toString(),R.drawable.newsletter_icons,"","");
        movieList.add(movie);

            movie = new AboutItem(getResources().getString(R.string.app_version).toString(),R.drawable.app_ver_icons,"2.3.2","بروز شده");
            movieList.add(movie);

            movie = new AboutItem(getResources().getString(R.string.share_the_app).toString(),R.drawable.share_icons,"","");
            movieList.add(movie);

            movie = new AboutItem(getResources().getString(R.string.rate_the_app).toString(),R.drawable.rate_icons,"","");
            movieList.add(movie);

            mAdapter = new MoviesAdapter(movieList, baseActivity);
            mAdapter.notifyDataSetChanged();

        return mAdapter;
        }

    private void setTargets(@Nullable List<TargetHelper> targetHelpers){
        if(CollectionUtils.isNotEmpty(targetHelpers)) {
            this.targets = new ArrayList<>();
            for (TargetHelper targetHelper : targetHelpers) {
                if (targetHelper.getTargetType() == ITargeting.TargetType.SHOP) {
                    this.targets.add(targetHelper);
                }
            }
        }
    }
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Case GET_FAQ_TERMS
        if (eventType == EventType.GET_FAQ_TERMS) {
            setTargets((MobileAbout) baseResponse.getMetadata().getData());
           // showMoreInfo();
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return ;
        }
        // Case GET_FAQ_TERMS
        if (eventType == EventType.GET_FAQ_TERMS) {
           // showMoreInfo();
            showFragmentContentContainer();
        }
    }
}

    class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.AboutSimpleViewHolder> {

        private List<AboutItem> moviesList;

        private Context mContext;

        private int[] mCheckBoxes;

        private View.OnClickListener onClick;

        public static final String NOTIFICATION_CHECKBOX_TAG = "checkbox_notification_tag";

        public class AboutSimpleViewHolder extends RecyclerView.ViewHolder {
            public TextView title , app_version , app_version_status;
            public LinearLayout root;
            public ImageView image;

            public AboutSimpleViewHolder(View view) {
                super(view);
                root = (LinearLayout) view.findViewById(R.id.root);
                title = (TextView) view.findViewById(R.id.about_title);
                image = (ImageView) view.findViewById(R.id.about_image);
            }
        }

        public class AboutNotificationViewHolder extends AboutSimpleViewHolder {
            public AboutNotificationViewHolder(View view) {
                super(view);
                root = (LinearLayout) view.findViewById(R.id.root);
                title = (TextView) view.findViewById(R.id.about_title);
                image = (ImageView) view.findViewById(R.id.about_image);
                app_version = (TextView) view.findViewById(R.id.app_version);
                app_version_status = (TextView) view.findViewById(R.id.app_version_status);

            }
        }

        public class AboutAppVersionViewHolder extends AboutSimpleViewHolder {
            public AboutAppVersionViewHolder(View view) {
                super(view);
                root = (LinearLayout) view.findViewById(R.id.root);
                title = (TextView) view.findViewById(R.id.about_title);
                image = (ImageView) view.findViewById(R.id.about_image);
            }
        }


        public MoviesAdapter(List<AboutItem> moviesList, final BaseActivity baseActivity) {
            this.moviesList = moviesList;
            mContext = baseActivity;

            onClick = new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    onItemClick((int)v.getTag(),baseActivity);
                }
            };
        }

        @Override
        public AboutSimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = null;
            AboutSimpleViewHolder viewHolder = null;
            switch (viewType) {
                case 0:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.about_simple_list_row, parent, false);
                    viewHolder = new AboutSimpleViewHolder(itemView);
                    break;
                case 1:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.about_notification_list_row, parent, false);
                    viewHolder = new AboutAppVersionViewHolder(itemView);
                break;
                case 2:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.about_app_version_list_row, parent, false);
                    viewHolder = new AboutNotificationViewHolder(itemView);
                   break;
            }
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(final AboutSimpleViewHolder holder, final int position) {
            if (holder instanceof AboutSimpleViewHolder)
            {
                AboutItem about = moviesList.get(position);
                holder.title.setText(about.getTitle());
                holder.image.setImageResource(about.getImage());
                holder.root.setTag(position);
                holder.root.setOnClickListener(onClick);
            }
            else
             if (holder instanceof AboutNotificationViewHolder)
             {
                 AboutItem about = moviesList.get(position);
                 holder.title.setText(about.getTitle());
                 holder.image.setImageResource(about.getImage());
                 holder.app_version.setText(about.getAppVersion());
                 holder.app_version_status.setText(about.getStatus());
                 holder.root.setTag(position);
                 holder.root.setOnClickListener(onClick);
             }
             else
             {
                 AboutItem about = moviesList.get(position);
                 holder.title.setText(about.getTitle());
                 holder.image.setImageResource(about.getImage());
                 holder.root.setTag(position);
                 holder.root.setOnClickListener(onClick);
             }
        }

        public void  onItemClick(int position, BaseActivity baseActivity) {
            switch (position) {
                case MyNewAccountFragment.POSITION_USER_DATA:
                    processOnClickUserData(baseActivity);
                    break;
                case MyNewAccountFragment.POSITION_MY_ADDRESSES:
                    processOnClickMyAddresses(baseActivity);
                    break;
                case MyNewAccountFragment.POSITION_EMAIL:
                processOnClickEmailNotification(baseActivity);
                    break;
                case MyNewAccountFragment.NOTIFICATION_STATUS:
                    final CheckBox optionsCheckbox = (CheckBox) baseActivity.findViewById(R.id.notification_status);
                        optionsCheckbox.setTag(NOTIFICATION_CHECKBOX_TAG);
                        optionsCheckbox.setVisibility(View.VISIBLE);
                        optionsCheckbox.post(new Runnable() {
                            @Override
                            public void run() {
                                optionsCheckbox.setChecked(Ad4PushTracker.getActiveAd4Push(mContext));
                            }
                        });
                        optionsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Ad4PushTracker.setActiveAd4Push(mContext, isChecked);
                            }
                        });
                    optionsCheckbox.setChecked(!optionsCheckbox.isChecked());
                    break;
                case MyNewAccountFragment.EMAIL_NOTIFICATION_STATUS:
              /*  processOnClickEmailNotification(baseActivity);*/
                    break;
                case MyNewAccountFragment.POSITION_SHARE_APP:
                    String text;
                    String preText = mContext.getString(R.string.install_android, mContext.getString(R.string.app_name_placeholder));
                    if(ShopSelector.isRtl()){
                        text = mContext.getString(R.string.share_app_link) + " " + preText;
                    } else {
                        text = preText + " " + mContext.getString(R.string.share_app_link);
                    }
                    ActivitiesWorkFlow.startActivitySendString(baseActivity, mContext.getString(R.string.share_the_app), text);
                    AnalyticsGoogle.get().trackShareApp(TrackingEvent.SHARE_APP, (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getId() + "" : "");
                    break;
                case MyNewAccountFragment.POSITION_RATE_APP:
                    goToMarketPage(baseActivity);
                    break;
                default:
                    break;
            }

        }
        private void processOnClickEmailNotification(BaseActivity baseActivity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.EMAIL_NOTIFICATION);
            baseActivity.onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        }

        private void processOnClickUserData(BaseActivity baseActivity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_USER_DATA);
            baseActivity.onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        }

        private void processOnClickMyAddresses(BaseActivity baseActivity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, FragmentType.MY_ACCOUNT);
            bundle.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, true);
            baseActivity.onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        private void goToMarketPage(BaseActivity baseActivity) {
            ActivitiesWorkFlow.startMarketActivity(baseActivity, mContext.getString(R.string.id_market));
        }
        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position==2){
                return 1;
            }
            if (position==4){
                return 2;
            }
            return 0;
        }
    }









