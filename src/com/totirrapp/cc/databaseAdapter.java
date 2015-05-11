package com.totirrapp.cc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class databaseAdapter {

    public static final String TAG ="DBAdapter";
    public static final String DATABASE_NAME = "settingsdata";
    public static final String CHARTS_TABLE = "Chart_Table";
    public static final String KEY_ROWID ="_id";
    public static final String CHART_NAME = "Chart_Name";
    public static final String KEY_TITLE1 = "Title1";
    public static final String KEY_TITLE2 = "Title2";
    public static final String KEY_START_DATE = "Start_Date";
    public static final String KEY_END_DATE = "End_Date";
    public static final String KEY_PIC = "Picture_URL";
    public static final String SETTINGS_TABLE = "Settings_Table";
    public static final String SET_TABLE2 = "Table2";
    private static DatabaseHelper myDatabaseHelper;
    private static SQLiteDatabase db;

    public databaseAdapter(Context context){
        myDatabaseHelper=new DatabaseHelper(context);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper{

        private static final int DATABASE_VERSION=3;

        private static final String CREATE_CHART_TABLE = " create table if not exists "+CHARTS_TABLE + " (_id integer primary key," +
                CHART_NAME+" TEXT UNIQUE ON CONFLICT ABORT," +
                KEY_TITLE1+" TEXT," +
                KEY_TITLE2+" TEXT," +
                KEY_START_DATE+" TEXT," +
                KEY_END_DATE+" TEXT," +
                KEY_PIC+" TEXT);";
        private static final String TRANSFER_DATA = "insert into "+CHARTS_TABLE+" ("+
                KEY_ROWID+","+
                KEY_TITLE2+","+
                KEY_START_DATE+","+
                KEY_END_DATE+","+
                KEY_PIC+") select * from "+SETTINGS_TABLE+" LIMIT 1";
        private static final String FILL_BLANKS = "update "+CHARTS_TABLE+" set "+CHART_NAME+" = 'My Chuff Chart', "+KEY_TITLE1+" ='I can''t wait'";

        public DatabaseHelper(Context context){
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase database){
            Log.i("Database", "No DB try to create...");
            createChartTable(database);
            Log.i("Database", "No previous information ws found new Database was created :D");
        }
        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Log.w(TAG,"Upgrade required from "+oldVersion+" to " + newVersion + ". please report any errors.");
            createChartTable(database);
            transferTableData(database);
            database.execSQL("drop table if exists "+SET_TABLE2);
            database.execSQL("drop table if exists "+SETTINGS_TABLE);

            Log.i("Upgrade Complete", "Old data preserved,old tables gone");

        }
        public void createChartTable(SQLiteDatabase database){
            Log.i("Database", "try to create chart table");
            database.execSQL(CREATE_CHART_TABLE);
            Log.i("Database", "Chart Table Created - Table is currently blank");
        }
        public void onOpen(SQLiteDatabase database){
//            Log.i("Database", ">>>>DB OPEN<<<<");
        }


        public void transferTableData(SQLiteDatabase database){
            Log.i("Database transfer", "Attempt database transfer using sql code");
            database.execSQL(TRANSFER_DATA);
            Log.i("Database transfer", "Attempt to fill blanks using sql code");
            database.execSQL(FILL_BLANKS);
            Log.i("Database transfer", "Old data inserted correctly!!");
        }
    }
    public databaseAdapter open(String x) throws SQLException{
        Log.d("Database",">>>>Open() called by "+x);
        db = myDatabaseHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        myDatabaseHelper.close();
        Log.i("Database", ">>>>DB Closed<<<<");
    }
//    Update database information
    public void newChart(String v1, String v2, String v3, String v4, String v5, String v6){
        ContentValues newChartValues = new ContentValues();
        newChartValues.put(CHART_NAME, v1);
        newChartValues.put(KEY_TITLE1, v2);
        newChartValues.put(KEY_TITLE2, v3);
        newChartValues.put(KEY_START_DATE, v4);
        newChartValues.put(KEY_END_DATE, v5);
        newChartValues.put(KEY_PIC, v6);
        db.insert(CHARTS_TABLE, null, newChartValues);
    }
    public boolean updateStartDate(String name, String date){
        Log.i("updateStartDate", date);
        ContentValues args = new ContentValues();
        args.put(KEY_START_DATE, date);
        return db.update(CHARTS_TABLE, args, CHART_NAME+" = '"+name+"'",null)>0;
    }
    public boolean updateEndDate(String name, String date){
        Log.i("updateEndDate", date);
        ContentValues args = new ContentValues();
        args.put(KEY_END_DATE, date);
        return db.update(CHARTS_TABLE, args, CHART_NAME+" = '"+name+"'",null)>0;
    }
    public boolean updatePic(String name, String url){
        Log.i("updatePic", url);
        ContentValues args = new ContentValues();
        args.put(KEY_PIC, url);
        return db.update(CHARTS_TABLE, args, CHART_NAME+" = '"+name+"'",null)>0;
    }
    public boolean updateTitle(String name, String url){
        ContentValues args = new ContentValues();
        Log.i("updateTitle", "updateTitle");
        args.put(KEY_TITLE2, url);
        return db.update(CHARTS_TABLE, args, CHART_NAME+" = '"+name+"'",null)>0;
    }
    public boolean deleteRecord(String name){
        Log.e("Delete Record", "Attempting to delete chart name..." + name);
        return db.delete(CHARTS_TABLE, CHART_NAME + " = '" + name + "'", null)>0;
    }
    //    Retrieve Database Information
    public Cursor getAllRecords()throws SQLException{
        Cursor myCursor;
        myCursor = db.query(true, SETTINGS_TABLE, new String[]{KEY_ROWID,CHART_NAME,KEY_TITLE1,KEY_TITLE2,KEY_START_DATE,KEY_END_DATE,KEY_PIC}, null, null, null, null, null, null);
        if(myCursor!=null){
            myCursor.moveToFirst();
        }
        return myCursor;
    }
    public Cursor getChartNo(int rowId)throws SQLException{
        Cursor myCursor;
        myCursor = db.query(true, CHARTS_TABLE, new String[]{KEY_ROWID,KEY_TITLE2,KEY_START_DATE,KEY_END_DATE,KEY_PIC},KEY_ROWID+"="+rowId, null, null, null, null, null);
        if(myCursor!=null){
            myCursor.moveToFirst();
        }
        return myCursor;
    }
    public Cursor getFullChartNo(int rowId)throws SQLException{
        Cursor myCursor;
        myCursor = db.query(true, CHARTS_TABLE, new String[]{KEY_ROWID,CHART_NAME,KEY_TITLE1,KEY_TITLE2,KEY_START_DATE,KEY_END_DATE,KEY_PIC},null, null, null, null, null, null);
        if(myCursor!=null){
            myCursor.moveToFirst();
            int i=1;
            while(i<rowId){
                myCursor.moveToNext();
                i++;
            }
        }
        return myCursor;
    }
    public String getChartName(int rowId)throws SQLException{
        Cursor myCursor;
        myCursor = db.query(true, CHARTS_TABLE, new String[]{CHART_NAME},null, null, null, null, null, null);
        if(myCursor!=null){
            myCursor.moveToFirst();
            int i=1;
            while(i<rowId){
                myCursor.moveToNext();
                i++;
            }
        }
        return myCursor.getString(0);
    }
    public Cursor getAllChartNames()throws SQLException{
        Cursor myCursor;
        myCursor = db.query(true, CHARTS_TABLE, new String[]{CHART_NAME},null, null, null, null, null, null);
        return myCursor;
    }
    public int chartCount()throws SQLException{
        Cursor myCursor;
        myCursor = db.query(true, CHARTS_TABLE, new String[]{KEY_ROWID},null, null, null, null, null, null);
        if(myCursor!=null){
            myCursor.moveToFirst();
        }
        return myCursor.getCount();
    }
}
