package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.activity.CategoryIncomeActivity;
import com.benjamin.ledet.budget.adapter.CategoryIncomeRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.tool.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class CategoriesIncomeFragment extends Fragment {

    @BindView(R.id.fragment_category_income_fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.fragment_category_income_rv)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_categories_income, container, false);
        ButterKnife.bind(this, v);

        final DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        final OrderedRealmCollection<Category> categoriesIncome = databaseHandler.getUnarchivedCategoriesIncome();

        //setup RecyclerView for categories income
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        CategoryIncomeRecyclerViewAdapter adapter = new CategoryIncomeRecyclerViewAdapter(categoriesIncome, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categoriesIncome.get(position);
                Intent intent = new Intent(getContext(),CategoryIncomeActivity.class);
                intent.putExtra("category",category.getId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(final View view, int position) {
                final Category category = categoriesIncome.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                TextView title = new TextView(getContext());
                title.setText(getString(R.string.what_do_you_want_to_do));
                title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setNeutralButton(getString(R.string.archive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                        TextView title = new TextView(getContext());
                        title.setText(R.string.archive);
                        title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                        title.setGravity(Gravity.CENTER);
                        title.setTextSize(22);
                        builder.setCustomTitle(title);
                        builder.setMessage(getString(R.string.archive_category_message, category.getLabel()));
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseHandler.archiveCategory(category);
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                        TextView title = new TextView(getContext());
                        title.setText(R.string.delete);
                        title.setTextColor(Color.RED);
                        title.setGravity(Gravity.CENTER);
                        title.setTextSize(22);
                        builder.setCustomTitle(title);
                        builder.setMessage(getString(R.string.delete_category_message,category.getLabel()));
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseHandler.deleteCategory(category);
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
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        }));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CategoryIncomeActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }
}