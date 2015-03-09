package com.totirrapp.cc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
		return rootView;
	}
    public interface clickCallback {
        public void buttonPressed(int id);
    }
    public void setupListeners(){
        shortPress= new View.OnClickListener() {
            public void onClick(View v){
               Log.e("shortPress", "viewID =" + v.getId());
            }
        };
        longPress = new View.OnLongClickListener() {
            public boolean onLongClick(View v){
                    Log.e("LOOOONGPress","viewID ="+v.getId());
                return false;
            }
        };
    }

    public void setupButtons(){
        View testButton;
        testButton = this.rootView.findViewById(id.daysDoneTextView);
        testButton.setOnClickListener(shortPress);
        testButton.setOnLongClickListener(longPress);
    }

}