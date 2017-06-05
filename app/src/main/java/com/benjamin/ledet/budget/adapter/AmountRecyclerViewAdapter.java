package com.benjamin.ledet.budget.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Amount;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(AmountRecyclerViewAdapter.ViewHolder holder, int position) {
        final Amount selectedAmount = mAmounts.get(position);
        holder.label.setText(selectedAmount.getLabel());
        String textDate = mContext.getString(R.string.amount_recycler_view_adapter_date,selectedAmount.getDay(),selectedAmount.getMonth().getMonth(),selectedAmount.getMonth().getYear());
        holder.date.setText(textDate);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String textAmount = mContext.getString(R.string.amount,df.format(selectedAmount.getAmount()));
        holder.amount.setText(textAmount);
    }

    @Override
    public int getItemCount() {
        return mAmounts.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

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

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Amount selectedAmount = mAmounts.get(getLayoutPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext,R.style.CustomAlertDialog);
                    final LayoutInflater layoutInflater = ((Activity)mContext).getLayoutInflater();
                    final View inflator = layoutInflater.inflate(R.layout.alert_dialog_update_amount, null);
                    builder.setView(inflator);
                    TextView title = new TextView(mContext);
                    title.setTextColor(ContextCompat.getColor(mContext,R.color.PrimaryColor));
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    if (selectedAmount.getCategory().isIncome()){
                        title.setText(mContext.getString(R.string.activity_amount_update_income_label,selectedAmount.getLabel()));
                    }else{
                        title.setText(mContext.getString(R.string.activity_amount_update_expense_label,selectedAmount.getLabel()));
                    }
                    builder.setCustomTitle(title);
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText etUpdateLabel= (EditText) inflator.findViewById(R.id.alert_dialog_update_edit_amount_label);
                            EditText etUpdateAmount= (EditText) inflator.findViewById(R.id.alert_dialog_update_edit_amount_amount);
                            String label = etUpdateLabel.getText().toString();
                            if (label.length() == 0){
                                label = selectedAmount.getLabel();
                            }
                            db.updateAmount(selectedAmount,label,Double.parseDouble(etUpdateAmount.getText().toString()));
                            notifyDataSetChanged();
                            if (selectedAmount.getCategory().isIncome()){
                                Snackbar snackbar = Snackbar.make(view , R.string.activity_amount_update_income_message, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.PrimaryColor));
                                snackbar.show();
                            }else{
                                Snackbar snackbar = Snackbar.make(view , R.string.activity_amount_update_expense_message, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.PrimaryColor));
                                snackbar.show();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Amount selectedAmount = mAmounts.get(getLayoutPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
                    TextView title = new TextView(mContext);
                    title.setTextColor(Color.RED);
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    if (selectedAmount.getCategory().isIncome()){
                        title.setText(R.string.activity_amount_delete_income_label);
                    }else{
                        title.setText(R.string.activity_amount_delete_expense_label);
                    }
                    builder.setCustomTitle(title);
                    if (selectedAmount.getCategory().isIncome()){
                        builder.setMessage(mContext.getString(R.string.activity_amount_delete_income_description,selectedAmount.getLabel()));
                    }else{
                        builder.setMessage(mContext.getString(R.string.activity_amount_delete_expense_description,selectedAmount.getLabel()));
                    }

                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Boolean isIncome = selectedAmount.getCategory().isIncome();
                            db.deleteAmount(selectedAmount);
                            notifyDataSetChanged();
                            if (isIncome){
                                Snackbar snackbar = Snackbar.make(view , R.string.activity_amount_delete_income_message, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.PrimaryColor));
                                snackbar.show();
                            }else{
                                Snackbar snackbar = Snackbar.make(view , R.string.activity_amount_delete_expense_message, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.PrimaryColor));
                                snackbar.show();
                            }
                            if(mAmounts.size() == 0){
                                ((Activity)mContext).finish();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
