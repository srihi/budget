package com.benjamin.ledet.budget.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CategoryMonthlyBudget extends RealmObject {

    @PrimaryKey
    private long id;

    private Category category;

    private Month month;

    private double monthlyBudget;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    @Override
    public String toString() {
        return  "id : " + id + " - " +
                "category : " + category.toString() + " - " +
                "month : " + month.toString() + " - " +
                "monthlyBudget : " + monthlyBudget;
    }
}
