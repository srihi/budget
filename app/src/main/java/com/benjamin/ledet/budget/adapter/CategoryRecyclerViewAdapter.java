package com.benjamin.ledet.budget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.Month;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private List<Category> mCategorys;
    private final Context mContext;
    private DatabaseHandler db;
    private Month month;

    public CategoryRecyclerViewAdapter(List<Category> mCategorys, Context context, Month month) {
        this.mCategorys = mCategorys;
        this.mContext = context;
        this.month = month;
        this.db = new DatabaseHandler(context);
    }

    @Override
    public CategoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(CategoryRecyclerViewAdapter.ViewHolder holder, int position) {
        final Category selectedCategory = mCategorys.get(position);
        holder.icon.setImageDrawable(selectedCategory.getIcon());
        holder.label.setText(selectedCategory.getLabel());
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double sum = db.getSumAmountsOfMonthOfCategory(month,selectedCategory);
        String textSum = mContext.getResources().getString(R.string.amount,df.format(sum));
        holder.sum.setText(textSum);
    }

    @Override
    public int getItemCount() {
        return mCategorys.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_icon)
        ImageView icon;

        @BindView(R.id.row_category_label)
        TextView label;

        @BindView(R.id.row_category_sum)
        TextView sum;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}