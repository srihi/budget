package com.benjamin.ledet.budget.model;

import com.benjamin.ledet.budget.BudgetApplication;
import com.benjamin.ledet.budget.R;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

import static com.benjamin.ledet.budget.R.string.april;
import static com.benjamin.ledet.budget.R.string.august;
import static com.benjamin.ledet.budget.R.string.february;
import static com.benjamin.ledet.budget.R.string.january;
import static com.benjamin.ledet.budget.R.string.july;
import static com.benjamin.ledet.budget.R.string.june;
import static com.benjamin.ledet.budget.R.string.march;
import static com.benjamin.ledet.budget.R.string.may;
import static com.benjamin.ledet.budget.R.string.november;
import static com.benjamin.ledet.budget.R.string.october;
import static com.benjamin.ledet.budget.R.string.september;

public class Month extends RealmObject {

    @PrimaryKey
    private long id;

    private int month;

    @Index
    private int year;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static int stringMonthToIntMonth(String month){
        int monthInt;
        if(month.equals(BudgetApplication.getContext().getString(january))){
            monthInt = 1;
        }else if(month.equals(BudgetApplication.getContext().getString(february))) {
            monthInt = 2;
        }else if(month.equals(BudgetApplication.getContext().getString(march))) {
            monthInt = 3;
        }else if(month.equals(BudgetApplication.getContext().getString(april))) {
            monthInt = 4;
        }else if(month.equals(BudgetApplication.getContext().getString(may))) {
            monthInt = 5;
        }else if(month.equals(BudgetApplication.getContext().getString(june))) {
            monthInt = 6;
        }else if(month.equals(BudgetApplication.getContext().getString(july))) {
            monthInt = 7;
        }else if(month.equals(BudgetApplication.getContext().getString(august))) {
            monthInt = 8;
        }else if(month.equals(BudgetApplication.getContext().getString(september))) {
            monthInt = 9;
        }else if(month.equals(BudgetApplication.getContext().getString(october))) {
            monthInt = 10;
        }else if(month.equals(BudgetApplication.getContext().getString(november))) {
            monthInt = 11;
        }else {
            monthInt = 12;
        }
        return monthInt;
    }

    public String monthString(){
        String monthString = "";
        switch (month){
            case 1:
                monthString = BudgetApplication.getContext().getString(january);
                break;
            case 2:
                monthString = BudgetApplication.getContext().getString(R.string.february);
                break;
            case 3:
                monthString = BudgetApplication.getContext().getString(R.string.march);
                break;
            case 4:
                monthString = BudgetApplication.getContext().getString(R.string.april);
                break;
            case 5:
                monthString = BudgetApplication.getContext().getString(R.string.may);
                break;
            case 6:
                monthString = BudgetApplication.getContext().getString(R.string.june);
                break;
            case 7:
                monthString = BudgetApplication.getContext().getString(R.string.july);
                break;
            case 8:
                monthString = BudgetApplication.getContext().getString(R.string.august);
                break;
            case 9:
                monthString = BudgetApplication.getContext().getString(R.string.september);
                break;
            case 10:
                monthString = BudgetApplication.getContext().getString(R.string.october);
                break;
            case 11:
                monthString = BudgetApplication.getContext().getString(R.string.november);
                break;
            case 12:
                monthString = BudgetApplication.getContext().getString(R.string.december);
                break;
            default:
                monthString = BudgetApplication.getContext().getString(R.string.error);
                break;
        }
        return  monthString;
    }

    @Override
    public String toString() {
        String monthString = "";
        switch (month){
            case 1:
                monthString = BudgetApplication.getContext().getString(january);
                break;
            case 2:
                monthString = BudgetApplication.getContext().getString(R.string.february);
                break;
            case 3:
                monthString = BudgetApplication.getContext().getString(R.string.march);
                break;
            case 4:
                monthString = BudgetApplication.getContext().getString(R.string.april);
                break;
            case 5:
                monthString = BudgetApplication.getContext().getString(R.string.may);
                break;
            case 6:
                monthString = BudgetApplication.getContext().getString(R.string.june);
                break;
            case 7:
                monthString = BudgetApplication.getContext().getString(R.string.july);
                break;
            case 8:
                monthString = BudgetApplication.getContext().getString(R.string.august);
                break;
            case 9:
                monthString = BudgetApplication.getContext().getString(R.string.september);
                break;
            case 10:
                monthString = BudgetApplication.getContext().getString(R.string.october);
                break;
            case 11:
                monthString = BudgetApplication.getContext().getString(R.string.november);
                break;
            case 12:
                monthString = BudgetApplication.getContext().getString(R.string.december);
                break;
            default:
                monthString = BudgetApplication.getContext().getString(R.string.error);
                break;
        }
        return  monthString + " " + year;
    }
}