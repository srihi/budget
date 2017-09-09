package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.CategoryExpenseRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.tool.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class ArchivedCategoriesExpenseFragment extends Fragment {

    @BindView(R.id.fragment_archived_category_expense_ll)
    LinearLayout linearLayout;

    @BindView(R.id.fragment_archived_category_expense_rv)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_archived_categories_expense, container, false);
        ButterKnife.bind(this, v);

        final DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        final OrderedRealmCollection<Category> archivedCategoriesExpense = databaseHandler.getArchivedCategoriesExpense();

        //setup RecyclerView for categories expense
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        CategoryExpenseRecyclerViewAdapter adapter = new CategoryExpenseRecyclerViewAdapter(archivedCategoriesExpense, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Category category = archivedCategoriesExpense.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                TextView title = new TextView(getContext());
                title.setText(getString(R.string.what_do_you_want_to_do));
                title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                //builder.setMessage(getString(R.string.activity_category_management_archive_category_description,category.getLabel()));
                builder.setNeutralButton(getString(R.string.unarchive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                        TextView title = new TextView(getContext());
                        title.setText(R.string.unarchive);
                        title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                        title.setGravity(Gravity.CENTER);
                        title.setTextSize(22);
                        builder.setCustomTitle(title);
                        builder.setMessage(getString(R.string.unarchive_category_message, category.getLabel()));
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseHandler.unarchiveCategory(category);
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
                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
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

            @Override
            public void onLongItemClick(final View view, int position) {

            }
        }));

        return v;
    }
}