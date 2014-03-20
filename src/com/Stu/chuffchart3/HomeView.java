package com.Stu.chuffchart3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HomeView extends View {
	private int screenW=200;
	private int screenH=400;
	private int daysLeft = 25;
	private int daysDone = 30;
	private int hoursLeft =10;
	private int hoursDone = 10;
//	private String myTitle1="I Can't Wait";
	private String size = "NOPE2";
	private Paint title1Paint;
	private Paint title2Paint;
	private Paint daysLeftPaint;
	private Paint daysDonePaint;
	private Paint bottomOverlayBox;
	private Paint redBox;
	private Paint blueBox;
	private Paint percentRemainingPaint;
	private Bitmap progBar = null;
	private Rect tangle;
	private int percent=50;
	private float blueBoxSize;
	private Context myContext;

	
        public HomeView(Context context, AttributeSet atts) {
		super(context);
		myContext=MainActivity.context;
		loadBMP();
		setText();
		refresh();
        }     
        public void loadBMP(){      		
        	progBar=BitmapFactory.decodeResource(myContext.getResources(), R.drawable.progress);
        	progBar=Bitmap.createBitmap(progBar);
        }
        @Override
		protected void onDraw(Canvas myCanvas){
        	Log.i("MainView","Draw");
        	myCanvas.drawRect(screenW/8, (screenH/7)*6, screenW, screenH, bottomOverlayBox);
        	myCanvas.drawRect(screenW/8, 0, screenW, screenH/8, bottomOverlayBox);
        	myCanvas.drawRect(0, 0, screenW/8, screenH,redBox);
        	myCanvas.drawRect(0, screenH-blueBoxSize, screenW/8, screenH,blueBox);
        	myCanvas.drawText(percent+"%", (screenW/16),  screenH-6, percentRemainingPaint);
        	myCanvas.drawText(DBV.Sheader, screenW/8+10,  (int)(screenH*0.05), title1Paint);
        	myCanvas.drawText(DBV.Stitle, screenW-10, (int)(screenH*0.12), title2Paint);
        	myCanvas.drawText(size, screenW-90, (int)(screenH*0.06), percentRemainingPaint);
        	myCanvas.drawBitmap(progBar,null,tangle, null);
          	if(daysDone==0){
          		myCanvas.drawText(hoursDone+" Hours Done", screenW-10, (int)(screenH*0.9), daysDonePaint);	
        	}else{
        		myCanvas.drawText(daysDone+" Days Done", screenW-10, (int)(screenH*0.9), daysDonePaint);
        	}
        	if(daysLeft==0){
        		myCanvas.drawText(hoursLeft+" Hours Left!", screenW-10,  screenH-20, daysLeftPaint);	
        	}else{
        		myCanvas.drawText(daysLeft+" Days Left", screenW-10,  screenH-20, daysLeftPaint);
        	}
        	myCanvas.drawText(percent+"%", (screenW/16),  screenH-6, percentRemainingPaint);
        }
        public void refresh(){
    		SetCounter.updateCounterAndDates(DBV.Sstart, DBV.Send);
        	daysLeft=SetCounter.getDaysTLeft();
        	daysDone=SetCounter.getDaysTDone();
        	hoursLeft=(int) SetCounter.getHoursLeft();
        	hoursDone=(int) SetCounter.getHoursDone();
			percent=SetCounter.getPercentCompleted();
			blueBoxSize = screenH*((float)percent/100);
			invalidate();
			
        }
        public void onSizeChanged (int w, int h, int oldw, int oldh){
    		super.onSizeChanged(w, h, oldw, oldh);
    		screenW = w;
    		screenH = h;
    		size = w+"x"+h;
    		blueBoxSize = screenH*((float)percent/100);
    		tangle = new Rect(0, 0, screenW/8, screenH);
    		setText();
    	}
        private void setText(){
        	title1Paint= new Paint();
    		title1Paint.setTextSize(screenH/17);
			title1Paint.setTextAlign(Paint.Align.LEFT);
    		title1Paint.setColor(Color.BLACK);
    		title1Paint.setFakeBoldText(false);
    		title1Paint.setAntiAlias(true);
    		
        	title2Paint= new Paint();
    		title2Paint.setTextSize(screenH/17);
			title2Paint.setTextAlign(Paint.Align.RIGHT);
    		title2Paint.setColor(Color.BLACK);
    		title2Paint.setFakeBoldText(true);
    		title2Paint.setAntiAlias(true);
                	
        	daysLeftPaint= new Paint();
    		daysLeftPaint.setTextSize(screenH/13);
			daysLeftPaint.setTextAlign(Paint.Align.RIGHT);
    		daysLeftPaint.setColor(Color.BLACK);
    		daysLeftPaint.setFakeBoldText(true);
    		daysLeftPaint.setAntiAlias(true);
    		
        	daysDonePaint= new Paint();
    		daysDonePaint.setTextSize(screenH/17);
			daysDonePaint.setTextAlign(Paint.Align.RIGHT);
    		daysDonePaint.setColor(Color.BLACK);
    		daysDonePaint.setAntiAlias(true);
    		
    		percentRemainingPaint= new Paint();
    		percentRemainingPaint.setTextSize(screenH/39);
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

        }
}
