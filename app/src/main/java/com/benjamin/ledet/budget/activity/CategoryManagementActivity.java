package com.benjamin.ledet.budget.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.ViewPagerAdapter;
import com.benjamin.ledet.budget.fragment.CategoryExpenseFragment;
import com.benjamin.ledet.budget.fragment.CategoryIncomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryManagementActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewpagerCategories)
    ViewPager viewPager;

    @BindView(R.id.tabsCategories)
    TabLayout tabLayout;

    private ViewPagerAdapter adapter;

    private CategoryExpenseFragment categoryExpenseFragment;
    private CategoryIncomeFragment categoryIncomeFragment;

    //return to the previous fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(getResources().getString(R.string.title_category_management));
        setSupportActionBar(toolbar);
        //display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //display viewPager with the tabs
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        categoryExpenseFragment = new CategoryExpenseFragment();
        categoryIncomeFragment = new CategoryIncomeFragment();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(categoryExpenseFragment, getString(R.string.category_expense));
        adapter.addFragment(categoryIncomeFragment, getString(R.string.category_income));
        viewPager.setAdapter(adapter);
    }
}