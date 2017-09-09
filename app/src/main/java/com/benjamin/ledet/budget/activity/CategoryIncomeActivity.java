package com.benjamin.ledet.budget.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.DatabaseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryIncomeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_category_income_label)
    EditText labelEditText;

    @BindView(R.id.activity_category_income_error)
    TextView errorTextview;

    private boolean addMode = true;
    private DatabaseHandler databaseHandler;
    private Category category;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        Drawable drawable = menu.findItem(R.id.action_save).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
        menu.findItem(R.id.action_save).setIcon(drawable);

        if (!addMode){
            menu.findItem(R.id.action_delete).setVisible(true);
            drawable = menu.findItem(R.id.action_delete).getIcon();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
            menu.findItem(R.id.action_delete).setIcon(drawable);

            menu.findItem(R.id.action_archive).setVisible(true);
            drawable = menu.findItem(R.id.action_archive).getIcon();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
            menu.findItem(R.id.action_archive).setIcon(drawable);
        }
        return true;
    }

    //return to the previous fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_save:
                if (checkCategory()){
                    if (addMode){
                        category.setLabel(labelEditText.getText().toString());
                        databaseHandler.addCategory(category);
                    } else {
                        databaseHandler.updateCategory(category,labelEditText.getText().toString());
                    }
                    finish();
                }
                return true;

            case  R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
                TextView title = new TextView(this);
                title.setText(R.string.delete);
                title.setTextColor(Color.RED);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(22);
                builder.setCustomTitle(title);
                builder.setMessage(getString(R.string.delete_category_message,category.getLabel()));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseHandler.deleteCategory(category);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            case  R.id.action_archive:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
                TextView title2 = new TextView(this);
                title2.setText(R.string.archive);
                title2.setTextColor(ContextCompat.getColor(this,R.color.PrimaryColor));
                title2.setGravity(Gravity.CENTER);
                title2.setTextSize(22);
                builder2.setCustomTitle(title2);
                builder2.setMessage(getString(R.string.archive_category_message, category.getLabel()));
                builder2.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseHandler.archiveCategory(category);
                        finish();
                    }
                });
                builder2.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = builder2.create();
                alertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_income);
        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(getString(R.string.new_category));
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        databaseHandler = new DatabaseHandler(this);

        if (getIntent().getExtras() != null ){
            addMode = false;
            category = databaseHandler.getCategory(getIntent().getExtras().getLong("category"));
            labelEditText.setText(category.getLabel());
            toolbar.setTitle(category.getLabel());
        }

        if (addMode){
            category = new Category();
            category.setId(databaseHandler.getCategoryNextKey());
            category.setIncome(true);
        }

    }

    private boolean checkCategory(){

        String text = "";
        boolean result = false;

        if (!TextUtils.isEmpty(labelEditText.getText().toString().trim())){

            if (databaseHandler.findCategoryIncomeByLabel(labelEditText.getText().toString(), category.getId()) == null){
                result = true;
            } else {
                text = getString(R.string.same_label);
            }
        } else {
            text = getString(R.string.you_must_enter_label);
        }
        errorTextview.setText(text);

        return result;
    }
}