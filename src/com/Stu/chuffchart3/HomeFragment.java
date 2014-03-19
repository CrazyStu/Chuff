package com.Stu.chuffchart3;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {
	public static String homeBGURL= "noURL";
	public static Bundle bund;
	public Drawable bg;
	public View rootView;
	public HomeFragment() {
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		homeBGURL = getArguments().getString(homeBGURL);
		bund = getArguments();
		rootView = inflater.inflate(R.layout.home_fragment,container, false);
//		loadBg();
		rootView.setBackground(bg);
		return rootView;
	}
	public void onResume(){
		super.onResume();
		loadBg();
	}
	public static Bundle getInfo(){
		return bund;
	}
    public void loadBg(){
		new BGTask().execute(homeBGURL);
    }
	class BGTask extends AsyncTask<String,Void,String>{
		protected String doInBackground(String... s) {
				Log.w("BGTask","doInBackground Started");
				bg=BackgroundHandlerV2.getBackground(s[0]);
				Log.w("BGTask s",s[0]);
				return s[0];
		}
		protected void onPostExecute(String s){
			Log.w("HomeFragment", "bg");
			  if (isCancelled()) {
				  Log.w("BGTask","Post Execute cancelled?");
			  }else{
//				  Log.w("HomeFragment", bg);
				  Log.w("BGTask","Post Execute other???");
				  rootView.setBackground(bg);
			  }
		}
	}
	
}