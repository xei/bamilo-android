package com.bamilo.android.appmodule.bamiloapp.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamilo.android.R;

public class CustomSearchActionView extends RelativeLayout {
    private EditText etSearchText;
    private ImageView imgMicOrClear;
    private VoiceSearchClickListener voiceSearchClickListener;
    private boolean voiceSearchEnabled;
    private int mMaxWidth;

    public CustomSearchActionView(Context context) {
        super(context);
        init();
    }

    public CustomSearchActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSearchActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        inflate(getContext(), R.layout.custom_search_view, this);
        imgMicOrClear = findViewById(R.id.imgMicOrClear);
        etSearchText = findViewById(R.id.etSearchText);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.isEmpty()) {
                    if (voiceSearchEnabled) {
                        imgMicOrClear.setImageResource(R.drawable.ic_mic_gray);
                    } else {
                        imgMicOrClear.setImageDrawable(null);
                    }
                } else {
                    imgMicOrClear.setImageResource(R.drawable.ic_clear_gray);
                }
            }
        });
        imgMicOrClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getText().isEmpty()) {
                    if (voiceSearchClickListener != null && voiceSearchEnabled) {
                        voiceSearchClickListener.onVoiceSearchClicked(v);
                    }
                } else {
                    setText("");
                }
            }
        });
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        etSearchText.addTextChangedListener(textWatcher);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        etSearchText.setOnEditorActionListener(onEditorActionListener);
    }

    public void setText(String searchedTerm) {
        etSearchText.setText(searchedTerm);
    }

    public void setSelection(int length) {
        etSearchText.setSelection(0, length);
    }

    public void setOnFieldFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        etSearchText.setOnFocusChangeListener(onFocusChangeListener);
    }

    public String getText() {
        return etSearchText.getText().toString();
    }

    public VoiceSearchClickListener getVoiceSearchClickListener() {
        return voiceSearchClickListener;
    }

    public void setVoiceSearchClickListener(VoiceSearchClickListener voiceSearchClickListener) {
        if (voiceSearchClickListener != null) {
            voiceSearchEnabled = true;
        }
        this.voiceSearchClickListener = voiceSearchClickListener;
    }

    public boolean isVoiceSearchEnabled() {
        return voiceSearchEnabled;
    }

    public void setVoiceSearchEnabled(boolean voiceSearchEnabled) {
        this.voiceSearchEnabled = voiceSearchEnabled;
    }

    public void setFocus(InputMethodManager inputMethodManager) {
        etSearchText.requestFocus();
        inputMethodManager.showSoftInput(etSearchText, InputMethodManager.SHOW_IMPLICIT);
    }

    public interface VoiceSearchClickListener {
        void onVoiceSearchClicked(View v);
    }

    /**
     * Makes the view at most this many pixels wide
     *
     * @attr ref android.support.v7.appcompat.R.styleable#SearchView_android_maxWidth
     */
    public void setMaxWidth(int maxpixels) {
        mMaxWidth = maxpixels;

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                // If there is an upper limit, don't exceed maximum width (explicit or implicit)
                if (mMaxWidth > 0) {
                    width = Math.min(mMaxWidth, width);
                }
                break;
        }
        widthMode = MeasureSpec.EXACTLY;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode),
                MeasureSpec.makeMeasureSpec(height, heightMode));
    }
}
