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

public class CategoryExpenseRecyclerViewAdapter extends RealmRecyclerViewAdapter<Category, CategoryExpenseRecyclerViewAdapter.MyViewHolder> {

    private Context context;

    public CategoryExpenseRecyclerViewAdapter(OrderedRealmCollection<Category> data, Context context) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_expense, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Category obj = getItem(position);
        //noinspection ConstantConditions
        holder.label.setText(obj.getLabel());
        if (obj.getDefaultBudget() != 0){
            holder.budget.setText(context.getString(R.string.default_budget_amount, String.valueOf(obj.getDefaultBudget())));
        } else {
            holder.budget.setText(context.getString(R.string.no_default_budget_defined));
        }
        holder.icon.setImageDrawable(obj.getIcon());

    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_expense_icon)
        ImageView icon;

        @BindView(R.id.row_category_expense_label)
        TextView label;

        @BindView(R.id.row_category_expense_budget)
        TextView budget;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
