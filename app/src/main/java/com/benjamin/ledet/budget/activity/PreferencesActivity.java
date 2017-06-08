package com.benjamin.ledet.budget.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.Month;

import java.util.ArrayList;
import java.util.Calendar;

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

    public static class MyPreferenceFragment extends PreferenceFragment {

        private DatabaseHandler databaseHandler;
        private SharedPreferences sharedPreferences;

        private ListPreference chartTypePref;
        private EditTextPreference addMonthPref;
        private ListPreference deleteMonthPref;
        private Preference myAccount;
        private Preference feedbackPref;
        private Preference ratePref;

        @Override
        public void onCreate(final Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            databaseHandler = new DatabaseHandler(getActivity());

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            chartTypePref = (ListPreference) findPreference(KEY_PREF_CHART_TYPE);
            addMonthPref = (EditTextPreference) findPreference("pref_add_month");
            deleteMonthPref = (ListPreference) findPreference("pref_delete_month");
            myAccount = findPreference("pref_my_account");
            feedbackPref = findPreference("pref_feedback");
            ratePref = findPreference("pref_rate");


            //chart type pref
            setChartTypePref();
            chartTypePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    ((ListPreference) preference).setValue(newValue.toString());
                    return false;
                }
            });

            //add month pref
            addMonthPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
                    TextView title = new TextView(getActivity());
                    title.setText(R.string.error);
                    title.setTextColor(Color.RED);
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    builder.setCustomTitle(title);
                    builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    try{

                        int position = newValue.toString().indexOf("/");
                        int actualYear = Calendar.getInstance().get(Calendar.YEAR);
                        int actualMonth = Calendar.getInstance().get(Calendar.MONTH);

                        Month month = new Month();
                        month.setId(databaseHandler.getMonthNextKey());
                        month.setMonth(Integer.parseInt(newValue.toString().substring(0,position)));
                        month.setYear(Integer.parseInt(newValue.toString().substring(position + 1)));

                        if (databaseHandler.getMonth(month.getMonth(),month.getYear()) != null){

                            builder.setMessage(R.string.activity_preferences_add_month_already_exist);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else if(month.getYear() > actualYear || (month.getYear() == actualYear && month.getMonth() > actualMonth)){

                            builder.setMessage(R.string.activity_preferences_add_month_not_yet);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else{

                            databaseHandler.addMonth(month);
                            setDeleteMonthPref();
                            //noinspection ConstantConditions
                            Snackbar snackbar = Snackbar.make(getView(), R.string.activity_preferences_add_month_success, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.PrimaryColor));
                            snackbar.show();
                        }

                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        builder.setMessage(R.string.activity_preferences_add_month_error);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    return false;
                }

            });

            //delete month pref
            setDeleteMonthPref();
            deleteMonthPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, final Object newValue) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
                    TextView title = new TextView(getActivity());
                    title.setText(R.string.activity_preferences_delete_month);
                    title.setTextColor(Color.RED);
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    builder.setCustomTitle(title);
                    builder.setMessage(getActivity().getString(R.string.activity_preferences_delete_month_message, newValue.toString()));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            int position = newValue.toString().indexOf(" ");
                            Month month = databaseHandler.getMonth(Month.stringMonthToIntMonth(newValue.toString().substring(0,position),getActivity()),Integer.parseInt(newValue.toString().substring(position + 1)));
                            databaseHandler.deleteMonth(month);
                            setDeleteMonthPref();
                            //noinspection ConstantConditions
                            Snackbar snackbar = Snackbar.make(getView() , R.string.activity_preferences_delete_month_success, Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.PrimaryColor));
                            snackbar.show();
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return false;
                }
            });

            //account pref
            myAccount.setSummary(databaseHandler.getUser().getEmail());


            //feedback pref
            feedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Open email intent
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ledet.benjamin23@gmail.com"));
                    boolean activityExists = emailIntent.resolveActivityInfo(getActivity().getPackageManager(), 0) != null;
                    if (activityExists) {
                        startActivity(emailIntent);
                    } else {
                        //noinspection ConstantConditions
                        Snackbar snackbar = Snackbar.make(getView() , R.string.activity_preferences_no_email_app , Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(Color.RED);
                        snackbar.show();
                    }

                    return false;
                }
            });

            //rate pref
            ratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.glucosio.android"));
                    startActivity(intent);

                    return false;
                }
            });

        }

        private void setChartTypePref(){

            ArrayList<String> chartTypesArray = new ArrayList<>();
            chartTypesArray.add(getString(R.string.activity_preferences_line_chart));
            chartTypesArray.add(getString(R.string.activity_preferences_bar_chart));

            CharSequence[] chartTypes = chartTypesArray.toArray(new CharSequence[chartTypesArray.size()]);
            chartTypePref.setEntryValues(chartTypes);
            chartTypePref.setEntries(chartTypes);

            chartTypePref.setSummary(sharedPreferences.getString(PreferencesActivity.KEY_PREF_CHART_TYPE,""));
            chartTypePref.setValue(sharedPreferences.getString(PreferencesActivity.KEY_PREF_CHART_TYPE,""));
        }

        private void setDeleteMonthPref(){

            ArrayList<String> monthArray = new ArrayList<>();
            for (Month month : databaseHandler.getMonths()){
                monthArray.add(Month.intMonthToStringMonth(month.getMonth(), getActivity()) + " " + month.getYear());
            }

            final CharSequence[] chartMonths = monthArray.toArray(new CharSequence[monthArray.size()]);
            deleteMonthPref.setEntryValues(chartMonths);
            deleteMonthPref.setEntries(chartMonths);

        }


    }

}