package com.totirrapp.cc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
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
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements ChartFragment.clickCallback, NewFragment.newChartCallback{
	public static Context			context;
//	private databaseAdapter db					= null;
	private static int				RESULT_LOAD_IMAGE	= 1;
	private ViewPager				mViewPager;
	private String					noImage				= "noImage";
	private boolean					running				= true;
	private detailsThread			MT;
	private Bitmap					bg1;
	private int screenHMem;
	private int screenWMem;
	String newStartDate;
	String newEndDate;
	private String 					tempTitle = null;
	private FragmentManager 		fm = this.getSupportFragmentManager();
	private ArrayList<ChartFragment> chartFragList = new ArrayList<ChartFragment>();
	private int width;
	private int height;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		context = this.getBaseContext();
//		db = new databaseAdapter(context);
		new databaseReader("Activity Create");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		initCharts();
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
//initCharts();
		running = true;
		try {
//			if(MT==null){
//				MT = new detailsThread();
//			}
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
			ChartFragment chartFrag;
			while (x < DBV.chartCount+1) {
				chartFrag = new ChartFragment();
				chartFragList.add(x, chartFrag);
				Bundle args = new Bundle();
				args.putInt("ChartNo", x);
				args.putInt("Height", height);
				args.putInt("Width", width);
				chartFrag.setArguments(args);
				chartFrag.getArgs();
				x++;
			}
		}
	}
	public void onShortPress(int v){
      if (v == R.id.frag_home_parent_view) {
		  ChartFragment temp = chartFragList.get(mViewPager.getCurrentItem());
            Intent intent = new Intent(this, DetailsActivity.class);
		 	intent.putExtra("number",mViewPager.getCurrentItem());
		  intent.putExtra("start", temp.getChartStart());
		  intent.putExtra("end",temp.getChartEnd());
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
		newChartDialog.setContentView(R.layout.dialog_new);
		newChartDialog.setTitle(R.string.newChartText);
		final EditText newTitle = (EditText) newChartDialog.findViewById(R.id.setNewTitle);
		newTitle.setText(R.string.chartName);
		newChartDialog.show();
		Button submitChart = (Button) newChartDialog.findViewById(R.id.button2);
		submitChart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				int x = mViewPager.getCurrentItem();
				String t1 = newTitle.getText().toString();
				String t2 = "I can't wait...";
				String t3 = "for what?";
				String t4 = "01/01/2015";
				String t5 = "11/12/2015";
				String t6 = "No Image";
				try {
					databaseReader.newChart(t1, t2, t3, t4, t5, t6);
					removePage(x, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				newChartDialog.dismiss();
			}
		});
    }

	private void myDateDialog(final boolean start){
		int x = mViewPager.getCurrentItem();
		final ChartFragment tempChart = chartFragList.get(x);
		final Dialog setDateDialog = new Dialog(this);
		setDateDialog.setContentView(R.layout.dialog_dates);
		newStartDate = null;
		newEndDate = null;
		OnClickListener doneListener;
		OnClickListener cancelListener;
		CalendarView.OnDateChangeListener datePicked;
		final Long startDateMills =tempChart.getCounterStartMills();
		final Long endDateMills =tempChart.getCounterEndMills();
		final CalendarView picker1 = (CalendarView) setDateDialog.findViewById(R.id.dialog_dates_picker_1);
		TextView doneButton = (TextView) setDateDialog.findViewById(R.id.dialog_dates_done_button);
		TextView cancelButton = (TextView) setDateDialog.findViewById(R.id.dialog_dates_cancel_button);
		cancelListener = new View.OnClickListener() {
			public void onClick(View view){
				setDateDialog.dismiss();
			}
		};

		if(start) {
			setDateDialog.setTitle("Set Start Date");
			datePicked = new CalendarView.OnDateChangeListener() {
				@Override
				public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
					Log.i("View selected", "Picker>" + view);
					newStartDate = dayOfMonth + "/" + (month + 1) + "/" + year;
					if(picker1.getDate()>endDateMills){
						newEndDate =  (dayOfMonth+1) + "/" + (month + 1) + "/" + year;
					};
				}
			};
			doneListener = new View.OnClickListener() {
				public void onClick(View view){
					setDateDialog.dismiss();
					if(newEndDate!=null){
						databaseReader.updateBothDate(tempChart.getChartName(), newStartDate, newEndDate);
					}else{
						databaseReader.updateStartDate(tempChart.getChartName(), newStartDate);
					}
					tempChart.readChartValues();
				}
			};
			picker1.setDate(startDateMills, true, true);
		}else {
			setDateDialog.setTitle("Set End Date");
			datePicked = new CalendarView.OnDateChangeListener() {
				@Override
				public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
					Log.i("View selected", "Picker>" + view);
					newEndDate = dayOfMonth + "/" + (month + 1) + "/" + year;
					if(picker1.getDate()<startDateMills){
						newStartDate =  (dayOfMonth-1) + "/" + (month + 1) + "/" + year;
					};
				}
			};
			doneListener = new View.OnClickListener() {
				public void onClick(View view){
					setDateDialog.dismiss();
					if(newStartDate!=null){
						databaseReader.updateBothDate(tempChart.getChartName(), newStartDate, newEndDate);
					}else{
						databaseReader.updateEndDate(tempChart.getChartName(), newEndDate);
					}
					tempChart.readChartValues();
				}
			};
			picker1.setDate(endDateMills, true, true);
		}
		picker1.setOnDateChangeListener(datePicked);
		doneButton.setOnClickListener(doneListener);
		cancelButton.setOnClickListener(cancelListener);
		setDateDialog.show();
	}
	private void myTitleDialog(){
		final Dialog setTitle = new Dialog(this);
		setTitle.setContentView(R.layout.dialog_title);
		setTitle.setTitle("Set Title Text");
		final EditText newTitle = (EditText) setTitle.findViewById(R.id.setNewTitle);
		int x = mViewPager.getCurrentItem();
		newTitle.setText(chartFragList.get(x).getChartTitle());
		setTitle.show();
		newTitle.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(newTitle, InputMethodManager.SHOW_FORCED);
		Button submitTitle = (Button) setTitle.findViewById(R.id.button2);
		submitTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
				int y = mViewPager.getCurrentItem();
                databaseReader.updateTitle(chartFragList.get(y).getChartName(), newTitle.getText().toString());
				chartFragList.get(y).readChartValues();
                setTitle.dismiss();
            }
        });
		newTitle.requestFocus();
	}
	private void myBGDialog(){
		final Dialog setBG = new Dialog(this);
		setBG.setContentView(R.layout.dialog_background);
		tempTitle = chartFragList.get(mViewPager.getCurrentItem()).getChartName();
		Log.e("tempTitle", "title is " + tempTitle);
		setBG.setTitle("Set background for " + tempTitle);

		setBG.show();
		TextView wallpaperButton = (TextView) setBG.findViewById(R.id.wallpaperButton);
		wallpaperButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setBG.dismiss();
				DBV.sBgUrl = noImage;
				databaseReader.updateBGURL(tempTitle, noImage);
				chartFragList.get(mViewPager.getCurrentItem()).readChartValues();
				useWallpaper(mViewPager.getCurrentItem());
			}
		});
		TextView galleryButton = (TextView) setBG.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                setBG.dismiss();
                choosePic();
            }
        });
		TextView deleteButton = (TextView) setBG.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				int x = mViewPager.getCurrentItem();
				setBG.dismiss();
