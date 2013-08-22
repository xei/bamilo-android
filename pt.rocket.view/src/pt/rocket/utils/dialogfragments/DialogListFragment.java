package pt.rocket.utils.dialogfragments;

import java.util.ArrayList;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class DialogListFragment extends DialogFragment implements OnItemClickListener {
	
    private final static String TAG = LogTagHelper.create( DialogListFragment.class );
	
	private static final long DELAY_DISMISS = 250;
	
	public static final int NO_INITIAL_POSITION = -1;
	
	private String mTitle;
	
	private ArrayList<String> mItems;
	
	private String mId;
	
	private int mInitialPosition;
	
	private Activity mActivity;
	
	private OnDialogListListener mListener;
	
	//private Dialog mDialog;
	private ListView list;
	
	private DialogListAdapter mAdapter;

	
	/**
	 * 
	 * @author sergiopereira
	 *
	 */
	public interface OnDialogListListener {
		public void onDialogListItemSelect(String id, int position, String value);
	}
	
	/**
	 * Empty constructor
	 */
	public DialogListFragment(){}

	
	/**
	 * 
	 * @param activity
	 * @param id
	 * @param title
	 * @param items
	 * @param initialPosition
	 * @return
	 */
	public static DialogListFragment newInstance(Activity activity, String id, String title, ArrayList<String> items, int initialPosition) {
	    Log.d(TAG, "NEW INSTANCE");
	    DialogListFragment dialogListFragment = new DialogListFragment();
	    dialogListFragment.mActivity = activity;
        if (activity instanceof OnDialogListListener) {
            dialogListFragment.mListener = (OnDialogListListener) activity;
        }
        dialogListFragment.mId = id;
        dialogListFragment.mTitle = title;
        dialogListFragment.mItems = items;
        dialogListFragment.mInitialPosition = initialPosition;
	    return dialogListFragment;
	}
	
	/**
	 * 
	 * @param activity
	 * @param listener
	 * @param id
	 * @param title
	 * @param items
	 * @param initialPosition
	 * @return
	 */
	public static DialogListFragment newInstance(Activity activity, OnDialogListListener listener, String id, String title, ArrayList<String> items, int initialPosition) {
	    Log.d(TAG, "NEW INSTANCE");
	    DialogListFragment dialogListFragment = new DialogListFragment();  
	    dialogListFragment.mActivity = activity;
	    dialogListFragment.mListener = listener;
	    dialogListFragment.mId = id;
	    dialogListFragment.mTitle = title;
	    dialogListFragment.mItems = items;
	    dialogListFragment.mInitialPosition = initialPosition;
	    return dialogListFragment;   
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setStyle(R.style.Theme_Jumia_Dialog_Blue_NoTitle, R.style.Theme_Jumia_Dialog_Blue_NoTitle);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_list_content, container);
        
        
        
//      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
//          resizeDialog(mDialog);

	    return view;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    TextView titleView = (TextView) getView().findViewById(R.id.title);
        titleView.setText(mTitle);

        list = (ListView) getView().findViewById(R.id.list);
        mAdapter = new DialogListAdapter();
        mAdapter.setCheckedPosition(mInitialPosition);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
        this.mActivity.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
	}

	private class DialogListAdapter extends BaseAdapter {
		private int mCheckedPosition = NO_INITIAL_POSITION;

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(mActivity);
				view = inflater.inflate(R.layout.dialog_list_item, parent,
						false);
			} else {
				view = convertView;
			}

			TextView textView = (TextView) view.findViewById(R.id.item_text);
			textView.setText(mItems.get(position));
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);
			checkBox.setChecked(position == mCheckedPosition);

			return view;

		}

		public void setCheckedPosition(int position) {
			mCheckedPosition = position;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			final int position, long id) {
		mAdapter.setCheckedPosition(position);
		mAdapter.notifyDataSetChanged();

		view.postDelayed(new Runnable() {

			@Override
			public void run() {
				dismiss();
				if (mListener != null) {
					mListener.onDialogListItemSelect(mId, position, mItems.get(position));
				}

			}
		}, DELAY_DISMISS);

	}

//	@SuppressWarnings("deprecation")
//	public void resizeDialog(Dialog dialog) {
//		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//		lp.copyFrom(dialog.getWindow().getAttributes());
//		lp.width = (int) (dialog.getWindow().getWindowManager()
//				.getDefaultDisplay().getWidth() * 0.9f);
//		lp.horizontalMargin = 0;
//		lp.verticalMargin = 0;
//		dialog.getWindow().setAttributes(lp);
//		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//
//	}
}
