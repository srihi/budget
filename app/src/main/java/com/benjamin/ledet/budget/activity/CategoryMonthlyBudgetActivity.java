package com.benjamin.ledet.budget.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.CategoryMonthlyBudget;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryMonthlyBudgetActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_category_monthly_budget_actual_budget)
    TextView actualBudgetTextView;

    @BindView(R.id.activity_category_monthly_budget_new_budget)
    EditText newBudgetEditText;

    @BindView(R.id.activity_category_monthly_budget_reset_default_budget)
    Button resetDefaultBudgetButton;

    @BindView(R.id.activity_category_monthly_budget_switch)
    Switch applyForNextMonths;

    private DatabaseHandler databaseHandler;
    private Category category;
    private Month month;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category_monthly_budget, menu);
        Drawable drawable = menu.findItem(R.id.action_save).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
        menu.findItem(R.id.action_save).setIcon(drawable);
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

                double budget = 0;
                if(newBudgetEditText.getText().toString().trim().length() != 0){
                    budget = Double.valueOf(newBudgetEditText.getText().toString());
                }
                CategoryMonthlyBudget categoryMonthlyBudget = databaseHandler.getCategoryMonthlyBudget(category,month);
                databaseHandler.updateCategoryMonthlyBudget(categoryMonthlyBudget,budget);
                if(applyForNextMonths.isChecked()){
                    databaseHandler.updateCategory(category,budget);
                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_monthly_budget);
        ButterKnife.bind(this);

        databaseHandler = new DatabaseHandler(this);
        //noinspection ConstantConditions
        month = databaseHandler.getMonth(getIntent().getExtras().getInt("month"),getIntent().getExtras().getInt("year"));
        category = databaseHandler.getCategory(getIntent().getExtras().getLong("category"));

        toolbar.setTitle(month.toString() + " - " + category.getLabel());

        //display toolbar
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(category.getMonthlyBudget(month) == 0){
            actualBudgetTextView.setText(getString(R.string.none));
        } else {
            actualBudgetTextView.setText(getString(R.string.amount,String.valueOf(category.getMonthlyBudget(month))));
        }

        resetDefaultBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBudgetEditText.setText(String.valueOf(category.getDefaultBudget()));
            }
        });

    }

}
