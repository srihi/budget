package com.benjamin.ledet.budget.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.CategoriesSpinAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.AutomaticTransaction;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class AmountActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_amount_categories)
    Spinner categoriesSpinner;

    @BindView(R.id.activity_amount_amount)
    EditText amountEditText;

    @BindView(R.id.activity_amount_label)
    EditText labelEditText;

    @BindView(R.id.activity_amount_day)
    EditText dayEditText;

    @BindView(R.id.activity_amount_automatic_transaction)
    Switch automaticTransactionSwitch;

    @BindView(R.id.activity_amount_error)
    TextView errorTextview;

    private boolean addMode = true;
    private boolean automaticTransactionMode = false;
    private DatabaseHandler databaseHandler;
    private Amount amount;
    private AutomaticTransaction automaticTransaction;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_amount, menu);
        Drawable drawable = menu.findItem(R.id.action_save).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
        menu.findItem(R.id.action_save).setIcon(drawable);

        if (!addMode){
            menu.findItem(R.id.action_delete).setVisible(true);
            drawable = menu.findItem(R.id.action_delete).getIcon();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
            menu.findItem(R.id.action_delete).setIcon(drawable);
        }
        return true;
    }

    //return to the previous fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_save:

                if (checkAmount()){
                    Category categorySelected = (Category) categoriesSpinner.getSelectedItem();
                    String label = labelEditText.getText().toString();
                    if (label.length() == 0){
                        label = categorySelected.getLabel();
                    }
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    if(dayEditText.getText().length() > 0){
                        day = Integer.parseInt(dayEditText.getText().toString());
                    }
                    if (automaticTransactionSwitch.isChecked()){
                        automaticTransactionMode = true;
                    }
                    if (automaticTransactionMode){
                        if (addMode){
                            automaticTransaction.setCategory(categorySelected);
                            automaticTransaction.setAmount(Double.valueOf(amountEditText.getText().toString()));
                            automaticTransaction.setDay(day);
                            automaticTransaction.setLabel(label);
                            databaseHandler.addAutomaticAmount(automaticTransaction);
                        } else {
                            databaseHandler.updateAutomaticAmount(automaticTransaction,categorySelected,label,day,Double.valueOf(amountEditText.getText().toString()));
                        }
                    } else {
                        if (addMode){
                            amount.setCategory(categorySelected);
                            amount.setAmount(Double.valueOf(amountEditText.getText().toString()));
                            amount.setDay(day);
                            amount.setLabel(label);
                            databaseHandler.addAmount(amount);
                        } else {
                            databaseHandler.updateAmount(amount,categorySelected,label,day,Double.valueOf(amountEditText.getText().toString()));
                        }
                    }
                    finish();
                }
                return true;

            case R.id.action_delete:

                if (automaticTransactionMode){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
                    TextView title = new TextView(this);
                    title.setText(R.string.delete);
                    title.setTextColor(Color.RED);
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    builder.setCustomTitle(title);
                    if (automaticTransaction.getCategory().isIncome()){
                        builder.setMessage(getString(R.string.delete_automatic_income_message, automaticTransaction.getLabel()));
                    } else {
                        builder.setMessage(getString(R.string.delete_automatic_expense_message, automaticTransaction.getLabel()));
                    }
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseHandler.deleteAutomaticAmount(automaticTransaction);
                            finish();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
                    TextView title = new TextView(this);
                    title.setText(R.string.delete);
                    title.setTextColor(Color.RED);
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    builder.setCustomTitle(title);
                    if (amount.getCategory().isIncome()){
                        builder.setMessage(getString(R.string.delete_income_message,amount.getLabel()));
                    } else {
                        builder.setMessage(getString(R.string.delete_expense_message,amount.getLabel()));
                    }
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseHandler.deleteAmount(amount);
                            finish();
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

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);
        ButterKnife.bind(this);

        databaseHandler = new DatabaseHandler(this);
        Month month = databaseHandler.getMonth(getIntent().getExtras().getInt("month"),getIntent().getExtras().getInt("year"));
        OrderedRealmCollection<Category> categories;

        if (getIntent().hasExtra("income")){
            categories = databaseHandler.getUnarchivedCategoriesIncome();
            toolbar.setTitle(getString(R.string.new_income));
        } else {
            categories = databaseHandler.getUnarchivedCategoriesExpense();
            toolbar.setTitle(getString(R.string.new_expense));
        }

        if (getIntent().hasExtra("automatic")){
            automaticTransactionMode = true;
            automaticTransactionSwitch.setVisibility(View.GONE);
            if (getIntent().hasExtra("income")){
                toolbar.setTitle(getString(R.string.new_automatic_income));
            } else {
                toolbar.setTitle(getString(R.string.new_automatic_expense));
            }
        }

        //display toolbar
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CategoriesSpinAdapter categoriesSpinAdapter = new CategoriesSpinAdapter(this,categories);
        categoriesSpinner.setAdapter(categoriesSpinAdapter);

        if (getIntent().hasExtra("amount") ){
            addMode = false;
            amount = databaseHandler.getAmount(getIntent().getExtras().getLong("amount"));
            categoriesSpinner.setSelection(categories.indexOf(amount.getCategory()));
            amountEditText.setText(String.valueOf(amount.getAmount()));
            labelEditText.setText(amount.getLabel());
            dayEditText.setText(String.valueOf(amount.getDay()));
            toolbar.setTitle(amount.getLabel());
            automaticTransactionSwitch.setVisibility(View.GONE);
        }

        if (getIntent().hasExtra("automatic_amount") ){
            addMode = false;
            automaticTransaction = databaseHandler.getAutomaticAmount(getIntent().getExtras().getLong("automatic_amount"));
            categoriesSpinner.setSelection(categories.indexOf(automaticTransaction.getCategory()));
            amountEditText.setText(String.valueOf(automaticTransaction.getAmount()));
            labelEditText.setText(automaticTransaction.getLabel());
            dayEditText.setText(String.valueOf(automaticTransaction.getDay()));
            toolbar.setTitle(automaticTransaction.getLabel());
        }

        if (addMode){
            amount = new Amount();
            amount.setId(databaseHandler.getAmountNextKey());
            amount.setMonth(month);

            automaticTransaction = new AutomaticTransaction();
            automaticTransaction.setId(databaseHandler.getAutomaticTransactionNextKey());
            automaticTransaction.setMonthOfCreation(month);
        }

    }

    private boolean checkAmount(){

        String text = "";
        boolean result = true;

        if (TextUtils.isEmpty(amountEditText.getText().toString().trim())){
            text = getString(R.string.you_must_enter_amount);
            result = false;
        }
        errorTextview.setText(text);

        return result;
    }
}
