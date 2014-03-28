package pt.rocket.utils;

import java.util.ArrayList;

import org.holoeverywhere.widget.AutoCompleteTextView;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import de.akquinet.android.androlog.Log;

/**
 * 
 * Class used to show the search suggestions
 * @author sergiopereira
 * 
 */
public class SearchSuggestionText extends AutoCompleteTextView {

    private static final String TAG = SearchSuggestionText.class.getSimpleName();

    private SearchSuggestionImeBackListener mOnImeBack;

    private ArrayList<TextWatcher> mWatcheListeners;

    /**
     * Constructor
     * @param context
     * @author sergiopereira
     */
    public SearchSuggestionText(Context context) {
        super(context);
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     * @author sergiopereira
     */
    public SearchSuggestionText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     * @param defStyle
     * @author sergiopereira
     */
    public SearchSuggestionText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.holoeverywhere.widget.AutoCompleteTextView#onKeyPreIme(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            Log.d(TAG, "SEARCH ON IME PRESSED BACK");
            if (mOnImeBack != null)
                mOnImeBack.onImeBackPressed(this, this.getText().toString());
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Save the listener
     * @param listener
     * @author sergiopereira
     */
    public void setOnImeBackListener(SearchSuggestionImeBackListener listener) {
        mOnImeBack = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.TextView#addTextChangedListener(android.text.TextWatcher)
     */
    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (mWatcheListeners == null) mWatcheListeners = new ArrayList<TextWatcher>();
        mWatcheListeners.add(watcher);
        super.addTextChangedListener(watcher);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.TextView#removeTextChangedListener(android.text.TextWatcher)
     */
    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        if (mWatcheListeners != null) {
            int i = mWatcheListeners.indexOf(watcher);
            if (i >= 0) mWatcheListeners.remove(i);
        }
        super.removeTextChangedListener(watcher);
    }

    /**
     * Clear the text changed lister
     * @author sergiopereira
     */
    public void clearTextChangedListeners() {
        if (mWatcheListeners != null) {
            for (TextWatcher watcher : mWatcheListeners) {
                super.removeTextChangedListener(watcher);
            }
            mWatcheListeners.clear();
            mWatcheListeners = null;
        }
    }
    
}
