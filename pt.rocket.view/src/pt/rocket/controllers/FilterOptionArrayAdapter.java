package pt.rocket.controllers;

import java.util.List;

import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.objects.CatalogFilterOption;
import pt.rocket.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Class used to fill the list view with filter options
 * @author sergiopereira
 *
 */
 public class FilterOptionArrayAdapter extends ArrayAdapter<CatalogFilterOption> {
        
    private static int layout = R.layout.dialog_list_sub_item_2;

    /**
     * Constructor
     * @param context
     * @param objects
     */
    public FilterOptionArrayAdapter(Context context, List<CatalogFilterOption> objects) {
        super(context, layout, objects);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get Filter
        CatalogFilterOption option = getItem(position);
        // Validate current view
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(layout, null);
        // Set title
        ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getLabel());
        // Set check box
        ((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)).setChecked(option.isSelected());
        // Return the filter view
        return convertView;
    }
}