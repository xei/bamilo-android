package pt.rocket.controllers;

import java.util.ArrayList;

import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import de.akquinet.android.androlog.Log;

public class ProductImagesAdapter extends BaseAdapter {

	private final static String TAG = LogTagHelper.create(ProductImagesAdapter.class);
	Context context;
	ArrayList<String> images;

	   public ProductImagesAdapter(Context context) {
	        this.context = context;

	    }
	
	public ProductImagesAdapter(Context context, ArrayList<String> images) {
		this.context = context;
		this.images = images;

	}

	public static ArrayList<String> createImageList(ArrayList<Variation> items) {
		ArrayList<String> images = new ArrayList<String>();
		for (Variation var : items) {
			images.add(var.getImage());
		}
		return images;
	}

	public void replaceAll(ArrayList<String> images) {
		this.images = images;
		this.notifyDataSetChanged();
		Log.d(TAG, "replaceAll: done - notfied");
	}

	@Override
	public int getCount() {
	    
		return images.size();
	}

	@Override
	public Object getItem(int position) {
	    Log.d(TAG, "codeImage position is : "+position);
		return images.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	private static class Holder {
		ImageView itemImage;
		ProgressBar itemProgress;
		View itemMarker;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		final Holder h;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.productdetails_images_item, parent, false);
			h = new Holder();
			h.itemImage = (ImageView) view.findViewById(R.id.image);
			h.itemProgress = (ProgressBar) view.findViewById(R.id.loading_progress);
			h.itemMarker = view.findViewById(R.id.marker);
			view.setTag(h);
		} else
			h = (Holder) view.getTag();

		AQuery mAQuery = new AQuery(view);
		
		String imageUrl = images.get(position);
		Log.d(TAG, "getView: loading imageUrl = " + imageUrl);
		h.itemProgress.setVisibility(View.VISIBLE);
		h.itemImage.setImageResource(R.drawable.no_image_small);

		final Holder fh = h;
		
        mAQuery.id(h.itemImage).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback() {

            @Override
            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                iv.setImageBitmap(bm);
                h.itemProgress.setVisibility(View.GONE);
            }
        });
//		ImageLoader.getInstance().displayImage(imageUrl, h.itemImage, new SimpleImageLoadingListener() {
//
//		    /* (non-Javadoc)
//		     * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingComplete(java.lang.String, android.view.View, android.graphics.Bitmap)
//		     */
//		    @Override
//		    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//		        Log.d(TAG, "getView: onLoadingComplete");
//                fh.itemProgress.setVisibility(View.GONE);
//		    }
//		    
//		});

		return view;
	}

	/**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946 
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if(observer !=null){
            super.unregisterDataSetObserver(observer);    
        }
    }
}
