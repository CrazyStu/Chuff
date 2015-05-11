package com.totirrapp.cc;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;

public class databaseReader {
    public static Context context;
    private static databaseAdapter db = null;

    public databaseReader(String x){
        context = MainActivity.context;
        db = new databaseAdapter(context);
//        try {
//            db.open(x);
//            Log.e("DB open test", "opened database");
//            db.close();
//        } catch (SQLException e) {
//            Log.e("DB open error", "failed to open database");
//        }
    }
    public static ArrayList getChartInfo(int x, String y){
        Cursor myCursor;
        try {
            db.open(y);
        } catch (SQLException e) {
            Log.e("DBReadAll error", "failed to open database");
            throw e;
        }
        ArrayList<String> values = new ArrayList<String>();
        myCursor = db.getFullChartNo(x);
        values.add(myCursor.getString(1));
        values.add(myCursor.getString(2));
        values.add(myCursor.getString(3));
        values.add(myCursor.getString(4));
        values.add(myCursor.getString(5));
        values.add(myCursor.getString(6));
        db.close();
        return values;
    }
    public static void getChartCount(String x){
        Cursor myCursor;
        try {
            db.open(x);
            DBV.chartCount = db.chartCount();
            db.close();
            Log.e("#-- DBR Chart Count--# ", DBV.chartCount + " charts found");
        } catch (SQLException e) {
            Log.e("DBReadAll error", "failed to count database");
            throw e;
        }
    }
    public static String getChartName(int x){
        db.open("get chart Name");
        String temp = db.getChartName(x);
        db.close();
        return temp;
    }
    public static void updateBGURL(String chart,String url){
        db.open("set bgurl");
        db.updatePic(chart, url);
        db.close();
    }
    public static void updateTitle(String chart,String url){
        db.open("set Title2");
        db.updateTitle(chart, url);
        db.close();
    }
    public static void updateStartDate(String chart,String url){
        db.open("setEndDate");
        db.updateStartDate(chart, url);
        db.close();
    }
    public static void updateEndDate(String chart,String url){
        db.open("setEndDate");
        db.updateEndDate(chart, url);
        db.close();
    }
    public static void deleteChart(String chart){
        db.open("delete chart");
        db.deleteRecord(chart);
        db.close();
    }
}