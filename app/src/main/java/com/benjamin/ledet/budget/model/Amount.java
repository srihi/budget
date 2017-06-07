package com.benjamin.ledet.budget.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Amount extends RealmObject {

    @PrimaryKey
    private long id;

    private Category category;

    private int day;

    private Month month;

    private String label;

    private double amount;

    private Boolean isAutomatic;

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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Boolean isAutomatic() {
        return isAutomatic;
    }

    public void setAutomatic(Boolean automatic) {
        isAutomatic = automatic;
    }

    @Override
    public String toString() {
        return  "id : " + id + " - " +
                "category : " + category.toString() + " - " +
                "day : " + day + " - " +
                "month : " + month.toString() + " - " +
                "label : " + label + " - " +
                "amount : " + amount +
                "isAutomatic : " + isAutomatic;

    }
}
