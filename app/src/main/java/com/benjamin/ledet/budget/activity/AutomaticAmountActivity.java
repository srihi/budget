package com.benjamin.ledet.budget.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.ViewPagerAdapter;
import com.benjamin.ledet.budget.fragment.AutomaticExpenseFragment;
import com.benjamin.ledet.budget.fragment.AutomaticIncomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutomaticAmountActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_automatic_amount_vp)
    ViewPager viewPager;

    @BindView(R.id.activity_automatic_amount_tl)
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
        setContentView(R.layout.activity_automatic_amount);
        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(R.string.title_activity_automatic_amount);
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
        AutomaticExpenseFragment automaticExpenseFragment = new AutomaticExpenseFragment();
        AutomaticIncomeFragment automaticIncomeFragment = new AutomaticIncomeFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(automaticExpenseFragment, getString(R.string.title_fragment_automatic_expense));
        adapter.addFragment(automaticIncomeFragment, getString(R.string.title_fragment_automatic_income));
        viewPager.setAdapter(adapter);
    }
}