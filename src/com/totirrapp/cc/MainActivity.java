package com.totirrapp.cc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static com.totirrapp.cc.R.id.daysDoneTextView;
import static com.totirrapp.cc.SetCounter.getDaysDone;
import static com.totirrapp.cc.SetCounter.getDaysLeft;
import static com.totirrapp.cc.SetCounter.getDaysTDone;
import static com.totirrapp.cc.SetCounter.getDaysTLeft;
import static com.totirrapp.cc.SetCounter.getEndDate;
import static com.totirrapp.cc.SetCounter.getHoursDone;
import static com.totirrapp.cc.SetCounter.getHoursLeft;
import static com.totirrapp.cc.SetCounter.getHoursTDone;
import static com.totirrapp.cc.SetCounter.getHoursTLeft;
import static com.totirrapp.cc.SetCounter.getMinsDone;
import static com.totirrapp.cc.SetCounter.getMinsLeft;
import static com.totirrapp.cc.SetCounter.getMinsTDone;
import static com.totirrapp.cc.SetCounter.getMinsTLeft;
import static com.totirrapp.cc.SetCounter.getMonthsDone;
import static com.totirrapp.cc.SetCounter.getMonthsLeft;
import static com.totirrapp.cc.SetCounter.getMonthsTDone;
import static com.totirrapp.cc.SetCounter.getMonthsTLeft;
import static com.totirrapp.cc.SetCounter.getSecsDone;
import static com.totirrapp.cc.SetCounter.getSecsLeft;
import static com.totirrapp.cc.SetCounter.getSecsTDone;
import static com.totirrapp.cc.SetCounter.getSecsTLeft;
import static com.totirrapp.cc.SetCounter.getStartDate;
import static com.totirrapp.cc.SetCounter.getWeeksDone;
import static com.totirrapp.cc.SetCounter.getWeeksLeft;
import static com.totirrapp.cc.SetCounter.getWeeksTDone;
import static com.totirrapp.cc.SetCounter.getWeeksTLeft;
import static com.totirrapp.cc.SetCounter.updateCounter;

public class MainActivity extends FragmentActivity implements SettingsFragment.SelectItemListener, View.OnClickListener {
	public static Context			context;
	private myDatabaseAdapter		db					= null;
	private static int				RESULT_LOAD_IMAGE	= 1;
	private SectionsPagerAdapter	mSectionsPagerAdapter;
	private ViewPager				mViewPager;
	private SettingsFragment		setFrag;
	private HomeFragment			homeFrag;
	private DetailsFragment			detFrag;
	private String					noImage				= "Android Wallpaper";
	private boolean					running				= true;
	private detailsThread			MT;
	private Bitmap					bg1;
//    private OnClickListener		shortPress;
//    private OnLongClickListener longPress;
	// private HomeView testings;
	private int count =0;

