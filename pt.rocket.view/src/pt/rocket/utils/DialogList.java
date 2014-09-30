//package pt.rocket.utils;
//
//import java.util.ArrayList;
//
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.view.R;
//import android.app.Activity;
//import android.app.Dialog;
//import android.database.DataSetObserver;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.ListView;
//import android.widget.TextView;
//
//public class DialogList implements OnItemClickListener {
//	private final static String TAG = LogTagHelper.create( DialogList.class );
//	private static final long DELAY_DISMISS = 250;
//	public static final int NO_INITIAL_POSITION = -1;
//	private String mTitle;
//	private ArrayList<String> mItems;
//	private String mId;
//	private int mInitialPosition;
//	private Activity mActivity;
//	private OnDialogListListener mListener;
//	private Dialog mDialog;
//	private ListView list;
//	private DialogListAdapter mAdapter;
//
//	public interface OnDialogListListener {
//		public void onDialogListItemSelect(String id, int position, String value);
//
//	}
//
//	public DialogList(Activity activity, String id, String title,
//			ArrayList<String> items, int initialPosition) {
//		mActivity = activity;
//		if (activity instanceof OnDialogListListener) {
//			mListener = (OnDialogListListener) activity;
//		}
//
//		mId = id;
//		mTitle = title;
//		mItems = items;
//		mInitialPosition = initialPosition;
//	}
//	
//	public DialogList( Activity activity, OnDialogListListener listener, String id, String title, ArrayList<String> items, int initialPosition) {
//		mActivity = activity;
//		mListener = listener;
//		mId = id;
//		mTitle = title;
//		mItems = items;
//		mInitialPosition = initialPosition;
//	}
//
//	public void show() {
//		mDialog = new Dialog(mActivity, R.style.Theme_Jumia_Dialog_Blue_NoTitle);
//		mDialog.setContentView(R.layout.dialog_list_content);
//
//		TextView titleView = (TextView) mDialog.findViewById(R.id.title);
//		titleView.setText(mTitle);
//
//		list = (ListView) mDialog.findViewById(R.id.list);
//		mAdapter = new DialogListAdapter();
//		mAdapter.setCheckedPosition(mInitialPosition);
//		list.setAdapter(mAdapter);
//		list.setOnItemClickListener(this);
//		mDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
////		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
////			resizeDialog(mDialog);
//		mDialog.show();
//	}
//
//	private class DialogListAdapter extends BaseAdapter {
//		private int mCheckedPosition = NO_INITIAL_POSITION;
//
//		@Override
//		public boolean hasStableIds() {
//			return true;
//		}
//
//		@Override
//		public int getCount() {
//			return mItems.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return mItems.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view;
//			if (convertView == null) {
//				LayoutInflater inflater = LayoutInflater.from(mActivity);
//				view = inflater.inflate(R.layout.dialog_list_item, parent,
//						false);
//			} else {
//				view = convertView;
//			}
//
//			TextView textView = (TextView) view.findViewById(R.id.item_text);
//			textView.setText(mItems.get(position));
//			CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_item_checkbox);
//			checkBox.setChecked(position == mCheckedPosition);
//
//			return view;
//
//		}
//
//		public void setCheckedPosition(int position) {
//			mCheckedPosition = position;
//		}
//		
//		/**
//	     * @FIX: java.lang.IllegalArgumentException: The observer is null.
//	     * @solution from : https://code.google.com/p/android/issues/detail?id=22946 
//	     */
//	    @Override
//	    public void unregisterDataSetObserver(DataSetObserver observer) {
//	        if(observer !=null){
//	            super.unregisterDataSetObserver(observer);    
//	        }
//	    }
//
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> adapterView, View view,
//			final int position, long id) {
//		mAdapter.setCheckedPosition(position);
//		mAdapter.notifyDataSetChanged();
//
//		view.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				mDialog.dismiss();
//				if (mListener != null) {
//					mListener.onDialogListItemSelect(mId, position, mItems.get(position));
//				}
//
//			}
//		}, DELAY_DISMISS);
//
//	}
//
////	@SuppressWarnings("deprecation")
////	public void resizeDialog(Dialog dialog) {
////		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
////		lp.copyFrom(dialog.getWindow().getAttributes());
////		lp.width = (int) (dialog.getWindow().getWindowManager()
////				.getDefaultDisplay().getWidth() * 0.9f);
////		lp.horizontalMargin = 0;
////		lp.verticalMargin = 0;
////		dialog.getWindow().setAttributes(lp);
////		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
////
////	}
//}
