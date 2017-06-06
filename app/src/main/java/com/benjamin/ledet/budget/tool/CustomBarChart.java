package com.benjamin.ledet.budget.tool;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

import com.benjamin.ledet.budget.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.List;

public class CustomBarChart extends BarChart {

    private String title;
    private List<BarEntry> entries1;
    private String nameEntries1;
    private int colorEntries1;
    private List<BarEntry> entries2;
    private String nameEntries2;
    private int colorEntries2;
    private boolean multipleEntries;
    private String[] xValues;
    private float groupSpace;
    private float barSpace;
    private boolean enlarge;

    public CustomBarChart(Context context) {
        super(context);
    }

    public CustomBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomBarChart(Context context, String title, boolean enlarge) {
        super(context);
        this.title = title;
        this.enlarge = enlarge;

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
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(xValues);
        this.getXAxis().setValueFormatter(formatter);
    }

    public void setEntries(List<BarEntry> entries, String name, int color, float barWidth){
        this.entries1 = entries;
        this.nameEntries1 = name;
        this.colorEntries1 = color;
        this.multipleEntries = false;

        BarDataSet set = new BarDataSet(entries, name);
        set.setColor(color);
        BarData data = new BarData(set);
        data.setValueFormatter(new ChartValueFormatter(this.getContext()));
        data.setBarWidth(barWidth);
        this.setData(data);
        this.setDesign();
    }

    public void setMultipleEntries(List<BarEntry> entries1, String name1, int color1, List<BarEntry> entries2, String name2, int color2, float barWidth, float groupSpace, float barSpace){
        this.entries1 = entries1;
        this.nameEntries1 = name1;
        this.colorEntries1 = color1;
        this.entries2 = entries2;
        this.nameEntries2 = name2;
        this.colorEntries2 = color2;
        this.multipleEntries = true;
        this.groupSpace = groupSpace;
        this.barSpace = barSpace;

        BarDataSet set1 = new BarDataSet(entries1, name1);
        set1.setColor(color1);
        BarDataSet set2 = new BarDataSet(entries2, name2);
        set2.setColor(color2);
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new ChartValueFormatter(this.getContext()));
        data.setBarWidth(barWidth);
        this.setData(data);
        this.groupBars(this.getXAxis().getAxisMinimum(), groupSpace, barSpace);
        this.setDesign();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BarEntry> getEntries1(){
        return this.entries1;
    }

    public List<BarEntry> getEntries2(){
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

    public float getGroupSpace() {
        return groupSpace;
    }

    public float getBarSpace() {
        return barSpace;
    }

    public void setEnlarge(boolean enlarge){
        this.enlarge = enlarge;
    }

    public boolean isEnlarge(){
        return this.enlarge;
    }

    private void setDesign(){
        if(enlarge){
            this.setScaleEnabled(true);
        }else{

            this.setScaleEnabled(false);
            this.setVisibleXRangeMaximum(5f);
        }
        this.getDescription().setEnabled(false);
        this.getAxisRight().setEnabled(false);
        this.setDoubleTapToZoomEnabled(false);
        this.moveViewToX(this.getXChartMax());
    }

}
