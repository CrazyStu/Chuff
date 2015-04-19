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

import static com.totirrapp.cc.R.*;


public class HomeFragment extends Fragment{
	public View rootView;
    private clickCallback call;
    private View.OnClickListener shortPress;
    private View.OnLongClickListener longPress;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            call = (clickCallback) activity;

        } catch (ClassCastException e) {
            Log.d("attach test","implementation failed");
            e.printStackTrace();

        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(layout.frag_home,container, false);
        setupListeners();
        setupButtons();
        call.initiateBG();
		return rootView;
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
        void initiateBG();
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

}