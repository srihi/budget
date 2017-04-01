package com.benjamin.ledet.budget.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.model.Amount;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by benjaminledet on 10/03/2017.
 */

public class AmountRecyclerViewAdapter extends RecyclerView.Adapter<AmountRecyclerViewAdapter.ViewHolder> {

    private List<Amount> mAmounts;
    private final Context mContext;
    private DatabaseHandler db;

    public AmountRecyclerViewAdapter(List<Amount> mAmounts, Context context) {
        this.mAmounts = mAmounts;
        this.mContext = context;
        this.db = new DatabaseHandler(context);
    }

    @Override
    public AmountRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_amount, parent, false);

        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AmountRecyclerViewAdapter.ViewHolder holder, int position) {
        final Amount selectedAmount = mAmounts.get(position);
        holder.label.setText(selectedAmount.getLabel());
        String textDate = mContext.getResources().getString(R.string.date,selectedAmount.getDay(),selectedAmount.getMonth().getMonth(),selectedAmount.getMonth().getYear());
        holder.date.setText(textDate);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String textAmount = mContext.getResources().getString(R.string.amount,df.format(selectedAmount.getAmount()));
        holder.amount.setText(textAmount);
    }

    @Override
    public int getItemCount() {
        return mAmounts.size();
    }

    public void replaceData(List<Amount> customers){
        mAmounts = customers;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_amount_amount)
        TextView amount;

        @BindView(R.id.row_amount_label)
        TextView label;

        @BindView(R.id.row_amount_date)
        TextView date;

        @BindView(R.id.row_amount_delete)
        Button delete;

        @BindView(R.id.row_amount_update)
        Button update;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Amount selectedAmount = mAmounts.get(getLayoutPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    if (selectedAmount.getCategory().isIncome()){
                        builder.setTitle("Supprimer un revenu");
                    }else{
                        builder.setTitle("Supprimer une dépense");
                    }

                    builder.setIcon(R.drawable.ic_delete);
                    if (selectedAmount.getCategory().isIncome()){
                        builder.setMessage("Voulez-vous vraiment supprimer le revenu \"" + selectedAmount.getLabel() + "\" ?");
                    }else{
                        builder.setMessage("Voulez-vous vraiment supprimer la dépense \"" + selectedAmount.getLabel() + "\" ?");
                    }

                    builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            db.deleteAmount(selectedAmount);
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }
}