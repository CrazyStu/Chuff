package com.Stu.chuffchart3;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends FragmentActivity implements SettingsFragment.SelectItemListener {
	public static Context context;
	private myDatabaseAdapter db = null;
	private static int RESULT_LOAD_IMAGE = 1;
	
    private String Title = "title";
    private String StartDate = "startdate";  
    private String EndDate = "enddate";
    private String BGURL = "background";
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getBaseContext();
		db = new myDatabaseAdapter(context);
		DBReadAll();
		Log.d("DB", Title);
		Log.d("DB", StartDate);
		Log.d("DB", EndDate);
		Log.d("DB", BGURL);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setPageMargin(0);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public void onListItemSelected(int position) {
		Log.i("MainActivity", "List Selected -position="+position);
     	if(position==0){
     		myTitleDialog();
     	}else if(position==1){
     	}else if(position==2){
     	}else if(position==3){
     		choosePic();
    	}
	}
	private void myTitleDialog(){
				final Dialog setTitle = new Dialog(this);
				setTitle.setContentView(R.layout.titlesetter);
				setTitle.setTitle("Set Title Text");
				db.open();
				Cursor V = db.getRecord("title",1);
				String title=V.getString(1);
				db.close();
				final EditText newTitle = (EditText)setTitle.findViewById(R.id.setNewTitle);
				newTitle.setText(title);
				newTitle.requestFocus();
				setTitle.show();
				Button submitTitle = (Button) setTitle.findViewById(R.id.button2);
				submitTitle.setOnClickListener(new View.OnClickListener(){
		        public void onClick(View view){
		        	String input = newTitle.getText().toString();
		        	Log.i("titleTest", "-->"+input);
		        	DBUpdate("title",input);
		       	 setTitle.dismiss();    
		        }
		    }); 
			   }
	public void choosePic(){
	    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
	    startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	public void DBUpdate(String item, String data){
		try{
			db.open();	
		}catch(SQLException sqle){throw sqle;}
		if(item=="title"){
		db.updateTitle(1,data);
		}else if(item=="background"){
			db.updatePic(1, data);
		}
   	 db.close();
	}
	public void DBReadAll(){
		Cursor myCursor = null;
		try{
			db.open();	
		}catch(SQLException sqle){throw sqle;}
        myCursor = db.getTable2("all",1);
        myCursor.moveToFirst();
        Title = myCursor.getString(1);
        myCursor.moveToNext();
        StartDate = myCursor.getString(1);
        myCursor.moveToNext();
        EndDate = myCursor.getString(1);
        myCursor.moveToNext();
        BGURL = myCursor.getString(1);
   	 db.close();
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Thumbnails.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Log.i("ChoosePic","URL Selected= "+picturePath);
			Log.i("ChoosePic","Open DB to update BGURL");
			DBUpdate("background",picturePath);
		}
	}
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if(position==1){
				fragment=new HomeFragment();
				Bundle args = new Bundle();
				args.putString("homeTitle", Title);
				args.putString("homeStartDate", StartDate);
				args.putString("homeEndDate", EndDate);
				args.putString(HomeFragment.homeBGURL, BGURL);
				fragment.setArguments(args);
			}else if(position==2){
				fragment=new SettingsFragment();
				Bundle args = new Bundle();
				args.putString(SettingsFragment.settingStartDate, StartDate);
				args.putString(SettingsFragment.settingEndDate, EndDate);
				args.putString(SettingsFragment.settingTitle, Title);
				args.putString(SettingsFragment.settingBGURL, BGURL);
				fragment.setArguments(args);
			}else{
				fragment = new DetailsFragmentTest();
				Bundle args = new Bundle();
				args.putInt(DetailsFragmentTest.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
			}
			return fragment;
		}
		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
}
