package com.benjamin.ledet.budget.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.activity.AmountActivity;
import com.benjamin.ledet.budget.activity.MainActivity;
import com.benjamin.ledet.budget.adapter.CategoryRecyclerViewAdapter;
import com.benjamin.ledet.budget.adapter.CategorySpinAdapter;
import com.benjamin.ledet.budget.adapter.RecyclerItemClickListener;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.Month;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenseFragment extends Fragment {

    @BindView(R.id.fab_category_expense)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.rv_category_expense)
    RecyclerView categoriesExpenseRecyclerView;

    //@BindView(R.id.tv_total_depenses_2)
    TextView tvTotalDepenses;

    //@BindView(R.id.tv_total_revenu_2)
    TextView tvTotalRevenu;

    //@BindView(R.id.tv_liquidites_2)
    TextView tvLiquidites;

    //@BindView(R.id.tv_pourcentage_2)
    TextView tvPourcentage;

    private DatabaseHandler databaseHandler;

    private List<Category> categoriesExpenseNotEmpty;
    private List<Category> categoriesExpense;
    private CategoryRecyclerViewAdapter categoriesExpenseAdapter;
    private CategorySpinAdapter categoriesSpinAdapter;

    private int day;
    private Month month;
    private String id;

    private double totalRevenu;
    private double totaldepenses;
    private double liquidites;
    private double pourcentage;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_expense,container,false);

        ButterKnife.bind(this,v);

        databaseHandler = new DatabaseHandler(this.getContext());

        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //put a line between each element in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesExpenseRecyclerView.getContext(),LinearLayoutManager.VERTICAL);
        categoriesExpenseRecyclerView.addItemDecoration(dividerItemDecoration);

        categoriesExpense = databaseHandler.getCategoriesExpense();

        //add expense
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final LayoutInflater layoutInflater = (getActivity().getLayoutInflater());
                //get the appropriate view
                final View inflator = layoutInflater.inflate(R.layout.alert_dialog_add_amount,null);
                final EditText etLabel = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_label);
                final EditText etAmount = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_amount);
                final EditText etDay = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_day);
                final Spinner spCategories = (Spinner) inflator.findViewById(R.id.alert_dialog_add_amount_categories);
                etDay.setText(String.valueOf(ExpenseFragment.this.day));
                categoriesSpinAdapter = new CategorySpinAdapter(getContext(),categoriesExpense);
                spCategories.setAdapter(categoriesSpinAdapter);
                builder.setView(inflator);
                builder.setTitle(R.string.fragment_expense_add_expense);
                builder.setIcon(R.drawable.ic_add_circle);
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
                        amount.setDay(Integer.parseInt(etDay.getText().toString()));
                        amount.setAmount(Double.parseDouble(etAmount.getText().toString()));
                        databaseHandler.addAmount(amount);
                        categoriesExpenseAdapter.notifyDataSetChanged();
                        if(!categoriesExpenseNotEmpty.contains(categorySelected)){
                            categoriesExpenseNotEmpty.add(categorySelected);
                        }
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
        String  id = this.getArguments().getString("id");
        if (id != null){
            month = databaseHandler.getMonth(Integer.parseInt(id.substring(4)),Integer.parseInt(id.substring(0,4)));
        }

        //title
        ((MainActivity) getActivity()).setActionBarTitle(Month.displayMonthString(month.getMonth(),getContext()) + " " + month.getYear());

        //setup RecyclerView for categories expense
        categoriesExpenseNotEmpty = databaseHandler.getCategoriesExpenseNotEmptyForMonth(month);

        RecyclerView.LayoutManager layoutManagerCategoriesExpense = new LinearLayoutManager(this.getContext());
        categoriesExpenseAdapter = new CategoryRecyclerViewAdapter(categoriesExpenseNotEmpty, this.getContext(), month);
        categoriesExpenseRecyclerView.setLayoutManager(layoutManagerCategoriesExpense);
        categoriesExpenseRecyclerView.setAdapter(categoriesExpenseAdapter);

        categoriesExpenseRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), categoriesExpenseRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categoriesExpenseNotEmpty.get(position);
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

        /*
        setTotalRevenus();
        setTotalDepenses();
        setLiquidites();
        setPourcentage();
*/

    }
/*
    //total des fragment_income_name du mois
    private void setTotalRevenus(){
        totalRevenu =  databaseHandler.getSumIncomesOfMonth(month);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String text = getString(R.string.amount,df.format(totalRevenu));
        tvTotalRevenu.setText(text);
    }

    //total des dépenses du mois
    private void setTotalDepenses(){
        totaldepenses = databaseHandler.getSumExpensesOfMonth(month);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String text = getString(R.string.amount,df.format(totaldepenses));
        tvTotalDepenses.setText(text);
    }

    //Liquidités du mois
    private void setLiquidites(){
        liquidites = totalRevenu - totaldepenses;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String text = getString(R.string.amount,df.format(liquidites));
        tvLiquidites.setText(text);
    }

    //Pourcentage du revenu dépensé
    private void setPourcentage(){
        if (totalRevenu == 0){
            pourcentage = 0;
        }else{
            pourcentage = (totaldepenses / totalRevenu) * 100;
        }
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.CEILING);
        String text = getString(R.string.pourcentage,df.format(pourcentage));
        tvPourcentage.setText(text);
    }
    */
}