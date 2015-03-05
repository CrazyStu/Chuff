package com.totirrapp.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HomeView extends View {
	private int screenW=200;
	private int screenH=400;

	private Paint redBox;
	private Paint blueBox;
	private Bitmap progBar = null;
	private int percent=50;
	private float blueBoxSize;
	private Context myContext;

	
        public HomeView(Context context, AttributeSet atts) {
		super(context);
		myContext=MainActivity.context;
		loadBMP();
		setPaint();
		refresh();
        }     
        public void loadBMP(){      		
        	try{
        	progBar=BitmapFactory.decodeResource(myContext.getResources(), R.drawable.progress);
        	progBar=Bitmap.createBitmap(progBar);
        	}catch(Exception ex){ex.printStackTrace();}
        }
        public void callDraw(){
        	Log.e("HomeView", "invalidate manually called!");
        	invalidate();
        }
        
        public void onDraw(Canvas myCanvas){
        	Log.e("MainView","##############> Draw <##############");
         	refresh();
        	myCanvas.drawRect(0, 0, screenW/8, screenH,redBox);
        	myCanvas.drawRect(0, screenH-blueBoxSize, screenW/8, screenH,blueBox);
        }
        public void refresh(){
    		SetCounter.updateCounterAndDates();
			percent=SetCounter.getPercentCompleted();
			blueBoxSize = screenH*((float)percent/100);
        }
        public void onSizeChanged (int w, int h, int oldw, int oldh){
    		super.onSizeChanged(w, h, oldw, oldh);
    		screenW = w;
    		screenH = h;
    		blueBoxSize = screenH*((float)percent/100);
    		setPaint();
    	}
        private void setPaint(){
    		redBox= new Paint();
    		redBox.setColor(Color.RED);
    		redBox.setAlpha(70);

    		blueBox= new Paint();
    		blueBox.setColor(Color.BLUE);
    		blueBox.setAlpha(200);

        }
}
