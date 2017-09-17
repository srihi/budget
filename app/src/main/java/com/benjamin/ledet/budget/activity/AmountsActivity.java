package com.benjamin.ledet.budget.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.AmountRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class AmountsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_amounts_rv)
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
        setContentView(R.layout.activity_amounts);
        ButterKnife.bind(this);

        //get the list of amounts of the selected category
        final DatabaseHandler databaseHandler = new DatabaseHandler(this);
        Month month = databaseHandler.getMonth(getIntent().getExtras().getInt("month"),getIntent().getExtras().getInt("year"));
        Category category = databaseHandler.getCategory(getIntent().getExtras().getLong("category"));
        final OrderedRealmCollection<Amount> amounts = databaseHandler.getAmountsOfMonthOfCategory(month,category);

        //display toolbar
        toolbar.setTitle(month.toString() + " - " + category.getLabel());
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
        }));

    }
}