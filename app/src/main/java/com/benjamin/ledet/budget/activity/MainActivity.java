package com.benjamin.ledet.budget.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.benjamin.ledet.budget.BudgetApplication;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.ViewPagerAdapter;
import com.benjamin.ledet.budget.fragment.ExpenseFragment;
import com.benjamin.ledet.budget.fragment.IncomeFragment;
import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.AutomaticTransaction;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.model.User;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_main_tl)
    TabLayout tabLayout;

    @BindView(R.id.activity_main_vp)
    ViewPager viewPager;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;

    @BindView(R.id.spinner_months)
    Spinner spinnerMonths;

    @BindView(R.id.summary_income)
    TextView summaryIncomeTextview;

    @BindView(R.id.summary_expenses)
    TextView summaryExpensesTextview;

    @BindView(R.id.summary_progress)
    DonutProgress summaryDonutProgress;

    private ExpenseFragment expenseFragment;
    private IncomeFragment incomeFragment;
    private Bundle bundle;
    private Calendar calendar = Calendar.getInstance();
    private int actualYear = calendar.get(Calendar.YEAR);
    private int actualMonth = calendar.get(Calendar.MONTH) + 1;
    private DatabaseHandler databaseHandler;
    private boolean userSelectSpinnerMonth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BudgetApplication budgetApplication = (BudgetApplication) getApplication();
        SharedPreferences sharedPreferences = budgetApplication.getSharedPreferences();
        databaseHandler = budgetApplication.getDatabaseHandler();

        // display the toolbar
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(sharedPreferences.getBoolean("first_launch",true)){
            //set default preferences at the first launch of the application
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

            addCategories();
            sharedPreferences.edit().putBoolean("first_launch",false).apply();
        }

        //launch the signIn activity if there is no user connected
        if(databaseHandler.getUser() == null) {
            finish(); // the signInActivity can be the taskRoot, useful in onBackPressMethod()
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }else{
            setupUserHeader();
        }

        checkNewMonth();
        checkAutomaticAmounts();

        /*
        for (Month month: databaseHandler.getMonths()){
            for(Category category: databaseHandler.getCategoriesExpenseWithExpensesOfMonth(month)){
                CategoryMonthlyBudget categoryMonthlyBudget = new CategoryMonthlyBudget();
                categoryMonthlyBudget.setId(databaseHandler.getCategoryMonthlyBudgetNextKey());
                categoryMonthlyBudget.setCategory(category);
                categoryMonthlyBudget.setMonth(month);
                categoryMonthlyBudget.setMonthlyBudget(category.getDefaultBudget());
                databaseHandler.addCategoryMonthlyBudget(categoryMonthlyBudget);
            }
        }
        */

        final Menu menu = navigationView.getMenu();
        setupNavigationViewMenu(menu);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // initialize the drawerLayout and the actionBarDrawer ( toolbar and navigationView)
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        //add actionBarToggle to drawerLayout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setupSpinnerMonths();

        //on item menu click
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //open the preference activity
                if( menuItem.getItemId() == R.id.preferences){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,PreferencesActivity.class);
                    startActivity(intent);
                    return true;
                }
                //open the categories activity
                else if(menuItem.getItemId() == R.id.categories){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,CategoriesActivity.class);
                    startActivity(intent);
                    return true;
                }
                //open the archived categories activity
                else if(menuItem.getItemId() == R.id.archivedCategories){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,ArchivedCategoriesActivity.class);
                    startActivity(intent);
                    return true;
                }
                //open the backup activity
                else if(menuItem.getItemId() == R.id.backup){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,BackupActivity.class);
                    startActivity(intent);
                    return true;
                }
                //open the statistic activity
                else if(menuItem.getItemId() == R.id.statistic){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,StatisticActivity.class);
                    startActivity(intent);
                    return true;
                }
                //open the automatic transactions activity
                else if(menuItem.getItemId() == R.id.automaticTransactions){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,AutomaticTransactionsActivity.class);
                    startActivity(intent);
                    return true;
                }
                //on the click of a year
                else if(String.valueOf(menuItem.getItemId()).length() == 4) {

                    if (menuItem.getActionView().getId() == R.id.row_year_icon_open) {
                        menuItem.setActionView(R.layout.row_year_close);

                    } else {
                        menuItem.setActionView(R.layout.row_year_open);
                    }
                    showOrCloseMonths(menu, menuItem.getItemId());
                    return true;
                //update the fragments with the selected month
                }else{
                    //indicates the item selected by a gray background
                    menuItem.setCheckable(true);
                    //close the menu
                    drawerLayout.closeDrawers();
                    //open the selected month by replacing the old selected
                    bundle.clear();
                    bundle.putString("id",String.valueOf(menuItem.getItemId()));
                    expenseFragment.getArguments().putAll(bundle);
                    incomeFragment.getArguments().putAll(bundle);
                    //display fragments according to the selected month
                    expenseFragment.onStart();
                    incomeFragment.onStart();
                    return true;
                }
            }
        });
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    //displays information for the selected month
    public void setupSummary(Month month){
        double totalExpenses = databaseHandler.getSpendingOfMonth(month);
        double totalIncome = databaseHandler.getSumIncomesOfMonth(month);
        double balance = totalIncome - totalExpenses;
        double percentage = 0;
        if (totalIncome != 0){
            percentage = (totalExpenses / totalIncome) * 100;
        }
        if (totalIncome == 0 && totalExpenses != 0){
            percentage = 100;
        }
        if(percentage > 100){
            percentage = 100;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        summaryIncomeTextview.setText(getString(R.string.amount,String.valueOf(df.format(totalIncome))));
        summaryExpensesTextview.setText(getString(R.string.amount,String.valueOf(df.format(totalExpenses))));
        summaryDonutProgress.setProgress((float)percentage);
        if (balance < 0){
            summaryDonutProgress.setFinishedStrokeColor(ContextCompat.getColor(this,android.R.color.holo_red_dark));
            summaryDonutProgress.setTextColor(ContextCompat.getColor(this,android.R.color.holo_red_dark));
            summaryDonutProgress.setText("-" + getString(R.string.amount,String.valueOf(df.format(balance))));
        } else {
            TypedValue typedValue = new TypedValue();
            TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
            int color = a.getColor(0, 0);
            a.recycle();
            summaryDonutProgress.setFinishedStrokeColor(color);
            summaryDonutProgress.setTextColor(color);
            summaryDonutProgress.setText("+" + getString(R.string.amount,String.valueOf(df.format(balance))));
        }

    }

    public void setupSpinnerMonths(){
        ArrayAdapter<Month> adapter =
                new ArrayAdapter<Month>(getApplicationContext(), R.layout.spinner_month, databaseHandler.getMonths());
        adapter.setDropDownViewResource(R.layout.spinner_month);
        spinnerMonths.setAdapter(adapter);
        spinnerMonths.setSelection(databaseHandler.getMonths().size() - 1);

        spinnerMonths.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelectSpinnerMonth = true;
                return false;
            }
        });
        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(userSelectSpinnerMonth) {
                    Month monthSelected = (Month) parent.getItemAtPosition(position);
                    bundle.clear();
                    bundle.putString("id",monthSelected.getYear() + "" + monthSelected.getMonth());
                    expenseFragment.getArguments().putAll(bundle);
                    incomeFragment.getArguments().putAll(bundle);
                    //display fragments according to the selected month
                    expenseFragment.onStart();
                    incomeFragment.onStart();
                    userSelectSpinnerMonth = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //displays information of the user
    public void setupUserHeader(){
        View header = navigationView.getHeaderView(0);
        CircleImageView profileImage = ButterKnife.findById(header,R.id.header_civ_profile);
        TextView displayName = ButterKnife.findById(header,R.id.header_tv_display_name);
        TextView email = ButterKnife.findById(header,R.id.header_tv_email);

        User user = databaseHandler.getUser();
        displayName.setText(user.getDisplayName());
        email.setText(user.getEmail());
        if(user.getPhotoUrl() != null){
            Picasso.with(this).load(Uri.parse(user.getPhotoUrl())).into(profileImage);
        }
    }

    //manages the user's click to view or closing the months of a year
    private void showOrCloseMonths(Menu menu,int year){
        for (Month month:databaseHandler.getMonthsOfYear(year)){
            String id = year + "" + month.getMonth();
            MenuItem item = menu.findItem(Integer.parseInt(id));
            if (item.isVisible()){
                item.setVisible(false);
            }else{
                item.setVisible(true);
            }
        }
    }

    //display the navigationView
    private void setupNavigationViewMenu(Menu menu){
        for (Integer year: databaseHandler.getYears()){
            menu.add(0,year,0, year.toString());
            MenuItem menuItemYear = menu.findItem(year);
            menuItemYear.setIcon(R.drawable.ic_today);
            for (Month month: databaseHandler.getMonthsOfYear(year)){
                String id = month.getYear() + "" + month.getMonth();
                menu.add(0,Integer.parseInt(id),0,month.monthString());
                MenuItem menuItemMonth = menu.findItem(Integer.parseInt(id));
                menuItemYear.setActionView(R.layout.row_year_open);
                menuItemMonth.setVisible(false);
            }
        }
        MenuItem menuItem = menu.findItem(Integer.parseInt(actualYear + "" + actualMonth));
        menuItem.setCheckable(true);
        menuItem.setChecked(true);
    }

    //display viewPager with the tabs
    private void setupViewPager(ViewPager viewPager) {
        expenseFragment = new ExpenseFragment();
        incomeFragment = new IncomeFragment();
        //send month and year to fragments
        bundle = new Bundle();
        bundle.putString("id",String.valueOf(actualYear + "" + actualMonth));
        expenseFragment.setArguments(bundle);
        incomeFragment.setArguments(bundle);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(expenseFragment, getString(R.string.expenses));
        adapter.addFragment(incomeFragment, getString(R.string.incomes));
        viewPager.setAdapter(adapter);
    }

    //check if it's a new month and add it
    private void checkNewMonth(){
        if(databaseHandler.getActualMonth() == null){
            Month month = new Month();
            month.setId(databaseHandler.getMonthNextKey());
            month.setMonth(actualMonth);
            month.setYear(actualYear);
            databaseHandler.addMonth(month);
        }
    }

    //check if there are automatic amounts to add
    private void checkAutomaticAmounts(){
        int countIncomes = 0;
        int countExpenses = 0;
        for (AutomaticTransaction automaticTransaction : databaseHandler.getAutomaticTransactions()){
        int actualDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (automaticTransaction.getDay() <= actualDay ){
            if (databaseHandler.findAmoutByAutomaticAmountAndMonth(automaticTransaction, databaseHandler.getActualMonth()) == null){
                Amount amount = new Amount();
                amount.setId(databaseHandler.getAmountNextKey());
                amount.setCategory(automaticTransaction.getCategory());
                amount.setDay(automaticTransaction.getDay());
                amount.setMonth(databaseHandler.getActualMonth());
                amount.setLabel(automaticTransaction.getLabel());
                amount.setAmount(automaticTransaction.getAmount());
                databaseHandler.addAmount(amount);
                if(amount.getCategory().isIncome()){
                    countIncomes ++;
                }else{
                    countExpenses ++;
                }
            }
        }

        }
        if (countIncomes >0 || countExpenses >0){
            String message = "";
            if (countIncomes == 1){
                message = getString(R.string.activity_main_one_income_added);
            }
            if (countIncomes > 1){
                message = getString(R.string.activity_main_several_incomes_added);
            }
            if (countExpenses == 1){
                message = getString(R.string.activity_main_one_expense_added);
            }
            if (countExpenses > 1){
                message = getString(R.string.activity_main_several_expenses_added);
            }
            if(countExpenses >=1 && countIncomes >=1){
                message = getString(R.string.activity_main_several_incomes_expenses_added);
            }
            Snackbar snackbar = Snackbar.make(drawerLayout , message, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.PrimaryColor));
            snackbar.show();
        }
    }

    //add some categories
    private void addCategories(){
        databaseHandler.addCategory(new Category(1,getString(R.string.category_various_purchases),false));
        databaseHandler.addCategory(new Category(2,getString(R.string.category_shopping),false));
        databaseHandler.addCategory(new Category(3,getString(R.string.category_apl),true));
        databaseHandler.addCategory(new Category(4,getString(R.string.category_salary),true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAutomaticAmounts();
    }
}