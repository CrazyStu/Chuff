package com.Stu.chuffchart3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;


public class MainView extends View {

	private int daysLeft;
	private int daysDone;
	private int hoursLeft;
	private int hoursDone;
	
	private int screenW=100;
	private int screenH=100;
	
	private String myTitle1="I Can't Wait...";
	private String myTitle2 = "fail";
	
	private Paint title1Paint;
	private Paint title2Paint;
	private Paint daysLeftPaint;
	private Paint daysDonePaint;
	private Paint bottomOverlayBox;
	private Paint redBox;
	private Paint blueBox;
	private Paint percentRemainingPaint;
//	private Paint dbgp;
	
	private Bitmap bg;
	private Bitmap progBar;

	private int percent=1;
	private float blueBoxSize;
	public static Context context;
	
        public MainView(Context context) {
		super(context);
		SetCounter.updateCounterAndDates();
		setText();
		refresh();
        }        
        @Override
		protected void onDraw(Canvas myCanvas){
        	Log.i("MainView","Draw");
        	myCanvas.drawBitmap(bg, -(bg.getWidth()-screenW)/2, 0, null);

        	myCanvas.drawRect(screenW/8, (screenH/7)*6, screenW, screenH, bottomOverlayBox);
        	myCanvas.drawRect(screenW/8, 0, screenW, screenH/8, bottomOverlayBox);
        	
        	myCanvas.drawRect(0, 0, screenW/8, screenH,redBox);
        	myCanvas.drawRect(0, screenH-blueBoxSize, screenW/8, screenH,blueBox);
        	
        	myCanvas.drawBitmap(progBar, 0, 0, null);
          	if(daysDone==0){
          		myCanvas.drawText(hoursDone+" Hours Done", screenW-10, screenH-70, daysDonePaint);	
        	}else{
        		myCanvas.drawText(daysDone+" Days Done", screenW-10, screenH-70, daysDonePaint);
        	}
        	if(daysLeft==0){
        		myCanvas.drawText(hoursLeft+" Hours Left!", screenW-10,  screenH-20, daysLeftPaint);	
        	}else{
        		myCanvas.drawText(daysLeft+" Days Left", screenW-10,  screenH-20, daysLeftPaint);
        	}
        	myCanvas.drawText(percent+"%", (screenW/16),  screenH-6, percentRemainingPaint);
        	
        	myCanvas.drawText(myTitle1, screenW/8+10, 40, title1Paint);
        	myCanvas.drawText(myTitle2, screenW-10, 80, title2Paint);
        }
        public void onSizeChanged (int w, int h, int oldw, int oldh){
    		super.onSizeChanged(w, h, oldw, oldh);
    		screenW = w;
    		screenH = h;
    		loadBg(getContext());
    		blueBoxSize = screenH*((float)percent/100);
    		
    	}
      
        public void loadBg(Context context){
      		progBar=BitmapFactory.decodeResource(context.getResources(), R.drawable.progress2);
    		progBar=Bitmap.createScaledBitmap(progBar, screenW/8, screenH, true);
        	bg=BitmapFactory.decodeResource(context.getResources(), R.drawable.pic0);
    		bg=Bitmap.createScaledBitmap(bg, screenW, screenH, true);
    		new BGTask().execute("test");
  
        }
    
    	private void setText(){
        	
        	title1Paint= new Paint();
    		title1Paint.setTextSize(30);
			title1Paint.setTextAlign(Paint.Align.LEFT);
    		title1Paint.setColor(Color.BLACK);
    		title1Paint.setFakeBoldText(false);
    		title1Paint.setAntiAlias(true);
    		
        	title2Paint= new Paint();
    		title2Paint.setTextSize(40);
			title2Paint.setTextAlign(Paint.Align.RIGHT);
    		title2Paint.setColor(Color.BLACK);
    		title2Paint.setFakeBoldText(true);
    		title2Paint.setAntiAlias(true);
                	
        	daysLeftPaint= new Paint();
    		daysLeftPaint.setTextSize(50);
			daysLeftPaint.setTextAlign(Paint.Align.RIGHT);
    		daysLeftPaint.setColor(Color.BLACK);
    		daysLeftPaint.setFakeBoldText(true);
    		daysLeftPaint.setAntiAlias(true);
    		
        	daysDonePaint= new Paint();
    		daysDonePaint.setTextSize(40);
			daysDonePaint.setTextAlign(Paint.Align.RIGHT);
    		daysDonePaint.setColor(Color.BLACK);
    		daysDonePaint.setAntiAlias(true);
    		
    		percentRemainingPaint= new Paint();
    		percentRemainingPaint.setTextSize(20);
			percentRemainingPaint.setTextAlign(Paint.Align.CENTER);
    		percentRemainingPaint.setColor(Color.BLACK);
    		percentRemainingPaint.setFakeBoldText(true);
    		
    		
    	 	bottomOverlayBox= new Paint();
    		bottomOverlayBox.setColor(Color.WHITE);
    		bottomOverlayBox.setAlpha(90);
    		
    		redBox= new Paint();
    		redBox.setColor(Color.RED);
    		redBox.setAlpha(70);

    		blueBox= new Paint();
    		blueBox.setColor(Color.BLUE);
    		blueBox.setAlpha(200);
//    		
//    		dbgp=new Paint();
//    		dbgp.setTextSize(20);
//    		dbgp.setColor(Color.BLUE);
        }
        public void refresh(){
        	daysLeft=SetCounter.getDaysTLeft();
        	daysDone=SetCounter.getDaysTDone();
        	hoursLeft=(int) SetCounter.getHoursLeft();
        	hoursDone=(int) SetCounter.getHoursDone();
        	
			percent=SetCounter.getPercentCompleted();
			blueBoxSize = screenH*((float)percent/100);
			myTitle2=BackgroundHandler.getTitle();
			invalidate();
        }
        
    	class BGTask extends AsyncTask<String,Void,String>{
    		protected String doInBackground(String... s) {
    				Log.w("BGTask","doInBackground");
    				bg=BackgroundHandler.getBackground(screenW, screenH);
    				return s[0];
    		}
    		protected void onPostExecute(String s){
    			  if (isCancelled()) {
    			  }else{
    				  invalidate();
    				  Log.w("BGTask","Post Execute");
    			  }
    		}
    	}
 
}
