package com.Stu.chuffchart3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class myDatabaseAdapter {

	public static final String TAG ="DBAdapter";
	public static final String DATABASE_NAME = "settingsdata";
	public static final String SETTINGS_TABLE = "Settings_Table";
	public static final String SET_TABLE2 = "Table2";
	
	public static final String KEY_SET ="Setting";
	public static final String KEY_VAL ="Value";
	
	public static final String KEY_ROWID ="_id";
	
	public static final String KEY_TITLE = "Title";
	public static final String KEY_START_DATE = "Start_Date";
	public static final String KEY_END_DATE = "End_Date";
	public static final String KEY_PIC = "Picture_URL";	

	public static final String NoImage = "NoImage";
	
	private static DatabaseHelper myDatabaseHelper;
	private static SQLiteDatabase db;
	
	public myDatabaseAdapter(Context context){
		myDatabaseHelper=new DatabaseHelper(context);
	}
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		private static final int DATABASE_VERSION=1;
		private static final String CREATE_SETTINGS_TABLE = " create table "+SETTINGS_TABLE + " (_id integer primary key autoincrement," +
				KEY_TITLE+" TEXT NOT NULL," +
				KEY_START_DATE+" TEXT NOT NULL," +
				KEY_END_DATE+" INTEGER NOT NULL," +
				KEY_PIC+" TEXT NOT NULL);";
		private static final String CREATE_SETTINGS_TABLE2 = " create table "+SET_TABLE2 + " (_id integer primary key autoincrement," +
				KEY_SET+" TEXT NOT NULL," +
				KEY_VAL+" TEXT NOT NULL);";
		public DatabaseHelper(Context context){
			super(context, DATABASE_NAME,null,DATABASE_VERSION);
		}
		public void onCreate(SQLiteDatabase database){
			Log.i("Database", "No DB try to create...");
			database.execSQL(CREATE_SETTINGS_TABLE);
			database.execSQL(CREATE_SETTINGS_TABLE2);
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TITLE,"to go Home!");
			initialValues.put(KEY_START_DATE,"20/01/2014");
			initialValues.put(KEY_END_DATE,"20/11/2014");
			initialValues.put(KEY_PIC,"NULL");
			database.insert(SETTINGS_TABLE, null, initialValues);
			
			ContentValues newValues = new ContentValues();
			newValues.put(KEY_SET,"Title");
			newValues.put(KEY_VAL,"What is this???");
			database.insert(SET_TABLE2, null, newValues);
			
			newValues.put(KEY_SET,"Start Date");
			newValues.put(KEY_VAL,"01/01/2014");
			
			database.insert(SET_TABLE2, null, newValues);
			newValues.put(KEY_SET,"End Date");
			newValues.put(KEY_VAL,"10/10/2014");
			database.insert(SET_TABLE2, null, newValues);

			newValues.put(KEY_SET,"Background");
			newValues.put(KEY_VAL,"//SD_Card/pictures...");
			database.insert(SET_TABLE2, null, newValues);
			Log.i("db", "New Database Created :D");	
		}
		public void onOpen(SQLiteDatabase database){
			Log.i("Database", "...is OPEN");
		}
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			Log.w(TAG,"Upgrading database from version"+oldVersion+" to" + newVersion + ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS todo");
			onCreate(database);
		}	
	}
	public myDatabaseAdapter open() throws SQLException{
		db = myDatabaseHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		myDatabaseHelper.close();
		Log.i("Database", "...is Closed");
	}
	public boolean updateDates(long rowId, String newSdate,String newEdate){
		ContentValues args = new ContentValues();
		args.put(KEY_START_DATE, newSdate);
		args.put(KEY_END_DATE, newEdate);
		
		ContentValues args2 = new ContentValues();
		args2.put(KEY_VAL, newSdate);
		db.update(SET_TABLE2, args2, KEY_ROWID+"="+2,null);
		args2.put(KEY_VAL, newEdate);
		db.update(SET_TABLE2, args2, KEY_ROWID+"="+3,null);
		return db.update(SETTINGS_TABLE, args, KEY_ROWID+"="+rowId,null)>0;
	}
	public boolean updatePic(long rowId, String url){
		ContentValues args = new ContentValues();
		Log.i("updatePic", "updatePicture");
		args.put(KEY_PIC, url);
		
		ContentValues args2 = new ContentValues();
		args2.put(KEY_VAL, url);
		db.update(SET_TABLE2, args2, KEY_ROWID+"="+4,null);
		
		return db.update(SETTINGS_TABLE, args, KEY_ROWID+"="+rowId,null)>0;
	}
	public boolean updateTitle(long rowId, String url){
		ContentValues args = new ContentValues();
		Log.i("updateTitle", "updateTitle");
		args.put(KEY_TITLE, url);
		ContentValues args2 = new ContentValues();
		args2.put(KEY_VAL, url);
		db.update(SET_TABLE2, args2, KEY_ROWID+"="+1,null);
		return db.update(SETTINGS_TABLE, args, KEY_ROWID+"="+rowId,null)>0;
	}
	public boolean deleteRecord(long rowId){
		return db.delete(SETTINGS_TABLE, KEY_ROWID+"="+rowId, null)>0;
	}
	public Cursor getRecord(String dataType, long rowId)throws SQLException{
		Cursor myCursor;
		if(dataType=="background"){
			myCursor = db.query(true, SETTINGS_TABLE, new String[]{KEY_ROWID, KEY_PIC}, KEY_ROWID+"="+rowId, null, null, null, null, null);
		}else if(dataType=="title"){
			myCursor = db.query(true, SETTINGS_TABLE, new String[]{KEY_ROWID, KEY_TITLE}, KEY_ROWID+"="+rowId, null, null, null, null, null);
		}else if(dataType=="Settings"){
			myCursor = db.query(true, SETTINGS_TABLE, new String[]{KEY_TITLE,KEY_START_DATE,KEY_END_DATE}, KEY_ROWID+"="+rowId, null, null, null, null, null);
		}else{
			myCursor = db.query(true, SETTINGS_TABLE, new String[]{KEY_ROWID, KEY_START_DATE,KEY_END_DATE}, KEY_ROWID+"="+rowId, null, null, null, null, null);
		}
		if(myCursor!=null){
			myCursor.moveToFirst();
		}
		return myCursor;
	}	
	public Cursor getTable2(String dataType, long rowId)throws SQLException{
		Cursor myCursor = null;
		if(dataType=="all"){
			myCursor = db.query(false, SET_TABLE2, new String[]{KEY_SET, KEY_VAL,KEY_ROWID}, null, null, null, null, null, null);
		}
		if(myCursor!=null){
			myCursor.moveToFirst();
		}
		return myCursor;
	}
	
}
