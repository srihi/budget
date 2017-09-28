package com.benjamin.ledet.budget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.CategoriesSpinAdapter;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class MergeCategoriesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_merge_categories_radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.activity_merge_categories_expenses)
    RadioButton expensesRadioButton;

    @BindView(R.id.activity_merge_categories_incomes)
    RadioButton incomesRadioButton;

    @BindView(R.id.activity_merge_categories_first)
    Spinner firstCategoriesSpinner;

    @BindView(R.id.activity_merge_categories_second)
    Spinner secondCategoriesSpinner;

    @BindView(R.id.activity_merge_categories_new_label)
    EditText newLabelEditText;

    @BindView(R.id.activity_merge_categories_new_budget)
    EditText newBudgetEditText;

    @BindView(R.id.activity_merge_categories_error)
    TextView errorTextView;

    @BindView(R.id.activity_merge_categories_merge)
    Button mergeButton;

    private DatabaseHandler databaseHandler;
    private boolean expenseMode = true;
    private Category oldCategory1;
    private Category oldCategory2;

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
        setContentView(R.layout.activity_merge_categories);
        ButterKnife.bind(this);

        databaseHandler = new DatabaseHandler(this);
        setSpinners(databaseHandler.getUnarchivedCategoriesExpense());

        //display toolbar
        toolbar.setTitle(getString(R.string.merge_categories));
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.activity_merge_categories_expenses:
                        setSpinners(databaseHandler.getUnarchivedCategoriesExpense());
                        expenseMode = true;
                        break;

                    case R.id.activity_merge_categories_incomes:
                        setSpinners(databaseHandler.getUnarchivedCategoriesIncome());
                        expenseMode = false;
                        break;
                }
            }
        });

        mergeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkMerge()){
                    Category newCategory = new Category();
                    newCategory.setId(databaseHandler.getCategoryNextKey());
                    newCategory.setLabel(newLabelEditText.getText().toString());
                    double budget = 0;
                    if (expenseMode) {
                        if (!TextUtils.isEmpty(newBudgetEditText.getText().toString().trim())){
                            budget = Double.valueOf(newBudgetEditText.getText().toString());
                        }
                        newCategory.setDefaultBudget(budget);
                        newCategory.setIncome(false);
                    } else {
                        newCategory.setIncome(true);
                    }
                    databaseHandler.addCategory(newCategory);
                    //databaseHandler.mergeCategories(oldCategory1,oldCategory2, databaseHandler.getCategory(databaseHandler.getCategoryNextKey() -1));
                    finish();
                }
            }
        });
    }

    private void setSpinners(OrderedRealmCollection<Category> categories){
        CategoriesSpinAdapter categoriesSpinAdapter = new CategoriesSpinAdapter(MergeCategoriesActivity.this,categories);
        firstCategoriesSpinner.setAdapter(categoriesSpinAdapter);
        secondCategoriesSpinner.setAdapter(categoriesSpinAdapter);
    }

    private boolean checkMerge(){
        String text = "";
        boolean result = false;
        oldCategory1 = (Category)firstCategoriesSpinner.getSelectedItem();
        oldCategory2 = (Category)secondCategoriesSpinner.getSelectedItem();
        if(oldCategory1.equals(oldCategory2)){
            text = getString(R.string.you_must_choose_different_categories);
            result = false;
        } else {
            if (!TextUtils.isEmpty(newLabelEditText.getText().toString().trim())){

                if (expenseMode){
                    if (databaseHandler.findCategoryExpenseByLabel(newLabelEditText.getText().toString(), oldCategory1.getId(),oldCategory2.getId()) == null){
                        result = true;
                    } else {
                        text = getString(R.string.same_label);
                    }
                } else {
                    if (databaseHandler.findCategoryIncomeByLabel(newLabelEditText.getText().toString(), oldCategory1.getId(), oldCategory2.getId()) == null){
                        result = true;
                    } else {
                        text = getString(R.string.same_label);
                    }
                }

            } else {
                text = getString(R.string.you_must_enter_label);
            }

        }
        errorTextView.setText(text);
        return result;
    }
}
