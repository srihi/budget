package com.benjamin.ledet.budget.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.adapter.CustomLineChartRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.CustomBarChart;
import com.benjamin.ledet.budget.tool.CustomLineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_charts_expenses)
    RecyclerView chartsExpensesRecyclerView;

    @BindView(R.id.rv_charts_incomes)
    RecyclerView chartsIncomesRecyclerView;

    @BindView(R.id.rv_main_charts)
    RecyclerView mainChartsRecyclerView;

    private SharedPreferences sharedPreferences;
    private DatabaseHandler databaseHandler;

    private ArrayList<CustomBarChart> customBarMainChartsArrayList = new ArrayList<>();
    private ArrayList<CustomBarChart> customBarChartsExpensesArrayList = new ArrayList<>();
    private ArrayList<CustomBarChart> customBarChartsIncomesArrayList = new ArrayList<>();

    private ArrayList<CustomLineChart> customLineMainChartsArrayList = new ArrayList<>();
    private ArrayList<CustomLineChart> customLineChartsExpensesArrayList = new ArrayList<>();
    private ArrayList<CustomLineChart> customLineChartsIncomesArrayList = new ArrayList<>();

    private List<Month> months;
    private String[] monthsDisplay;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(R.string.title_activity_statistic);
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        databaseHandler = new DatabaseHandler(StatisticActivity.this);

        months = databaseHandler.getMonths();
        monthsDisplay = databaseHandler.getDisplayMonths(months);

        addChartBalance();
        addChartExpenseIncome();
        addChartsCategoriesExpense();
        addChartsCategoriesIncome();

        //main charts
        LinearLayoutManager layoutManagerMainCharts = new LinearLayoutManager(StatisticActivity.this);
        mainChartsRecyclerView.setLayoutManager(layoutManagerMainCharts);

        //CustomBarChartRecyclerViewAdapter customBarMainChartsAdapter = new CustomBarChartRecyclerViewAdapter(customBarMainChartsArrayList);
        //mainChartsRecyclerView.setAdapter(customBarMainChartsAdapter);

        CustomLineChartRecyclerViewAdapter customLineMainChartsAdapter = new CustomLineChartRecyclerViewAdapter(customLineMainChartsArrayList, StatisticActivity.this);
        mainChartsRecyclerView.setAdapter(customLineMainChartsAdapter);


        //charts expenses
        LinearLayoutManager layoutManagerChartsExpenses = new LinearLayoutManager(StatisticActivity.this,LinearLayoutManager.HORIZONTAL,false);
        chartsExpensesRecyclerView.setLayoutManager(layoutManagerChartsExpenses);

        //CustomBarChartRecyclerViewAdapter customBarChartsExpensesAdapter = new CustomBarChartRecyclerViewAdapter(customBarChartsExpensesArrayList);
        //chartsExpensesRecyclerView.setAdapter(customBarChartsExpensesAdapter);

        CustomLineChartRecyclerViewAdapter customLineChartsExpensesAdapter = new CustomLineChartRecyclerViewAdapter(customLineChartsExpensesArrayList, StatisticActivity.this);
        chartsExpensesRecyclerView.setAdapter(customLineChartsExpensesAdapter);

        //charts incomes
        LinearLayoutManager layoutManagerChartsIncomes = new LinearLayoutManager(StatisticActivity.this,LinearLayoutManager.HORIZONTAL,false);
        chartsIncomesRecyclerView.setLayoutManager(layoutManagerChartsIncomes);

        //CustomBarChartRecyclerViewAdapter customBarChartsIncomesAdapter = new CustomBarChartRecyclerViewAdapter(customBarChartsIncomesArrayList);
        //chartsIncomesRecyclerView.setAdapter(customBarChartsIncomesAdapter);

        CustomLineChartRecyclerViewAdapter customLineChartsIncomesAdapter = new CustomLineChartRecyclerViewAdapter(customLineChartsIncomesArrayList, StatisticActivity.this);
        chartsIncomesRecyclerView.setAdapter(customLineChartsIncomesAdapter);

    }

    private void addChartBalance(){
        List<BarEntry> barEntries = new ArrayList<>();
        List<Entry> lineEntries = new ArrayList<>();

        for (int i = 0; i < months.size(); i++){
            barEntries.add(new BarEntry((float)i,(float)databaseHandler.getBalanceOfMonth(months.get(i))));
            lineEntries.add(new Entry((float)i,(float)databaseHandler.getBalanceOfMonth(months.get(i))));
        }

        CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, getString(R.string.activity_statistic_textview_balance));
        customBarChart.setEntries(barEntries, getString(R.string.activity_statistic_balance), ContextCompat.getColor(StatisticActivity.this,R.color.PrimaryColor), 0.7f);
        customBarChart.setSpaceTop(50f);
        customBarChart.setSpacebottom(30f);
        customBarChart.setxValues(monthsDisplay);

        CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, getString(R.string.activity_statistic_textview_balance));
        customLineChart.setEntries(lineEntries, getString(R.string.activity_statistic_balance), ContextCompat.getColor(StatisticActivity.this,R.color.PrimaryColor));
        customLineChart.setSpaceTop(50f);
        customLineChart.setSpacebottom(30f);
        customLineChart.setxValues(monthsDisplay);

        customBarMainChartsArrayList.add(customBarChart);
        customLineMainChartsArrayList.add(customLineChart);
    }

    private void addChartExpenseIncome(){
        float barWidth = 0.45f;
        float groupSpace = 0.06f;
        float barSpace = 0.02f;

        //expenses
        List<BarEntry> barEntriesExpense = new ArrayList<>();
        List<Entry> lineEntriesExpense = new ArrayList<>();

        for (int i = 0; i < months.size(); i++){
            barEntriesExpense.add(new BarEntry((float)i, (float)databaseHandler.getSumExpensesOfMonth(months.get(i))));
            lineEntriesExpense.add(new Entry((float)i, (float)databaseHandler.getSumExpensesOfMonth(months.get(i))));
        }

        //incomes
        List<BarEntry> barEntriesIncome = new ArrayList<>();
        List<Entry> lineEntriesIncome = new ArrayList<>();

        for (int i = 0; i < months.size(); i++){
            barEntriesIncome.add(new BarEntry((float)i, (float)databaseHandler.getSumIncomesOfMonth(months.get(i))));
            lineEntriesIncome.add(new Entry((float)i, (float)databaseHandler.getSumIncomesOfMonth(months.get(i))));
        }

        CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, getString(R.string.activity_statistic_expenses_incomes));
        customBarChart.setMultipleEntries(barEntriesIncome, getString(R.string.activity_statistic_incomes), ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor), barEntriesExpense, getString(R.string.activity_statistic_expenses), Color.RED, barWidth, groupSpace, barSpace);
        customBarChart.setSpaceTop(50f);
        customBarChart.setSpacebottom(30f);
        customBarChart.setxValues(monthsDisplay);

        CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, getString(R.string.activity_statistic_expenses_incomes));
        customLineChart.setMultipleEntries(lineEntriesIncome, getString(R.string.activity_statistic_incomes), ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor), lineEntriesExpense, getString(R.string.activity_statistic_expenses), Color.RED);
        customLineChart.setSpaceTop(50f);
        customLineChart.setSpacebottom(30f);
        customLineChart.setxValues(monthsDisplay);

        customBarMainChartsArrayList.add(customBarChart);
        customLineMainChartsArrayList.add(customLineChart);
    }

    private void addChartsCategoriesExpense(){
        for (Category category : databaseHandler.getCategoriesExpense()){

            List<BarEntry> barEntries = new ArrayList<>();
            List<Entry> lineEntries = new ArrayList<>();

            for (int i = 0; i < months.size(); i++){
                barEntries.add(new BarEntry((float)i,(float)databaseHandler.getSumAmountsOfMonthOfCategory(months.get(i),category)));
                lineEntries.add(new Entry((float)i,(float)databaseHandler.getSumAmountsOfMonthOfCategory(months.get(i),category)));
            }

            CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, category.getLabel());
            customBarChart.setEntries(barEntries, getString(R.string.activity_statistic_expenses), Color.RED, 0.7f);
            customBarChart.setSpaceTop(50f);
            customBarChart.setSpacebottom(30f);
            customBarChart.setxValues(monthsDisplay);

            CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, category.getLabel());
            customLineChart.setEntries(lineEntries, getString(R.string.activity_statistic_expenses), Color.RED);
            customLineChart.setSpaceTop(50f);
            customLineChart.setSpacebottom(30f);
            customLineChart.setxValues(monthsDisplay);

            customBarChartsExpensesArrayList.add(customBarChart);
            customLineChartsExpensesArrayList.add(customLineChart);
        }
    }

    private void addChartsCategoriesIncome(){
        for (Category category : databaseHandler.getCategoriesIncome()){

            List<BarEntry> barEntries = new ArrayList<>();
            List<Entry> lineEntries = new ArrayList<>();

            for (int i = 0; i < months.size(); i++){
                barEntries.add(new BarEntry((float)i,(float)databaseHandler.getSumAmountsOfMonthOfCategory(months.get(i),category)));
                lineEntries.add(new Entry((float)i,(float)databaseHandler.getSumAmountsOfMonthOfCategory(months.get(i),category)));
            }

            CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, category.getLabel());
            customBarChart.setEntries(barEntries, getString(R.string.activity_statistic_incomes),ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor), 0.7f);
            customBarChart.setSpaceTop(50f);
            customBarChart.setSpacebottom(30f);
            customBarChart.setxValues(monthsDisplay);

            CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, category.getLabel());
            customLineChart.setEntries(lineEntries, getString(R.string.activity_statistic_incomes),ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor));
            customLineChart.setSpaceTop(50f);
            customLineChart.setSpacebottom(30f);
            customLineChart.setxValues(monthsDisplay);

            customBarChartsIncomesArrayList.add(customBarChart);
            customLineChartsIncomesArrayList.add(customLineChart);
        }
    }

}
