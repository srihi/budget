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
import com.benjamin.ledet.budget.tool.CustomBarChart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomBarChartRecyclerViewAdapter extends RecyclerView.Adapter<CustomBarChartRecyclerViewAdapter.ViewHolder> {

    private List<CustomBarChart> mCustomBarCharts;
    private Context mContext;

    public CustomBarChartRecyclerViewAdapter(List<CustomBarChart> mCustomBarCharts, Context context) {
        this.mCustomBarCharts = mCustomBarCharts;
        this.mContext = context;
    }

    @Override
    public CustomBarChartRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chart, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final CustomBarChartRecyclerViewAdapter.ViewHolder holder, int position) {
        final CustomBarChart selectedCustomBarChart = mCustomBarCharts.get(position);

        holder.title.setText(selectedCustomBarChart.getTitle());

        if (selectedCustomBarChart.getParent() != null){
            ((ViewGroup)selectedCustomBarChart.getParent()).removeView(selectedCustomBarChart);
        }
        holder.linearLayout.addView(selectedCustomBarChart);

        holder.extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomBarChart customBarChart = new CustomBarChart(mContext,holder.title.getText().toString());
                if (selectedCustomBarChart.asMultipleEntries()){
                    customBarChart.setMultipleEntries(selectedCustomBarChart.getEntries1(), selectedCustomBarChart.getNameEntries1(), selectedCustomBarChart.getColorEntries1(),selectedCustomBarChart.getEntries2(), selectedCustomBarChart.getNameEntries2(), selectedCustomBarChart.getColorEntries2(),selectedCustomBarChart.getData().getBarWidth(),selectedCustomBarChart.getGroupSpace(),selectedCustomBarChart.getBarSpace());
                }else{
                    customBarChart.setEntries(selectedCustomBarChart.getEntries1(), selectedCustomBarChart.getNameEntries1(), selectedCustomBarChart.getColorEntries1(),selectedCustomBarChart.getData().getBarWidth());
                }
                customBarChart.setSpaceTop(50f);
                customBarChart.setSpacebottom(30f);
                customBarChart.setxValues(selectedCustomBarChart.getxValues());

                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                customBarChart.setLayoutParams(new ViewGroup.LayoutParams( Double.valueOf(metrics.widthPixels * 0.9).intValue(), Double.valueOf(metrics.heightPixels * 0.8).intValue()));

                ScrollView scrollView = new ScrollView(mContext);
                LinearLayout layout = new LinearLayout(mContext);
                scrollView.addView(layout);
                layout.addView(customBarChart);
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
        return mCustomBarCharts.size();
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