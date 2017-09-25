package com.benjamin.ledet.budget.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    @BindView(R.id.summary_tv_total_expenses)
    TextView tvTotalExpenses;

    @BindView(R.id.summary_tv_total_income)
    TextView tvTotalIncome;

    @BindView(R.id.summary_tv_balance)
    TextView tvBalance;

    @BindView(R.id.summary_tv_percentage)
    TextView tvPercentage;

    @BindView(R.id.summary_iv_arrow)
    ImageView ivArrow;

    @BindView(R.id.summary_ll)
    RelativeLayout llSummary;

    @BindView(R.id.summary_ll_more)
    LinearLayout llSummaryMore;

    private ValueAnimator animator;
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
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        tvTotalExpenses.setText(getString(R.string.amount,String.valueOf(df.format(totalExpenses))));
        tvTotalIncome.setText(getString(R.string.amount,String.valueOf(df.format(totalIncome))));
        tvPercentage.setText(getString(R.string.percentage,String.valueOf(df.format(percentage))));
        tvPercentage.setTextColor(tvTotalExpenses.getTextColors());
        if (percentage >= 100){
            tvPercentage.setTextColor(Color.RED);
        }
        tvBalance.setText(getString(R.string.amount,String.valueOf(df.format(balance))));
        tvBalance.setTextColor(tvTotalExpenses.getTextColors());
        if (balance < 0){
            tvBalance.setTextColor(Color.RED);
        }

        ivArrow.setColorFilter(Color.WHITE);

        //expand or collapse the entire summary
        llSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llSummaryMore.getVisibility()==View.GONE){
                    ivArrow.setImageResource(R.drawable.ic_arrow_drop_up);
                    expand();
                }else{
                    ivArrow.setImageResource(R.drawable.ic_arrow_drop_down);
                    collapse();
                }
            }
        });

        //collapse the entire summary
        llSummaryMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivArrow.setImageResource(R.drawable.ic_arrow_drop_down);
                collapse();
            }
        });

        //animation for the entire summary
        llSummaryMore.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        llSummaryMore.getViewTreeObserver().removeOnPreDrawListener(this);
                        llSummaryMore.setVisibility(View.GONE);
                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        llSummaryMore.measure(widthSpec, heightSpec);
                        animator = slideAnimator(0, llSummaryMore.getMeasuredHeight());
                        return true;
                    }
                });
    }

    //expand the entire summary
    private void expand() {
        llSummaryMore.setVisibility(View.VISIBLE);
         animator.start();
    }

    //collapse the entire summary
    private void collapse() {
        int finalHeight = llSummaryMore.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                llSummaryMore.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    //animation for the entire summary
    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = llSummaryMore.getLayoutParams();
                layoutParams.height = value;
                llSummaryMore.setLayoutParams(layoutParams);
            }
        });
        return animator;
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