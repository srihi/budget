package com.benjamin.ledet.budget.model;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject {

    @PrimaryKey
    private long id;

    private String label;

    private boolean isIncome;

    private double budget;

    private boolean isArchived;

    private RealmList<Amount> amounts;

    private RealmList<AutomaticTransaction> automaticTransactions;

    @Ignore
    private TextDrawable icon;

    public Category() {
    }

    public Category(int id, String label, boolean isIncome) {
        this.id = id;
        this.label = label;
        this.isIncome = isIncome;
    }

    public Category(int id, String label, boolean isIncome, double budget) {
        this.id = id;
        this.label = label;
        this.isIncome = isIncome;
        this.budget = budget;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public void setIcon(TextDrawable icon) {
        this.icon = icon;
    }

    public RealmList<Amount> getAmounts() {
        return amounts;
    }

    public void setAmounts(RealmList<Amount> amounts) {
        this.amounts = amounts;
    }

    public RealmList<AutomaticTransaction> getAutomaticTransactions() {
        return automaticTransactions;
    }

    public void setAutomaticTransactions(RealmList<AutomaticTransaction> automaticTransactions) {
        this.automaticTransactions = automaticTransactions;
    }

    public TextDrawable getIcon() {
        String firstLetter = this.label.substring(0,1).toUpperCase();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        this.icon = TextDrawable.builder().buildRound(firstLetter, color);
        return this.icon;
    }

    @Override
    public String toString() {
        return  "id : " + id + " - " +
                "label : " + label + " - " +
                "income : " + isIncome + " - " +
                "budget : " + budget + " - " +
                "archived : " + isArchived;
    }

}
