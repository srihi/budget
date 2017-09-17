package com.benjamin.ledet.budget.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AutomaticAmount extends RealmObject {

    @PrimaryKey
    private long id;

    private Category category;

    private Month monthOfCreation;

    private int day;

    private String label;

    private double amount;

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

    public Month getMonthOfCreation() {
        return monthOfCreation;
    }

    public void setMonthOfCreation(Month monthOfCreation) {
        this.monthOfCreation = monthOfCreation;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    @Override
    public String toString() {
        return  "id : " + id + " - " +
                "monthOfCreation : " + monthOfCreation.toString() + " - " +
                "category : " + category.toString() + " - " +
                "day : " + day + " - " +
                "label : " + label + " - " +
                "amount : " + amount;
    }
}
