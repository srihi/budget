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

    private boolean isArchived;

    private RealmList<Amount> amounts;

    private RealmList<AutomaticAmount> automaticAmounts;

    @Ignore
    private TextDrawable icon;

    public Category() {
    }

    public Category(int id, String label, boolean isIncome) {
        this.id = id;
        this.label = label;
        this.isIncome = isIncome;
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

    public RealmList<AutomaticAmount> getAutomaticAmounts() {
        return automaticAmounts;
    }

    public void setAutomaticAmounts(RealmList<AutomaticAmount> automaticAmounts) {
        this.automaticAmounts = automaticAmounts;
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
                "archived : " + isArchived;
    }

}
