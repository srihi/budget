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

    @BindView(R.id.fragment_category_income_fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.fragment_category_income_rv)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_category_income_archived_rv)
    RecyclerView archivedRecyclerView;

    @BindView(R.id.fragment_category_income_tv_no_archived_categories)
    TextView tvNoArchived;

    private DatabaseHandler databaseHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category_income, container, false);
        ButterKnife.bind(this, v);

        databaseHandler = new DatabaseHandler(getContext());
        OrderedRealmCollection<Category> categoriesIncome = databaseHandler.getUnarchivedCategoriesIncome();
        OrderedRealmCollection<Category> archivedCategoriesIncome = databaseHandler.getArchivedCategoriesIncome();

        if (archivedCategoriesIncome.isEmpty()){
            tvNoArchived.setVisibility(View.VISIBLE);
            archivedRecyclerView.setPadding(0,0,0,0);
        }

        //setup RecyclerView for categories income
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        CategoryManagementRecyclerViewAdapter adapter = new CategoryManagementRecyclerViewAdapter(categoriesIncome, getContext(), tvNoArchived, archivedRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //setup RecyclerView for archived categories income
        RecyclerView.LayoutManager archivedLayoutManager = new LinearLayoutManager(getContext());
        CategoryManagementRecyclerViewAdapter archivedAdapter = new CategoryManagementRecyclerViewAdapter(archivedCategoriesIncome, getContext(), tvNoArchived, archivedRecyclerView);
        archivedRecyclerView.setLayoutManager(archivedLayoutManager);
        archivedRecyclerView.setAdapter(archivedAdapter);
        archivedRecyclerView.setHasFixedSize(false);
        archivedRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //add category
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_add_category, null);
                final EditText etAddLibelleCategorie = (EditText) inflator.findViewById(R.id.alert_dialog_add_category_label);
                builder.setView(inflator);
                TextView title = new TextView(getContext());
                title.setText(R.string.fragment_category_income_add_category_label);
                title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //add the category in database
                        if (databaseHandler.findCategoryIncomeByLabel(etAddLibelleCategorie.getText().toString()) != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                            TextView title = new TextView(getContext());
                            title.setText(R.string.fragment_category_income_add_category_label);
                            title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                            title.setGravity(Gravity.CENTER);
                            title.setTextSize(22);
                            builder.setCustomTitle(title);
                            builder.setMessage(R.string.fragment_category_income_add_duplicate_category_message);
                            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Category categoryToAdd = new Category(databaseHandler.getCategoryNextKey(), etAddLibelleCategorie.getText().toString(), true);
                                    databaseHandler.addCategory(categoryToAdd);
                                    Snackbar snackbar = Snackbar.make(view, R.string.fragment_category_income_add_category_success, Snackbar.LENGTH_SHORT);
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
                            Snackbar snackbar = Snackbar.make(view, R.string.fragment_category_income_add_category_success, Snackbar.LENGTH_SHORT);
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