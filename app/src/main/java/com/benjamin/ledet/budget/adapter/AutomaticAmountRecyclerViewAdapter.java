package com.benjamin.ledet.budget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.AutomaticTransaction;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class AutomaticAmountRecyclerViewAdapter extends RealmRecyclerViewAdapter<AutomaticTransaction, AutomaticAmountRecyclerViewAdapter.MyViewHolder> {

    private Context context;

    public AutomaticAmountRecyclerViewAdapter(OrderedRealmCollection<AutomaticTransaction> data, Context context) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_amount, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AutomaticTransaction obj = getItem(position);
        //noinspection ConstantConditions
        if(obj.getLabel().equals(obj.getCategory().getLabel())){
            holder.label.setText(obj.getLabel());
        } else {
            holder.label.setText(obj.getLabel() + " (" + obj.getCategory().getLabel() + ")");
        }

        if (obj.getDay() == 1){
            holder.date.setText(context.getString(R.string.fragment_automatic_expense_income_first_day_of_month));
        }else{
            holder.date.setText(context.getString(R.string.fragment_automatic_expense_income_day_of_month, String.valueOf(obj.getDay())));
        }
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String textAutomaticAmount = context.getString(R.string.amount,df.format(obj.getAmount()));
        holder.amount.setText(textAutomaticAmount);
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_amount_amount)
        TextView amount;

        @BindView(R.id.row_amount_label)
        TextView label;

        @BindView(R.id.row_amount_date)
        TextView date;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
