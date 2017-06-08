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
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.DatabaseHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class AmountRecyclerViewAdapter extends RealmRecyclerViewAdapter<Amount, AmountRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private DatabaseHandler databaseHandler;

    public AmountRecyclerViewAdapter(OrderedRealmCollection<Amount> data, Context context) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
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
        holder.data = obj;
        //noinspection ConstantConditions
        holder.label.setText(obj.getLabel());
        holder.date.setText(context.getString(R.string.amount_recycler_view_adapter_date, obj.getDay(), obj.getMonth().getMonth(), obj.getMonth().getYear()));
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String textAmount = context.getString(R.string.amount,df.format(obj.getAmount()));
        holder.amount.setText(textAmount);
        
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(context).inflate(R.layout.alert_dialog_update_amount, null);
                builder.setView(inflator);
                TextView title = new TextView(context);
                title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                if (obj.getCategory().isIncome()){
                    title.setText(context.getString(R.string.activity_amount_update_income_label,obj.getLabel()));
                }else{
                    title.setText(context.getString(R.string.activity_amount_update_expense_label,obj.getLabel()));
                }
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText etUpdateLabel= (EditText) inflator.findViewById(R.id.alert_dialog_update_edit_amount_label);
                        EditText etUpdateAmount= (EditText) inflator.findViewById(R.id.alert_dialog_update_edit_amount_amount);
                        String label = etUpdateLabel.getText().toString();
                        if (label.length() == 0){
                            label = obj.getLabel();
                        }
                        if (!etUpdateAmount.getText().toString().equals("")){
                            databaseHandler.updateAmount(obj,label,Double.parseDouble(etUpdateAmount.getText().toString()));
                            if (obj.getCategory().isIncome()){
                                Snackbar snackbar = Snackbar.make(v , R.string.activity_amount_update_income_message, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                                snackbar.show();
                            }else{
                                Snackbar snackbar = Snackbar.make(v , R.string.activity_amount_update_expense_message, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                                snackbar.show();
                            }
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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                TextView title = new TextView(context);
                title.setTextColor(Color.RED);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                if (obj.getCategory().isIncome()){
                    title.setText(R.string.activity_amount_delete_income_label);
                }else{
                    title.setText(R.string.activity_amount_delete_expense_label);
                }
                builder.setCustomTitle(title);
                if (obj.getCategory().isIncome()){
                    builder.setMessage(context.getString(R.string.activity_amount_delete_income_description,obj.getLabel()));
                }else{
                    builder.setMessage(context.getString(R.string.activity_amount_delete_expense_description,obj.getLabel()));
                }

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Boolean isIncome = obj.getCategory().isIncome();
                        databaseHandler.deleteAmount(obj);
                        if (isIncome){
                            Snackbar snackbar = Snackbar.make(v, R.string.activity_amount_delete_income_message, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                            snackbar.show();
                        }else{
                            Snackbar snackbar = Snackbar.make(v , R.string.activity_amount_delete_expense_message, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                            snackbar.show();
                        }
                        //noinspection ConstantConditions
                        if(getData().size() == 0){
                            ((Activity)context).finish();
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

        @BindView(R.id.row_amount_delete)
        Button delete;

        @BindView(R.id.row_amount_update)
        Button update;

        public Amount data;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
