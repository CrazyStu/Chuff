package com.totirrapp.cc;

import android.app.Dialog;
import android.support.v4.app.FragmentTransaction;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.totirrapp.cc.R.id.timeDoneTextView;
import static com.totirrapp.cc.SetCounter.getDaysTDone;
import static com.totirrapp.cc.SetCounter.getDaysTLeft;
import static com.totirrapp.cc.SetCounter.getHoursTDone;
import static com.totirrapp.cc.SetCounter.getHoursTLeft;

public class MainActivity extends FragmentActivity implements HomeFragment.clickCallback, NewFragment.newChartCallback{
        public static Context			context;
	private databaseAdapter db					= null;
	private databaseReader dbr					= null;
	private static int				RESULT_LOAD_IMAGE	= 1;
	private SectionsPagerAdapter	mSectionsPagerAdapter;
	private ViewPager				mViewPager;
	private HomeFragment			homeFrag;
	private HelpFragment            helpFrag;
    private NewFragment             newFrag;
	private String					noImage				= "Android Wallpaper";
	private boolean					running				= true;
	private detailsThread			MT;
	private Bitmap					bg1;
	private TextView				percent				= null;
	private TextView				timeDoneText		= null;
	private TextView				timeLeftText		= null;
	private TextView				titleBot			= null;
	private FragmentManager fm = this.getSupportFragmentManager();
	private ArrayList<chart> chartList = new ArrayList<chart>();

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		context = this.getBaseContext();
		db = new databaseAdapter(context);
		dbr = new databaseReader();
		initCharts();

		chart chart1 = new chart(1);
		chartList.add(chart1);
//		processDB();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setPageMargin(0);
	}
	protected void onPause(){
		super.onPause();
		running = false;
		Log.e("MT State", MT.getState() + "");
	}
	protected void onResume(){
		super.onResume();
		running = true;
		try {
			MT = null;
			MT = new detailsThread();
			MT.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void initCharts(){
		dbr.getChartCount();
		if(DBV.chartCount>0) {
			int x = 0;
			chart tempChart;
			while (x < DBV.chartCount) {
				x++;
				tempChart = new chart(x);
				chartList.add(tempChart);
			}
			getChartValues(1);
		}
	}
	public void getChartValues(int x){
		ArrayList<String> tempList = chartList.get(x-1).getValues();
		DBV.sName = tempList.get(0);
		DBV.sTitle = tempList.get(2);
		DBV.Sstart = tempList.get(3);
		DBV.sEnd = tempList.get(4);
		DBV.sBgUrl = tempList.get(5);
	}
//	public void processDB(){
//		dbChartCount();
//		if(DBV.chartCount>0) {
//			DBReadChart(1);
//			Log.i("DBV1", DBV.sTitle);
//			Log.i("DBV2", DBV.Sstart);
//			Log.i("DBV3", DBV.sEnd);
//			Log.i("DBV4", DBV.sBgUrl);
//		}else{
//			Log.e("No Charts Found","HELP!>!!??!");
//		}
//	}
	public void onShortPress(int v){
      if (v == R.id.frag_home_parent_view) {
            Intent intent = new Intent(this, DetailsActivity.class);
            startActivity(intent);
        }
	}
    public void onLongPress(int v){
        if (v == R.id.chart_title) {
            myTitleDialog();
        } else if (v == R.id.timeDoneTextView) {
            myDateDialog(true);
        } else if (v == R.id.timeLeftTextView) {
            myDateDialog(false);
        } else if (v == R.id.frag_home_parent_view) {
            myBGDialog();
        }
    }
    public void initiateBG(){
        ImageThread testMe = new ImageThread();
        testMe.run("initiateBG()");
    }
    public void newChartRequest(int v){

		final Dialog newChartDialog = new Dialog(this);
		newChartDialog.setContentView(R.layout.dialog_title);
		newChartDialog.setTitle("Chart Name");
		final EditText newTitle = (EditText) newChartDialog.findViewById(R.id.setNewTitle);
		newTitle.setText(DBV.sName);
		newChartDialog.show();
		Button submitChart = (Button) newChartDialog.findViewById(R.id.button2);
		submitChart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				DBV.sName = newTitle.getText().toString();
				int x = mViewPager.getCurrentItem();
				String t1 = DBV.sName;
				String t2 = "I can't wait...";
				String t3 = "for what?";
				String t4 = "01/01/2015";
				String t5 = "11/12/2015";
				String t6 = "No Image";
				try {
					db.open();
					db.newChart(t1, t2, t3, t4, t5, t6);
					db.close();
					removePage(x);
				}catch (Exception e){
					e.printStackTrace();
				}
				newChartDialog.dismiss();
			}
		});
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
                homeFrag.removeBackground();
            }
        });
		Button galleryButton = (Button) setBG.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                setBG.dismiss();
                choosePic();
            }
        });
        Button deleteButton = (Button) setBG.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
				String num = (String)mSectionsPagerAdapter.getPageTitle(mViewPager.getCurrentItem());
				setBG.dismiss();
				db.open();
                db.deleteRecord(num);
				db.close();
				int x = mViewPager.getCurrentItem();
				removePage(x);
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

            ImageThread testMe = new ImageThread();
            testMe.run("activityResult");
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
                homeFrag.removeBackground();
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
    public void dbChartCount(){
        Cursor myCursor;
        try {
            db.open();
            myCursor = db.chartCount();
            DBV.chartCount = myCursor.getCount();
            db.close();
            Log.e("#-- Chart Count--# ", DBV.chartCount+" charts found");
        } catch (SQLException e) {
            Log.e("DBReadAll error", "failed to count database");
            throw e;
        }
    }
