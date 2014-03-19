package com.Stu.chuffchart3;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SettingsFragment extends ListFragment{
	public static String settingTitle = "noTitle";
	public static String settingBGURL = "noURL";
	public static String settingStartDate = "noDate";
	public static String settingEndDate = "noDate";
	SelectItemListener mCallback;

	private myDatabaseAdapter db = null;
	private Cursor myCursor;
	private Context context;
	private SimpleCursorAdapter myAdapter;
	private int[] views = new int[]{R.id.rowText1,R.id.rowText2};
	
	
	public interface SelectItemListener {
        public void onListItemSelected(int position);
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (SelectItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SelectItemListener");
        }
    }


	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.settings_fragment, container, false);
		context = MainActivity.context;
		settingTitle = getArguments().getString(settingTitle);
		settingStartDate = getArguments().getString(settingStartDate);
		settingEndDate = getArguments().getString(settingEndDate);
		settingBGURL = getArguments().getString(settingBGURL);
//		Bundle bund = getArguments();
		db = new myDatabaseAdapter(context);
		return rootView;
	}
		public void loadList(){
	        try{
	        	Log.i("ListFragment", "Open DB loadList()");
		        db.open();
		        myCursor = db.getTable2("all",1);
		        db.close();
	        }catch(Exception ex){ex.printStackTrace();}
	        myAdapter = new SimpleCursorAdapter(MainActivity.context, R.layout.row_formats,myCursor,myCursor.getColumnNames(),views,
	        		CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	        setListAdapter(myAdapter);
		}
	    @Override 
	    public void onListItemClick(ListView l, View v, int position, long id) {
		    mCallback.onListItemSelected(position);
//			loadList();
	    }
		public void onResume(){
			super.onResume();
			 Log.i("ListFragment", "Resume -loadList()");
			loadList();
		}
	  
	    
}

