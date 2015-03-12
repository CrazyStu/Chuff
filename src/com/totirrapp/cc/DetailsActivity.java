package com.totirrapp.cc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

public class DetailsActivity extends Activity {
    public static Context context;
    private myDatabaseAdapter		db					= null;
    private boolean					running				= true;
    private detailsThread			MT;
	public DetailsActivity() {
	}
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.frag_details);
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
    }
    public void DBReadAll(){
        Cursor myCursor;
        try {
            db.open();
        } catch (SQLException e) {
            Log.e("DBReadAll error", "failed to open database");
            throw e;
        }
        myCursor = db.getChartNo(1);
        DBV.sTitle = myCursor.getString(1);
        DBV.Sstart = myCursor.getString(2);
        DBV.sEnd = myCursor.getString(3);
        DBV.sBgUrl = myCursor.getString(4);
        db.close();
    }
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
    private class detailsThread extends Thread {
        public void run(){
            Log.e("MT State", MT.getState() + "");
            while (running) {

                    try {
                        updateCounter();
                        runOnUiThread(new Runnable() {public void run(){updateDetailsView();}});
                    } catch (Exception e) {e.printStackTrace();}
                try {
                    sleep(1000);
                } catch (Exception e) {e.printStackTrace();}
            }
            Log.e("MT State", MT.getState() +"stopped");
        }
    }
}