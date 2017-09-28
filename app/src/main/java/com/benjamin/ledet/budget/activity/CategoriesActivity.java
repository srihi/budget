package com.benjamin.ledet.budget.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.ViewPagerAdapter;
import com.benjamin.ledet.budget.fragment.CategoriesExpenseFragment;
import com.benjamin.ledet.budget.fragment.CategoriesIncomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_categories_vp)
    ViewPager viewPager;

    @BindView(R.id.activity_categories_tb)
    TabLayout tabLayout;

    //return to the previous fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_merge:
                Intent intent = new Intent(CategoriesActivity.this,MergeCategoriesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_categories, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(R.string.categories);
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    //display viewPager with the tabs
    private void setupViewPager(ViewPager viewPager) {
        CategoriesExpenseFragment categoriesExpenseFragment = new CategoriesExpenseFragment();
        CategoriesIncomeFragment categoriesIncomeFragment = new CategoriesIncomeFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(categoriesExpenseFragment, getString(R.string.expenses));
        adapter.addFragment(categoriesIncomeFragment, getString(R.string.incomes));
        viewPager.setAdapter(adapter);
    }
}