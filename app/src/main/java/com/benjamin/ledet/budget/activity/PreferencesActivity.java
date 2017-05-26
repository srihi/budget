package com.benjamin.ledet.budget.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.benjamin.ledet.budget.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreferencesActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    public static final String KEY_PREF_CHART_TYPE = "pref_chart_type";

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
        setContentView(R.layout.activity_preferences);

        getFragmentManager().beginTransaction()
                .replace(R.id.preferencesFrame, new MyPreferenceFragment()).commit();

        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(R.string.title_activity_preferences);
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment  {

        private ListPreference chartTypePref;
        private SharedPreferences sharedPreferences;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            chartTypePref = (ListPreference) findPreference("pref_chart_type");

            ArrayList<String> chartTypesArray = new ArrayList<>();
            chartTypesArray.add(getString(R.string.activity_preferences_line_chart));
            chartTypesArray.add(getString(R.string.activity_preferences_bar_chart));

            CharSequence[] chartTypes = chartTypesArray.toArray(new CharSequence[chartTypesArray.size()]);
            chartTypePref.setEntryValues(chartTypes);
            chartTypePref.setEntries(chartTypes);

            chartTypePref.setSummary(sharedPreferences.getString(PreferencesActivity.KEY_PREF_CHART_TYPE,""));
            chartTypePref.setValue(sharedPreferences.getString(PreferencesActivity.KEY_PREF_CHART_TYPE,""));

            chartTypePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    ((ListPreference) preference).setValue(newValue.toString());
                    return false;
                }
            });
        }


    }

}