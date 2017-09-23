package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.activity.AmountActivity;
import com.benjamin.ledet.budget.adapter.AutomaticAmountRecyclerViewAdapter;
import com.benjamin.ledet.budget.model.AutomaticTransaction;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class AutomaticIncomesFragment extends Fragment {

    @BindView(R.id.fragment_automatic_income_fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.fragment_automatic_income_rv)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_automatic_incomes, container, false);
        ButterKnife.bind(this, v);

        final DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        final OrderedRealmCollection<AutomaticTransaction> automaticIncomes = databaseHandler.getAutomaticsIncomes();
        final OrderedRealmCollection<Category> categoriesIncome = databaseHandler.getUnarchivedCategoriesIncome();
        final Month month = databaseHandler.getActualMonth();

        //setup recylerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        AutomaticAmountRecyclerViewAdapter adapter = new AutomaticAmountRecyclerViewAdapter(automaticIncomes, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AutomaticTransaction automaticTransaction = automaticIncomes.get(position);
                Intent intent = new Intent(getActivity(), AmountActivity.class);
                intent.putExtra("automatic_amount", automaticTransaction.getId());
                intent.putExtra("month", month.getMonth());
                intent.putExtra("year", month.getYear());
                intent.putExtra("income", true);
                intent.putExtra("automatic",true);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                final AutomaticTransaction automaticTransaction = automaticIncomes.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                TextView title = new TextView(getContext());
                title.setText(R.string.delete);
                title.setTextColor(Color.RED);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setMessage(getString(R.string.delete_automatic_income_message, automaticTransaction.getLabel()));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseHandler.deleteAutomaticAmount(automaticTransaction);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }));


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AmountActivity.class);
                intent.putExtra("month", month.getMonth());
                intent.putExtra("year", month.getYear());
                intent.putExtra("income", true);
                intent.putExtra("automatic",true);
                startActivity(intent);
            }
        });

        return v;
    }
}