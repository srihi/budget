package com.benjamin.ledet.budget.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.ViewPagerAdapter;
import com.benjamin.ledet.budget.fragment.ArchivedCategoriesExpenseFragment;
import com.benjamin.ledet.budget.fragment.ArchivedCategoriesIncomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArchivedCategoriesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_archived_categories_vp)
    ViewPager viewPager;

    @BindView(R.id.activity_archived_categories_tb)
    TabLayout tabLayout;

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
        setContentView(R.layout.activity_archived_categories);
        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(R.string.archived_categories);
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
        ArchivedCategoriesExpenseFragment archivedCategoriesExpenseFragment = new ArchivedCategoriesExpenseFragment();
        ArchivedCategoriesIncomeFragment archivedCategoriesIncomeFragment = new ArchivedCategoriesIncomeFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(archivedCategoriesExpenseFragment, getString(R.string.expenses));
        adapter.addFragment(archivedCategoriesIncomeFragment, getString(R.string.incomes));
        viewPager.setAdapter(adapter);
    }
}