package com.benjamin.ledet.budget.tool;

import android.content.Context;

import com.benjamin.ledet.budget.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class ChartValueFormatter implements IValueFormatter {

    private Context context;

    public ChartValueFormatter(Context context) {
        this.context = context;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return  context.getString(R.string.amount,String.valueOf(value));
    }
}
