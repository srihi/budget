package com.benjamin.ledet.budget.model;

import android.content.Context;

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

    public static String intMonthToStringMonth(int month, Context context){
        String monthString = "";
        switch (month){
            case 1:
                monthString = context.getString(january);
                break;
            case 2:
                monthString = context.getString(R.string.february);
                break;
            case 3:
                monthString = context.getString(R.string.march);
                break;
            case 4:
                monthString = context.getString(R.string.april);
                break;
            case 5:
                monthString = context.getString(R.string.may);
                break;
            case 6:
                monthString = context.getString(R.string.june);
                break;
            case 7:
                monthString = context.getString(R.string.july);
                break;
            case 8:
                monthString = context.getString(R.string.august);
                break;
            case 9:
                monthString = context.getString(R.string.september);
                break;
            case 10:
                monthString = context.getString(R.string.october);
                break;
            case 11:
                monthString = context.getString(R.string.november);
                break;
            case 12:
                monthString = context.getString(R.string.december);
                break;
            default:
                monthString = context.getString(R.string.error);
                break;
        }
        return  monthString;
    }

    public static int stringMonthToIntMonth(String month, Context context){
        int monthInt;
        if(month.equals(context.getString(january))){
            monthInt = 1;
        }else if(month.equals(context.getString(february))) {
            monthInt = 2;
        }else if(month.equals(context.getString(march))) {
            monthInt = 3;
        }else if(month.equals(context.getString(april))) {
            monthInt = 4;
        }else if(month.equals(context.getString(may))) {
            monthInt = 5;
        }else if(month.equals(context.getString(june))) {
            monthInt = 6;
        }else if(month.equals(context.getString(july))) {
            monthInt = 7;
        }else if(month.equals(context.getString(august))) {
            monthInt = 8;
        }else if(month.equals(context.getString(september))) {
            monthInt = 9;
        }else if(month.equals(context.getString(october))) {
            monthInt = 10;
        }else if(month.equals(context.getString(november))) {
            monthInt = 11;
        }else {
            monthInt = 12;
        }
        return monthInt;
    }

    @Override
    public String toString() {
        return  "id : " + id + " - " +
                "month : " + month + " - " +
                "year : " + year;
    }
}