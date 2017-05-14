
package com.benjamin.ledet.budget.tool;

import android.content.Context;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustonChartMarkerView extends MarkerView {

    private TextView tvContent;
    private Context context;

    public CustonChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        this.context = context;

        tvContent = (TextView) findViewById(R.id.custom_chart_marker_view);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        tvContent.setText(context.getString(R.string.amount,String.valueOf(e.getY())));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
