package com.benjamin.ledet.budget.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.CustomBarChartRecyclerViewAdapter;
import com.benjamin.ledet.budget.adapter.CustomLineChartRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.CustomBarChart;
import com.benjamin.ledet.budget.tool.CustomLineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class StatisticActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_statistic_tv_no_data_available)
    TextView noDataAvailable;

    @BindView(R.id.activity_statistic_rv_charts_expenses)
    RecyclerView chartsExpensesRecyclerView;

    @BindView(R.id.activity_statistic_rv_charts_incomes)
    RecyclerView chartsIncomesRecyclerView;

    @BindView(R.id.activity_statistic_rv_main_charts)
    RecyclerView mainChartsRecyclerView;

    private DatabaseHandler databaseHandler;

    private ArrayList<CustomBarChart> customBarMainChartsArrayList = new ArrayList<>();
    private ArrayList<CustomBarChart> customBarChartsExpensesArrayList = new ArrayList<>();
    private ArrayList<CustomBarChart> customBarChartsIncomesArrayList = new ArrayList<>();

    private ArrayList<CustomLineChart> customLineMainChartsArrayList = new ArrayList<>();
    private ArrayList<CustomLineChart> customLineChartsExpensesArrayList = new ArrayList<>();
    private ArrayList<CustomLineChart> customLineChartsIncomesArrayList = new ArrayList<>();

    private List<Month> months;
    private String[] monthsDisplay;

    private SharedPreferences sharedPreferences;
    private boolean barChart = false;
    private boolean lineChart = false;

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPreferences.getString(PreferencesActivity.KEY_PREF_CHART_TYPE,"").equals(getString(R.string.activity_preferences_bar_chart))){
            barChart = true;
        }
        if(sharedPreferences.getString(PreferencesActivity.KEY_PREF_CHART_TYPE,"").equals(getString(R.string.activity_preferences_line_chart))){
            lineChart = true;
        }

        databaseHandler = new DatabaseHandler(StatisticActivity.this);

        months = databaseHandler.getMonths();

        monthsDisplay = new String[months.size()];
        for (int i = 0; i < months.size(); i++){
            monthsDisplay[i] = months.get(i).toString();
        }

        if(databaseHandler.getAmounts().size() != 0){

            addChartBalance();
            addChartExpenseIncome();
            addChartsCategoriesExpense();
            addChartsCategoriesIncome();

            //main charts
            LinearLayoutManager layoutManagerMainCharts = new LinearLayoutManager(this);
            mainChartsRecyclerView.setLayoutManager(layoutManagerMainCharts);

            //charts expenses
            LinearLayoutManager layoutManagerChartsExpenses = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            chartsExpensesRecyclerView.setLayoutManager(layoutManagerChartsExpenses);

            //charts incomes
            LinearLayoutManager layoutManagerChartsIncomes = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            chartsIncomesRecyclerView.setLayoutManager(layoutManagerChartsIncomes);

            if(barChart){

                //main charts
                CustomBarChartRecyclerViewAdapter customBarMainChartsAdapter = new CustomBarChartRecyclerViewAdapter(customBarMainChartsArrayList,this);
                mainChartsRecyclerView.setAdapter(customBarMainChartsAdapter);

                //charts expenses
                CustomBarChartRecyclerViewAdapter customBarChartsExpensesAdapter = new CustomBarChartRecyclerViewAdapter(customBarChartsExpensesArrayList,this);
                chartsExpensesRecyclerView.setAdapter(customBarChartsExpensesAdapter);

                //charts incomes
                CustomBarChartRecyclerViewAdapter customBarChartsIncomesAdapter = new CustomBarChartRecyclerViewAdapter(customBarChartsIncomesArrayList,this);
                chartsIncomesRecyclerView.setAdapter(customBarChartsIncomesAdapter);
            }

            if(lineChart){

                //main charts
                CustomLineChartRecyclerViewAdapter customLineMainChartsAdapter = new CustomLineChartRecyclerViewAdapter(customLineMainChartsArrayList, this);
                mainChartsRecyclerView.setAdapter(customLineMainChartsAdapter);

                //charts expenses
                CustomLineChartRecyclerViewAdapter customLineChartsExpensesAdapter = new CustomLineChartRecyclerViewAdapter(customLineChartsExpensesArrayList,this);
                chartsExpensesRecyclerView.setAdapter(customLineChartsExpensesAdapter);

                //charts incomes
                CustomLineChartRecyclerViewAdapter customLineChartsIncomesAdapter = new CustomLineChartRecyclerViewAdapter(customLineChartsIncomesArrayList, this);
                chartsIncomesRecyclerView.setAdapter(customLineChartsIncomesAdapter);
            }

        }else{
            noDataAvailable.setVisibility(View.VISIBLE);
        }

    }

    private void addChartBalance(){

        if(barChart){
            List<BarEntry> barEntries = new ArrayList<>();
            for (int i = 0; i < months.size(); i++){
                barEntries.add(new BarEntry((float)i,(float)databaseHandler.getBalanceOfMonth(months.get(i))));
            }
            CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, getString(R.string.activity_statistic_balance_label),false);
            customBarChart.setEntries(barEntries, getString(R.string.activity_statistic_balance), ContextCompat.getColor(StatisticActivity.this,R.color.PrimaryColor), 0.7f);
            customBarChart.setSpaceTop(50f);
            customBarChart.setSpacebottom(30f);
            customBarChart.setxValues(monthsDisplay);
            customBarMainChartsArrayList.add(customBarChart);
        }

        if(lineChart){
            List<Entry> lineEntries = new ArrayList<>();
            for (int i = 0; i < months.size(); i++){
                lineEntries.add(new Entry((float)i,(float)databaseHandler.getBalanceOfMonth(months.get(i))));
            }
            CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, getString(R.string.activity_statistic_balance_label),false);
            customLineChart.setEntries(lineEntries, getString(R.string.activity_statistic_balance), ContextCompat.getColor(StatisticActivity.this,R.color.PrimaryColor));
            customLineChart.setSpaceTop(50f);
            customLineChart.setSpacebottom(30f);
            customLineChart.setxValues(monthsDisplay);
            customLineMainChartsArrayList.add(customLineChart);
        }
    }

    private void addChartExpenseIncome(){

        if(barChart){
            float barWidth = 0.45f;
            float groupSpace = 0.06f;
            float barSpace = 0.02f;

            //expenses
            List<BarEntry> barEntriesExpense = new ArrayList<>();

            for (int i = 0; i < months.size(); i++){
                barEntriesExpense.add(new BarEntry((float)i, (float)databaseHandler.getSpendingOfMonth(months.get(i))));
            }

            //incomes
            List<BarEntry> barEntriesIncome = new ArrayList<>();

            for (int i = 0; i < months.size(); i++){
                barEntriesIncome.add(new BarEntry((float)i, (float)databaseHandler.getSumIncomesOfMonth(months.get(i))));
            }

            CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, getString(R.string.activity_statistic_expenses_incomes_label),false);
            customBarChart.setMultipleEntries(barEntriesIncome, getString(R.string.activity_statistic_incomes), ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor), barEntriesExpense, getString(R.string.activity_statistic_expenses), Color.RED, barWidth, groupSpace, barSpace);
            customBarChart.setSpaceTop(50f);
            customBarChart.setSpacebottom(30f);
            customBarChart.setxValues(monthsDisplay);
            customBarMainChartsArrayList.add(customBarChart);
        }

        if(lineChart){

            //expenses
            List<Entry> lineEntriesExpense = new ArrayList<>();

            for (int i = 0; i < months.size(); i++){
                lineEntriesExpense.add(new Entry((float)i, (float)databaseHandler.getSpendingOfMonth(months.get(i))));
            }

            //incomes
            List<Entry> lineEntriesIncome = new ArrayList<>();

            for (int i = 0; i < months.size(); i++){
                lineEntriesIncome.add(new Entry((float)i, (float)databaseHandler.getSumIncomesOfMonth(months.get(i))));
            }

            CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, getString(R.string.activity_statistic_expenses_incomes_label),false);
            customLineChart.setMultipleEntries(lineEntriesIncome, getString(R.string.activity_statistic_incomes), ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor), lineEntriesExpense, getString(R.string.activity_statistic_expenses), Color.RED);
            customLineChart.setSpaceTop(50f);
            customLineChart.setSpacebottom(30f);
            customLineChart.setxValues(monthsDisplay);
            customLineMainChartsArrayList.add(customLineChart);
        }

    }

    private void addChartsCategoriesExpense(){

        OrderedRealmCollection<Category> categoriesExpense;
        if(sharedPreferences.getBoolean(PreferencesActivity.KEY_PREF_ARCHIVED_CATEGORIES,true)){
            categoriesExpense = databaseHandler.getCategoriesExpense();
        } else {
            categoriesExpense = databaseHandler.getUnarchivedCategoriesExpense();
        }

        if(barChart){

            for (Category category : categoriesExpense){

                List<BarEntry> barEntries = new ArrayList<>();

                for (int i = 0; i < months.size(); i++){
                    barEntries.add(new BarEntry((float)i,(float)databaseHandler.getSpendingOfMonthOfCategory(months.get(i),category)));
                }

                CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, category.getLabel(),false);
                customBarChart.setEntries(barEntries, getString(R.string.activity_statistic_expenses), Color.RED, 0.7f);
                customBarChart.setSpaceTop(50f);
                customBarChart.setSpacebottom(30f);
                customBarChart.setxValues(monthsDisplay);
                customBarChartsExpensesArrayList.add(customBarChart);
            }
        }

        if(lineChart){

            for (Category category : categoriesExpense){

                List<Entry> lineEntries = new ArrayList<>();

                for (int i = 0; i < months.size(); i++){
                    lineEntries.add(new Entry((float)i,(float)databaseHandler.getSpendingOfMonthOfCategory(months.get(i),category)));
                }

                CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, category.getLabel(),false);
                customLineChart.setEntries(lineEntries, getString(R.string.activity_statistic_expenses), Color.RED);
                customLineChart.setSpaceTop(50f);
                customLineChart.setSpacebottom(30f);
                customLineChart.setxValues(monthsDisplay);
                customLineChartsExpensesArrayList.add(customLineChart);
            }
        }
    }

    private void addChartsCategoriesIncome(){

        OrderedRealmCollection<Category> categoriesIncome;
        if(sharedPreferences.getBoolean(PreferencesActivity.KEY_PREF_ARCHIVED_CATEGORIES,true)){
           categoriesIncome = databaseHandler.getCategoriesIncome();
        } else {
            categoriesIncome = databaseHandler.getUnarchivedCategoriesIncome();
        }

        if(barChart){

            for (Category category : categoriesIncome){

                List<BarEntry> barEntries = new ArrayList<>();

                for (int i = 0; i < months.size(); i++){
                    barEntries.add(new BarEntry((float)i,(float)databaseHandler.getSpendingOfMonthOfCategory(months.get(i),category)));
                }

                CustomBarChart customBarChart = new CustomBarChart(StatisticActivity.this, category.getLabel(),false);
                customBarChart.setEntries(barEntries, getString(R.string.activity_statistic_incomes),ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor), 0.7f);
                customBarChart.setSpaceTop(50f);
                customBarChart.setSpacebottom(30f);
                customBarChart.setxValues(monthsDisplay);
                customBarChartsIncomesArrayList.add(customBarChart);
            }
        }

        if(lineChart){

            for (Category category : categoriesIncome){
                List<Entry> lineEntries = new ArrayList<>();

                for (int i = 0; i < months.size(); i++){
                    lineEntries.add(new Entry((float)i,(float)databaseHandler.getSpendingOfMonthOfCategory(months.get(i),category)));
                }

                CustomLineChart customLineChart = new CustomLineChart(StatisticActivity.this, category.getLabel(),false);
                customLineChart.setEntries(lineEntries, getString(R.string.activity_statistic_incomes),ContextCompat.getColor(StatisticActivity.this, R.color.PrimaryColor));
                customLineChart.setSpaceTop(50f);
                customLineChart.setSpacebottom(30f);
                customLineChart.setxValues(monthsDisplay);
                customLineChartsIncomesArrayList.add(customLineChart);
            }
        }
    }

}
