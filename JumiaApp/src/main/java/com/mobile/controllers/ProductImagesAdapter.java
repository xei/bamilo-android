package com.mobile.controllers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobile.newFramework.objects.product.Variation;
import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

@Deprecated
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
		ArrayList<String> images = new ArrayList<>();
		for (Variation var : items) {
			images.add(var.getImage());
		}
		return images;
	}

	public void replaceAll(ArrayList<String> images) {
		this.images = images;
		this.notifyDataSetChanged();
		Print.d(TAG, "replaceAll: done - notfied");
	}

	@Override
	public int getCount() {
	    
		return images.size();
	}

	@Override
	public Object getItem(int position) {
	    Print.d(TAG, "codeImage position is : " + position);
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
		//View itemMarker;
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
			//h.itemMarker = view.findViewById(R.id.marker);
			view.setTag(h);
		} else
			h = (Holder) view.getTag();
		
		String imageUrl = images.get(position);
		Print.d(TAG, "getView: loading imageUrl = " + imageUrl);
		h.itemProgress.setVisibility(View.VISIBLE);
		h.itemImage.setImageResource(R.drawable.no_image_small);
		
		RocketImageLoader.instance.loadImage(imageUrl, h.itemImage, h.itemProgress, R.drawable.no_image_small);

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
