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
import com.benjamin.ledet.budget.activity.AmountActivity;
import com.benjamin.ledet.budget.activity.MainActivity;
import com.benjamin.ledet.budget.adapter.CategoryRecyclerViewAdapter;
import com.benjamin.ledet.budget.adapter.CategorySpinAdapter;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.RecyclerItemClickListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

public class ExpenseFragment extends Fragment {

    @BindView(R.id.fab_category_expense)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.rv_category_expense)
    RecyclerView categoriesExpenseRecyclerView;

    private DatabaseHandler databaseHandler;
    private Month month;
    private OrderedRealmCollection<Category> categoriesExpenseNotEmpty;
    private OrderedRealmCollection<Category> categoriesExpense;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_expense,container,false);
        ButterKnife.bind(this,v);

        databaseHandler = new DatabaseHandler(getContext());
        categoriesExpense = databaseHandler.getCategoriesExpense();

        //setup RecyclerView for categories expense
        RecyclerView.LayoutManager layoutManagerCategoriesExpense = new LinearLayoutManager(getContext());
        categoriesExpenseRecyclerView.setLayoutManager(layoutManagerCategoriesExpense);
        categoriesExpenseRecyclerView.setHasFixedSize(true);
        categoriesExpenseRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //add expense
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                final View inflator = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_add_amount, null);
                final EditText etLabel = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_label);
                final EditText etAmount = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_amount);
                final EditText etDay = (EditText) inflator.findViewById(R.id.alert_dialog_add_amount_day);
                final Spinner spCategories = (Spinner) inflator.findViewById(R.id.alert_dialog_add_amount_categories);
                CategorySpinAdapter categoriesSpinAdapter = new CategorySpinAdapter(getContext(),categoriesExpense);
                spCategories.setAdapter(categoriesSpinAdapter);
                builder.setView(inflator);
                TextView title = new TextView(getContext());
                title.setText(R.string.fragment_expense_add_expense);
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
                            amount.setDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        }else{
                            amount.setDay(Integer.parseInt(etDay.getText().toString()));
                        }
                        amount.setAmount(Double.parseDouble(etAmount.getText().toString()));
                        amount.setAutomatic(false);
                        databaseHandler.addAmount(amount);

                        Snackbar snackbar = Snackbar.make(view , R.string.fragment_expense_add_expense_message, Snackbar.LENGTH_SHORT);
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
        String id = getArguments().getString("id");
        if (id != null){
            month = databaseHandler.getMonth(Integer.parseInt(id.substring(4)),Integer.parseInt(id.substring(0,4)));
            categoriesExpenseNotEmpty = databaseHandler.getCategoriesExpenseNotEmptyForMonth(month);
        }
        //title
        ((MainActivity) getActivity()).setActionBarTitle(Month.intMonthToStringMonth(month.getMonth(),getContext()) + " " + month.getYear());

        ((MainActivity)getActivity()).setupSummary(month);

        //setup RecyclerView for categories expense
        CategoryRecyclerViewAdapter categoriesExpenseAdapter = new CategoryRecyclerViewAdapter(categoriesExpenseNotEmpty, month, getContext());
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

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setupSummary(month);
    }
}