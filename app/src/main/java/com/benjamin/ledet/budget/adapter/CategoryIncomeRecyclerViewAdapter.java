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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CategoryIncomeRecyclerViewAdapter extends RealmRecyclerViewAdapter<Category, CategoryIncomeRecyclerViewAdapter.MyViewHolder> {

    private Context context;

    public CategoryIncomeRecyclerViewAdapter(OrderedRealmCollection<Category> data, Context context) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_income, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Category obj = getItem(position);
        //noinspection ConstantConditions
        holder.label.setText(obj.getLabel());
        holder.icon.setImageDrawable(obj.getIcon());

    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_income_icon)
        ImageView icon;

        @BindView(R.id.row_category_income_label)
        TextView label;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
