package com.benjamin.ledet.budget.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.tool.CustomLineChart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomLineChartRecyclerViewAdapter extends RecyclerView.Adapter<CustomLineChartRecyclerViewAdapter.ViewHolder> {

    private List<CustomLineChart> mCustomLineCharts;
    private Context mContext;

    public CustomLineChartRecyclerViewAdapter(List<CustomLineChart> mCustomLineCharts, Context context) {
        this.mCustomLineCharts = mCustomLineCharts;
        mContext = context;
    }

    @Override
    public CustomLineChartRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chart, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final CustomLineChartRecyclerViewAdapter.ViewHolder holder, int position) {
        final CustomLineChart selectedCustomLineChart = mCustomLineCharts.get(position);

        holder.title.setText(selectedCustomLineChart.getTitle());

        if (selectedCustomLineChart.getParent() != null){
            ((ViewGroup)selectedCustomLineChart.getParent()).removeView(selectedCustomLineChart);
        }
        holder.linearLayout.addView(selectedCustomLineChart);

        holder.extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomLineChart customLineChart = new CustomLineChart(mContext,holder.title.getText().toString());
                if (selectedCustomLineChart.asMultipleEntries()){
                    customLineChart.setMultipleEntries(selectedCustomLineChart.getEntries1(), selectedCustomLineChart.getNameEntries1(), selectedCustomLineChart.getColorEntries1(),selectedCustomLineChart.getEntries2(), selectedCustomLineChart.getNameEntries2(), selectedCustomLineChart.getColorEntries2());
                }else{
                    customLineChart.setEntries(selectedCustomLineChart.getEntries1(), selectedCustomLineChart.getNameEntries1(), selectedCustomLineChart.getColorEntries1());
                }
                customLineChart.setSpaceTop(50f);
                customLineChart.setSpacebottom(30f);
                customLineChart.setxValues(selectedCustomLineChart.getxValues());

                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                customLineChart.setLayoutParams(new ViewGroup.LayoutParams( Double.valueOf(metrics.widthPixels * 0.9).intValue(), Double.valueOf(metrics.heightPixels * 0.8).intValue()));

                ScrollView scrollView = new ScrollView(mContext);
                LinearLayout layout = new LinearLayout(mContext);
                scrollView.addView(layout);
                layout.addView(customLineChart);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
                TextView title = new TextView(mContext);
                title.setText(holder.title.getText().toString());
                title.setTextColor(ContextCompat.getColor(mContext,R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setView(scrollView);
                builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCustomLineCharts.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_chart_layout)
        LinearLayout linearLayout;

        @BindView(R.id.row_chart_title)
        TextView title;

        @BindView(R.id.row_chart_extend)
        Button extend;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
