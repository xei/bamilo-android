package com.bamilo.android.appmodule.modernbamilo.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bamilo.android.R;


public class ExpandableXeiTextView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = ExpandableXeiTextView.class.getSimpleName();

    private static final int EXPAND_INDICATOR_IMAGE_BUTTON = 0;

    private static final int EXPAND_INDICATOR_TEXT_VIEW = 1;

    private static final int DEFAULT_TOGGLE_TYPE = EXPAND_INDICATOR_IMAGE_BUTTON;

    /* The default number of lines */
    private static final int MAX_COLLAPSED_LINES = 8;

    /* The default animation duration */
    private static final int DEFAULT_ANIM_DURATION = 300;

    /* The default alpha value when the animation starts */
    private static final float DEFAULT_ANIM_ALPHA_START = 0.7f;

    protected TextView mTv;

    protected View mToggleView; // View to expand/collapse

    private boolean mRelayout;

    private boolean mCollapsed = true; // Show short version as default.

    private int mCollapsedHeight;

    private int mTextHeightWithMaxLines;

    private int mMaxCollapsedLines;

    private int mMarginBetweenTxtAndBottom;

    public ExpandIndicatorController mExpandIndicatorController;

    private int mAnimationDuration;

    private float mAnimAlphaStart;

    private boolean mAnimating;

    @IdRes
    private int mExpandableTextId = R.id.expandable_text;

    @IdRes
    private int mExpandCollapseToggleId = R.id.expand_collapse;

    private boolean mExpandToggleOnTextClick;

    /* Listener for callback */
    private OnExpandStateChangeListener mListener;

    /* For saving collapsed status when used in ListView */
    private SparseBooleanArray mCollapsedStatus;
    private int mPosition;

    public ExpandableXeiTextView(Context context) {
        this(context, null);
    }

    public ExpandableXeiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExpandableXeiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @Override
    public void setOrientation(int orientation){
        if(LinearLayout.HORIZONTAL == orientation){
            throw new IllegalArgumentException("ExpandableTextView only supports Vertical Orientation.");
        }
        super.setOrientation(orientation);
    }

    @Override
    public void onClick(View view) {
        if (mToggleView.getVisibility() != View.VISIBLE) {
            return;
        }

        mCollapsed = !mCollapsed;
        mExpandIndicatorController.changeState(mCollapsed);

        if (mCollapsedStatus != null) {
            mCollapsedStatus.put(mPosition, mCollapsed);
        }

        // mark that the animation is in progress
        mAnimating = true;

        Animation animation;
        if (mCollapsed) {
            animation = new ExpandCollapseAnimation(this, getHeight(), mCollapsedHeight);
        } else {
            animation = new ExpandCollapseAnimation(this, getHeight(), getHeight() +
                    mTextHeightWithMaxLines - mTv.getHeight());
        }

        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                applyAlphaAnimation(mTv, mAnimAlphaStart);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // clear animation here to avoid repeated applyTransformation() calls
                clearAnimation();
                // clear the animation flag
                mAnimating = false;

                // notify the listener
                if (mListener != null) {
                    mListener.onExpandStateChanged(mTv, !mCollapsed);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        clearAnimation();
        startAnimation(animation);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // while an animation is in progress, intercept all the touch events to children to
        // prevent extra clicks during the animation
        return mAnimating;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If no change, measure and return
        if (!mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;

        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mToggleView.setVisibility(View.GONE);
        mTv.setMaxLines(Integer.MAX_VALUE);

        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // If the text fits in collapsed mode, we are done.
        if (mTv.getLineCount() <= mMaxCollapsedLines) {
            return;
        }

        // Saves the text height w/ max lines
        mTextHeightWithMaxLines = getRealTextViewHeight(mTv);

        // Doesn't fit in collapsed mode. Collapse text view as needed. Show
        // button.
        if (mCollapsed) {
            mTv.setMaxLines(mMaxCollapsedLines);
        }
        mToggleView.setVisibility(View.VISIBLE);

        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mTv.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTxtAndBottom = getHeight() - mTv.getHeight();
                }
            });
            // Saves the collapsed height of this ViewGroup
            mCollapsedHeight = getMeasuredHeight();
        }
    }

    public void setText(@Nullable CharSequence text) {
        mRelayout = true;
        mTv.setText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        clearAnimation();
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }

    public void setText(@Nullable CharSequence text, @NonNull SparseBooleanArray collapsedStatus, int position) {
        mCollapsedStatus = collapsedStatus;
        mPosition = position;
        boolean isCollapsed = collapsedStatus.get(position, true);
        clearAnimation();
        mCollapsed = isCollapsed;
        mExpandIndicatorController.changeState(mCollapsed);
        setText(text);
    }

    @Nullable
    public CharSequence getText() {
        if (mTv == null) {
            return "";
        }
        return mTv.getText();
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_maxCollapsedLines, MAX_COLLAPSED_LINES);
        mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_animDuration, DEFAULT_ANIM_DURATION);
        mAnimAlphaStart = typedArray.getFloat(R.styleable.ExpandableTextView_animAlphaStart, DEFAULT_ANIM_ALPHA_START);
        mExpandableTextId = typedArray.getResourceId(R.styleable.ExpandableTextView_expandableTextId, R.id.expandable_text);
        mExpandCollapseToggleId = typedArray.getResourceId(R.styleable.ExpandableTextView_expandCollapseToggleId, R.id.expand_collapse);
        mExpandToggleOnTextClick = typedArray.getBoolean(R.styleable.ExpandableTextView_expandToggleOnTextClick, true);

        mExpandIndicatorController = setupExpandToggleController(getContext(), typedArray);

        typedArray.recycle();

        // enforces vertical orientation
        setOrientation(LinearLayout.VERTICAL);

        // default visibility is gone
        setVisibility(GONE);
    }

    private void findViews() {
        mTv = findViewById(mExpandableTextId);
        if (mExpandToggleOnTextClick) {
            mTv.setOnClickListener(this);
        } else {
            mTv.setOnClickListener(null);
        }
        mToggleView = findViewById(mExpandCollapseToggleId);
        mExpandIndicatorController.setView(mToggleView);
        mExpandIndicatorController.changeState(mCollapsed);
        mToggleView.setOnClickListener(this);
    }

    private static void applyAlphaAnimation(View view, float alpha) {
        view.setAlpha(alpha);
    }

    private static int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    private static ExpandIndicatorController setupExpandToggleController(@NonNull Context context, TypedArray typedArray) {
        final int expandToggleType = typedArray.getInt(R.styleable.ExpandableTextView_expandToggleType, DEFAULT_TOGGLE_TYPE);
        final ExpandIndicatorController expandIndicatorController;
        String expandText = typedArray.getString(R.styleable.ExpandableTextView_expandIndicator);
        String collapseText = typedArray.getString(R.styleable.ExpandableTextView_collapseIndicator);
        expandIndicatorController = new TextViewExpandController(expandText, collapseText);

        return expandIndicatorController;
    }

    class ExpandCollapseAnimation extends Animation {
        private final View mTargetView;
        private final int mStartHeight;
        private final int mEndHeight;

        public ExpandCollapseAnimation(View view, int startHeight, int endHeight) {
            mTargetView = view;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(mAnimationDuration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final int newHeight = (int)((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTv.setMaxHeight(newHeight - mMarginBetweenTxtAndBottom);
            if (Float.compare(mAnimAlphaStart, 1.0f) != 0) {
                applyAlphaAnimation(mTv, mAnimAlphaStart + interpolatedTime * (1.0f - mAnimAlphaStart));
            }
            mTargetView.getLayoutParams().height = newHeight;
            mTargetView.requestLayout();
        }

        @Override
        public void initialize( int width, int height, int parentWidth, int parentHeight ) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds( ) {
            return true;
        }
    }

    public interface OnExpandStateChangeListener {
        /**
         * Called when the expand/collapse animation has been finished
         *
         * @param textView - TextView being expanded/collapsed
         * @param isExpanded - true if the TextView has been expanded
         */
        void onExpandStateChanged(TextView textView, boolean isExpanded);
    }

    public interface ExpandIndicatorController {
        void changeState(boolean collapsed);

        void setView(View toggleView);

        TextView getIndicator();
        void hideIndicator();
    }

    static class TextViewExpandController implements ExpandIndicatorController {

        private final String mExpandText;
        private final String mCollapseText;

        private TextView mTextView;

        TextViewExpandController(String expandText, String collapseText) {
            mExpandText = expandText;
            mCollapseText = collapseText;
        }

        @Override
        public void changeState(boolean collapsed) {
            mTextView.setText(collapsed ? mExpandText : mCollapseText);
        }

        @Override
        public void setView(View toggleView) {
            mTextView = (TextView) toggleView;
        }

        @Override
        public TextView getIndicator() {
            return mTextView;
        }

        @Override
        public void hideIndicator() {
            mTextView.setVisibility(GONE);
        }

    }
}