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
    private TextView tvNoArchived;
    private RecyclerView archivedRecyclerView;

    public CategoryManagementRecyclerViewAdapter(OrderedRealmCollection<Category> data, Context context, TextView tvNoArchived, RecyclerView archivedRecyclerView) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
        this.tvNoArchived = tvNoArchived;
        this.archivedRecyclerView = archivedRecyclerView;
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
        //noinspection ConstantConditions
        holder.label.setText(obj.getLabel());
        holder.icon.setImageDrawable(obj.getIcon());

        if(obj.isArchived()) {
            holder.update.setVisibility(View.GONE);
            holder.archive.setVisibility(View.GONE);
            holder.unarchive.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(context).inflate(R.layout.alert_dialog_update_category, null);
                builder.setView(inflator);
                TextView title = new TextView(context);
                title.setText(context.getString(R.string.fragment_category_expense_income_update_category_label,obj.getLabel()));
                title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final EditText etUpdateLabel = (EditText) inflator.findViewById(R.id.alert_dialog_update_category_label);
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
                            title.setText(context.getString(R.string.fragment_category_expense_income_update_category_label,obj.getLabel()));
                            title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                            title.setGravity(Gravity.CENTER);
                            title.setTextSize(22);
                            builder.setCustomTitle(title);
                            if (obj.isIncome()){
                                builder.setMessage(R.string.fragment_category_income_update_duplicate_category_message);
                            }else{
                                builder.setMessage(R.string.fragment_category_expense_update_duplicate_category_message);
                            }
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    databaseHandler.updateCategory(obj,etUpdateLabel.getText().toString());
                                    String message = context.getString(R.string.fragment_category_expense_update_category_success);
                                    if (obj.isIncome()){
                                        message = context.getString(R.string.fragment_category_income_update_category_success);
                                    }
                                    Snackbar snackbar = Snackbar.make(v , message, Snackbar.LENGTH_SHORT);
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
                            databaseHandler.updateCategory(obj,etUpdateLabel.getText().toString());;
                            String message = context.getString(R.string.fragment_category_expense_update_category_success);
                            if (obj.isIncome()){
                                message = context.getString(R.string.fragment_category_income_update_category_success);
                            }
                            Snackbar snackbar = Snackbar.make(v , message, Snackbar.LENGTH_SHORT);
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

        holder.archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.CustomAlertDialog);
                TextView title = new TextView(context);
                title.setText(R.string.activity_category_management_archive_category_label);
                title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setMessage(context.getString(R.string.activity_category_management_archive_category_description,obj.getLabel()));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseHandler.archiveCategory(obj);
                        tvNoArchived.setVisibility(View.GONE);
                        int padding_in_dp = 70;  // 70 dps
                        final float scale = context.getResources().getDisplayMetrics().density;
                        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                        archivedRecyclerView.setPadding(0,0,0,padding_in_px);
                        Snackbar snackbar = Snackbar.make(v , R.string.activity_category_management_archive_category_success, Snackbar.LENGTH_SHORT);
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

        holder.unarchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.CustomAlertDialog);
                TextView title = new TextView(context);
                title.setText(R.string.activity_category_management_unarchive_category_label);
                title.setTextColor(ContextCompat.getColor(context,R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setMessage(context.getString(R.string.activity_category_management_unarchive_category_description,obj.getLabel()));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //noinspection ConstantConditions
                        databaseHandler.unarchiveCategory(obj);
                        if(getData().isEmpty()){
                            tvNoArchived.setVisibility(View.VISIBLE);
                            archivedRecyclerView.setPadding(0,0,0,0);
                        }
                        Snackbar snackbar = Snackbar.make(v , R.string.activity_category_management_unarchive_category_success, Snackbar.LENGTH_SHORT);
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
                        if(getData().isEmpty()){
                            tvNoArchived.setVisibility(View.VISIBLE);
                            archivedRecyclerView.setPadding(0,0,0,0);
                        }
                        Snackbar snackbar = Snackbar.make(v , R.string.activity_category_management_delete_category_success, Snackbar.LENGTH_SHORT);
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

        @BindView(R.id.row_category_management_archive)
        Button archive;

        @BindView(R.id.row_category_management_unarchive)
        Button unarchive;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
