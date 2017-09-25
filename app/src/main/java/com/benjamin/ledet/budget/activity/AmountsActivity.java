package com.benjamin.ledet.budget.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.AmountRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.RecyclerItemClickListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class AmountsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_amounts_cv)
    CardView cardView;

    @BindView(R.id.activity_amounts_rv)
    RecyclerView recyclerView;

    @BindView(R.id.activity_amounts_row_text_view)
    TextView rowCategoryTextView;

    @BindView(R.id.activity_amounts_row_category_budget)
    GridLayout rowCategoryBudget;

    @BindView(R.id.row_category_budget_expense_icon)
    ImageView icon;

    @BindView(R.id.row_category_budget_expense_label)
    TextView tvLabel;

    @BindView(R.id.row_category_budget_expense_budget)
    TextView tvBudget;

    @BindView(R.id.row_category_budget_expense_spending)
    TextView tvSpending;

    @BindView(R.id.row_category_budget_expense_remaining)
    TextView tvRemaining;

    @BindView(R.id.row_category_budget_expense_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.activity_amounts_row_category_no_budget)
    GridLayout rowCategoryNoBudget;

    @BindView(R.id.row_category_no_budget_expense_icon)
    ImageView iconNoBudget;

    @BindView(R.id.row_category_no_budget_expense_label)
    TextView tvLabelNoBudget;

    @BindView(R.id.row_category_no_budget_expense_spending)
    TextView tvSpendingNoBudget;

    @BindView(R.id.activity_amounts_row_category_income)
    RelativeLayout rowCategoryIncome;

    @BindView(R.id.row_category_budget_income_icon)
    ImageView iconIncome;

    @BindView(R.id.row_category_budget_income_label)
    TextView tvLabelIncome;

    @BindView(R.id.row_category_budget_income_income)
    TextView tvIncome;

    private Category category;
    private DatabaseHandler databaseHandler;
    private Month month;
    OrderedRealmCollection<Amount> amounts;

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
        setContentView(R.layout.activity_amounts);
        ButterKnife.bind(this);

        //get the list of amounts of the selected category
        databaseHandler = new DatabaseHandler(this);
        month = databaseHandler.getMonth(getIntent().getExtras().getInt("month"),getIntent().getExtras().getInt("year"));
        category = databaseHandler.getCategory(getIntent().getExtras().getLong("category"));
        amounts = databaseHandler.getAmountsOfMonthOfCategory(month,category);

        //display toolbar
        toolbar.setTitle(month.toString() + " - " + category.getLabel());
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupRowCategory();

        if(!category.isIncome()){
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AmountsActivity.this, CategoryMonthlyBudgetActivity.class);
                    intent.putExtra("category",category.getId());
                    intent.putExtra("month", month.getMonth());
                    intent.putExtra("year", month.getYear());
                    startActivity(intent);
                }
            });
        }

        //setup recylerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        AmountRecyclerViewAdapter adapter = new AmountRecyclerViewAdapter(amounts,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Amount amount = amounts.get(position);
                Intent intent = new Intent(AmountsActivity.this, AmountActivity.class);
                intent.putExtra("amount",amount.getId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(final View view, int position) {
                final Amount amount = amounts.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(AmountsActivity.this,R.style.CustomAlertDialog);
                TextView title = new TextView(AmountsActivity.this);
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
                        if(amounts.size() == 0){
                            finish();
                        }

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
        }));

    }

    private void setupRowCategory(){

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        rowCategoryBudget.setVisibility(View.GONE);
        rowCategoryNoBudget.setVisibility(View.GONE);
        rowCategoryIncome.setVisibility(View.GONE);
        if(category.isIncome()){
            rowCategoryIncome.setVisibility(View.VISIBLE);
            rowCategoryTextView.setVisibility(View.GONE);
            iconIncome.setImageDrawable(category.getIcon());
            tvLabelIncome.setText(category.getLabel());
            double income = databaseHandler.getSumAmountOfMonthOfCategory(month,category);
            tvIncome.setText(getString(R.string.amount,df.format(income)));

        } else {
            if (category.getMonthlyBudget(month) > 0 ){
                rowCategoryBudget.setVisibility(View.VISIBLE);
                icon.setImageDrawable(category.getIcon());
                tvLabel.setText(category.getLabel());
                tvBudget.setText(getString(R.string.amount,df.format(category.getMonthlyBudget(month))));
                double spending = databaseHandler.getSumAmountOfMonthOfCategory(month,category);
                tvSpending.setText(getString(R.string.amount,df.format(spending)));
                double remaining = category.getMonthlyBudget(month) - spending;
                tvRemaining.setText(getString(R.string.amount,df.format(remaining)));
                Double progress = (spending/category.getMonthlyBudget(month))*100;
                if (progress > 75){
                    progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(this,android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);
                    tvRemaining.setTextColor(ContextCompat.getColor(this,android.R.color.holo_orange_dark));
                }
                if (progress > 100){
                    progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(this,android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);;
                    tvRemaining.setTextColor(ContextCompat.getColor(this,android.R.color.holo_red_dark));
                }
                progressBar.setProgress(progress.intValue());

            } else {
                rowCategoryNoBudget.setVisibility(View.VISIBLE);
                iconNoBudget.setImageDrawable(category.getIcon());
                tvLabelNoBudget.setText(category.getLabel());
                double spending = databaseHandler.getSumAmountOfMonthOfCategory(month,category);
                tvSpendingNoBudget.setText(getString(R.string.amount,df.format(spending)));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRowCategory();
        if(amounts.size() == 0){
            finish();
        }
    }
}