package com.perfection.newkeyboard.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.R;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>CountryListAdapter</H1>
 *
 * <p> An adapter for displaying the country list with their country codes</p>
 *
 * @author Divya Thakur
 * @since 10/11/16
 * @version 1.0
 */
public class CountryListAdapter extends BaseAdapter  implements Filterable {

    private ViewHolder holder				    = null;
    private String[] countryCodeList  = null;
    private Context context					    = null;
    private final String TAG				    = getClass().getSimpleName();
    private String[] mOriginalValues;

    /**
     * Parameterized Constructor
     * @param context holds the context
     * @param countryCodeList holds the list of countries with codes
     */
    public CountryListAdapter(Context context, String[] countryCodeList) {
        this.context = context;
        this.countryCodeList = countryCodeList;
        this.mOriginalValues = this.countryCodeList;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Utils.printLogs(TAG, "Inside getView()");

        if(convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            // Inflating the view
            convertView = inflater.inflate(R.layout.country_list_item, null);
            this.setWidgetReferences(convertView);
            this.setTag(convertView);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        this.editViews(position);

        Utils.printLogs(TAG, "Outside getView()");

        return convertView;
    }

    /**
     * Private view holder class to hold the data
     */
    private class ViewHolder {
        TextView tvCountryName;
    }

    /**
     * Identifying the views by using their ID's
     * and setting the references
     * @param view holds the view
     */
    private void setWidgetReferences(View view){

        Utils.printLogs(TAG, "Inside setWidgetReferences()");

        holder.tvCountryName   = (TextView) view.findViewById(R.id.tvCountryName);

        Utils.printLogs(TAG, "Outside setWidgetReferences()");
    }

    /**
     * Sets the text on the text views
     * @param position holds the position
     */
    private void editViews(int position) {

        Utils.printLogs(TAG, "Inside editViews()");

        holder.tvCountryName.setText(countryCodeList[position]);

        holder.tvCountryName.setTag(position); 					// This line is important

        Utils.printLogs(TAG, "Outside editViews()");
    }

    /**
     * Sets the tags to the views
     * @param view holds the view
     */
    private void setTag(View view){

        Utils.printLogs(TAG, "Inside setTag()");

        view.setTag(holder);
        view.setTag(R.id.tvCountryName, holder.tvCountryName);

        Utils.printLogs(TAG, "Outside setTag()");
    }

    @Override
    public int getCount() {
        if(countryCodeList != null)
            return countryCodeList.length;
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        if(countryCodeList != null)
            return countryCodeList[position];
        else
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                // has the filtered values
                countryCodeList = (String[]) results.values;
                Constants.countryArray = countryCodeList;
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Holds the results of a filtering operation in values
                FilterResults results = new FilterResults();
                String[] FilteredArrList = new String[]{};

                if (mOriginalValues == null) {
                    // saves the original data in mOriginalValues
                    mOriginalValues = countryCodeList;
                }

                /**
                 *  If constraint(CharSequence that is received) is null returns
                 *  the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 */
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.length;
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    int k = 0;
                    for (int i = 0; i < mOriginalValues.length; i++) {
                        String data = mOriginalValues[i];
                        if (data.toLowerCase().startsWith(constraint.toString())) {

                            FilteredArrList[k++] = data;
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.length;
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
    }
}
