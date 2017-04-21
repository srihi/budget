package com.benjamin.ledet.budget.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benjamin.ledet.budget.BudgetApplication;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.adapter.ViewPagerAdapter;
import com.benjamin.ledet.budget.fragment.ExpenseFragment;
import com.benjamin.ledet.budget.fragment.IncomeFragment;
import com.benjamin.ledet.budget.model.Month;
import com.benjamin.ledet.budget.tool.Animation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SharedPreferences sharedPreferences;

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabsMain)
    TabLayout tabLayout;

    @BindView(R.id.viewpagerMain)
    ViewPager viewPager;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;

    @BindView(R.id.summary_total_expenses)
    TextView tvTotalExpenses;

    @BindView(R.id.summary_total_income)
    TextView tvTotalIncome;

    @BindView(R.id.summary_balance)
    TextView tvBalance;

    @BindView(R.id.summary_pourcentage)
    TextView tvPercentage;

    @BindView(R.id.ll_summary)
    LinearLayout llSummary;

    @BindView(R.id.ll_summary_1)
    LinearLayout llSummary1;

    @BindView(R.id.ll_summary_2)
    LinearLayout llSummary2;

    CircleImageView civProfil;

    TextView tvUserName;

    TextView tvUserEmail;

    private ExpenseFragment expenseFragment;
    private IncomeFragment incomeFragment;
    private Bundle bundle;
    private Calendar calendar = Calendar.getInstance();
    private int actualYear = calendar.get(Calendar.YEAR);
    private int actualMonth = calendar.get(Calendar.MONTH) + 1;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BudgetApplication application = (BudgetApplication) getApplication();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        databaseHandler = application.getDBHandler();

        // display the toolbar
        setSupportActionBar(toolbar);

        //asks the user to connect to the first launch
        sharedPreferences = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("first_launch",true)){

            signIn();
            sharedPreferences.edit().putBoolean("first_launch",false).apply();
        } else {
            setupHeader();
        }

        //check if it's a new month and add it
        checkNewMonth();

        //display the navigationView
        final Menu menu = navigationView.getMenu();
        setupNavigationViewMenu(menu);

        //display viewPager with the tabs
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

        //on item menu click
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //open the parameter activity
                if( menuItem.getItemId() == R.id.parameter){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                    startActivity(intent);
                    return true;
                }
                //open the management category activity
                else if(menuItem.getItemId() == R.id.category_management){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,CategoryManagementActivity.class);
                    startActivity(intent);
                    return true;
                }
                else if(menuItem.getItemId() == R.id.backup){
                    //close the menu
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(MainActivity.this,BackupActivity.class);
                    startActivity(intent);
                    return true;
                }
                //select year item
                else if(String.valueOf(menuItem.getItemId()).length() == 4){

                    if(menuItem.getActionView().getId() == R.id.row_year_icon_open ){
                        menuItem.setActionView(R.layout.row_year_close);

                    }else{
                        menuItem.setActionView(R.layout.row_year_open);
                    }
                    showOrCloseMonths(menu, menuItem.getItemId());
                    return true;

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

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,9001);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //Storing the information of the user
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null){
                String userName = acct.getDisplayName();
                String userEmail = acct.getEmail();
                String userId = acct.getId();
                Uri userPhoto = acct.getPhotoUrl();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_id",userId);
                editor.putString("user_name",userName);
                editor.putString("user_email",userEmail);
                if (userPhoto != null) {
                    editor.putString("user_photo",userPhoto.toString());
                }
                editor.apply();
                setupHeader();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("MainActivity", "onConnectionFailed:" + connectionResult);
    }

    private void checkNewMonth(){
        if(databaseHandler.getMonth(actualMonth, actualYear) == null){
            Month month = new Month();
            month.setId(databaseHandler.getMonthNextKey());
            month.setMonth(actualMonth);
            month.setYear(actualYear);
            databaseHandler.addMonth(month);
        }
    }

    private void setupHeader(){
        View header = navigationView.getHeaderView(0);
        civProfil = ButterKnife.findById(header,R.id.header_profile_image);
        tvUserName = ButterKnife.findById(header,R.id.header_username);
        tvUserEmail = ButterKnife.findById(header,R.id.header_email);
        //get the information of the user
        if (sharedPreferences.getString("user_name",null) != null){
            tvUserName.setText(sharedPreferences.getString("user_name",null));
        }
        if (sharedPreferences.getString("user_email",null) != null){
            tvUserEmail.setText(sharedPreferences.getString("user_email",null));
        }
        if (sharedPreferences.getString("user_photo",null) != null){
            Picasso.with(this).load(Uri.parse(sharedPreferences.getString("user_photo",null))).into(civProfil);
        }

    }

    private void setupNavigationViewMenu(Menu menu){
        ArrayList<Integer> years = databaseHandler.getYears();
        for (Integer year: years) {
            menu.add(0, year, 0, year.toString());
            MenuItem menuItemYear = menu.findItem(year);
            menuItemYear.setIcon(R.drawable.ic_today);
            ArrayList<Integer> months = databaseHandler.getMonthsOfYear(year);
            for (Integer month: months) {
                String id = year + "" + month;
                menu.add(0,Integer.parseInt(id),0,Month.displayMonthString(month,getApplicationContext()));
                MenuItem menuItemMonth = menu.findItem(Integer.parseInt(id));
                if(year != actualYear){
                    menuItemYear.setActionView(R.layout.row_year_open);
                    menuItemMonth.setVisible(false);
                }else{
                    menuItemYear.setActionView(R.layout.row_year_close);
                }
            }
        }
        MenuItem menuItem = menu.findItem(Integer.parseInt(actualYear + "" + actualMonth));
        menuItem.setCheckable(true);
        menuItem.setChecked(true);
    }

    private void showOrCloseMonths(Menu menu,int year){
        ArrayList<Integer> months = databaseHandler.getMonthsOfYear(year);
        for (Integer month: months) {
            String id = year + "" + month;
            MenuItem item = menu.findItem(Integer.parseInt(id));
            if (item.isVisible()){
                item.setVisible(false);
            }else{
                item.setVisible(true);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        expenseFragment = new ExpenseFragment();
        incomeFragment = new IncomeFragment();
        //send month and year to fragments
        bundle = new Bundle();
        bundle.putString("id",String.valueOf(actualYear + "" + actualMonth));
        expenseFragment.setArguments(bundle);
        incomeFragment.setArguments(bundle);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(expenseFragment, getString(R.string.title_fragment_expense));
        adapter.addFragment(incomeFragment, getString(R.string.title_fragment_income));
        viewPager.setAdapter(adapter);
    }

    public void setSummary(Month month){
        double totalExpenses = databaseHandler.getSumExpensesOfMonth(month);
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

        llSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llSummary1.isShown()){
                    Animation.slideUp(getApplicationContext(), llSummary1);
                    llSummary1.setVisibility(View.GONE);
                }
                else{
                    llSummary1.setVisibility(View.VISIBLE);
                    Animation.slideDown(getApplicationContext(), llSummary1);
                }
            }
        });

    }
}