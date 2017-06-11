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
import com.benjamin.ledet.budget.adapter.AmountRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class AmountActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_amount_rv)
    RecyclerView recyclerView;

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

        //get the list of amounts of the selected category
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        Month month = databaseHandler.getMonth(getIntent().getExtras().getInt("month"),getIntent().getExtras().getInt("year"));
        Category category = databaseHandler.getCategory(getIntent().getExtras().getLong("category"));
        OrderedRealmCollection<Amount> amounts = databaseHandler.getAmountsOfMonthOfCategory(month,category);

        //display toolbar
        toolbar.setTitle(Month.intMonthToStringMonth(month.getMonth(),this) + " " + month.getYear() + " - " + category.getLabel());
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //setup recylerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        AmountRecyclerViewAdapter adapter = new AmountRecyclerViewAdapter(amounts,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }
}