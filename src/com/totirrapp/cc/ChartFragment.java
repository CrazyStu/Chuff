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
    private int screenHeight = 123;
    private int screenWidth = 123;
    private CounterFragment counter;
    private int chartNo = 99;
    private ArrayList<String> values;
    public ChartFragment(){
        Log.d("--Chart Fragment--", "ChartFreagment() Called");
    }
    @Override
    public void onAttach(Activity activity) {
        Log.d("--Chart Fragment--","onAttach Called");
        super.onAttach(activity);
        try {
            call = (clickCallback) activity;
        } catch (ClassCastException e) {
            Log.d("attach test","implementation failed");
            e.printStackTrace();
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("--Chart Fragment--","onCreateView Called");
		rootView = inflater.inflate(layout.frag_home, container, false);
        try {
            Log.e("screenHeight","=="+screenHeight);
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
        screenHeight = getArguments().getInt("Height");
        screenWidth =getArguments().getInt("Width");
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

            counter = new CounterFragment(chartStart,chartEnd);
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
        if(rootView!=null) {
            try {
                counter.updateCounter();
                int height = 200;
                try {
                    height = (int) (screenHeight * ((float) counter.getPercentDone() / 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rootView.findViewById(id.progBarBlue).setMinimumHeight(height);
                rootView.invalidate();
                if (percent == null) {
                    percent = (TextView) rootView.findViewById(R.id.percDoneText);
                    timeDoneText = (TextView) rootView.findViewById(timeDoneTextView);
                    timeLeftText = (TextView) rootView.findViewById(R.id.timeLeftTextView);
                    titleBot = (TextView) rootView.findViewById(R.id.titleBot);
                }
                percent.setText(counter.getPercentDone() + "%");
                if (counter.getDaysTotalDone() > 0) {
                    timeDoneText.setText(counter.getDaysTotalDone() + " " + getString(R.string.daysDone));
                } else {
                    timeDoneText.setText(counter.getHoursTotalDone() + " " + getString(R.string.hoursDone));
                }
                if (counter.getDaysTotalLeft() > 0) {
                    timeLeftText.setText(counter.getDaysTotalLeft() + " " + getString(R.string.daysLeft));
                } else {
                    timeLeftText.setText(counter.getHoursTotalLeft() + " " + getString(R.string.hoursLeft));
                }
                titleBot.setText(chartTitle);
            } catch (Exception e) {
                Log.d("Update Home View", "update home View failed");
                e.printStackTrace();
            }
        }else{
            Log.e("RootView Error","Root View not setup just yet...?");
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
}