package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.AutomaticAmountRecyclerViewAdapter;
import com.benjamin.ledet.budget.adapter.CategorySpinAdapter;
import com.benjamin.ledet.budget.model.AutomaticAmount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class AutomaticIncomeFragment extends Fragment {

    @BindView(R.id.fragment_automatic_income_fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.fragment_automatic_income_rv)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_automatic_income, container, false);
        ButterKnife.bind(this, v);

        final DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        OrderedRealmCollection<AutomaticAmount> automaticIncomes = databaseHandler.getAutomaticsIncomes();
        final OrderedRealmCollection<Category> categoriesIncome = databaseHandler.getCategoriesIncome();

        //setup recylerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        AutomaticAmountRecyclerViewAdapter adapter = new AutomaticAmountRecyclerViewAdapter(automaticIncomes, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //add automatic amount
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_add_amount, null);
                final EditText etLabel = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_label);
                final EditText etAmount = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_amount);
                final EditText etDay = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_day);
                etDay.setHint(R.string.alert_dialog_add_amount_day_default);
                final Spinner spCategories = (Spinner) inflator.findViewById(R.id.alert_dialog_add_amount_categories);
                CategorySpinAdapter categoriesSpinAdapter = new CategorySpinAdapter(getContext(), categoriesIncome);
                spCategories.setAdapter(categoriesSpinAdapter);
                builder.setView(inflator);
                TextView title = new TextView(getContext());
                title.setText(R.string.fragment_automatic_income_add_income_label);
                title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Category categorySelected = (Category) spCategories.getSelectedItem();
                        String label = etLabel.getText().toString();
                        if (label.length() == 0){
                            label = categorySelected.getLabel();
                        }
                        final AutomaticAmount automaticAmount = new AutomaticAmount();
                        automaticAmount.setId(databaseHandler.getAutomaticAmountNextKey());
                        automaticAmount.setCategory(categorySelected);
                        automaticAmount.setMonthOfCreation(databaseHandler.getActualMonth());
                        if(etDay.getText().length() == 0){
                            automaticAmount.setDay(1);
                        }else{
                            automaticAmount.setDay(Integer.parseInt(etDay.getText().toString()));
                        }
                        automaticAmount.setLabel(label);
                        automaticAmount.setAmount(Double.parseDouble(etAmount.getText().toString()));

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                        TextView title = new TextView(getContext());
                        title.setText(R.string.fragment_automatic_income_add_income_label);
                        title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                        title.setGravity(Gravity.CENTER);
                        title.setTextSize(22);
                        builder.setCustomTitle(title);
                        builder.setMessage(R.string.fragment_automatic_income_add_income_message);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                automaticAmount.setNextMonth(false);
                                databaseHandler.addAutomaticAmount(automaticAmount);
                                Snackbar snackbar = Snackbar.make(v , R.string.fragment_automatic_income_add_income_success, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.PrimaryColor));
                                snackbar.show();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                automaticAmount.setNextMonth(true);
                                databaseHandler.addAutomaticAmount(automaticAmount);
                                Snackbar snackbar = Snackbar.make(v , R.string.fragment_automatic_income_add_income_success, Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.PrimaryColor));
                                snackbar.show();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
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