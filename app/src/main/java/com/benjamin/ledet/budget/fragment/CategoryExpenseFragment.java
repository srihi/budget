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
import com.benjamin.ledet.budget.adapter.ManagementCategoryRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by benjaminledet on 06/03/2017.
 */

public class CategoryExpenseFragment extends Fragment {

    @BindView(R.id.fab_category_management_expense)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.rv_category_management_expense)
    RecyclerView categoriesExpenseRecyclerView;

    private DatabaseHandler databaseHandler;

    private List<Category> categoriesExpense;
    private ManagementCategoryRecyclerViewAdapter categoriesExpenseAdapter;
    private RecyclerView.LayoutManager layoutManagerCategoriesExpense;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category_expense,container,false);
        ButterKnife.bind(this,v);

        databaseHandler = new DatabaseHandler(this.getContext());

        //setup RecyclerView for categories expense
        categoriesExpense = databaseHandler.getCategoriesExpense();
        layoutManagerCategoriesExpense = new LinearLayoutManager(this.getContext());
        categoriesExpenseAdapter = new ManagementCategoryRecyclerViewAdapter(categoriesExpense, this.getContext());
        categoriesExpenseRecyclerView.setLayoutManager(layoutManagerCategoriesExpense);
        categoriesExpenseRecyclerView.setAdapter(categoriesExpenseAdapter);
        //put a line between each element in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesExpenseRecyclerView.getContext(),LinearLayoutManager.VERTICAL);
        categoriesExpenseRecyclerView.addItemDecoration(dividerItemDecoration);

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
                builder.setTitle(getResources().getString(R.string.ajouter_categorie));

                builder.setIcon(R.drawable.ic_add_circle);
                builder.setPositiveButton(getResources().getString(R.string.confirmer), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //add the category in database
                        Category categoryToAdd = new Category(databaseHandler.getCategoryNextKey(),etAddLibelleCategorie.getText().toString(),false);
                        databaseHandler.addCategory(categoryToAdd);
                        categoriesExpenseAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
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