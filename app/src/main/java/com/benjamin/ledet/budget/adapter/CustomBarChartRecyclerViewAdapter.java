package com.benjamin.ledet.budget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.tool.CustomBarChart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomBarChartRecyclerViewAdapter extends RecyclerView.Adapter<CustomBarChartRecyclerViewAdapter.ViewHolder> {

    private List<CustomBarChart> mCustomBarCharts;

    public CustomBarChartRecyclerViewAdapter(List<CustomBarChart> mCustomBarCharts) {
        this.mCustomBarCharts = mCustomBarCharts;
    }

    @Override
    public CustomBarChartRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chart, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(CustomBarChartRecyclerViewAdapter.ViewHolder holder, int position) {
        final CustomBarChart selectedCustomBarChart = mCustomBarCharts.get(position);

        holder.title.setText(selectedCustomBarChart.getTitle());

        if (selectedCustomBarChart.getParent() != null){
            ((ViewGroup)selectedCustomBarChart.getParent()).removeView(selectedCustomBarChart);
        }
        holder.linearLayout.addView(selectedCustomBarChart);

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

            extend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            
        }
    }
}