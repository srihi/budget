package com.benjamin.ledet.budget.Realm;

import android.content.Context;

import com.benjamin.ledet.budget.model.Amount;
import com.benjamin.ledet.budget.model.Category;
import com.benjamin.ledet.budget.model.Month;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DatabaseHandler {

    private static RealmConfiguration mRealmConfig;
    private Context mContext;
    private Realm realm;

    public DatabaseHandler(Context context) {
        this.mContext = context;

        this.realm = getNewRealmInstance();
    }

    private Realm getNewRealmInstance() {
        if (mRealmConfig == null) {
            Realm.init(mContext);
            mRealmConfig = new RealmConfiguration.Builder()
                    .schemaVersion(2)
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }
        return Realm.getInstance(mRealmConfig); // Automatically run migration if needed
    }

    public Realm getRealmInstance() {
        return realm;
    }


    public void deleteAll(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    // Category

    public int getCategoryNextKey() {
        try {
            return realm.where(Category.class)
                    .max("id").intValue() + 1;
        }
        catch (NullPointerException e) {
            return 1;
        }
    }

    public RealmResults<Category> getCategories() {

        return realm.where(Category.class)
                .findAllSorted("label");
    }

    public RealmResults<Category> getCategoriesIncome() {

        return realm.where(Category.class)
                .equalTo("isIncome",true)
                .findAllSorted("label");
    }

    public ArrayList<Category> getCategoriesIncomeNotEmptyForMonth(Month month) {
        ArrayList<Category> list = new ArrayList<>();
        for (Category category: getCategoriesIncome()) {
            if (getAmountsOfMonthOfCategory(month,category).size() != 0){
                list.add(category);
            }
        }
        return list;
    }

    public RealmResults<Category> getCategoriesExpense() {

        return realm.where(Category.class)
                .equalTo("isIncome",false)
                .findAllSorted("label");
    }

    public ArrayList<Category> getCategoriesExpenseNotEmptyForMonth(Month month) {
        ArrayList<Category> list = new ArrayList<>();
        for (Category category: getCategoriesExpense()) {
            if (getAmountsOfMonthOfCategory(month,category).size() != 0){
                list.add(category);
            }
        }
        return list;
    }

    public Category getCategory(long id) {

        return realm.where(Category.class).equalTo("id", id).findFirst();
    }

    public Category findCategoryExpenseByLabel(String label){
        return realm.where(Category.class)
                .like("label", label)
                .equalTo("isIncome",false)
                .findFirst();
    }

    public Category findCategoryIncomeByLabel(String label){
        return realm.where(Category.class)
                .like("label", label)
                .equalTo("isIncome",true)
                .findFirst();
    }

    public void addCategory(final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(category);
            }
        });
    }

    public void updateCategory(final Category category, final String label){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category.setLabel(label);
                realm.insertOrUpdate(category);
            }
        });
    }

    public void deleteCategory(final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Category.class).equalTo("id",category.getId()).findFirst().deleteFromRealm();
            }
        });
    }

    // Month

    public int getMonthNextKey() {
        try {
            return realm.where(Month.class)
                    .max("id").intValue() + 1;
        }
        catch (NullPointerException e) {
            return 1;
        }
    }

    public RealmResults<Month> getMonths() {

        return realm.where(Month.class).findAll();
    }

    public Month getMonth(int month, int year) {

        return realm.where(Month.class)
                .equalTo("month", month)
                .equalTo("year", year)
                .findFirst();
    }

    public void addMonth(final Month month){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(month);
            }
        });
    }

    public ArrayList<Integer> getYears(){
        ArrayList<Integer> list = new ArrayList<>();
        RealmResults<Month> months = realm.where(Month.class)
                .distinct("year")
                .sort("year");
        for (Month month: months) {
            list.add(month.getYear());
        }
        return list;
    }

    public ArrayList<Integer> getMonthsOfYear(int year){
        ArrayList<Integer> list = new ArrayList<>();
        RealmResults<Month> months = realm.where(Month.class)
                .equalTo("year",year)
                .findAll()
                .sort("month");
        for (Month month: months){
            list.add(month.getMonth());
        }
        return list;
    }

    public String[] getDisplayMonths(List<Month> months){
        String[] list = new String[months.size()];
        for (int i = 0; i < months.size(); i++){
            list[i] = Month.displayMonthString(months.get(i).getMonth(), mContext);
        }
        return  list;
    }

    // Amount

    public int getAmountNextKey() {
        try {
            return realm.where(Amount.class)
                    .max("id").intValue() + 1;
        }
        catch (NullPointerException e) {
            return 1;
        }
    }

    public RealmResults<Amount> getAmounts() {

        return realm.where(Amount.class)
                .findAll();
    }

    public Amount getAmount(long id) {

        return realm.where(Amount.class)
                .equalTo("id", id)
                .findFirst();
    }

    public RealmResults<Amount> getAmountsOfMonthOfCategory(Month month, Category category){

        return realm.where(Amount.class)
                .equalTo("month.id",month.getId())
                .equalTo("category.id",category.getId())
                .findAll();
    }

    public double getSumAmountsOfMonthOfCategory(Month month, Category category){
        double sum = 0;
        for (Amount amount : getAmountsOfMonthOfCategory(month,category)){
            sum += amount.getAmount();
        }
        return sum;
    }



    public double getSumExpensesOfMonth(Month month){
        double sum = 0;
        RealmResults<Amount> list = realm.where(Amount.class)
                .equalTo("month.id",month.getId())
                .equalTo("category.isIncome",false)
                .findAll();
        for (Amount amount: list) {
            sum += amount.getAmount();
        }
        return sum;
    }

    public double getSumIncomesOfMonth(Month month){
        double sum = 0;
        RealmResults<Amount> list = realm.where(Amount.class)
                .equalTo("month.id",month.getId())
                .equalTo("category.isIncome",true)
                .findAll();
        for (Amount amount: list) {
            sum += amount.getAmount();
        }
        return sum;
    }

    public void addAmount(final Amount amount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(amount);
            }
        });
    }

    public void updateAmount(final Amount amount, final String label, final double sum){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amount.setLabel(label);
                amount.setAmount(sum);
                realm.insertOrUpdate(amount);
            }
        });
    }

    public void deleteAmount(final Amount amount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Amount.class).equalTo("id",amount.getId()).findFirst().deleteFromRealm();
            }
        });
    }

}