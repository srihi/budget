package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.CategoryManagementRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class CategoryIncomeFragment extends Fragment {

    @BindView(R.id.fab_category_income)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.rv_category_income)
    RecyclerView categoriesIncomeRecyclerView;

    private DatabaseHandler databaseHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category_income,container,false);
        ButterKnife.bind(this, v);

        databaseHandler = new DatabaseHandler(getContext());
        OrderedRealmCollection<Category> categoriesIncome = databaseHandler.getCategoriesIncome();

        //setup RecyclerView for categories income
        RecyclerView.LayoutManager layoutManagerCategoriesIncome = new LinearLayoutManager(getContext());
        CategoryManagementRecyclerViewAdapter categoriesIncomeAdapter = new CategoryManagementRecyclerViewAdapter(categoriesIncome,getContext());
        categoriesIncomeRecyclerView.setLayoutManager(layoutManagerCategoriesIncome);
        categoriesIncomeRecyclerView.setAdapter(categoriesIncomeAdapter);
        categoriesIncomeRecyclerView.setHasFixedSize(true);
        categoriesIncomeRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //add category
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_add_category, null);
                final EditText etAddLibelleCategorie = (EditText) inflator.findViewById(R.id.alert_dialog_add_label_category);
                builder.setView(inflator);
                TextView title = new TextView(getContext());
                title.setText(R.string.activity_category_management_add_category);
                title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //add the category in database
                        if (databaseHandler.findCategoryIncomeByLabel(etAddLibelleCategorie.getText().toString()) != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                            TextView title = new TextView(getContext());
                            title.setText(R.string.activity_category_management_add_category);
                            title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                            title.setGravity(Gravity.CENTER);
                            title.setTextSize(22);
                            builder.setCustomTitle(title);
                            builder.setMessage(R.string.activity_category_management_duplicate_category_add);
                            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Category categoryToAdd = new Category(databaseHandler.getCategoryNextKey(), etAddLibelleCategorie.getText().toString(), true);
                                    databaseHandler.addCategory(categoryToAdd);
                                    Snackbar snackbar = Snackbar.make(view, R.string.activity_category_management_add_category_expense_message, Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.PrimaryColor));
                                    snackbar.show();
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Category categoryToAdd = new Category(databaseHandler.getCategoryNextKey(), etAddLibelleCategorie.getText().toString(), true);
                            databaseHandler.addCategory(categoryToAdd);
                            Snackbar snackbar = Snackbar.make(view, R.string.activity_category_management_add_category_expense_message, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.PrimaryColor));
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

        return v;
    }
}