package com.benjamin.ledet.budget.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.adapter.CategorySpinAdapter;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.ChartValueFormatter;
import com.benjamin.ledet.budget.tool.CustonChartMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StatisticActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_statistic_categories_expense)
    Spinner spCategoriesExpense;

    @BindView(R.id.activity_statistic_categories_income)
    Spinner spCategoriesIncome;

    @BindView(R.id.bar_chart_category_expense)
    BarChart barChartCategoryExpense;

    @BindView(R.id.line_chart_category_expense)
    LineChart lineChartCategoryExpense;

    @BindView(R.id.bar_chart_category_income)
    BarChart barChartCategoryIncome;

    @BindView(R.id.line_chart_category_income)
    LineChart lineChartCategoryIncome;

    private DatabaseHandler databaseHandler;

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

        databaseHandler = new DatabaseHandler(StatisticActivity.this);

        List<Category> categoriesExpense = databaseHandler.getCategoriesExpense();
        List<Category> categoriesIncome = databaseHandler.getCategoriesIncome();
        months = databaseHandler.getMonths();
        monthsDisplay = databaseHandler.getDisplayMonths(months);


        //spinner for categories expense, update the chart on category selected
        CategorySpinAdapter categoriesExpenseSpinAdapter = new CategorySpinAdapter(StatisticActivity.this, categoriesExpense);
        spCategoriesExpense.setAdapter(categoriesExpenseSpinAdapter);
        spCategoriesExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) spCategoriesExpense.getSelectedItem();
                barChartCategory(category, barChartCategoryExpense, getString(R.string.activity_statistic_expenses));

                lineChartCategory(category,lineChartCategoryExpense, getString(R.string.activity_statistic_expenses));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner for categories income, update the chart on category selected
        CategorySpinAdapter categoriesIncomeSpinAdapter = new CategorySpinAdapter(StatisticActivity.this, categoriesIncome);
        spCategoriesIncome.setAdapter(categoriesIncomeSpinAdapter);
        spCategoriesIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) spCategoriesIncome.getSelectedItem();
                barChartCategory(category, barChartCategoryIncome, getString(R.string.activity_statistic_incomes));

                lineChartCategory(category,lineChartCategoryIncome, getString(R.string.activity_statistic_incomes));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void lineChartCategory(Category category, LineChart chart, String name){

        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < months.size(); i++){
            entries.add(new BarEntry((float)i, (float)databaseHandler.getSumAmountsOfMonthOfCategory(months.get(i),category)));
        }

        LineDataSet set = new LineDataSet(entries, name);
        LineData data = new LineData(set);
        //data.setValueTextSize(12);

        data.setValueFormatter(new ChartValueFormatter(StatisticActivity.this));

        IAxisValueFormatter formatterMonths = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return monthsDisplay[ (int) value];
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatterMonths);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setSpaceTop(70f);

        chart.setData(data);

        //display a bubble with the amount when there is a click on a value
        CustonChartMarkerView custom_marker_view = new CustonChartMarkerView(this, R.layout.custom_chart_marker_view);
        custom_marker_view.setChartView(chart);
        chart.setMarker(custom_marker_view);

        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);
        chart.setVisibleXRangeMaximum(5f);
        chart.moveViewToX(months.size() +1);

    }

    private void barChartCategory(Category category, BarChart chart, String name){

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < months.size(); i++){
            entries.add(new BarEntry((float)i, (float)databaseHandler.getSumAmountsOfMonthOfCategory(months.get(i),category)));
        }

        BarDataSet set = new BarDataSet(entries, name);
        BarData data = new BarData(set);
       // data.setValueTextSize(12);
        data.setValueFormatter(new ChartValueFormatter(StatisticActivity.this));

        IAxisValueFormatter formatterMonths = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return monthsDisplay[ (int) value];
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatterMonths);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setSpaceTop(70f);

        chart.setData(data);

        //display a bubble with the amount when there is a click on a value
        CustonChartMarkerView custom_marker_view = new CustonChartMarkerView(this, R.layout.custom_chart_marker_view);
        custom_marker_view.setChartView(chart);
        chart.setMarker(custom_marker_view);

        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);
        chart.setVisibleXRangeMaximum(5f);
        chart.moveViewToX(months.size());
    }


}