	private TextView				percent				= null;
	private TextView				timeDoneText			= null;
	private TextView				timeLeftText			= null;
	private TextView				titleBot			= null;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		context = this.getBaseContext();
		db = new myDatabaseAdapter(context);
		DBReadAll();
		Log.i("DBV1", DBV.sTitle);
		Log.i("DBV2", DBV.Sstart);
		Log.i("DBV3", DBV.sEnd);
		Log.i("DBV4", DBV.sBgUrl);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setTheme(R.style.myTheme2);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setPageMargin(0);
		BitmapDrawable temp2 = new BitmapDrawable(context.getResources(), getBackground(DBV.sWidth, DBV.sHeight));
		temp2.setGravity(Gravity.CENTER);
		mViewPager.setBackground(temp2);
//        setupListeners();
//        setupHomeViewButtons();

	}
	protected void onPause(){
		super.onPause();
		running = false;
		Log.e("MT State", MT.getState() + "");
	}
	protected void onResume(){
		super.onResume();
		running = true;
		// Log.e("MT State", MT.getState()+"");
		try {
			MT = null;
			MT = new detailsThread();
			MT.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
//        setupHomeViewButtons();
	}

/*    public void setupListeners(){
        shortPress= new View.OnClickListener() {
            public void onClick(View v){
               Log.d("shortPress","viewID ="+v.getId());
            }
        };
        longPress = new View.OnLongClickListener() {
            public boolean onLongClick(View v){
                    Log.d("shortPress","viewID ="+v.getId());
                return false;
            }
        };
    }*/


	// ### test setting up home view buttons
	public void setupHomeViewButtons(){
        Log.i("setupHomeViewButtons","setupHomeViewButtons called");
//		findViewById(daysDoneTextView).setOnClickListener(this);
//        findViewById(daysDoneTextView).setOnClickListener(shortPress);
//        findViewById(daysDoneTextView).setOnLongClickListener(longPress);
	
	}
	public void onClick(View view){
		if (view.getId() == daysDoneTextView) {
			count ++;
			Log.i("done time", "click recorded correctly >>> "+ count);
		}
	}
	
	// ### Update settings methods
	@Override
	public void onListItemSelected(int position){
		Log.i("MainActivity", "List Selected -position=" + position);
		if (position == 0) {
			myTitleDialog();
		} else if (position == 1) {
			myDateDialog(true);
		} else if (position == 2) {
			myDateDialog(false);
		} else if (position == 3) {
			myBGDialog();
		}
	}
	private void myDateDialog(boolean start){
		final Dialog setDate = new Dialog(this);
		setDate.setContentView(R.layout.dialog_dates);
		setDate.show();

		String[] DateInts;
		OnClickListener test = null;
		final DatePicker picker1 = (DatePicker) setDate.findViewById(R.id.datePicker1);
		if (start) {
			setDate.setTitle("Set Start Date");
			DateInts = DBV.Sstart.split("/");
			test = new View.OnClickListener() {
				public void onClick(View view){
					//----check date validity
					String[] EndDate=DBV.sEnd.split("/");
					int Eday = Integer.parseInt(EndDate[0]);
					int Emonth = Integer.parseInt(EndDate[1])-1;
					int Eyear = Integer.parseInt(EndDate[2]);
					int Sday = picker1.getDayOfMonth();
					int Smonth = picker1.getMonth();
					int Syear = picker1.getYear();
					Locale home = new Locale("en");
					Calendar calStart = Calendar.getInstance(home);
					Calendar calEnd = Calendar.getInstance(home);
					calStart.set(Syear,Smonth,Sday,0,0,0);
					calEnd.set(Eyear,Emonth,Eday,0,0,0);
					
					if(calStart.getTimeInMillis()>calEnd.getTimeInMillis()){
						DBV.sEnd = Sday + "/" + (Smonth+1) + "/" + Syear;
						DBUpdate("endDate", DBV.sEnd);
						Toast.makeText(getBaseContext(), "End date moved",Toast.LENGTH_SHORT).show();
					}
					DBV.Sstart  = Sday + "/" + (Smonth+1) + "/" + Syear;
					DBUpdate("startDate", DBV.Sstart);
					setFrag.loadList();
					setDate.dismiss();
					SetCounter.updateCounterAndDates();
				}
			};
		} else {
			setDate.setTitle("Set End Date");
			DateInts = DBV.sEnd.split("/");
			test = new View.OnClickListener() {
				public void onClick(View view){
					//----Check date validity
					String[] StartDate= DBV.Sstart.split("/");
					int Sday = Integer.parseInt(StartDate[0]);
					int Smonth = Integer.parseInt(StartDate[1])-1;
					int Syear = Integer.parseInt(StartDate[2]);
					int Eday = picker1.getDayOfMonth();
					int Emonth = picker1.getMonth();
					int Eyear = picker1.getYear();
					Locale home = new Locale("en");
					Calendar calStart = Calendar.getInstance(home);
					Calendar calEnd = Calendar.getInstance(home);
					calStart.set(Syear,Smonth,Sday,0,0,0);
					calEnd.set(Eyear,Emonth,Eday,0,0,0);
					if(calStart.getTimeInMillis()>calEnd.getTimeInMillis()){
						DBV.sEnd = Sday + "/" + (Smonth+1) + "/" + Syear;
						Toast.makeText(getBaseContext(), "Flux capacitor malfunction!",Toast.LENGTH_SHORT).show();
					}else{
						DBV.sEnd = Eday + "/" + (Emonth+1) + "/" + Eyear;	
					}
					DBUpdate("endDate", DBV.sEnd);
					setFrag.loadList();
					setDate.dismiss();
					SetCounter.updateCounterAndDates();
				}
			};
		}

		int iday = Integer.parseInt(DateInts[0]);
		int imonth = (Integer.parseInt(DateInts[1]) - 1);
		int iyear = Integer.parseInt(DateInts[2]);
		picker1.init(iyear, imonth, iday, null);
		Button submitDate = (Button) setDate.findViewById(R.id.dateButton);
		submitDate.setOnClickListener(test);
	}
	private void myTitleDialog(){
		final Dialog setTitle = new Dialog(this);
		setTitle.setContentView(R.layout.dialog_title);
		setTitle.setTitle("Set Title Text");
		final EditText newTitle = (EditText) setTitle.findViewById(R.id.setNewTitle);
		newTitle.setText(DBV.sTitle);
		setTitle.show();
		newTitle.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(newTitle, InputMethodManager.SHOW_FORCED);
		Button submitTitle = (Button) setTitle.findViewById(R.id.button2);
		submitTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DBV.sTitle = newTitle.getText().toString();
                DBUpdate("title", DBV.sTitle);
                Log.i("title updated", "--> " + DBV.sTitle + " <--");
                setFrag.loadList();
                setTitle.dismiss();
            }
        });
		newTitle.requestFocus();

	}
	private void myBGDialog(){
		final Dialog setBG = new Dialog(this);
		setBG.setContentView(R.layout.dialog_background);
		setBG.setTitle("Choose Background");

		setBG.show();
		Button wallpaperButton = (Button) setBG.findViewById(R.id.wallpaperButton);
		wallpaperButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setBG.dismiss();
                DBV.sBgUrl = noImage;
                db.open();
                db.updatePic(1, DBV.sBgUrl);
                db.close();
                mViewPager.setBackgroundResource(0);
                setFrag.loadList();
            }
        });
		Button galleryButton = (Button) setBG.findViewById(R.id.galleryButton);
		galleryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view){
				setBG.dismiss();
				choosePic();
			}
		});
	}

	// ## Choose Picture / Update DB / Scale Image / Set Background
	public void choosePic(){
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Thumbnails.DATA };
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Log.i("pic", picturePath);

			db.open();
			db.updatePic(1, picturePath);
			db.close();
			DBV.sBgUrl = picturePath;
			setFrag.loadList();
			BitmapDrawable temp2 = new BitmapDrawable(context.getResources(), getBackground(DBV.sWidth, DBV.sHeight));
			temp2.setGravity(Gravity.CENTER);
			mViewPager.setBackground(temp2);
		}

	}
	public Bitmap getBackground(int screenW,int screenH){
		// Context context = MainActivity.context;
		Log.i("BgHandler", "Start Bitmap Processing");
		String bgURL = DBV.sBgUrl;
		if (bgURL.toString() == noImage) {
			mViewPager.setBackgroundResource(0);
		} else {
			Log.i("BgHandler", "Find image: " + bgURL);
			BitmapFactory.Options bgOptions = new BitmapFactory.Options();
			bgOptions.inJustDecodeBounds = true;
			try {
				@SuppressWarnings("unused")
				Bitmap test = BitmapFactory.decodeFile(bgURL, bgOptions);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (bgOptions.outHeight > 0) {
				Log.i("BgHandler", "Image found---Loading selected picture");
				loadSelectedImage(bgURL, screenW, screenH);
			} else {
				Log.i("BgHandler", "Image not found---Loading default backgorund");
				mViewPager.setBackgroundResource(0);
			}
		}

		return bg1;
	}
	private Bitmap loadSelectedImage(String bgURL,int screenW,int screenH){
		boolean landscape = true;
		BitmapFactory.Options bgOptions = new BitmapFactory.Options();
		// ========Check size and get sample size======================
		bgOptions.inJustDecodeBounds = true;
		bg1 = BitmapFactory.decodeFile(bgURL, bgOptions);
		int imageHeight = bgOptions.outHeight;
		int imageWidth = bgOptions.outWidth;
		float ScreenAspect = (float) screenH / (float) screenW;
		float ImageAspect = (float) bgOptions.outHeight / (float) bgOptions.outWidth;
		if (ImageAspect < 1) {
			landscape = true;
		}
		Log.i("BgHandler", "Origional Width= " + bgOptions.outWidth);
		Log.i("BgHandler", "Origional Height= " + bgOptions.outHeight);
		Log.i("BgHandler", "Landscape= " + landscape);

		int iscale;
		if (landscape) {
			ImageAspect = (float) bgOptions.outWidth / (float) bgOptions.outHeight;
			if (imageHeight > screenH) {
				iscale = (int) ((float) imageHeight / (float) screenH);
				bgOptions.inSampleSize = iscale;
			} else {
				iscale = 1;
				bgOptions.inSampleSize = iscale;
			}
		} else {
			ImageAspect = (float) bgOptions.outHeight / (float) bgOptions.outWidth;
			if (imageWidth > screenW) {
				iscale = (int) ((float) imageWidth / (float) screenW) + 2;
				bgOptions.inSampleSize = iscale;
			} else {
				iscale = 1;
				bgOptions.inSampleSize = iscale;
			}
		}
		Log.i("BgHandler", "Sample Size= " + iscale);
		Log.i("BgHandler", "Image aspect ratio= " + ImageAspect);
		Log.i("BgHandler", "Screen aspect ratio= " + ScreenAspect);

		// =============Read rotation==============================
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(bgURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		Log.i("oriented", orientation + "");
		// =============Get full image and resize=====================
		bgOptions.inJustDecodeBounds = false;
		bg1 = BitmapFactory.decodeFile(bgURL, bgOptions);

		Log.i("BgHandler", "New Width= " + bg1.getWidth());
		Log.i("BgHandler", "New Height= " + bg1.getHeight());
		if (landscape) {
			bg1 = Bitmap.createScaledBitmap(bg1, (int) (screenH * ImageAspect), screenH, true);
		} else {
			bg1 = Bitmap.createScaledBitmap(bg1, screenW, (int) (screenW * ImageAspect), true);
			Log.i("BgHandler", "Size=" + screenW + " x " + (int) (screenW * ImageAspect));
		}
		// ==============rotate as required============================
		int degrees = 0;
		if (orientation == 3) {
			degrees = 180;
		} else if (orientation == 6) {
			degrees = 90;
		} else if (orientation == 8) {
			degrees = 270;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		bg1 = Bitmap.createBitmap(bg1, 0, 0, bg1.getWidth(), bg1.getHeight(), matrix, true);

		return bg1;
	}
	// ### Update Database value
	public void DBUpdate(String item,String data){
		try {
			db.open();
		} catch (SQLException sqle) {
			throw sqle;
		}
		if (item == "title") {
			db.updateTitle(1, data);
		} else if (item == "background") {
			db.updatePic(1, data);
		} else if (item == "startDate") {
			db.updateStartDate(1, DBV.Sstart);
		} else if (item == "endDate") {
			db.updateEndDate(1, DBV.sEnd);
		}
		db.close();
	}
	public void DBReadAll(){
		Cursor myCursor = null;
		try {
			db.open();
		} catch (SQLException sqle) {
			throw sqle;
		}
		myCursor = db.getTable2("all", 1);
		myCursor.moveToFirst();
		DBV.sTitle = myCursor.getString(1);
		myCursor.moveToNext();
		DBV.Sstart = myCursor.getString(1);
		myCursor.moveToNext();
		DBV.sEnd = myCursor.getString(1);
		myCursor.moveToNext();
		DBV.sBgUrl = myCursor.getString(1);
		db.close();
	}
	// ### Details update method
	public void updateDetailsView(){
		Log.i("detailsFrag", "updateDetailsView()");

		TextView startD = (TextView) findViewById(R.id.startDate);
		TextView endD = (TextView) findViewById(R.id.endDate);

		TextView num106 = (TextView) findViewById(R.id.monthsDoneT);
		TextView num105 = (TextView) findViewById(R.id.weeksDoneT);
		TextView num104 = (TextView) findViewById(R.id.daysDoneT);
		TextView num103 = (TextView) findViewById(R.id.hoursDoneT);
		TextView num102 = (TextView) findViewById(R.id.minsDoneT);
		TextView num101 = (TextView) findViewById(R.id.secsDoneT);

		TextView num160 = (TextView) findViewById(R.id.monthsDone);
		TextView num150 = (TextView) findViewById(R.id.weeksDone);
		TextView num140 = (TextView) findViewById(R.id.daysDone);
		TextView num130 = (TextView) findViewById(R.id.hoursDone);
		TextView num120 = (TextView) findViewById(R.id.minsDone);
		TextView num110 = (TextView) findViewById(R.id.secsDone);

		TextView num206 = (TextView) findViewById(R.id.monthsLeftT);
		TextView num205 = (TextView) findViewById(R.id.weeksLeftT);
		TextView num204 = (TextView) findViewById(R.id.daysLeftT);
		TextView num203 = (TextView) findViewById(R.id.hoursLeftT);
		TextView num202 = (TextView) findViewById(R.id.minsLeftT);
		TextView num201 = (TextView) findViewById(R.id.secsLeftT);

		TextView num210 = (TextView) findViewById(R.id.secsLeft);
		TextView num220 = (TextView) findViewById(R.id.minsLeft);
		TextView num230 = (TextView) findViewById(R.id.hoursLeft);
		TextView num240 = (TextView) findViewById(R.id.daysLeft);
		TextView num250 = (TextView) findViewById(R.id.weeksLeft);
		TextView num260 = (TextView) findViewById(R.id.monthsLeft);

		startD.setText("Start: " + getStartDate());
		endD.setText("End: " + getEndDate());

		num101.setText(String.format("%,d", getSecsTDone()));
		num102.setText(String.format("%,d", getMinsTDone()));
		num103.setText(String.format("%,d", getHoursTDone()));
		num104.setText(String.format("%,d", getDaysTDone()));
		num105.setText(String.format("%,d", getWeeksTDone()));
		num106.setText(String.format("%,d", getMonthsTDone()));

		num110.setText("" + getSecsDone());
		num120.setText("" + getMinsDone());
		num130.setText("" + getHoursDone());
		num140.setText("" + getDaysDone());
		num150.setText("" + getWeeksDone());
		num160.setText("" + getMonthsDone());

		num201.setText(String.format("%,d", getSecsTLeft()));
		num202.setText(String.format("%,d", getMinsTLeft()));
		num203.setText(String.format("%,d", getHoursTLeft()));
		num204.setText(String.format("%,d", getDaysTLeft()));
		num205.setText(String.format("%,d", getWeeksTLeft()));
		num206.setText(String.format("%,d", getMonthsTLeft()));

		num210.setText("" + getSecsLeft());
		num220.setText("" + getMinsLeft());
		num230.setText("" + getHoursLeft());
		num240.setText("" + getDaysLeft());
		num250.setText(getWeeksLeft() + "");
		num260.setText(getMonthsLeft() + "");

	}
	public void updateHomeView(){
		try {
			if(percent == null){
				percent = (TextView) findViewById(R.id.percDoneText);
				timeDoneText = (TextView) findViewById(daysDoneTextView);
				timeLeftText = (TextView) findViewById(R.id.daysLeftTextView);
				titleBot = (TextView) findViewById(R.id.titleBot);
			}
			percent.setText(DBV.percentDone + "%");
			if(getDaysTDone()>0){
				timeDoneText.setText(getDaysTDone() + " " + getString(R.string.daysDone));
			}else{
				timeDoneText.setText(getHoursTDone() + " " + getString(R.string.hoursDone));
			}
			if(getDaysTLeft()>0){
				timeLeftText.setText(getDaysTLeft() + " " + getString(R.string.daysLeft));
			}else{
				timeLeftText.setText(getHoursTLeft() + " " + getString(R.string.hoursLeft));
			}
			titleBot.setText(DBV.sTitle);
		
			
		} catch (Exception e) {
			Log.d("Update Home View", "update home View failed");
			e.printStackTrace();
		}
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int position){
			if (position == 1) {
				
				homeFrag = new HomeFragment();
				return homeFrag;
			} else if (position == 2) {
				setFrag = new SettingsFragment();
				return setFrag;
			} else {
				detFrag = new DetailsFragment();
				return detFrag;
			}
		}
		@Override
		public int getCount(){
			// Show 3 total pages.
			return 3;
		}
		@Override
		public CharSequence getPageTitle(int position){
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

	private class detailsThread extends Thread {
		public void run(){
			Log.e("MT State", MT.getState() + "");
			while (running) {
//				Log.v("detailsTread","current view = "+mViewPager.getCurrentItem());
				if (mViewPager.getCurrentItem() == 0) {
					try {
						updateCounter();
						runOnUiThread(new Runnable() {public void run(){updateDetailsView();}});
					} catch (Exception e) {e.printStackTrace();}
				}
				if (mViewPager.getCurrentItem() == 1) {
					try {
						SetCounter.updateCounter();
						runOnUiThread(new Runnable(){public void run() {updateHomeView();}});
					} catch (Exception e) {e.printStackTrace();}
				}
				try {
					sleep(1000);
				} catch (Exception e) {e.printStackTrace();}
			}
			Log.e("MT State", MT.getState() +"stopped");
		}
	}

}
