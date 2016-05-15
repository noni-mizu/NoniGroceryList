package org.azurespot.nonigrocerylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mizu on 5/11/16.
 */
public class ListViewItemAdapter extends ArrayAdapter<ListItemsModel> {

    public ListViewItemAdapter(Context context, int rowLayout, ArrayList<ListItemsModel> items){
        super(context, rowLayout, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        ListItemsModel listItem = getItem(position);

        if(row == null) {
            row = LayoutInflater.from(getContext())
                                .inflate(R.layout.list_row, parent, false);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox)row.findViewById(R.id.checkBox);
            holder.itemTitleET = (TextView)row.findViewById(R.id.listItem);
            holder.itemQtyTV = (TextView)row.findViewById(R.id.itemQty);

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder)row.getTag();
        }

        holder.itemTitleET.setText(listItem.getTitle());
        // setText() only accepts Strings
        holder.itemQtyTV.setText(listItem.getQty());

        return row;
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView itemTitleET;
        TextView itemQtyTV;
    }
}
