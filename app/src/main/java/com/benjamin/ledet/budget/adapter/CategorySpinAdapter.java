package com.benjamin.ledet.budget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.Category;
import java.util.List;

/**
 * Created by benjaminledet on 10/03/2017.
 */

public class CategorySpinAdapter extends ArrayAdapter<Category> {
    private CategorySpinAdapter.DisplayCategoryViewHolder viewHolder;
    private Category category;

    public CategorySpinAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //get the view (item of the listview)
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_alert_dialog_category,parent, false);
        }

        //get objects of the view
        viewHolder = (CategorySpinAdapter.DisplayCategoryViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new CategorySpinAdapter.DisplayCategoryViewHolder();
            viewHolder.label = (TextView) convertView.findViewById(R.id.row_alert_dialog_category_label);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.row_alert_dialog_category_icon);
            convertView.setTag(viewHolder);
        }

        //set objects with category data
        category = getItem(position);
        viewHolder.label.setText(category.getLabel());
        viewHolder.icon.setImageDrawable(category.getIcon());

        return convertView;
    }
    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    //display objects of item
    private class DisplayCategoryViewHolder {
        public ImageView icon;
        public TextView label;
    }
}