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

import static com.totirrapp.cc.R.*;
import static com.totirrapp.cc.R.id.timeDoneTextView;
import static com.totirrapp.cc.SetCounter.getDaysTDone;
import static com.totirrapp.cc.SetCounter.getDaysTLeft;
import static com.totirrapp.cc.SetCounter.getHoursTDone;
import static com.totirrapp.cc.SetCounter.getHoursTLeft;


public class ChartFragment extends Fragment{
	public View rootView;
    private clickCallback call;
    private View.OnClickListener shortPress;
    private View.OnLongClickListener longPress;
    private TextView				percent				= null;
    private TextView				timeDoneText		= null;
    private TextView				timeLeftText		= null;
    private TextView				titleBot			= null;
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
        values = getArguments().getStringArrayList("values");
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
    public void updateHomeView(){
        try {
            if(percent == null){
                percent = (TextView) rootView.findViewById(R.id.percDoneText);
                timeDoneText = (TextView) rootView.findViewById(timeDoneTextView);
                timeLeftText = (TextView) rootView.findViewById(R.id.timeLeftTextView);
                titleBot = (TextView) rootView.findViewById(R.id.titleBot);
            }
            percent.setText(DBV.percentDone + "%");
            if(getDaysTDone()>0){
                timeDoneText.setText(getDaysTDone() + " " + getString(R.string.daysDone));
            }else{
                timeDoneText.setText(getHoursTDone() + " " + getString(R.string.hoursDone));
            }
            if(getDaysTLeft()>0){
                timeLeftText.setText(getDaysTLeft() + " " + getString(R.string.daysLeft));
            }else{
                timeLeftText.setText(getHoursTLeft() + " " + getString(R.string.hoursLeft));
            }
            titleBot.setText(values.get(2));
        } catch (Exception e) {
            Log.d("Update Home View", "update home View failed");
            e.printStackTrace();
        }
    }
}