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
import android.widget.ImageView;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.model.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryManagementRecyclerViewAdapter extends RecyclerView.Adapter<CategoryManagementRecyclerViewAdapter.ViewHolder> {

    private List<Category> mCategorys;
    private final Context mContext;
    private DatabaseHandler db;

    public CategoryManagementRecyclerViewAdapter(List<Category> mCategorys, Context context) {
        this.mCategorys = mCategorys;
        this.mContext = context;
        this.db = new DatabaseHandler(context);
    }

    @Override
    public CategoryManagementRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_management, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(CategoryManagementRecyclerViewAdapter.ViewHolder holder, int position) {
        final Category selectedCategory = mCategorys.get(position);
        holder.label.setText(selectedCategory.getLabel());
        holder.icon.setImageDrawable(selectedCategory.getIcon());

    }

    @Override
    public int getItemCount() {
        return mCategorys.size();
    }

   class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_management_icon)
        ImageView icon;

        @BindView(R.id.row_category_management_label)
        TextView label;

        @BindView(R.id.row_category_management_delete)
        Button delete;

        @BindView(R.id.row_category_management_update)
        Button update;


        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Category selectedCategory = mCategorys.get(getLayoutPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
                    final LayoutInflater layoutInflater = ((Activity)mContext).getLayoutInflater();
                    final View inflator = layoutInflater.inflate(R.layout.alert_dialog_update_category, null);
                    builder.setView(inflator);
                    TextView title = new TextView(mContext);
                    title.setText(mContext.getString(R.string.activity_category_management_update_category_label,selectedCategory.getLabel()));
                    title.setTextColor(ContextCompat.getColor(mContext,R.color.PrimaryColor));
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    builder.setCustomTitle(title);
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final EditText etUpdateLabel = (EditText) inflator.findViewById(R.id.alert_dialog_update_edit_category);
                            Category category;
                            if (selectedCategory.isIncome()){
                                category = db.findCategoryIncomeByLabel(etUpdateLabel.getText().toString());
                            }else{
                                category = db.findCategoryExpenseByLabel(etUpdateLabel.getText().toString());
                            }
                            if (category != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
                                TextView title = new TextView(mContext);
                                title.setText(mContext.getString(R.string.activity_category_management_update_category_label,selectedCategory.getLabel()));
                                title.setTextColor(ContextCompat.getColor(mContext,R.color.PrimaryColor));
                                title.setGravity(Gravity.CENTER);
                                title.setTextSize(22);
                                builder.setCustomTitle(title);
                                builder.setMessage(R.string.activity_category_management_duplicate_category_update);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        db.updateCategory(selectedCategory,etUpdateLabel.getText().toString());
                                        notifyDataSetChanged();
                                        Snackbar snackbar = Snackbar.make(view , R.string.activity_category_management_update_category_message, Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.PrimaryColor));
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
                                db.updateCategory(selectedCategory,etUpdateLabel.getText().toString());
                                notifyDataSetChanged();
                                Snackbar snackbar = Snackbar.make(view , R.string.activity_category_management_update_category_message, Snackbar.LENGTH_SHORT);
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
                    final Category selectedCategory = mCategorys.get(getLayoutPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext,R.style.CustomAlertDialog);
                    TextView title = new TextView(mContext);
                    title.setText(R.string.activity_category_management_delete_category_label);
                    title.setTextColor(Color.RED);
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    builder.setCustomTitle(title);
                    builder.setMessage(mContext.getString(R.string.activity_category_management_delete_category_description,selectedCategory.getLabel()));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.deleteCategory(selectedCategory);
                            notifyDataSetChanged();
                            Snackbar snackbar = Snackbar.make(view , R.string.activity_category_management_delete_category_message, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.PrimaryColor));
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
    }
}