package com.benjamin.ledet.budget.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
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
                    .schemaVersion(6)
                    .migration(new Migration())
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

    public RealmResults<Category> getCategoriesIncomeWithIncomesOfMonth(Month month) {

        return realm.where(Category.class)
                .equalTo("isIncome",true)
                .equalTo("amounts.month.id",month.getId())
                .findAllSorted("label");
    }

    public RealmResults<Category> getCategoriesExpense() {

        return realm.where(Category.class)
                .equalTo("isIncome",false)
                .findAllSorted("label");
    }

    public RealmResults<Category> getCategoriesExpenseWithExpensesOfMonth(Month month) {

        return realm.where(Category.class)
                .equalTo("isIncome",false)
                .equalTo("amounts.month.id",month.getId())
                .findAllSorted("label");
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
                Log.d("add category ", category.toString());
            }
        });
    }

    public void updateCategory(final Category category, final String label){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category.setLabel(label);
                realm.insertOrUpdate(category);
                Log.d("update category ", category.toString());
            }
        });
    }

    public void deleteCategory(final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete category ", category.toString());
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
            list[i] = Month.intMonthToStringMonth(months.get(i).getMonth(), mContext);
        }
        return  list;
    }

    public Month getMonth(int month, int year) {

        return realm.where(Month.class)
                .equalTo("month", month)
                .equalTo("year", year)
                .findFirst();
    }

    public Month getActualMonth(){
        return realm.where(Month.class)
                .equalTo("month", Calendar.getInstance().get(Calendar.MONTH) + 1)
                .equalTo("year", Calendar.getInstance().get(Calendar.YEAR))
                .findFirst();
    }

    public void addMonth(final Month month){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(month);
                Log.d("add month ", month.toString());
            }
        });
    }

    public void deleteMonth(final Month month){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete month ", month.toString());
                realm.where(Month.class).equalTo("id",month.getId()).findFirst().deleteFromRealm();
            }
        });
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

    public Amount findAmoutByAutomaticAmountAndMonth(AutomaticAmount automaticAmount, Month month){
        return realm.where(Amount.class)
                .equalTo("category.id",automaticAmount.getCategory().getId())
                .equalTo("label",automaticAmount.getLabel())
                .equalTo("amount",automaticAmount.getAmount())
                .equalTo("month.id",month.getId())
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

    public double getBalanceOfMonth(Month month){
        return getSumIncomesOfMonth(month) - getSumExpensesOfMonth(month);
    }

    public void addAmount(final Amount amount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(amount);
                amount.getCategory().getAmounts().add(amount);
                Log.d("add amount ", amount.toString());
            }
        });
    }

    public void updateAmount(final Amount amount, final String label, final int day, final double sum){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amount.setLabel(label);
                amount.setAmount(sum);
                amount.setDay(day);
                realm.insertOrUpdate(amount);
                Log.d("update amount ", amount.toString());
            }
        });
    }

    public void deleteAmount(final Amount amount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete amount ", amount.toString());
                realm.where(Amount.class).equalTo("id",amount.getId()).findFirst().deleteFromRealm();
            }
        });
    }

    // User

    public User getUser() {

        return realm.where(User.class)
                .findFirst();
    }

    public void addUser(final User user){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(user);
                Log.d("add user ", user.toString());
            }
        });
    }

    public void deleteUser(final User user){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete user ", user.toString());
                realm.where(User.class).equalTo("id",user.getId()).findFirst().deleteFromRealm();
            }
        });
    }

    // Automatic Amount

    public int getAutomaticAmountNextKey() {
        try {
            return realm.where(AutomaticAmount.class)
                    .max("id").intValue() + 1;
        }
        catch (NullPointerException e) {
            return 1;
        }
    }

    public RealmResults<AutomaticAmount> getAutomaticAmounts() {

        return realm.where(AutomaticAmount.class)
                .findAll();
    }

    public RealmResults<AutomaticAmount> getAutomaticsIncomes() {

        return realm.where(AutomaticAmount.class)
                .equalTo("category.isIncome",true)
                .findAllSorted("category.label");
    }

    public RealmResults<AutomaticAmount> getAutomaticsExpenses() {

        return realm.where(AutomaticAmount.class)
                .equalTo("category.isIncome",false)
                .findAllSorted("category.label");
    }

    public AutomaticAmount getAutomaticAmount(long id) {

        return realm.where(AutomaticAmount.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void addAutomaticAmount(final AutomaticAmount automaticAmount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(automaticAmount);
                automaticAmount.getCategory().getAutomaticAmounts().add(automaticAmount);
                Log.d("add automatic amount ", automaticAmount.toString());
            }
        });
    }

    public void updateAutomaticAmount(final AutomaticAmount automaticAmount, final String label, final int day, final double sum){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                automaticAmount.setLabel(label);
                automaticAmount.setDay(day);
                automaticAmount.setAmount(sum);
                realm.insertOrUpdate(automaticAmount);
                Log.d("update auto amount ", automaticAmount.toString());
            }
        });
    }

    public void deleteAutomaticAmount(final AutomaticAmount automaticAmount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete auto amount ", automaticAmount.toString());
                realm.where(AutomaticAmount.class).equalTo("id",automaticAmount.getId()).findFirst().deleteFromRealm();

            }
        });
    }

}