package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys;/*
package com.mobile.libraries.emarsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import com.emarsys.predict.RecommendedItem;
import com.mobile.libraries.emarsys.predict.recommended.Item;
import com.bamilo.android.R;


public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private final List<RecommendedItem> items;
    private OnItemClickListener listener;

    public RecommendedAdapter(List<RecommendedItem> data) {
        this.items = new ArrayList<>();
        items.addAll(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View row = inflater.inflate(R.layout.recommended_row, parent, false);

        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        TextView title = (TextView) row.findViewById(R.id.title);
        title.setMaxWidth(160);

        return new ViewHolder(row, image, title);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //new DownloadImageTask(holder.image, 120, 120).execute(items.get(position).getImage());
        //holder.title.setText(items.get(position).getData()..getTitle());
        holder.title.setText("text");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public final ImageView image;
        public final TextView title;

        public ViewHolder(View v, ImageView image, TextView title) {
            super(v);
            this.image = image;
            this.title = title;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (listener != null) {
                listener.onItemClick(items.get(position));
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    public interface OnItemClickListener {

        void onItemClick(RecommendedItem item);
    }

    public void clear() {
        items.clear();
    }

    public void setData(List<RecommendedItem> resultData) {
        items.addAll(resultData);
    }

    public void addAll(List<RecommendedItem> newData) {

            items.addAll(newData);

    }
}
*/
