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
import android.support.v4.app.FragmentTransaction;
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

public class MainActivity extends FragmentActivity implements ChartFragment.clickCallback, NewFragment.newChartCallback{
        public static Context			context;
	private databaseAdapter db					= null;
	private databaseReader dbr					= null;
	private static int				RESULT_LOAD_IMAGE	= 1;
	private SectionsPagerAdapter	mSectionsPagerAdapter;
	private ViewPager				mViewPager;
	private ChartFragment 			chartFrag;
	private HelpFragment            helpFrag;
    private NewFragment             newFrag;
	private String					noImage				= "noImage";
	private boolean					running				= true;
	private detailsThread			MT;
	private Bitmap					bg1;
	private String 					tempTitle = null;
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
		dbr = new databaseReader("Activity Create");
		initCharts();
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
		databaseReader.getChartCount("initCharts()");
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
    public void initiateBG(int x){
		Log.e("initBGcalled", "called by chartFrag no>" + x);
        ImageThread testMe = new ImageThread();
        testMe.run(x);
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
					db.open("new Chart");
					db.newChart(t1, t2, t3, t4, t5, t6);
					db.close();
					removePage(x);
				} catch (Exception e) {
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
		tempTitle = chartList.get(mViewPager.getCurrentItem()-1).getChartName();
		Log.e("tempTitle", "title is " + tempTitle);
		setBG.setTitle("Set background for " + tempTitle);
//		tempTitle = (String)mSectionsPagerAdapter.getPageTitle(mViewPager.getCurrentItem());

		setBG.show();
		Button wallpaperButton = (Button) setBG.findViewById(R.id.wallpaperButton);
		wallpaperButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setBG.dismiss();
				DBV.sBgUrl = noImage;
				databaseReader.updateBGURL(tempTitle, noImage);
				chartList.get(mViewPager.getCurrentItem()-1).readChart();
				useWallpaper(mViewPager.getCurrentItem());
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
			public void onClick(View view) {
				int x = mViewPager.getCurrentItem();
				setBG.dismiss();
				databaseReader.deleteChart(tempTitle);
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
			databaseReader.updateBGURL(tempTitle,picturePath);
			chartList.get(mViewPager.getCurrentItem()-1).readChart();
			DBV.sBgUrl = picturePath;
            ImageThread testMe = new ImageThread();
            testMe.run("onActivityResult");
		}
	}
	public Bitmap getBackground(String url,int chartNo, int screenW,int screenH){
		// Context context = MainActivity.context;
		Log.i("BgHandler", "Start Bitmap Processing");
		if (url.equals(noImage)) {
			Log.i("BgHandler", "No BG set Loading default background");
			useWallpaper(chartNo);
			bg1=null;
		} else {
			BitmapFactory.Options bgOptions = new BitmapFactory.Options();
			Bitmap test;
			bgOptions.inJustDecodeBounds = true;
			try {
				 test = BitmapFactory.decodeFile(url, bgOptions);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (bgOptions.outHeight > 0) {
				Log.i("BgHandler", "Image found---Loading selected picture");
				bg1 = loadSelectedImage(url, screenW, screenH);
			} else {
				Log.i("BgHandler", "Image not found---Loading default background");
               useWallpaper(chartNo);
				bg1=null;
			}
		}
		return bg1;
	}
	private Bitmap loadSelectedImage(String bgURL,int screenW,int screenH){
		boolean landscape = false;
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
		Log.i("Image","W="+bgOptions.outWidth+"H= " + bgOptions.outHeight+"Landscape=" + landscape);
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
		Log.i("Image","SampleSize="+iscale);
		// =============Read rotation==============================
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(bgURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int orientation = 0;
		try{
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		}catch (NullPointerException e){
			e.printStackTrace();
		}
		// =============Get full image and resize=====================
		bgOptions.inJustDecodeBounds = false;
		bg1 = BitmapFactory.decodeFile(bgURL, bgOptions);
		Log.i("Image", "Width="+bg1.getWidth()+"Height= " + bg1.getHeight());
		if (landscape) {
			bg1 = Bitmap.createScaledBitmap(bg1, (int) (screenH * ImageAspect), screenH, true);
		} else {
			bg1 = Bitmap.createScaledBitmap(bg1, screenW, (int) (screenW * ImageAspect), true);
//			Log.i("BgHandler", "Size=" + screenW + " x " + (int) (screenW * ImageAspect));
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
	private void useWallpaper(int chartNo){
		String name = "android:switcher:" + mViewPager.getId() + ":" + chartNo;
		ChartFragment myFragment=(ChartFragment) fm.findFragmentByTag(name);
		if (myFragment != null) {
			myFragment.removeBackground();
		} else {
			Log.e("Fragment NOT Found", "??? #" + chartNo);
		}
	}
	public void DBUpdate(String item,String data){
		try {
			db.open("DBUpdate()");
		} catch (SQLException sqle) {
			throw sqle;
		}
		if (item == "title") {
			db.updateTitle(1, data);
		} else if (item == "startDate") {
			db.updateStartDate(1, DBV.Sstart);
		} else if (item == "endDate") {
			db.updateEndDate(1, DBV.sEnd);
		}
		db.close();
	}
	public void updateHomeView(){
		long itemId;
		String name;
		ChartFragment myFragment;
		itemId = mViewPager.getCurrentItem();
		name = "android:switcher:" + mViewPager.getId() + ":" + itemId;
		myFragment= (ChartFragment) fm.findFragmentByTag(name);
		Log.e(">>Update<<", "Current Frag ID=" + itemId + ", Frag=" + myFragment);
		Log.e(">>Update<<", "Current Background="+chartList.get(mViewPager.getCurrentItem()-1).getChartBgUrl());
		try{
			myFragment.updateHomeView();
		}catch (Exception e){
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
			if (position == 0) {
                helpFrag = new HelpFragment();
				Log.i("Get Item","num="+position+" HelpFrag");
                return helpFrag;
            }else if(position == DBV.chartCount+1) {
                newFrag = new NewFragment();
				Log.i("Get Item", "num=" + position + " NewFrag");
                return newFrag;
            } else {
                chartFrag = new ChartFragment();
				Log.i("Get Item","num="+position+" ChartFrag");
				Bundle args = new Bundle();
				args.putInt("ChartNo", position);
				args.putStringArrayList("values",chartList.get(position-1).getValues());
				chartFrag.setArguments(args);
				chartFrag.getArgs();
                return chartFrag;
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
					name =  chartList.get(position-1).getChartName();
//                    name = databaseReader.getChartName(position);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return name;
            }
		}
	}
    private class detailsThread extends Thread {
        public void run(){
			int itemId;
            Log.e("MT State", MT.getState() + "");
            while (running) {
				itemId = mViewPager.getCurrentItem();
				if(itemId>0&&itemId<DBV.chartCount+1) {
					try {
						SetCounter.updateCounter();
						runOnUiThread(new Runnable() {
							public void run() {
								updateHomeView();
							}
						});
						sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            }
            Log.e("MT State", MT.getState() +"stopped");
        }
    }
    private class ImageThread extends Thread {
        public void run(int x){
            Log.e("ImageThread","Thread1 started by no"+x);
            try {
				tempTitle = chartList.get(x-1).getChartName();
//				tempTitle = (String)mSectionsPagerAdapter.getPageTitle(x+1);
				String url = chartList.get(x-1).getChartBgUrl();
                BitmapDrawable temp2 = new BitmapDrawable(context.getResources(), getBackground(url,x, DBV.sWidth, DBV.sHeight));
                temp2.setGravity(Gravity.CENTER);
				long itemId = (long) x;
				String name = "android:switcher:" + mViewPager.getId() + ":" + itemId;
				ChartFragment myFragment=(ChartFragment) fm.findFragmentByTag(name);
				if (myFragment != null) {

					myFragment.setNewBackground(temp2);
				} else {
					Log.e("Fragment NOT Found", "??? #" + itemId + ": f=" + myFragment);
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("imageThread","Thread ended");
        }
		public void run(String x){
			Log.e("ImageThread","Thread2 started by "+x);
			try {
				int chartNo =mViewPager.getCurrentItem()-1;
				tempTitle = chartList.get(chartNo).getChartName();
				String url = chartList.get(chartNo).getChartBgUrl();
				BitmapDrawable temp2 = new BitmapDrawable(context.getResources(), getBackground(url,chartNo+1,DBV.sWidth, DBV.sHeight));
				temp2.setGravity(Gravity.CENTER);
				long itemId = (long) mViewPager.getCurrentItem();
				String name = "android:switcher:" + mViewPager.getId() + ":" + itemId;
				ChartFragment myFragment=(ChartFragment) fm.findFragmentByTag(name);
				if (myFragment != null) {

					myFragment.setNewBackground(temp2);
				} else {
					Log.e("Fragment NOT Found", "??? #" + itemId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e("imageThread","Thread ended");
		}
    }
}
