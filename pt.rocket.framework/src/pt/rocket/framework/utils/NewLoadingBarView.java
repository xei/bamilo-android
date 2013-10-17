/**
 * 
 */
package pt.rocket.framework.utils;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author nutzer2
 *
 */
public class NewLoadingBarView extends View {
	
    private Movie mMovie;
    private long mMovieStart;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public NewLoadingBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		mMovie = Movie.decodeStream(context.getResources().openRawResource(R.drawable.loading_bar));
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public NewLoadingBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		mMovie = Movie.decodeStream(context.getResources().openRawResource(R.drawable.loading_bar));
	}

	/**
	 * @param context
	 */
	public NewLoadingBarView(Context context) {
		super(context);
//		mMovie = Movie.decodeStream(context.getResources().openRawResource(R.drawable.loading_bar));
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		long now = android.os.SystemClock.uptimeMillis();
		if (mMovieStart == 0) {   // first time
			mMovieStart = now;
		}
		if (mMovie != null) {
			int dur = mMovie.duration();
			if (dur == 0) {
				dur = 1000;
			}
			int relTime = (int)((now - mMovieStart) % dur);
			mMovie.setTime(relTime);
			mMovie.draw(canvas, getWidth() - mMovie.width(),
					getHeight() - mMovie.height());
			invalidate();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mMovie.width(), mMovie.height());
	}
	

}
