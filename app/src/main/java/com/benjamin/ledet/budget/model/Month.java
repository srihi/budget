package com.benjamin.ledet.budget.model;

import android.content.Context;
import com.benjamin.ledet.budget.R;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by benjaminledet on 05/03/2017.
 */

public class Month extends RealmObject {

    @PrimaryKey
    private long id;

    private int month;

    @Index
    private int year;

    private RealmList<Amount> amounts;

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

    public RealmList<Amount> getAmounts() {
        return amounts;
    }

    public void setAmounts(RealmList<Amount> amounts) {
        this.amounts = amounts;
    }

    public static String displayMonthString(int month, Context context){
        String monthString = "";
        switch (month){
            case 1:
                monthString = context.getString(R.string.january);
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

    @Override
    public String toString() {
        return  "id : " + id + " - " +
                "month : " + month + " - " +
                "year : " + year;
    }
}