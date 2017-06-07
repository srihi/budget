package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.activity.AmountActivity;
import com.benjamin.ledet.budget.activity.MainActivity;
import com.benjamin.ledet.budget.adapter.CategoryRecyclerViewAdapter;
import com.benjamin.ledet.budget.adapter.CategorySpinAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.RecyclerItemClickListener;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeFragment extends Fragment {

    @BindView(R.id.fab_category_income)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.rv_category_income)
    RecyclerView categoriesIncomeRecyclerView;

    private DatabaseHandler databaseHandler;

    private List<Category> categoriesIncomeNotEmpty;
    private List<Category> categoriesIncome;
    private CategoryRecyclerViewAdapter categoriesIncomeAdapter;
    private CategorySpinAdapter categoriesSpinAdapter;

    private Month month;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_income,container,false);
        ButterKnife.bind(this,v);

        databaseHandler = new DatabaseHandler(this.getContext());

        final Calendar calendar = Calendar.getInstance();

        //put a line between each element in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesIncomeRecyclerView.getContext(),LinearLayoutManager.VERTICAL);
        categoriesIncomeRecyclerView.addItemDecoration(dividerItemDecoration);

        categoriesIncome = databaseHandler.getCategoriesIncome();

        //add income
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),R.style.CustomAlertDialog);
                final LayoutInflater layoutInflater = (getActivity().getLayoutInflater());
                final View inflator = layoutInflater.inflate(R.layout.alert_dialog_add_amount,null);
                final EditText etLabel = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_label);
                final EditText etAmount = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_amount);
                final EditText etDay = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_day);
                final Spinner spCategories = (Spinner) inflator.findViewById(R.id.alert_dialog_add_amount_categories);
                categoriesSpinAdapter = new CategorySpinAdapter(getContext(),categoriesIncome);
                spCategories.setAdapter(categoriesSpinAdapter);
                builder.setView(inflator);
                TextView title = new TextView(getContext());
                title.setText(R.string.fragment_income_add_income);
                title.setTextColor(ContextCompat.getColor(getContext(),R.color.PrimaryColor));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Category categorySelected = (Category) spCategories.getSelectedItem();
                        String label = etLabel.getText().toString();
                        if (label.length() == 0){
                            label = categorySelected.getLabel();
                        }
                        Amount amount = new Amount();
                        amount.setId(databaseHandler.getAmountNextKey());
                        amount.setLabel(label);
                        amount.setCategory(categorySelected);
                        amount.setMonth(month);
                        if(etDay.getText().length() == 0){
                            amount.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                        }else{
                            amount.setDay(Integer.parseInt(etDay.getText().toString()));
                        }
                        amount.setAmount(Double.parseDouble(etAmount.getText().toString()));
                        amount.setAutomatic(false);
                        databaseHandler.addAmount(amount);
                        categoriesIncomeAdapter.notifyDataSetChanged();
                        if(!categoriesIncomeNotEmpty.contains(categorySelected)){
                            categoriesIncomeNotEmpty.add(databaseHandler.getCategoriesIncomeNotEmptyForMonth(month).indexOf(categorySelected),categorySelected);
                        }
                        Snackbar snackbar = Snackbar.make(view , R.string.fragment_income_add_income_message, Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.PrimaryColor));
                        snackbar.show();

                        ((MainActivity)getActivity()).setupSummary(month);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        //get the month from the id given by mainActivity
        String id = this.getArguments().getString("id");
        if (id != null){
            month = databaseHandler.getMonth(Integer.parseInt(id.substring(4)),Integer.parseInt(id.substring(0,4)));
        }
        //title
        ((MainActivity) getActivity()).setActionBarTitle(Month.intMonthToStringMonth(month.getMonth(),getContext()) + " " + month.getYear());

        ((MainActivity)getActivity()).setupSummary(month);

        //setup RecyclerView for categories income
        categoriesIncomeNotEmpty = databaseHandler.getCategoriesIncomeNotEmptyForMonth(month);

        RecyclerView.LayoutManager layoutManagerCategoriesIncome = new LinearLayoutManager(this.getContext());
        categoriesIncomeAdapter = new CategoryRecyclerViewAdapter(categoriesIncomeNotEmpty, this.getContext(), month);
        categoriesIncomeRecyclerView.setLayoutManager(layoutManagerCategoriesIncome);
        categoriesIncomeRecyclerView.setAdapter(categoriesIncomeAdapter);

        categoriesIncomeRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), categoriesIncomeRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categoriesIncomeNotEmpty.get(position);
                Intent intent = new Intent(getContext(),AmountActivity.class);
                intent.putExtra("month", month.getMonth());
                intent.putExtra("year", month.getYear());
                intent.putExtra("category",category.getId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setupSummary(month);
    }
}