//    public void DBReadChart(int x){
//        Cursor myCursor;
//        try {
//            db.open();
//        } catch (SQLException e) {
//            Log.e("DBReadAll error", "failed to open database");
//            throw e;
//        }
//        myCursor = db.getChartNo(x);
//        DBV.sTitle = myCursor.getString(1);
//        DBV.Sstart = myCursor.getString(2);
//        DBV.sEnd = myCursor.getString(3);
//        DBV.sBgUrl = myCursor.getString(4);
//        db.close();
//    }

	public void updateHomeView(){
		try {
			if(percent == null){
				percent = (TextView) findViewById(R.id.percDoneText);
				timeDoneText = (TextView) findViewById(timeDoneTextView);
				timeLeftText = (TextView) findViewById(R.id.timeLeftTextView);
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
	public void removePage(int x){
		recreate();
		long itemId = (long) x;
		FragmentTransaction ft;
		String name = "android:switcher:" + mViewPager.getId() + ":" + itemId;
		Fragment myFragment= fm.findFragmentByTag(name);
		if (myFragment != null) {
			ft = fm.beginTransaction();
			ft.remove(myFragment);
			ft.commit();
		} else {
			Log.e("Fragment NOT Found", "??? #" + itemId + ": f=" + myFragment);
		}
		mViewPager.getAdapter().notifyDataSetChanged();
	}
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int position){
			Log.e("getItem() Called","Position="+position+". DBV.chartCount="+DBV.chartCount);
			if (position == 0) {
                helpFrag = new HelpFragment();
				Log.i("Get Item","num="+position+" HelpFrag");
                return helpFrag;
            }else if(position == DBV.chartCount+1) {
                newFrag = new NewFragment();
				Log.i("Get Item","num="+position+" NewFrag");
                return newFrag;
            } else {
                homeFrag = new HomeFragment();
				Log.i("Get Item","num="+position+" HomeFrag");
                return homeFrag;
            }
		}
		@Override
		public int getCount(){
			// Show number of pages equal to chart count +2 (help and new chart pages).
			return DBV.chartCount+2;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object){
			Log.d("destroy","requestPage Destroy");
			super.destroyItem(container,position,object);

		}
//		@Override
//		public Object instantiateItem (ViewGroup container, int position) {
//			Log.d("create", "requestPage create at postition--"+position);
//			return super.instantiateItem(container,position);
//		}
		@Override
		public CharSequence getPageTitle(int position){
			Locale l = Locale.getDefault();
            if (position==0) {
                return getString(R.string.title_help).toUpperCase(l);
            }else if(position == DBV.chartCount+1) {
                return getString(R.string.title_new).toUpperCase(l);
            }else{
                String name = "no name";
                try {
                    db.open();
                    name = db.getChartName(position);
                    db.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                return name;
            }
		}
	}
    private class detailsThread extends Thread {
        public void run(){
            Log.e("MT State", MT.getState() + "");
            while (running) {
                try {
                    SetCounter.updateCounter();
                    runOnUiThread(new Runnable(){public void run() {updateHomeView();}});
                    sleep(1000);
                } catch (Exception e) {e.printStackTrace();}
            }
            Log.e("MT State", MT.getState() +"stopped");
        }
    }
    private class ImageThread extends Thread {
        public void run(String owner){
            Log.e("imageThread","Thread started by "+owner);
            try {
                BitmapDrawable temp2 = new BitmapDrawable(context.getResources(), getBackground(DBV.sWidth, DBV.sHeight));

                temp2.setGravity(Gravity.CENTER);

                homeFrag.setNewBackground(temp2);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("imageThread","Thread ended");
        }
    }
}
