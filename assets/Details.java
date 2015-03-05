package com.Stu.chuffchart3;

import static com.Stu.chuffchart3.SetCounter.getDaysDone;
import static com.Stu.chuffchart3.SetCounter.getDaysLeft;
import static com.Stu.chuffchart3.SetCounter.getDaysTDone;
import static com.Stu.chuffchart3.SetCounter.getDaysTLeft;
import static com.Stu.chuffchart3.SetCounter.getEndDate;
import static com.Stu.chuffchart3.SetCounter.getHoursDone;
import static com.Stu.chuffchart3.SetCounter.getHoursLeft;
import static com.Stu.chuffchart3.SetCounter.getHoursTDone;
import static com.Stu.chuffchart3.SetCounter.getHoursTLeft;
import static com.Stu.chuffchart3.SetCounter.getMinsDone;
import static com.Stu.chuffchart3.SetCounter.getMinsLeft;
import static com.Stu.chuffchart3.SetCounter.getMinsTDone;
import static com.Stu.chuffchart3.SetCounter.getMinsTLeft;
import static com.Stu.chuffchart3.SetCounter.getMonthsDone;
import static com.Stu.chuffchart3.SetCounter.getMonthsLeft;
import static com.Stu.chuffchart3.SetCounter.getMonthsTDone;
import static com.Stu.chuffchart3.SetCounter.getMonthsTLeft;
import static com.Stu.chuffchart3.SetCounter.getSecsDone;
import static com.Stu.chuffchart3.SetCounter.getSecsLeft;
import static com.Stu.chuffchart3.SetCounter.getSecsTDone;
import static com.Stu.chuffchart3.SetCounter.getSecsTLeft;
import static com.Stu.chuffchart3.SetCounter.getStartDate;
import static com.Stu.chuffchart3.SetCounter.getWeeksDone;
import static com.Stu.chuffchart3.SetCounter.getWeeksLeft;
import static com.Stu.chuffchart3.SetCounter.getWeeksTDone;
import static com.Stu.chuffchart3.SetCounter.getWeeksTLeft;
import static com.Stu.chuffchart3.SetCounter.updateCounter;
import static com.Stu.chuffchart3.SetCounter.updateCounterAndDates;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Details extends Activity{
	boolean running = true;
	myThread MT;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("detailsFrag","CREATED");
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.details);
		updateCounterAndDates(DBV.Sstart, DBV.Send);
		updateView();
		MT = new myThread();
		MT.start();
	}
	public void onPause(){
		super.onPause();
		Log.i("detailsFrag","PAUSE called");
		running=false;
	}
	public void onResume(){
		super.onResume();
		Log.i("detailsFrag","RESUME called");
		running = true;
	}
	private class myThread extends Thread{
		public void run(){
			while(running){
				updateCounter();
				Log.i("detailsFrag","updateCounter() called");
				runOnUiThread(new Runnable(){public void run() {updateView();}});
				try {
					sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	public void updateView(){
		Log.i("detailsFrag","updateView()");
		   TextView startD= (TextView) findViewById(R.id.startDate);
		   TextView endD= (TextView) findViewById(R.id.endDate);
		   
		   TextView num106= (TextView) findViewById(R.id.monthsDoneT);
		   TextView num105= (TextView) findViewById(R.id.weeksDoneT);
		   TextView num104= (TextView) findViewById(R.id.daysDoneT);
		   TextView num103= (TextView) findViewById(R.id.hoursDoneT);
		   TextView num102= (TextView) findViewById(R.id.minsDoneT);
		   TextView num101= (TextView) findViewById(R.id.secsDoneT);
		   
		   TextView num160= (TextView) findViewById(R.id.monthsDone);
		   TextView num150= (TextView) findViewById(R.id.weeksDone);
		   TextView num140= (TextView) findViewById(R.id.daysDone);
		   TextView num130= (TextView) findViewById(R.id.hoursDone);
		   TextView num120= (TextView) findViewById(R.id.minsDone);
		   TextView num110= (TextView) findViewById(R.id.secsDone);
		   
		   TextView num206= (TextView) findViewById(R.id.monthsLeftT);
		   TextView num205= (TextView) findViewById(R.id.weeksLeftT);
		   TextView num204= (TextView) findViewById(R.id.daysLeftT);
		   TextView num203= (TextView) findViewById(R.id.hoursLeftT);
		   TextView num202= (TextView) findViewById(R.id.minsLeftT);
		   TextView num201= (TextView) findViewById(R.id.secsLeftT);
		   
		   TextView num210= (TextView) findViewById(R.id.secsLeft);
		   TextView num220= (TextView) findViewById(R.id.minsLeft);
		   TextView num230= (TextView) findViewById(R.id.hoursLeft);
		   TextView num240= (TextView) findViewById(R.id.daysLeft);
		   TextView num250= (TextView) findViewById(R.id.weeksLeft);
		   TextView num260= (TextView) findViewById(R.id.monthsLeft);
		   
		   startD.setText("Start: "+getStartDate());
		   endD.setText("End: "+getEndDate());
		   		  
		   num101.setText(String.format("%,d",getSecsTDone()));
		   num102.setText(String.format("%,d",getMinsTDone()));
		   num103.setText(String.format("%,d",getHoursTDone()));
		   num104.setText(String.format("%,d",getDaysTDone()));
		   num105.setText(String.format("%,d",getWeeksTDone()));
		   num106.setText(String.format("%,d",getMonthsTDone()));

		   num110.setText(""+getSecsDone());
		   num120.setText(""+getMinsDone());
		   num130.setText(""+ getHoursDone());
		   num140.setText(""+getDaysDone());
		   num150.setText(""+getWeeksDone());
		   num160.setText(""+getMonthsDone());
		   
		   num201.setText(String.format("%,d",getSecsTLeft()));
		   num202.setText(String.format("%,d",getMinsTLeft()));
		   num203.setText(String.format("%,d",getHoursTLeft()));
		   num204.setText(String.format("%,d",getDaysTLeft()));
		   num205.setText(String.format("%,d",getWeeksTLeft()));
		   num206.setText(String.format("%,d",getMonthsTLeft()));
		   
		   num210.setText(""+getSecsLeft());
		   num220.setText(""+getMinsLeft());
		   num230.setText(""+getHoursLeft());
		   num240.setText(""+getDaysLeft());
		   num250.setText(getWeeksLeft()+"");
		   num260.setText(getMonthsLeft()+"");
		   
	}

}
