package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.R;

public class ErrorItemRecyclerAdapter extends RecyclerView.Adapter<ErrorItemRecyclerAdapter.ErrorViewHolder> {

    private View.OnClickListener onRetryButtonClickListener;
    @ErrorLayoutFactory.LayoutErrorType
    private int errorType;

    public ErrorItemRecyclerAdapter(View.OnClickListener onRetryButtonClickListener, int errorType) {
        this.onRetryButtonClickListener = onRetryButtonClickListener;
        this.errorType = errorType;
    }

    @Override
    public ErrorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ErrorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_root_error, parent, false));
    }

    @Override
    public void onBindViewHolder(ErrorViewHolder holder, int position) {
        new ErrorLayoutFactory((ViewGroup) holder.itemView)
                .showErrorLayout(errorType);
        holder.itemView.findViewById(R.id.fragment_root_error_button)
                .setOnClickListener(onRetryButtonClickListener);
    }

    @Override
    public int getItemCount() {
        // the adapter only generates error layout
        return 1;
    }

    public static class ErrorViewHolder extends RecyclerView.ViewHolder {

        public ErrorViewHolder(View itemView) {
            super(itemView);
        }
    }
}
