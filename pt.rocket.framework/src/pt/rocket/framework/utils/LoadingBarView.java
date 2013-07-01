package pt.rocket.framework.utils;

import java.io.InputStream;

import de.akquinet.android.androlog.Log;

import pt.rocket.framework.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

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

	public static final int MAX_RUNNING_FRAMES = 50;

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
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.LoadingBarView);
		int drawableId = ta.getResourceId(
				R.styleable.LoadingBarView_gifResource, 0);
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

	/**
	 * Plays the an input stream
	 */
	public synchronized void startRendering() {
		Log.d( TAG, "startRendering" );
		if (mThread != null && mThread.isAlive()) {
			Log.d( TAG, "startRendering: thread already running" );
			return;
		}

		mThread = new Thread(new Runnable() {
			public void run() {
				final int n = mGifDecoder.getFrameCount();
				final int ntimes = mGifDecoder.getLoopCount();
				int repetitionCounter = 0;
				int runningFrames = 0;
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
						} catch (InterruptedException e) {
							return;
						} catch (IllegalArgumentException e ) {
							return;
						}
					}
					if (ntimes != 0) {
						repetitionCounter++;
					}

					if (++runningFrames == MAX_RUNNING_FRAMES) {
						return;
					}

				} while (repetitionCounter <= ntimes);
			}
		});
		mThread.start();
	}

	/**
	 * Stops the rendering
	 */
	public synchronized void stopRendering() {
		Log.d( TAG, "stopRendering" );
		if ( mThread != null) {
			mThread.interrupt();
			mThread = null;
		}
	}
}
