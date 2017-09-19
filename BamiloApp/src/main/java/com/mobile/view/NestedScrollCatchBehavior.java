package com.mobile.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class NestedScrollCatchBehavior<V extends View> extends VerticalScrollingBehavior<V> {
    private NestedScrollListener nestedScrollListener;

    public NestedScrollCatchBehavior() {
        super();
    }

    public NestedScrollCatchBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static <V extends View> NestedScrollCatchBehavior<V> from(@NonNull V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof NestedScrollCatchBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with NestedScrollCatchBehavior");
        }
        return (NestedScrollCatchBehavior<V>) behavior;
    }

    @Override
    public void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, V child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll) {
    }

    @Override
    public void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection) {
        if (nestedScrollListener != null) {
            nestedScrollListener.onDirectionNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, scrollDirection);
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (nestedScrollListener != null) {
            nestedScrollListener.onDirectionNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, getScrollDirection());
        }
    }

    @Override
    protected boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection) {
        if (nestedScrollListener != null) {
            nestedScrollListener.onDirectionNestedFling(coordinatorLayout, child, target, velocityX, velocityY, scrollDirection);
        }
        return true;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
        if (nestedScrollListener != null) {
            nestedScrollListener.onStopNestedDirectionScroll(coordinatorLayout, child, target, getScrollDirection());
        }
    }

    public NestedScrollListener getNestedScrollListener() {
        return nestedScrollListener;
    }

    public void setNestedScrollListener(NestedScrollListener nestedScrollListener) {
        this.nestedScrollListener = nestedScrollListener;
    }

    public interface NestedScrollListener {
        void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection);

        void onDirectionNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int scrollDirection);

        void onDirectionNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection);

        void onStopNestedDirectionScroll(CoordinatorLayout coordinatorLayout, View child, View target, int scrollDirection);
    }
}
