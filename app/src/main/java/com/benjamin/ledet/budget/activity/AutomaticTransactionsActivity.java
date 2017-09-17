package com.benjamin.ledet.budget.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.adapter.ViewPagerAdapter;
import com.benjamin.ledet.budget.fragment.AutomaticExpensesFragment;
import com.benjamin.ledet.budget.fragment.AutomaticIncomesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutomaticTransactionsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_automatic_transactions);
        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(R.string.automatic_transactions);
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
        AutomaticExpensesFragment automaticExpensesFragment = new AutomaticExpensesFragment();
        AutomaticIncomesFragment automaticIncomesFragment = new AutomaticIncomesFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(automaticExpensesFragment, getString(R.string.automatic_expenses));
        adapter.addFragment(automaticIncomesFragment, getString(R.string.automatic_incomes));
        viewPager.setAdapter(adapter);
    }
}