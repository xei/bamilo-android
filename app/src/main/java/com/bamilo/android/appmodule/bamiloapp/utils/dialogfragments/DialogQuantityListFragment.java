package com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import android.widget.TextView;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.R;

import java.lang.ref.WeakReference;

/**
 * Class used to show a dialog quantity.
 * @author sergiopereira
 */
public class DialogQuantityListFragment extends BottomSheet implements OnItemClickListener {

    private final static String TAG = DialogListFragment.class.getSimpleName();

    @StringRes
    private int mTitle;
    private int mInitial;
    private int mMax;
    private WeakReference<? extends FragmentActivity> mActivity;
    private OnDialogListListener mSelectListener;
    private DialogQuantityListAdapter mAdapter;

    /**
     * @author sergiopereira
     */
    public interface OnDialogListListener {
        void onDialogListItemSelect(AdapterView<?> adapterView, View view, final int position, long id);
        void onDismiss();
    }

    /**
     * Empty constructor
     */
    public DialogQuantityListFragment() {
    }

    /**
     * Called from Shopping cart.
     */
    public static DialogQuantityListFragment newInstance(@NonNull WeakReference<? extends FragmentActivity> weakActivity, @StringRes int title, int max, int initial) {
        DialogQuantityListFragment dialogListFragment = new DialogQuantityListFragment();
        dialogListFragment.mActivity = weakActivity;
        dialogListFragment.mTitle = title;
        dialogListFragment.mMax = max;
        dialogListFragment.mInitial = initial;
        return dialogListFragment;
    }

    /**
     * Add listener
     */
    public DialogQuantityListFragment addOnItemClickListener(@Nullable OnDialogListListener listener) {
        mSelectListener = listener;
        return this;
    }

    /**
     * Show dialog
     */
    public void show() {
        // Validate current activity
        if (this.mActivity != null && this.mActivity.get() != null) {
            show(this.mActivity.get().getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_list_content, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Validate current activity
        if (this.mActivity == null || this.mActivity.get() == null) {
            dismissAllowingStateLoss();
            return;
        }
        // Set title
        ((TextView) view.findViewById(R.id.dialog_list_title)).setText(getString(mTitle));
        // Set size guide
        setSizeGuide(view);
        // Get list
        ListView list = view.findViewById(R.id.dialog_list_view);
        // Set Max list size with size guide
        setListSize(list, mMax);
        // Validate adapter
        if (mAdapter == null) {
            mAdapter = new DialogQuantityListAdapter(mActivity.get());
        }
        // Add adapter
        mAdapter.setCheckedPosition(mInitial);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
        // Show pre-selection
        //list.setSelection(mInitial);
        this.mActivity.get().getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

    }

    /**
     * Set the size guide button
     *
     * @author sergiopereira
     */
    private void setSizeGuide(View view) {
        // Get views
        View button = view.findViewById(R.id.dialog_list_size_guide_button);
        button.setVisibility(View.GONE);
        button.setOnClickListener(null);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        mActivity = null;
        mSelectListener = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mSelectListener != null) {
            mSelectListener.onDismiss();
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
            // Trying fix https://rink.hockeyapp.net/manage/apps/33641/app_versions/143/crash_reasons/38911893?type=crashes
            // Or try this solution http://dimitar.me/android-displaying-dialogs-from-background-threads/
        } catch (IllegalStateException | WindowManager.BadTokenException ignored) {
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        DialogQuantityListAdapter adapter = (DialogQuantityListAdapter) adapterView.getAdapter();
        adapter.setCheckedPosition(adapter.getItem(position));
        adapter.notifyDataSetChanged();
        view.postDelayed(() -> {
            dismissAllowingStateLoss();
            if (mSelectListener != null) {
                mSelectListener.onDialogListItemSelect(adapterView, view, position, id);
            }
        }, IntConstants.DIALOG_DELAY_DISMISS);
    }

    /**
     * Adapter that shows a list of generic items for a dialg
     * <p/>
     * Created by rsoares on 8/25/15.
     *
     * @modified Paulo Carvalho
     */
    public class DialogQuantityListAdapter extends ArrayAdapter<Integer> {

        protected LayoutInflater mInflater;
        private int initial;

        public DialogQuantityListAdapter(Context context) {
            super(context, R.layout.dialog_list_item);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mMax;
        }

        @Override
        public Integer getItem(int position) {
            return position + 1;
        }

        public int getInitial() {
            return initial;
        }

        public void setCheckedPosition(int position) {
            initial = position;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mInflater.inflate(R.layout.dialog_list_item, parent, false);
            } else {
                view = convertView;
            }
            TextView textView = view.findViewById(R.id.item_text);
            CheckBox checkBox = view.findViewById(R.id.dialog_item_checkbox);
            textView.setText(String.valueOf(getItem(position)));
            checkBox.setChecked(getItem(position) == getInitial());
            return view;
        }
    }
}
