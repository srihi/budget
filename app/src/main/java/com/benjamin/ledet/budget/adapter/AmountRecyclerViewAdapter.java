package com.benjamin.ledet.budget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.Amount;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class AmountRecyclerViewAdapter extends RealmRecyclerViewAdapter<Amount, AmountRecyclerViewAdapter.MyViewHolder> {

    private Context context;

    public AmountRecyclerViewAdapter(OrderedRealmCollection<Amount> data, Context context) {
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
        final Amount obj = getItem(position);
        //noinspection ConstantConditions
        holder.label.setText(obj.getLabel());
        holder.date.setText(context.getString(R.string.full_date, obj.getDay(), obj.getMonth().getMonth(), obj.getMonth().getYear()));
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String textAmount = context.getString(R.string.amount,df.format(obj.getAmount()));
        holder.amount.setText(textAmount);

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
