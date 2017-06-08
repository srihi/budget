package com.benjamin.ledet.budget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CategoryRecyclerViewAdapter extends RealmRecyclerViewAdapter<Category, CategoryRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private DatabaseHandler databaseHandler;
    private Month month;

    public CategoryRecyclerViewAdapter(OrderedRealmCollection<Category> data, Month month, Context context) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
        this.month = month;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Category obj = getItem(position);
        holder.data = obj;
        //noinspection ConstantConditions
        holder.icon.setImageDrawable(obj.getIcon());
        holder.label.setText(obj.getLabel());
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double sum = databaseHandler.getSumAmountsOfMonthOfCategory(month,obj);
        String textSum = context.getString(R.string.amount,df.format(sum));
        holder.sum.setText(textSum);
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_icon)
        ImageView icon;

        @BindView(R.id.row_category_label)
        TextView label;

        @BindView(R.id.row_category_sum)
        TextView sum;

        public Category data;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
