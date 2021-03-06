package com.benjamin.ledet.budget.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.benjamin.ledet.budget.R;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Case;
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
                    .schemaVersion(10)
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

    //region Amount

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

    public Amount findAmoutByAutomaticAmountAndMonth(AutomaticTransaction automaticTransaction, Month month){
        return realm.where(Amount.class)
                .equalTo("category.id", automaticTransaction.getCategory().getId())
                .equalTo("label", automaticTransaction.getLabel())
                .equalTo("amount", automaticTransaction.getAmount())
                .equalTo("month.id",month.getId())
                .findFirst();
    }

    public RealmResults<Amount> getAmountsOfMonthOfCategory(Month month, Category category){

        return realm.where(Amount.class)
                .equalTo("month.id",month.getId())
                .equalTo("category.id",category.getId())
                .findAll();
    }

    public double getSumAmountOfMonthOfCategory(Month month, Category category){
        double sum = 0;
        for (Amount amount : getAmountsOfMonthOfCategory(month,category)){
            sum += amount.getAmount();
        }
        return sum;
    }

    public double getSpendingOfMonth(Month month){
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
        return getSumIncomesOfMonth(month) - getSpendingOfMonth(month);
    }

    public void addAmount(final Amount amount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(amount);
                amount.getCategory().getAmounts().add(amount);
                Log.d("add amount ", amount.toString());
                if (amount.getCategory().isIncome()){
                    Toast.makeText(mContext,mContext.getString(R.string.income_added), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext,mContext.getString(R.string.expense_added), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!amount.getCategory().hasMonthlyBudget(amount.getMonth())){
            CategoryMonthlyBudget categoryMonthlyBudget = new CategoryMonthlyBudget();
            categoryMonthlyBudget.setId(getCategoryMonthlyBudgetNextKey());
            categoryMonthlyBudget.setCategory(amount.getCategory());
            categoryMonthlyBudget.setMonth(amount.getMonth());
            categoryMonthlyBudget.setMonthlyBudget(amount.getCategory().getDefaultBudget());
            addCategoryMonthlyBudget(categoryMonthlyBudget);
        }
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
                if (amount.getCategory().isIncome()){
                    Toast.makeText(mContext,mContext.getString(R.string.income_updated), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext,mContext.getString(R.string.expense_updated), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateAmount(final Amount amount, final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amount.setCategory(category);
                realm.insertOrUpdate(amount);
            }
        });
    }

    public void updateAmount(final Amount amount, final Category category, final String label, final int day, final double sum){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amount.setCategory(category);
                amount.setLabel(label);
                amount.setAmount(sum);
                amount.setDay(day);
                realm.insertOrUpdate(amount);
                Log.d("update amount ", amount.toString());
                if (amount.getCategory().isIncome()){
                    Toast.makeText(mContext,mContext.getString(R.string.income_updated), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext,mContext.getString(R.string.expense_updated), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteAmount(final Amount amount){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete amount ", amount.toString());
                if (amount.getCategory().isIncome()){
                    Toast.makeText(mContext,mContext.getString(R.string.income_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext,mContext.getString(R.string.expense_deleted), Toast.LENGTH_SHORT).show();
                }
                realm.where(Amount.class).equalTo("id",amount.getId()).findFirst().deleteFromRealm();
            }
        });
    }
    //endregion

    //region AutomaticTransaction

    public int getAutomaticTransactionNextKey() {
        try {
            return realm.where(AutomaticTransaction.class)
                    .max("id").intValue() + 1;
        }
        catch (NullPointerException e) {
            return 1;
        }
    }

    public RealmResults<AutomaticTransaction> getAutomaticTransactions() {

        return realm.where(AutomaticTransaction.class)
                .findAll();
    }

    public RealmResults<AutomaticTransaction> getAutomaticsIncomes() {

        return realm.where(AutomaticTransaction.class)
                .equalTo("category.isIncome",true)
                .findAllSorted("category.label");
    }

    public RealmResults<AutomaticTransaction> getAutomaticsExpenses() {

        return realm.where(AutomaticTransaction.class)
                .equalTo("category.isIncome",false)
                .findAllSorted("category.label");
    }

    public AutomaticTransaction getAutomaticAmount(long id) {

        return realm.where(AutomaticTransaction.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void addAutomaticAmount(final AutomaticTransaction automaticTransaction){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(automaticTransaction);
                automaticTransaction.getCategory().getAutomaticTransactions().add(automaticTransaction);
                Log.d("add automatic amount ", automaticTransaction.toString());
            }
        });
    }

    public void updateAutomaticAmount(final AutomaticTransaction automaticTransaction, final Category category, final String label, final int day, final double sum){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                automaticTransaction.setCategory(category);
                automaticTransaction.setLabel(label);
                automaticTransaction.setDay(day);
                automaticTransaction.setAmount(sum);
                realm.insertOrUpdate(automaticTransaction);
                Log.d("update auto amount ", automaticTransaction.toString());
            }
        });
    }

    public void updateAutomaticAmount(final AutomaticTransaction automaticTransaction, final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                automaticTransaction.setCategory(category);
                realm.insertOrUpdate(automaticTransaction);
            }
        });
    }

    public void deleteAutomaticAmount(final AutomaticTransaction automaticTransaction){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete auto amount ", automaticTransaction.toString());
                realm.where(AutomaticTransaction.class).equalTo("id", automaticTransaction.getId()).findFirst().deleteFromRealm();

            }
        });
    }
    //endregion

    //region Category

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

    public RealmResults<Category> getArchivedCategoriesIncome() {

        return realm.where(Category.class)
                .equalTo("isIncome",true)
                .equalTo("isArchived",true)
                .findAllSorted("label");
    }

    public RealmResults<Category> getUnarchivedCategoriesIncome() {

        return realm.where(Category.class)
                .equalTo("isIncome",true)
                .equalTo("isArchived",false)
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

    public RealmResults<Category> getArchivedCategoriesExpense() {

        return realm.where(Category.class)
                .equalTo("isIncome",false)
                .equalTo("isArchived",true)
                .findAllSorted("label");
    }

    public RealmResults<Category> getUnarchivedCategoriesExpense() {

        return realm.where(Category.class)
                .equalTo("isIncome",false)
                .equalTo("isArchived",false)
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

    public Category findCategoryExpenseByLabel(String label, long id){
        return realm.where(Category.class)
                .equalTo("label", label, Case.INSENSITIVE)
                .notEqualTo("id",id)
                .equalTo("isIncome",false)
                .findFirst();
    }

    public Category findCategoryExpenseByLabel(String label, long id1, long id2){
        return realm.where(Category.class)
                .equalTo("label", label, Case.INSENSITIVE)
                .notEqualTo("id",id1)
                .notEqualTo("id",id2)
                .equalTo("isIncome",false)
                .findFirst();
    }

    public Category findCategoryIncomeByLabel(String label, long id){
        return realm.where(Category.class)
                .equalTo("label", label, Case.INSENSITIVE)
                .notEqualTo("id",id)
                .equalTo("isIncome",true)
                .findFirst();
    }

    public Category findCategoryIncomeByLabel(String label, long id1, long id2){
        return realm.where(Category.class)
                .equalTo("label", label, Case.INSENSITIVE)
                .notEqualTo("id",id1)
                .notEqualTo("id",id2)
                .equalTo("isIncome",true)
                .findFirst();
    }

    public void addCategory(final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(category);
                Toast.makeText(mContext,mContext.getString(R.string.category_added), Toast.LENGTH_SHORT).show();
                Log.d("add category ", category.toString());
            }
        });
    }

    public void archiveCategory(final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category.setArchived(true);
                realm.insertOrUpdate(category);
                Log.d("archive category ", category.toString());
                Toast.makeText(mContext,mContext.getString(R.string.category_archived), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unarchiveCategory(final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category.setArchived(false);
                realm.insertOrUpdate(category);
                Log.d("unarchive category ", category.toString());
                Toast.makeText(mContext,mContext.getString(R.string.category_unarchived), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext,mContext.getString(R.string.category_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCategory(final Category category, final double defaultBudget){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category.setDefaultBudget(defaultBudget);
                realm.insertOrUpdate(category);
                Log.d("update category ", category.toString());
                Toast.makeText(mContext,mContext.getString(R.string.category_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCategory(final Category category, final String label, final double budget){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category.setLabel(label);
                category.setDefaultBudget(budget);
                realm.insertOrUpdate(category);
                Log.d("update category ", category.toString());
                Toast.makeText(mContext,mContext.getString(R.string.category_updated), Toast.LENGTH_SHORT).show();

            }
        });
        if(getCategoryMonthlyBudget(category,getActualMonth()) != null){
            updateCategoryMonthlyBudget(getCategoryMonthlyBudget(category,getActualMonth()),budget);
        }
    }

    public void deleteCategory(final Category category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete category ", category.toString());
                realm.where(Category.class).equalTo("id",category.getId()).findFirst().deleteFromRealm();
                Toast.makeText(mContext,mContext.getString(R.string.category_deleted), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mergeCategories(final Category category1, final Category category2, final Category newCategory){
        for (Amount amount : category1.getAmounts()){
            updateAmount(amount,newCategory);
        }
        for (Amount amount : category2.getAmounts()){
            updateAmount(amount,newCategory);
        }
        for (AutomaticTransaction automaticTransaction : category1.getAutomaticTransactions()){
            updateAutomaticAmount(automaticTransaction,newCategory);
        }
        for (AutomaticTransaction automaticTransaction : category2.getAutomaticTransactions()){
            updateAutomaticAmount(automaticTransaction,newCategory);
        }
        deleteCategory(category1);
        deleteCategory(category2);
    }

    //endregion

    //region CategoryMonthlyBudget

    public int getCategoryMonthlyBudgetNextKey() {
        try {
            return realm.where(CategoryMonthlyBudget.class)
                    .max("id").intValue() + 1;
        }
        catch (NullPointerException e) {
            return 1;
        }
    }

    public RealmResults<CategoryMonthlyBudget> getCategoriesMonthlyBudget() {

        return realm.where(CategoryMonthlyBudget.class)
                .findAll();
    }

    public CategoryMonthlyBudget getCategoryMonthlyBudget(long id) {

        return realm.where(CategoryMonthlyBudget.class).equalTo("id", id).findFirst();
    }

    public CategoryMonthlyBudget getCategoryMonthlyBudget(Category category, Month month) {

        return realm.where(CategoryMonthlyBudget.class)
                .equalTo("category.id", category.getId())
                .equalTo("month.id", month.getId())
                .findFirst();
    }

    public void addCategoryMonthlyBudget(final CategoryMonthlyBudget categoryMonthlyBudget){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(categoryMonthlyBudget);
                Log.d("add categoryMonthly", categoryMonthlyBudget.toString());
            }
        });
    }

    public void updateCategoryMonthlyBudget(final CategoryMonthlyBudget categoryMonthlyBudget, final double monthlyBudget){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                categoryMonthlyBudget.setMonthlyBudget(monthlyBudget);
                realm.insertOrUpdate(categoryMonthlyBudget);
                Log.d("update categoryMonthly ", categoryMonthlyBudget.toString());
                Toast.makeText(mContext,mContext.getString(R.string.category_monthly_budget_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteCategoryMonthlyBudget(final CategoryMonthlyBudget categoryMonthlyBudget){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d("delete categoryMonthly ", categoryMonthlyBudget.toString());
                realm.where(CategoryMonthlyBudget.class).equalTo("id",categoryMonthlyBudget.getId()).findFirst().deleteFromRealm();
                Toast.makeText(mContext,mContext.getString(R.string.category_monthly_budget_deleted), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //endregion

    //region Month

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

    public RealmResults<Month> getMonthsOfYear(int year) {

        return realm.where(Month.class).equalTo("year",year).findAll();
    }

    public Month getMonth(int month, int year) {

        return realm.where(Month.class)
                .equalTo("month", month)
                .equalTo("year", year)
                .findFirst();
    }

    public Month getMonth(long id) {

        return realm.where(Month.class)
                .equalTo("id", id)
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
    //endregion

    //region User

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
    //endregion

}