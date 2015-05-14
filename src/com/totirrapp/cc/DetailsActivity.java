package com.totirrapp.cc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import static com.totirrapp.cc.SetCounter.getDaysDone;
import static com.totirrapp.cc.SetCounter.getDaysLeft;
import static com.totirrapp.cc.SetCounter.getDaysTDone;
import static com.totirrapp.cc.SetCounter.getDaysTLeft;
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
import static com.totirrapp.cc.SetCounter.getWeeksDone;
import static com.totirrapp.cc.SetCounter.getWeeksLeft;
import static com.totirrapp.cc.SetCounter.getWeeksTDone;
import static com.totirrapp.cc.SetCounter.getWeeksTLeft;

public class DetailsActivity extends Activity {
    private boolean					running				= true;
    private detailsThread			MT;
    private String chartName = "Chart Name Test";
    private String chartHeader = "I Can't Wait...";
    private String chartTitle = "StaticTitle";
    private String chartStart = "02/02/2002";
    private String chartEnd = "02/02/2020";
    private String chartBgUrl = "StaticURL";
    private ArrayList<String> values;
    private int chartNo = 99;
    private TextView startD;
    private TextView endD;

    private TextView num106;
    private TextView num105;
    private TextView num104;
    private TextView num103;
    private  TextView num102;
    private  TextView num101;

    private TextView num160;
    private TextView num150;
    private TextView num140;
    private    TextView num130;
    private   TextView num120;
    private   TextView num110;

    private   TextView num206;
    private   TextView num205;
    private  TextView num204;
    private TextView num203;
    private  TextView num202;
    private   TextView num201;

    private   TextView num210;
    private   TextView num220;
    private   TextView num230;
    private  TextView num240;
    private   TextView num250;
    private  TextView num260;

    public DetailsActivity() {
    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        try {
            chartNo = getIntent().getExtras().getInt("number");
            readChartValues();
        }catch(Exception e){
            e.printStackTrace();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.frag_details);
        setupView();
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
    public void readChartValues(){
        String y = "Chart"+chartNo+">ReadChart()";
        try{
            values = databaseReader.getChartInfo(chartNo,y);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        if (values!=null) {
            chartName = values.get(0);
            chartHeader = values.get(1);
            chartTitle = values.get(2);
            chartStart = values.get(3);
            chartEnd = values.get(4);
            chartBgUrl = values.get(5);
//
//            String[] StartDate = chartStart.split("/");
//            startDay = Integer.parseInt(StartDate[0]);
//            startMonth = Integer.parseInt(StartDate[1]) - 1;
//            startYear = Integer.parseInt(StartDate[2]);
//
//            String[] EndDate = chartEnd.split("/");
//            endDay = Integer.parseInt(EndDate[0]);
//            endMonth = Integer.parseInt(EndDate[1]) - 1;
//            endYear = Integer.parseInt(EndDate[2]);
        }
    }
    public void setupView(){
        startD = (TextView) findViewById(R.id.startDate);
        endD = (TextView) findViewById(R.id.endDate);

        num106 = (TextView) findViewById(R.id.monthsDoneT);
        num105 = (TextView) findViewById(R.id.weeksDoneT);
        num104 = (TextView) findViewById(R.id.daysDoneT);
        num103 = (TextView) findViewById(R.id.hoursDoneT);
        num102 = (TextView) findViewById(R.id.minsDoneT);
        num101 = (TextView) findViewById(R.id.secsDoneT);

        num160 = (TextView) findViewById(R.id.monthsDone);
        num150 = (TextView) findViewById(R.id.weeksDone);
        num140 = (TextView) findViewById(R.id.daysDone);
        num130 = (TextView) findViewById(R.id.hoursDone);
        num120 = (TextView) findViewById(R.id.minsDone);
        num110 = (TextView) findViewById(R.id.secsDone);

        num206 = (TextView) findViewById(R.id.monthsLeftT);
        num205 = (TextView) findViewById(R.id.weeksLeftT);
        num204 = (TextView) findViewById(R.id.daysLeftT);
        num203 = (TextView) findViewById(R.id.hoursLeftT);
        num202 = (TextView) findViewById(R.id.minsLeftT);
        num201 = (TextView) findViewById(R.id.secsLeftT);

        num210 = (TextView) findViewById(R.id.secsLeft);
        num220 = (TextView) findViewById(R.id.minsLeft);
        num230 = (TextView) findViewById(R.id.hoursLeft);
        num240 = (TextView) findViewById(R.id.daysLeft);
        num250 = (TextView) findViewById(R.id.weeksLeft);
        num260 = (TextView) findViewById(R.id.monthsLeft);
    }
    public void updateDetailsView(){
        Log.i("detailsFrag", "updateDetailsView()");
        startD.setText("Start: " + chartStart);
        endD.setText("End: " + chartEnd);

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
//                        updateCounter();
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