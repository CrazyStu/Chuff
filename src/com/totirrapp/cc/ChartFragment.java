package com.totirrapp.cc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.totirrapp.cc.R.id;
import static com.totirrapp.cc.R.id.timeDoneTextView;
import static com.totirrapp.cc.R.layout;


public class ChartFragment extends Fragment{
	public View rootView;
    private clickCallback call;
    private View.OnClickListener shortPress;
    private View.OnLongClickListener longPress;
    private TextView				percent				= null;
    private TextView				timeDoneText		= null;
    private TextView				timeLeftText		= null;
    private TextView				titleBot			= null;
    private String chartName = "Chart Name Test";
    private String chartHeader = "I Can't Wait...";
    private String chartTitle = "StaticTitle";
    private String chartStart = "02/02/2002";
    private String chartEnd = "02/02/2020";
    private String chartBgUrl = "StaticURL";
    private int startYear = 2013;
    private int startMonth =02;
    private int startDay =25;
    private int endYear = 2013;
    private int endMonth = 7;
    private int endDay = 3;
    private FragmentCounter counter;
    private int chartNo = 99;
    private ArrayList<String> values;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            call = (clickCallback) activity;

        } catch (ClassCastException e) {
            Log.d("attach test","implementation failed");
            e.printStackTrace();

        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(layout.frag_home, container, false);
        counter = new FragmentCounter();
        try {
            chartNo = getArguments().getInt("ChartNo", 0);
        }catch(Exception e){
            e.printStackTrace();
        }
        setupListeners();
        setupButtons();
        call.initiateBG(chartNo);
        return rootView;
	}
    public void getArgs(){
        chartNo = getArguments().getInt("ChartNo");
        readChartValues();
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

            String[] StartDate = chartStart.split("/");
            startDay = Integer.parseInt(StartDate[0]);
            startMonth = Integer.parseInt(StartDate[1]) - 1;
            startYear = Integer.parseInt(StartDate[2]);

            String[] EndDate = chartEnd.split("/");
            endDay = Integer.parseInt(EndDate[0]);
            endMonth = Integer.parseInt(EndDate[1]) - 1;
            endYear = Integer.parseInt(EndDate[2]);
        }
    }
    public void setNewBackground(BitmapDrawable bmp){
         try{
             rootView.setBackground(bmp);
         }catch(Exception e){
             e.printStackTrace();
         }
     }
    public void removeBackground(){
        try{
            rootView.setBackground(null);
            rootView.setBackgroundColor(Color.TRANSPARENT);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public interface clickCallback {
        void onShortPress(int v);
        void onLongPress(int v);
        void initiateBG(int x);
    }
    public void setupListeners(){
        shortPress= new View.OnClickListener() {
            public void onClick(View v){
               Log.e("shortPress", "viewID =" + v.getId());
                call.onShortPress(v.getId());
//                v.setVisibility(View.VISIBLE);
            }
        };
        longPress = new View.OnLongClickListener() {
            public boolean onLongClick(View v){
                Log.e("LOOOONGPress","viewID ="+v.getId());
                call.onLongPress(v.getId());
//                v.setVisibility(View.INVISIBLE);
                return true;
            }
        };
    }
    public void setupButtons(){
        View timeDoneButton;
        timeDoneButton = this.rootView.findViewById(id.timeDoneTextView);
        timeDoneButton.setOnClickListener(shortPress);
        timeDoneButton.setOnLongClickListener(longPress);

        View timeLeftButton;
        timeLeftButton = this.rootView.findViewById(id.timeLeftTextView);
        timeLeftButton.setOnClickListener(shortPress);
        timeLeftButton.setOnLongClickListener(longPress);

        View chartTitle;
        chartTitle = this.rootView.findViewById(id.chart_title);
        chartTitle.setOnClickListener(shortPress);
        chartTitle.setOnLongClickListener(longPress);

        View progressBar;
        progressBar = this.rootView.findViewById(id.progBar);
        progressBar.setOnClickListener(shortPress);
        progressBar.setOnLongClickListener(longPress);

        View chartBackground;
        chartBackground = this.rootView.findViewById(id.frag_home_parent_view);
        chartBackground.setOnClickListener(shortPress);
        chartBackground.setOnLongClickListener(longPress);
    }
    public void updateChartView(){
        counter.updateCounter();
        try {
            if(percent == null){
                percent = (TextView) rootView.findViewById(R.id.percDoneText);
                timeDoneText = (TextView) rootView.findViewById(timeDoneTextView);
                timeLeftText = (TextView) rootView.findViewById(R.id.timeLeftTextView);
                titleBot = (TextView) rootView.findViewById(R.id.titleBot);
            }
            percent.setText(DBV.percentDone + "%");
            if(counter.getDaysTotalDone()>0){
                timeDoneText.setText(counter.getDaysTotalDone() + " " + getString(R.string.daysDone));
            }else{
                timeDoneText.setText(counter.getHoursTotalDone() + " " + getString(R.string.hoursDone));
            }
            if(counter.getDaysTotalLeft()>0){
                timeLeftText.setText(counter.getDaysTotalLeft() + " " + getString(R.string.daysLeft));
            }else{
                timeLeftText.setText(counter.getHoursTotalLeft() + " " + getString(R.string.hoursLeft));
            }
            titleBot.setText(values.get(2));
        } catch (Exception e) {
            Log.d("Update Home View", "update home View failed");
            e.printStackTrace();
        }
    }
    public String getChartName(){
        return chartName;
    }
    public String getChartHeader(){
        return chartHeader;
    }
    public String getChartTitle(){
        return chartTitle;
    }
    public String getChartStart(){
        return chartStart;
    }
    public String getChartEnd(){
        return chartEnd;
    }
    public String getChartBgUrl(){
        return chartBgUrl;
    }

    public class FragmentCounter {
        private Locale home = new Locale("en");
        private Calendar calCurrent = Calendar.getInstance(home);
        private Calendar calStart = Calendar.getInstance(home);
        private Calendar calTarget = Calendar.getInstance(home);
        private Calendar compLeft = Calendar.getInstance(home);
        private Calendar compDone = Calendar.getInstance(home);
        private long compareLeft;
        private long compareDone;
        private int percent1;
        private int percent2;
//        private int startYear = 2013;
//        private int startMonth =02;
//        private int startDay =25;
//        private int endYear = 2013;
//        private int endMonth = 7;
//        private int endDay = 3;
        private int monthsTotalLeft;
        private int monthsTotalDone;
        private int weeksTotalLeft;
        private int weeksTotalDone;
        private int daysTotalLeft;
        private long daysTotalDone;
        private long hoursTotalLeft;
        private long hoursTotalDone;
        private long minsTotalLeft;
        private long minsTotalDone;
        private long secsTotalLeft;
        private long secsTDone;
        private int monthsLeft;
        private int monthsDone;
        private int weeksLeft;
        private int weeksDone;
        private int daysLeft;
        private int daysDone;
        private int hoursLeft;
        private int hoursDone;
        private int minsLeft;
        private int minsDone;
        private int secsLeft;
        private int secsDone;

        public void updateCounter() {
            Log.i("SetCounter", "Update Counter");
            calCurrent.setTime(new Date());
            calTarget.set(endYear, endMonth, endDay,0,0,0);
            calStart.set(startYear, startMonth, startDay,0,0,0);

//----Check date validity
            compareLeft = calTarget.getTimeInMillis()-calCurrent.getTimeInMillis();
            compareDone = calCurrent.getTimeInMillis()-calStart.getTimeInMillis();
            if(compareLeft<0){
                compareLeft=0;
                compareDone = calTarget.getTimeInMillis()-calStart.getTimeInMillis();
            }
            if(compareDone<0){
                compareDone = 0;
                compareLeft = calTarget.getTimeInMillis()-calStart.getTimeInMillis();
            }
            percent1 = (int) ((float)compareLeft/(compareLeft+compareDone)*100);
            percent2 = (int) ((float)compareDone/(compareLeft+compareDone)*100);
            DBV.percentDone = percent2;
//----Time done TOTALS
            compDone.setTimeInMillis(compareDone);
            secsTDone=compDone.getTimeInMillis()/1000;
            minsTotalDone =secsTDone/60;
            hoursTotalDone = minsTotalDone /60;
            daysTotalDone =(hoursTotalDone /24);
            weeksTotalDone =(int) (daysTotalDone /7);
            if(daysTotalDone >29){
                monthsTotalDone = calCurrent.get(Calendar.MONTH)- startMonth -1;
            }else{
                monthsTotalDone =0;}
            DBV.daysDone = (int) daysTotalDone;
            DBV.hoursDone = (int) hoursTotalDone;
//----Time left TOTALS
            compLeft.setTimeInMillis(compareLeft);
            secsTotalLeft =compareLeft/1000;
            minsTotalLeft = secsTotalLeft /60;
            hoursTotalLeft = minsTotalLeft /60;
            daysTotalLeft =(int) (hoursTotalLeft /24);
            weeksTotalLeft = daysTotalLeft /7;
            if(daysTotalLeft >29){
                monthsTotalLeft =(int) Math.floor(daysTotalLeft /30.14667);
            }else{
                monthsTotalLeft =0;}
            DBV.daysLeft = daysTotalLeft;
            DBV.hoursLeft = (int) hoursTotalLeft;
//----Time Done breakdown
            monthsDone = monthsTotalDone;
            weeksDone=(int) ((compareDone/1000/60/60/24/7)-(monthsTotalDone *4));
            daysDone=(int) ((compareDone/1000/60/60/24)-(weeksTotalDone *7));
            hoursDone=(int) ((compareDone/1000/60/60)-(daysTotalDone *24));
            minsDone=(int) ((compareDone/1000/60)-(hoursTotalDone *60));
            secsDone=(int) ((compareDone/1000)-(minsTotalDone *60));
//----Time Left breakdown
            monthsLeft= monthsTotalLeft;
            weeksLeft=(int) ((compareLeft/1000/60/60/24/7)-(monthsTotalLeft *4));
            daysLeft=(int) ((compareLeft/1000/60/60/24)-(weeksTotalLeft *7));
            hoursLeft=(int) ((compareLeft/1000/60/60)-(daysTotalLeft *24));
            minsLeft=(int) ((compareLeft/1000/60)-(hoursTotalLeft *60));
            secsLeft=(int) ((compareLeft/1000)-(minsTotalLeft *60));

        }
        public int getPercentRemaining(){
            return percent1;
        }
        public int getPercentCompleted(){
            return percent2;
        }
        public String getStartDate(){
            String x = startDay +"/"+(1+ startMonth)+"/"+ startYear;
            return x;
        }
        public String getEndDate(){
            String x = endDay +"/"+(1+ endMonth)+"/"+ endYear;
            return x;
        }
        public int getDaysTotalLeft(){
            return daysTotalLeft;
        }
        public int getDaysTotalDone(){
            return (int) daysTotalDone;
        }
        public long getHoursTotalLeft(){
            return hoursTotalLeft;
        }
        public long getHoursTotalDone(){
            return hoursTotalDone;
        }
        public long getMinsTotalLeft(){
            return minsTotalLeft;
        }
        public long getMinsTotalDone(){
            return minsTotalDone;
        }
        public long getSecsTotalLeft(){
            return secsTotalLeft;
        }
        public long getSecsTDone(){
            return secsTDone;
        }
        public int getWeeksTotalLeft() {
            return weeksTotalLeft;
        }
        public int getWeeksTotalDone() {
            return weeksTotalDone;
        }
        public int getMonthsTotalLeft() {
            return monthsTotalLeft;
        }
        public int getMonthsTotalDone() {
            return monthsTotalDone;
        }
        public int getDaysLeft(){
            return daysLeft;
        }
        public int getDaysDone(){
            return daysDone;
        }
        public long getHoursLeft(){
            return hoursLeft;
        }
        public long getHoursDone(){
            return hoursDone;
        }
        public long getMinsLeft(){
            return minsLeft;
        }
        public long getMinsDone(){
            return minsDone;
        }
        public long getSecsLeft(){
            return secsLeft;
        }
        public long getSecsDone(){
            return secsDone;
        }
        public int getWeeksLeft() {
            return weeksLeft;
        }
        public int getWeeksDone() {
            return weeksDone;
        }
        public int getMonthsLeft() {
            return monthsLeft;
        }
        public int getMonthsDone() {
            return monthsDone;
        }
    }


}