//				databaseReader.deleteChart(tempTitle);
				removePage(x, tempTitle);
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
			chartFragList.get(mViewPager.getCurrentItem()).readChartValues();//
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
				bg1=null;
			}
		}
		return bg1;
	}
	private Bitmap loadSelectedImage(String bgURL,int screenW,int screenH){
		if(screenHMem==0){
			screenHMem = screenH;
			screenWMem = screenW;
		}
		boolean landscape = false;
		BitmapFactory.Options bgOptions = new BitmapFactory.Options();
		// ========Check size and get sample size======================
		bgOptions.inJustDecodeBounds = true;
		bg1 = BitmapFactory.decodeFile(bgURL, bgOptions);
		int imageHeight = bgOptions.outHeight;
		int imageWidth = bgOptions.outWidth;
		float ScreenAspect = (float) screenHMem / (float) screenWMem;
		float ImageAspect = (float) bgOptions.outHeight / (float) bgOptions.outWidth;
		if (ImageAspect < 1) {
			landscape = true;
		}
		Log.i("Screen","W="+screenWMem+" H= " + screenHMem+"Landscape=" + landscape);
		Log.i("Image","W="+bgOptions.outWidth+" H= " + bgOptions.outHeight+"Landscape=" + landscape);
		int iscale;
		if (landscape) {
			ImageAspect = (float) bgOptions.outWidth / (float) bgOptions.outHeight;
			if (imageHeight > screenHMem) {
				iscale = (int) ((float) imageHeight / (float) screenHMem);
				bgOptions.inSampleSize = iscale;
			} else {
				iscale = 1;
				bgOptions.inSampleSize = iscale;
			}
		} else {
			ImageAspect = (float) bgOptions.outHeight / (float) bgOptions.outWidth;
			if (imageWidth > screenWMem) {
				iscale = (int) ((float) imageWidth / (float) screenWMem) + 2;
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
			bg1 = Bitmap.createScaledBitmap(bg1, (int) (screenHMem * ImageAspect), screenHMem, true);
		} else {
			bg1 = Bitmap.createScaledBitmap(bg1, screenWMem, (int) (screenWMem * ImageAspect), true);
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
			chartFragList.get(chartNo-1).removeBackground();
	}
	public void updateHomeView(){
		int num = mViewPager.getCurrentItem();
		Log.e(">>UpdateChart<<", "FragID="+num+",Frag="+chartFragList.get(num)+"Background="+chartFragList.get(mViewPager.getCurrentItem()).getChartBgUrl());
		try{
			chartFragList.get(num).updateChartView();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void removePage(int x, String title){
		Fragment myFragment;
		if(title!=null){
			databaseReader.deleteChart(title);
			myFragment= chartFragList.get(x);
			mViewPager.setCurrentItem(x-1);
		}else{
			long itemId = (long) x;
			String name = "android:switcher:" + mViewPager.getId() + ":" + itemId;
			myFragment= fm.findFragmentByTag(name);
		}
		recreate();
		FragmentTransaction ft;
		if (myFragment != null) {
			ft = fm.beginTransaction();
			ft.remove(myFragment);
			ft.commit();
		} else {
			Log.e("Fragment NOT Found", "??? #" + x);
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
//                HelpFragment helpFrag = new HelpFragment();
//				Log.i("Get Item","num="+position+" HelpFrag");
                return new HelpFragment();
            }else if(position == DBV.chartCount+1) {
//                NewFragment newFrag = new NewFragment();
//				Log.i("Get Item", "num=" + position + " NewFrag");
                return new NewFragment();
            } else {
				Log.i("Get Item", "num=" + position + "ChartFrag");
                return chartFragList.get(position);
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
					name =  chartFragList.get(position).getChartName();
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
//						SetCounter.updateCounter();
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
            Log.e("ImageThread","ImageThread-Chart"+x+"H*W="+height+"*"+width);
            try {
				tempTitle = chartFragList.get(x).getChartName();
				String url = chartFragList.get(x).getChartBgUrl();
                BitmapDrawable temp2 = new BitmapDrawable(context.getResources(), getBackground(url,x, width, height));
				temp2.setGravity(Gravity.CENTER);
				if(temp2==null){
					useWallpaper(x);
				}else{
					chartFragList.get(x).setNewBackground(temp2);
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("imageThread","Thread ended");
        }
		public void run(String x){
			Log.e("ImageThread","Thread2 started by "+x);
			try {
				int chartNo =mViewPager.getCurrentItem();
				tempTitle = chartFragList.get(chartNo).getChartName();
				String url = chartFragList.get(chartNo).getChartBgUrl();
				BitmapDrawable temp2 = new BitmapDrawable(context.getResources(), getBackground(url,chartNo,width, height));
				temp2.setGravity(Gravity.CENTER);
				if(temp2==null){
					useWallpaper(chartNo);
				}else{
					chartFragList.get(chartNo).setNewBackground(temp2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e("imageThread","Thread ended");
		}
    }
}
