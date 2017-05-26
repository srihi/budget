package com.benjamin.ledet.budget.tool;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

import com.benjamin.ledet.budget.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class CustomBarChart extends BarChart {

    private String title;

    public CustomBarChart(Context context) {
        super(context);
    }

    public CustomBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomBarChart(Context context, String title) {
        super(context);
        this.title = title;

        this.setLayoutParams(new AppBarLayout.LayoutParams(LayoutParams.MATCH_PARENT, 600));

        XAxis xAxis = this.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        //display a bubble with the amount when there is a click on a value
        CustonChartMarkerView custom_marker_view = new CustonChartMarkerView(context, R.layout.custom_chart_marker_view);
        custom_marker_view.setChartView(this);
        this.setMarker(custom_marker_view);

    }

    public void setSpaceTop(float spaceTop) {
        this.getAxisLeft().setSpaceTop(spaceTop);
    }

    public void setSpacebottom(float spacebottom) {
        this.getAxisLeft().setSpaceBottom(spacebottom);
    }

    public void setxValues(final String[] xValues) {
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xValues[ (int) value];
            }
        };
        this.getXAxis().setValueFormatter(formatter);
    }

    public void setEntries(List<BarEntry> entries, String name, int color, float barWidth){
        BarDataSet set = new BarDataSet(entries, name);
        set.setColor(color);
        BarData data = new BarData(set);
        data.setValueFormatter(new ChartValueFormatter(this.getContext()));
        data.setBarWidth(barWidth);
        this.setData(data);
        setDesign();
    }

    public void setMultipleEntries(List<BarEntry> entries1, String name1, int color1, List<BarEntry> entries2, String name2, int color2, float barWidth, float groupSpace, float barSpace){
        BarDataSet set1 = new BarDataSet(entries1, name1);
        set1.setColor(color1);
        BarDataSet set2 = new BarDataSet(entries2, name2);
        set2.setColor(color2);
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new ChartValueFormatter(this.getContext()));
        data.setBarWidth(barWidth);
        this.setData(data);
        this.groupBars(this.getXAxis().getAxisMinimum(), groupSpace, barSpace);
        setDesign();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void setDesign(){
        this.getDescription().setEnabled(false);
        this.getAxisRight().setEnabled(false);
        this.setDoubleTapToZoomEnabled(false);
        this.setScaleEnabled(false);
        this.setVisibleXRangeMaximum(5f);
        this.moveViewToX(this.getXChartMax());
    }
}
