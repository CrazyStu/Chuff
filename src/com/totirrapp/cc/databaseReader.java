package com.totirrapp.cc;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;

public class databaseReader {
    public static Context context;
    private databaseAdapter db = null;

    public databaseReader(){
        context = MainActivity.context;
        db = new databaseAdapter(context);
        try {
            db.open();
            Log.e("DB open test", "opened database");
            db.close();
        } catch (SQLException e) {
            Log.e("DB open error", "failed to open database");
        }
    }
    public ArrayList getChartInfo(int x){
        Cursor myCursor;
        try {
            db.open();
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
    public void getChartCount(){
        Cursor myCursor;
        try {
            db.open();
            myCursor = db.chartCount();
            DBV.chartCount = myCursor.getCount();
            db.close();
            Log.e("#-- DBR Chart Count--# ", DBV.chartCount + " charts found");
        } catch (SQLException e) {
            Log.e("DBReadAll error", "failed to count database");
            throw e;
        }
    }

}