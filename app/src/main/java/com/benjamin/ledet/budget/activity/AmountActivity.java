package com.benjamin.ledet.budget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.adapter.AmountRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.Month;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmountActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_amount)
    RecyclerView amountRecyclerView;

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

        setContentView(R.layout.activity_amount);

        ButterKnife.bind(this);

        DatabaseHandler databaseHandler;
        Month month;
        Category category;
        List<Amount> amounts;
        AmountRecyclerViewAdapter amountAdapter;
        RecyclerView.LayoutManager layoutManagerAmount;

        databaseHandler = new DatabaseHandler(this);
       //test

        month = databaseHandler.getMonth(getIntent().getExtras().getInt("month"),getIntent().getExtras().getInt("year"));
        category = databaseHandler.getCategory(getIntent().getExtras().getLong("category"));
        amounts = databaseHandler.getAmountsOfMonthOfCategory(month,category);

        //display toolbar
        toolbar.setTitle(Month.displayMonthString(month.getMonth(),AmountActivity.this) + " " + month.getYear() + " - " + category.getLabel());
        setSupportActionBar(toolbar);

        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutManagerAmount = new LinearLayoutManager(AmountActivity.this);
        amountAdapter = new AmountRecyclerViewAdapter(amounts, AmountActivity.this);
        amountRecyclerView.setLayoutManager(layoutManagerAmount);
        amountRecyclerView.setAdapter(amountAdapter);
        //put a line between each element in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(amountRecyclerView.getContext(),LinearLayoutManager.VERTICAL);
        amountRecyclerView.addItemDecoration(dividerItemDecoration);

    }
}