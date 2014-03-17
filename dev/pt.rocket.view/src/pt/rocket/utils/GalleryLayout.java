package pt.rocket.utils;

import java.util.List;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.Toast;


/**
* This Class is used to create an images gallery layout. <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.  
*
* @author Sergio Pereira
*
* @version 1.01 
*
* 2012/06/19 
*
* {@code}
*     <!--
         <Gallery
        android:id="@+id/homepage_bestseller_gallery"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingBottom="5dp"
        android:paddingTop="5dp"
        android:spacing="5dp" />
    -->
 *
 */
public class GalleryLayout {

	/**
	 * 
	 * showGalleryImages(activity, R.id.homepage_brands_gallery, topBrandsImageLayoutWidth, topBrandsImageLayoutHeigh, topBrandsImages)
	 * 
	 * @param activity
	 * @param galleryId
	 * @param width
	 * @param heigh
	 * @param images
	 */
	public static void showGalleryImages(final Activity activity, int galleryId, int width, int heigh, List<Bitmap> images){
		// Get Gallery
	    final Gallery gallery = (Gallery) activity.findViewById(galleryId);
	    // Set Listener
	    gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(activity, "Image: " + position, Toast.LENGTH_SHORT).show();
			}
		});    
	}
	
	/**
	 * 
	 * http://www.marvinlabs.com/2011/06/proper-layout-gravity-gallery-widget/
	 * 
	 * @param activity
	 * @param gallery
	 * @param imageWidth
	 * @param gallerySpacing
	 */
	public static void alignGalleryToLeft(Activity activity, Gallery gallery, int imageWidth, int gallerySpacing) {
		// Get Display Width
	    int displayWidth = activity.getResources().getDisplayMetrics().widthPixels;
	    // The offset to pull the gallery to the left
	    int offset = displayWidth - imageWidth - 3 * gallerySpacing; // - itemWidth; // + itemWidth + spacing;
	    // Now update the layout parameters of the gallery in order to set the left margin
	    MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
	    mlp.setMargins(-offset, mlp.topMargin, mlp.rightMargin, mlp.bottomMargin);
	}
	
}
