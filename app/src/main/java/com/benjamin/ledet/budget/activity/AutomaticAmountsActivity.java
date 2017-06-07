package com.benjamin.ledet.budget.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.AmountRecyclerViewAdapter;
import com.benjamin.ledet.budget.adapter.CategorySpinAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutomaticAmountsActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_automatic_amount)
    RecyclerView automaticAmountRecyclerView;

    @BindView(R.id.fab_add_automatic_amount)
    FloatingActionButton floatingActionButton;

    private boolean isIncome = true;

    //return to the previous fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_amounts);
        ButterKnife.bind(this);

        final DatabaseHandler databaseHandler = new DatabaseHandler(this);
        final List<Category> categories;
        final List<Amount> automaticAmounts;

        if (getIntent().getExtras().get("amount").equals("expenses")){
            isIncome = false;
        }

        if(isIncome){
            toolbar.setTitle(getString(R.string.activity_preferences_automatic_incomes));
            categories = databaseHandler.getCategoriesIncome();
            automaticAmounts = databaseHandler.getAutomaticsIncomes();
        } else {
            toolbar.setTitle(getString(R.string.activity_preferences_automatic_expenses));
            categories = databaseHandler.getCategoriesExpense();
            automaticAmounts = databaseHandler.getAutomaticsExpenses();
        }

        //display the toolbar
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //setup recylerView
        RecyclerView.LayoutManager layoutManagerAmount = new LinearLayoutManager(this);
        final AmountRecyclerViewAdapter automaticAmountAdapter = new AmountRecyclerViewAdapter(automaticAmounts, this);
        automaticAmountRecyclerView.setLayoutManager(layoutManagerAmount);
        automaticAmountRecyclerView.setAdapter(automaticAmountAdapter);
        //put a line between each element in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(automaticAmountRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        automaticAmountRecyclerView.addItemDecoration(dividerItemDecoration);

        //add an automatic amount
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),R.style.CustomAlertDialog);
                final LayoutInflater layoutInflater = (getLayoutInflater());
                final View inflator = layoutInflater.inflate(R.layout.alert_dialog_add_amount,null);
                final EditText etLabel = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_label);
                final EditText etAmount = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_amount);
                final EditText etDay = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_day);
                final Spinner spCategories = (Spinner) inflator.findViewById(R.id.alert_dialog_add_amount_categories);
                CategorySpinAdapter categoriesSpinAdapter = new CategorySpinAdapter(AutomaticAmountsActivity.this,categories);
                spCategories.setAdapter(categoriesSpinAdapter);
                builder.setView(inflator);
                TextView title = new TextView(AutomaticAmountsActivity.this);
                title.setText(R.string.fragment_expense_add_expense);
                title.setTextColor(ContextCompat.getColor(AutomaticAmountsActivity.this,R.color.PrimaryColor));
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
                        Amount amount = new Amount();
                        amount.setId(databaseHandler.getAmountNextKey());
                        amount.setLabel(label);
                        amount.setCategory(categorySelected);
                        Month month = databaseHandler.getMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,Calendar.getInstance().get(Calendar.YEAR));
                        amount.setMonth(month);
                        if(etDay.getText().length() == 0){
                            amount.setDay(1);
                        }else{
                            amount.setDay(Integer.parseInt(etDay.getText().toString()));
                        }
                        amount.setAmount(Double.parseDouble(etAmount.getText().toString()));
                        amount.setAutomatic(true);
                        databaseHandler.addAmount(amount);
                        automaticAmountAdapter.notifyDataSetChanged();

                        Snackbar snackbar = Snackbar.make(view , R.string.fragment_expense_add_expense_message, Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(AutomaticAmountsActivity.this, R.color.PrimaryColor));
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
    }
}