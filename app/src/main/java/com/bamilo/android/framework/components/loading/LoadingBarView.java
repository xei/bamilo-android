package com.bamilo.android.framework.components.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bamilo.android.R;
import com.bamilo.android.framework.service.utils.output.Print;

import java.io.InputStream;

/**
 * Class used to render the loading_bar animation.
 * 
 * @author GuilhermeSilva
 * 
 */
public class LoadingBarView extends ImageView {

	private final static String TAG = LoadingBarView.class.getSimpleName();

	private static GifDecoder mGifDecoder = null;

	private Bitmap mTmpBitmap;

	final Handler mHandler = new Handler();

	//public static final int MAX_RUNNING_FRAMES = 50;

	private Thread mThread;

	/**
	 * Unable used to render the frames of the application.
	 */
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
				LoadingBarView.this.setImageBitmap(mTmpBitmap);
			}
		}
	};

	/**
	 * Default constructor
	 * 
	 * @param context
	 *            of the activity
	 * @param attrs
	 *            attributes of the XML
	 */
	public LoadingBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingBarView);
		int drawableId = ta.getResourceId(R.styleable.LoadingBarView_gifResource, 0);
		if (drawableId != 0) {
			initialize(context.getResources().openRawResource(drawableId));
		} else {
			initialize(null);
		}
		ta.recycle();
	}

	public void initialize(InputStream stream) {
		if (mGifDecoder == null) {
			mGifDecoder = new GifDecoder();
			mGifDecoder.read(stream);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Print.i(TAG, "ON ATTACH TO WINDOW: START RENDERING");
		// TODO: Validate this approach
		startRendering();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Print.i(TAG, "ON DETACHED FROM WINDOW: STOP RENDERING");
		// TODO: Validate this approach
		stopRendering();
	}

	@Override
	protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		// TODO: Validate this approach
		/*
		Log.i(TAG, "ON VISIBILITY CHANGED: IS VISIBLE: " + (visibility == View.VISIBLE));
		if (visibility == View.VISIBLE) {
			startRendering();
		} else {
			stopRendering();
		}
		*/
	}

	/**
	 * Plays the an input stream
	 */
	public synchronized void startRendering() {
		Print.d(TAG, "startRendering");
		if (mThread != null && mThread.isAlive()) {
			Print.d(TAG, "startRendering: thread already running");
			return;
		}

		mThread = new Thread(new Runnable() {
			public void run() {
				final int n = mGifDecoder.getFrameCount();
				final int ntimes = mGifDecoder.getLoopCount();
				int repetitionCounter = 0;
			//	int runningFrames = 0;

				do {
					for (int i = 0; i < n; i++) {
						if (Thread.interrupted()) {
							return;
						}
						mTmpBitmap = mGifDecoder.getFrame(i);
						int t = mGifDecoder.getDelay(i);
						mHandler.post(mUpdateResults);
						try {
							Thread.sleep(t);
						} catch (InterruptedException | IllegalArgumentException e) {
							return;
						}
					}
			/*		if (ntimes != 0) {
						repetitionCounter++;
					}

					if (++runningFrames == MAX_RUNNING_FRAMES) {
						return;
					}*/

				} while (repetitionCounter <= ntimes);
			}
		});
		mThread.setName(TAG);
		mThread.start();
	}

	/**
	 * Stops the rendering
	 */
	public synchronized void stopRendering() {
		Print.d(TAG, "stopRendering");
		if (mThread != null) {
			mThread.interrupt();
			mThread = null;
		}
	}
}
