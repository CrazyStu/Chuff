package com.totirrapp.cc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailsActivity extends Activity {
    private boolean running = true;
    private detailsThread MT;
    private String chartStart = "02/02/2002";
    private String chartEnd = "02/02/2020";
    private CounterFragment counter;
    private ArrayList<String> values;
    private int chartNo = 99;
//
//    private static final int PROGRESS = 0x1;
    private ProgressBar monthProgress;
    private ProgressBar weekProgress;
    private ProgressBar dayProgress;
    private ProgressBar hourProgress;

    private TextView targetDate;
    private TextView percent;
    private TextView monthCount;
    private TextView weekCount;
    private TextView dayCount;
    private TextView hourCount;
    private int mProgressStatus = 0;


    public DetailsActivity() {
//        counter = new CounterFragment();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            chartStart = getIntent().getExtras().getString("start");
            chartEnd = getIntent().getExtras().getString("end");
            Log.e("gotExtras?", "start#end " + chartStart + "#" + chartEnd);

        } catch (Exception e) {
            e.printStackTrace();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.lightTheme);
        setContentView(R.layout.details_activity_test);
        setupView();

    }

    protected void onPause() {
        super.onPause();
        running = false;
        Log.e("DetailsTread State", MT.getState() + "");
    }

    protected void onResume() {
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

    public void setupView() {
        counter = new CounterFragment(chartStart, chartEnd);
        targetDate = (TextView) findViewById(R.id.details_activity_target_date);
        percent = (TextView) findViewById(R.id.details_activity_percentage);
        monthCount = (TextView) findViewById(R.id.details_activity_months_count);
        weekCount = (TextView) findViewById(R.id.details_activity_weeks_count);
        dayCount = (TextView) findViewById(R.id.details_activity_days_count);
        hourCount = (TextView) findViewById(R.id.details_activity_hours_count);

        monthProgress = (ProgressBar) findViewById(R.id.details_activity_progress_months);
        weekProgress = (ProgressBar) findViewById(R.id.details_activity_progress_weeks);
        dayProgress = (ProgressBar) findViewById(R.id.details_activity_progress_days);
        hourProgress = (ProgressBar) findViewById(R.id.details_activity_progress_hours);
    }

    public void updateDetailsView() {
        Log.i("detailsFrag", "updateDetailsView()");
        targetDate.setText(chartEnd);
        percent.setText(counter.getPercentDone() + "%");
        monthCount.setText(Integer.toString(counter.getMonthsDone()));
        weekCount.setText(Integer.toString(counter.getWeeksDone()));
        dayCount.setText(Integer.toString(counter.getDaysDone()));
        hourCount.setText(Integer.toString(counter.getHoursDone()));

    }

    private class detailsThread extends Thread {
        public void run() {
            Log.e("MT State", MT.getState() + "");
            while (running) {
                try {
                    counter.updateCounter();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            updateDetailsView();
                        }
                    });
                    mProgressStatus++;
                            monthProgress.setProgress(mProgressStatus);
                    weekProgress.setProgress(mProgressStatus);
                    dayProgress.setProgress(mProgressStatus);
                    hourProgress.setProgress(mProgressStatus);
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("MT State", MT.getState() + "stopped");
        }
    }

}