package com.benjamin.ledet.budget.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class CategoryBudgetRecyclerViewAdapter extends RealmRecyclerViewAdapter<Category, RecyclerView.ViewHolder> {

    private Context context;
    private DatabaseHandler databaseHandler;
    private Month month;

    public CategoryBudgetRecyclerViewAdapter(OrderedRealmCollection<Category> data, Month month, Context context) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
        this.month = month;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category_no_budget_expense, parent, false);
            return new ViewHolderNoBudget(itemView);
        } else if (viewType == 1){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category_budget_expense, parent, false);
            return new ViewHolderBudget(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category_budget_income, parent, false);
            return new ViewHolderIncome(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Category obj = getItem(position);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double spending;

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderNoBudget viewHolderNoBudget = (ViewHolderNoBudget) holder;
                //noinspection ConstantConditions
                ((ViewHolderNoBudget) holder).icon.setImageDrawable(obj.getIcon());
                ((ViewHolderNoBudget) holder).tvLabel.setText(obj.getLabel());
                spending = databaseHandler.getSumAmountOfMonthOfCategory(month,obj);
                ((ViewHolderNoBudget) holder).tvSpending.setText(context.getString(R.string.amount,df.format(spending)));
                break;
            case 1:
                ViewHolderBudget viewHolderBudget = (ViewHolderBudget) holder;
                //noinspection ConstantConditions
                ((ViewHolderBudget) holder).icon.setImageDrawable(obj.getIcon());
                ((ViewHolderBudget) holder).tvLabel.setText(obj.getLabel());
                ((ViewHolderBudget) holder).tvBudget.setText(context.getString(R.string.amount,df.format(obj.getMonthlyBudget(month))));
                spending = databaseHandler.getSumAmountOfMonthOfCategory(month,obj);
                ((ViewHolderBudget) holder).tvSpending.setText(context.getString(R.string.amount,df.format(spending)));
                double remaining = obj.getMonthlyBudget(month) - spending;
                ((ViewHolderBudget) holder).tvRemaining.setText(context.getString(R.string.amount,df.format(remaining)));
                Double progress = (spending/obj.getMonthlyBudget(month))*100;
                if (progress > 75){
                    ((ViewHolderBudget) holder).progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);
                    ((ViewHolderBudget) holder).tvRemaining.setTextColor(ContextCompat.getColor(context,android.R.color.holo_orange_dark));
                }
                if (progress > 100){
                    ((ViewHolderBudget) holder).progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);;
                    ((ViewHolderBudget) holder).tvRemaining.setTextColor(ContextCompat.getColor(context,android.R.color.holo_red_dark));
                }
                ((ViewHolderBudget) holder).progressBar.setProgress(progress.intValue());
                break;
            case 2:
                ViewHolderIncome viewHolderIncome = (ViewHolderIncome) holder;
                //noinspection ConstantConditions
                ((ViewHolderIncome) holder).icon.setImageDrawable(obj.getIcon());
                ((ViewHolderIncome) holder).tvLabel.setText(obj.getLabel());
                double income = databaseHandler.getSumAmountOfMonthOfCategory(month,obj);
                ((ViewHolderIncome) holder).tvIncome.setText(context.getString(R.string.amount,df.format(income)));
                break;
        }

    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        //noinspection ConstantConditions
        if(!getData().get(position).isIncome()){
            if (getData().get(position).getMonthlyBudget(month) > 0){
                type = 1;
            }
        } else {
            type = 2;
        }

       return type;
    }

    class ViewHolderBudget extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_budget_expense_icon)
        ImageView icon;

        @BindView(R.id.row_category_budget_expense_label)
        TextView tvLabel;

        @BindView(R.id.row_category_budget_expense_budget)
        TextView tvBudget;

        @BindView(R.id.row_category_budget_expense_spending)
        TextView tvSpending;

        @BindView(R.id.row_category_budget_expense_remaining)
        TextView tvRemaining;

        @BindView(R.id.row_category_budget_expense_progress_bar)
        ProgressBar progressBar;

        ViewHolderBudget(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderNoBudget extends RecyclerView.ViewHolder{

        @BindView(R.id.row_category_no_budget_expense_icon)
        ImageView icon;

        @BindView(R.id.row_category_no_budget_expense_label)
        TextView tvLabel;

        @BindView(R.id.row_category_no_budget_expense_spending)
        TextView tvSpending;

        ViewHolderNoBudget(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderIncome extends RecyclerView.ViewHolder{

        @BindView(R.id.row_category_budget_income_icon)
        ImageView icon;

        @BindView(R.id.row_category_budget_income_label)
        TextView tvLabel;

        @BindView(R.id.row_category_budget_income_income)
        TextView tvIncome;

        ViewHolderIncome(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
