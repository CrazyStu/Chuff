package com.totirrapp.cc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewFragment extends Fragment{
    private newChartCallback call;
    private View.OnClickListener shortPress;
    public View rootView;
	public NewFragment() {

	}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            call = (newChartCallback) activity;

        } catch (ClassCastException e) {
            Log.d("attach test", "implementation failed");
            e.printStackTrace();

        }
    }
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.frag_new,container, false);
        setupListeners();
        setupButtons();
		return rootView;
	}
    public interface newChartCallback {
        public void newChartRequest(int v);
    }
    public void setupListeners(){
        shortPress= new View.OnClickListener() {
            public void onClick(View v){
                Log.e("shortPress", "viewID =" + v.getId());
                call.newChartRequest(v.getId());
            }
        };
    }
    public void setupButtons(){
        View newChartButton;
        newChartButton = this.rootView.findViewById(R.id.new_chart_text);
        newChartButton.setOnClickListener(shortPress);
    }
}