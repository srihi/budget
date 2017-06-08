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
import android.widget.ImageView;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CategoryManagementRecyclerViewAdapter extends RealmRecyclerViewAdapter<Category, CategoryManagementRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private DatabaseHandler databaseHandler;

    public CategoryManagementRecyclerViewAdapter(OrderedRealmCollection<Category> data, Context context) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_management, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Category obj = getItem(position);
        holder.data = obj;
        //noinspection ConstantConditions
        holder.label.setText(obj.getLabel());
        holder.icon.setImageDrawable(obj.getIcon());

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(context).inflate(R.layout.alert_dialog_update_category, null);
                builder.setView(inflator);
                TextView title = new TextView(context);
                title.setText(context.getString(R.string.activity_category_management_update_category_label,obj.getLabel()));
                title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final EditText etUpdateLabel = (EditText) inflator.findViewById(R.id.alert_dialog_update_edit_category);
                        Category category;
                        if (obj.isIncome()){
                            category = databaseHandler.findCategoryIncomeByLabel(etUpdateLabel.getText().toString());
                        }else{
                            category = databaseHandler.findCategoryExpenseByLabel(etUpdateLabel.getText().toString());
                        }
                        //duplicate category
                        if (category != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                            TextView title = new TextView(context);
                            title.setText(context.getString(R.string.activity_category_management_update_category_label,obj.getLabel()));
                            title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                            title.setGravity(Gravity.CENTER);
                            title.setTextSize(22);
                            builder.setCustomTitle(title);
                            builder.setMessage(R.string.activity_category_management_duplicate_category_update);
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    databaseHandler.updateCategory(obj,etUpdateLabel.getText().toString());
                                   // notifyDataSetChanged();
                                    Snackbar snackbar = Snackbar.make(v , R.string.activity_category_management_update_category_message, Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                                    snackbar.show();
                                }
                            });
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            databaseHandler.updateCategory(obj,etUpdateLabel.getText().toString());
                           // notifyDataSetChanged();
                            Snackbar snackbar = Snackbar.make(v , R.string.activity_category_management_update_category_message, Snackbar.LENGTH_SHORT);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.CustomAlertDialog);
                TextView title = new TextView(context);
                title.setText(R.string.activity_category_management_delete_category_label);
                title.setTextColor(Color.RED);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setMessage(context.getString(R.string.activity_category_management_delete_category_description,obj.getLabel()));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseHandler.deleteCategory(obj);
                        //notifyDataSetChanged();
                        Snackbar snackbar = Snackbar.make(v , R.string.activity_category_management_delete_category_message, Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.PrimaryColor));
                        snackbar.show();
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

        @BindView(R.id.row_category_management_icon)
        ImageView icon;

        @BindView(R.id.row_category_management_label)
        TextView label;

        @BindView(R.id.row_category_management_delete)
        Button delete;

        @BindView(R.id.row_category_management_update)
        Button update;

        public Category data;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
