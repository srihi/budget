package com.benjamin.ledet.budget.adapter;

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
import com.benjamin.ledet.budget.model.AutomaticAmount;
import com.benjamin.ledet.budget.model.DatabaseHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class AutomaticAmountRecyclerViewAdapter extends RealmRecyclerViewAdapter<AutomaticAmount, AutomaticAmountRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private DatabaseHandler databaseHandler;

    public AutomaticAmountRecyclerViewAdapter(OrderedRealmCollection<AutomaticAmount> data, Context context) {
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
        final AutomaticAmount obj = getItem(position);
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

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(context).inflate(R.layout.alert_dialog_update_amount, null);
                final EditText etUpdateLabel= (EditText) inflator.findViewById(R.id.alert_dialog_update_amount_label);
                final EditText etUpdateAmount= (EditText) inflator.findViewById(R.id.alert_dialog_update_amount_amount);
                final EditText etUpdateDay = (EditText) inflator.findViewById(R.id.alert_dialog_update_amount_day);
                builder.setView(inflator);
                TextView title = new TextView(context);
                title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                if (obj.getCategory().isIncome()){
                    title.setText(context.getString(R.string.fragment_automatic_income_update_income_label,obj.getLabel()));
                }else{
                    title.setText(context.getString(R.string.fragment_automatic_expense_update_expense_label,obj.getLabel()));
                }
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String label = obj.getLabel();
                        int day = obj.getDay();
                        double amount = obj.getAmount();
                        if (!etUpdateLabel.getText().toString().equals("")){
                            label = etUpdateLabel.getText().toString();
                        }
                        if(!etUpdateDay.getText().toString().equals("")){
                            day = Integer.parseInt(etUpdateDay.getText().toString());
                        }
                        if(!etUpdateAmount.getText().toString().equals("")){
                            amount = Double.parseDouble(etUpdateAmount.getText().toString());
                        }
                        databaseHandler.updateAutomaticAmount(obj,label,day,amount);
                        if (obj.getCategory().isIncome()){
                            Snackbar snackbar = Snackbar.make(v , R.string.fragment_automatic_expense_update_expense_success, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                            snackbar.show();
                        }else{
                            Snackbar snackbar = Snackbar.make(v , R.string.fragment_automatic_income_update_income_success, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                TextView title = new TextView(context);
                title.setTextColor(Color.RED);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                if (obj.getCategory().isIncome()){
                    title.setText(R.string.fragment_automatic_income_delete_income_label);
                    builder.setMessage(context.getString(R.string.fragment_automatic_income_delete_income_message,obj.getLabel()));
                }else{
                    title.setText(R.string.fragment_automatic_expense_delete_expense_label);
                    builder.setMessage(context.getString(R.string.fragment_automatic_expense_delete_expense_message,obj.getLabel()));
                }
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Boolean isIncome = obj.getCategory().isIncome();
                        databaseHandler.deleteAutomaticAmount(obj);
                        if (isIncome){
                            Snackbar snackbar = Snackbar.make(v, R.string.fragment_automatic_income_delete_income_success, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                            snackbar.show();
                        }else{
                            Snackbar snackbar = Snackbar.make(v , R.string.fragment_automatic_expense_delete_expense_success, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                            snackbar.show();
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

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
