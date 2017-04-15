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

public class CategoryExpenseFragment extends Fragment {

    @BindView(R.id.fab_category_management_expense)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.rv_category_management_expense)
    RecyclerView categoriesExpenseRecyclerView;

    private DatabaseHandler databaseHandler;

    private CategoryManagementRecyclerViewAdapter categoriesExpenseAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category_expense,container,false);
        ButterKnife.bind(this,v);

        databaseHandler = new DatabaseHandler(this.getContext());

        //setup RecyclerView for categories expense
        List<Category> categoriesExpense = databaseHandler.getCategoriesExpense();
        RecyclerView.LayoutManager layoutManagerCategoriesExpense = new LinearLayoutManager(this.getContext());
        categoriesExpenseAdapter = new CategoryManagementRecyclerViewAdapter(categoriesExpense, this.getContext());
        categoriesExpenseRecyclerView.setLayoutManager(layoutManagerCategoriesExpense);
        categoriesExpenseRecyclerView.setAdapter(categoriesExpenseAdapter);
        //put a line between each element in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesExpenseRecyclerView.getContext(),LinearLayoutManager.VERTICAL);
        categoriesExpenseRecyclerView.addItemDecoration(dividerItemDecoration);

        //add category
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                        Category categoryToAdd = new Category(databaseHandler.getCategoryNextKey(),etAddLibelleCategorie.getText().toString(),false);
                        databaseHandler.addCategory(categoryToAdd);
                        categoriesExpenseAdapter.notifyDataSetChanged();
                        Snackbar snackbar = Snackbar.make(view , R.string.activity_category_management_add_category_expense_message, Snackbar.LENGTH_SHORT);
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
            }
        });

        return v;
    }
}