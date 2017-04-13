package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.adapter.CategoryManagementRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryIncomeFragment extends Fragment {

    @BindView(R.id.fab_category_income)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.rv_category_income)
    RecyclerView categoriesIncomeRecyclerView;

    private DatabaseHandler databaseHandler;

    private CategoryManagementRecyclerViewAdapter categoriesIncomeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category_income,container,false);
        ButterKnife.bind(this,v);

        databaseHandler = new DatabaseHandler(this.getContext());

        //setup RecyclerView for categories income
        List<Category> categoriesIncome = databaseHandler.getCategoriesIncome();
        RecyclerView.LayoutManager layoutManagerCategoriesIncome = new LinearLayoutManager(this.getContext());
        categoriesIncomeAdapter = new CategoryManagementRecyclerViewAdapter(categoriesIncome,this.getContext());
        categoriesIncomeRecyclerView.setLayoutManager(layoutManagerCategoriesIncome);
        categoriesIncomeRecyclerView.setAdapter(categoriesIncomeAdapter);
        //put a line between each element in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesIncomeRecyclerView.getContext(),LinearLayoutManager.VERTICAL);
        categoriesIncomeRecyclerView.addItemDecoration(dividerItemDecoration);

        //add category
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final LayoutInflater layoutInflater = (getActivity().getLayoutInflater());
                //get the appropriate view
                final View inflator = layoutInflater.inflate(R.layout.alert_dialog_add_category,null);
                final EditText etAddLibelleCategorie = (EditText) inflator.findViewById(R.id.alert_dialog_add_label_category);
                builder.setView(inflator);
                builder.setTitle(R.string.activity_category_management_add_category);
                builder.setIcon(R.drawable.ic_add_circle);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //add the category in database
                        Category categoryToAdd = new Category(databaseHandler.getCategoryNextKey(),etAddLibelleCategorie.getText().toString(),true);
                        databaseHandler.addCategory(categoryToAdd);
                        categoriesIncomeAdapter.notifyDataSetChanged();
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