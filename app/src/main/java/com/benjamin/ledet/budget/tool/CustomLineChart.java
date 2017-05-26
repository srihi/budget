package com.benjamin.ledet.budget.tool;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

import com.benjamin.ledet.budget.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class CustomLineChart extends LineChart{
    private String title;
    private List<Entry> entries1;
    private String nameEntries1;
    private int colorEntries1;
    private List<Entry> entries2;
    private String nameEntries2;
    private int colorEntries2;
    private boolean multipleEntries;
    private String[] xValues;

    public CustomLineChart(Context context) {
        super(context);
    }

    public CustomLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomLineChart(Context context, String title) {
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
        this.xValues = xValues;
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xValues[ (int) value];
            }
        };
        this.getXAxis().setValueFormatter(formatter);
    }

    public void setEntries(List<Entry> entries, String name, int color){
        this.entries1 = entries;
        this.nameEntries1 = name;
        this.colorEntries1 = color;
        this.multipleEntries = false;

        LineDataSet set = new LineDataSet(entries, name);
        set.setColor(color);
        LineData data = new LineData(set);
        data.setValueFormatter(new ChartValueFormatter(this.getContext()));
        this.setData(data);
        this.setDesign();
    }

    public void setMultipleEntries(List<Entry> entries1, String name1, int color1, List<Entry> entries2, String name2, int color2){
        this.entries1 = entries1;
        this.nameEntries1 = name1;
        this.colorEntries1 = color1;
        this.entries2 = entries2;
        this.nameEntries2 = name2;
        this.colorEntries2 = color2;
        this.multipleEntries = true;

        LineDataSet set1 = new LineDataSet(entries1, name1);
        set1.setColor(color1);
        LineDataSet set2 = new LineDataSet(entries2, name2);
        set2.setColor(color2);
        LineData data = new LineData(set1, set2);
        data.setValueFormatter(new ChartValueFormatter(this.getContext()));
        this.setData(data);
        this.setDesign();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Entry> getEntries1(){
        return this.entries1;
    }

    public List<Entry> getEntries2(){
        return this.entries2;
    }

    public String getNameEntries1(){
        return this.nameEntries1;
    }

    public String getNameEntries2(){
        return this.nameEntries2;
    }

    public int getColorEntries1(){
        return this.colorEntries1;
    }

    public int getColorEntries2(){
        return this.colorEntries2;
    }

    public boolean asMultipleEntries(){
        return this.multipleEntries;
    }

    public String[] getxValues(){
        return this.xValues;
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
