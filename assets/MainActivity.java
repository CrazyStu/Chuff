package com.stu.chuff;

import static com.stu.chuff.SetCounter.*;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class MainActivity extends FragmentActivity {
	public static MyViewGroup vgp1;
	
    private static int RESULT_LOAD_IMAGE = 1;
	private Integer Syear = 2013;
	private Integer Smonth=00;
	private Integer Sday=01;
	private Integer Eyear= 2013;
	private Integer Emonth= 12;
	private Integer Eday= 30;
	public static Context context;
	private static myDatabaseAdapter db = null;
	private boolean info=false;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		vgp1 = new MyViewGroup(context);
        setContentView(vgp1);
        vgp1.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View view){
            	openOptionsMenu();
				return true;
            }
        });
        vgp1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
            	configDetails();
				info=true;
            }
        });
        db = new myDatabaseAdapter(context);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.TitleButton:
			myTitleDialog();
			break;
		case R.id.Change:
	           Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
               startActivityForResult(i, RESULT_LOAD_IMAGE);
               break;
		case R.id.SelectDate:
			myDialog();
			break;
		case R.id.settings:
			Intent myIntent = new Intent(this, ListViewLoader.class);
			startActivity(myIntent);
		}
		return false;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Thumbnails.DATA };
	            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
	            cursor.moveToFirst();
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	            Log.i("pic",picturePath);
	    		
	            db.open();
	    		db.updatePic(1, picturePath);
	    		db.close();
	    		vgp1.updateMainPic();
	        }
	   }
	private void myDialog(){
		final Dialog setDate = new Dialog(this);
		setDate.setContentView(R.layout.dialog);
		setDate.setTitle("Select Dates");
		
		final DatePicker picker1 = (DatePicker) setDate.findViewById(R.id.datePicker1);
		final DatePicker picker2 = (DatePicker) setDate.findViewById(R.id.datePicker2);
		
		loadDatesV();
		Log.i("Dates","-"+Sday+" "+Smonth+" "+Syear+" "+Eday+" "+Emonth+" "+Eyear);
		picker1.init(Syear, Smonth, Sday, null);
		picker2.init(Eyear, Emonth, Eday, null);
		setDate.show();
	
		Button submitDate = (Button) setDate.findViewById(R.id.button1);
   submitDate.setOnClickListener(new View.OnClickListener(){
        public void onClick(View view){
    		try{
    			String newStartDate = 	picker1.getDayOfMonth()+"/"+picker1.getMonth()+"/"+picker1.getYear();
    			String newEndDate = 	picker2.getDayOfMonth()+"/"+picker2.getMonth()+"/"+picker2.getYear();
    					
    			db.open();
		db.updateDates(1,newStartDate,newEndDate);
		}catch(SQLException sqle){throw sqle;}
       	 db.close();
       	 updateCounterAndDates();
       	 vgp1.updateMain(); //mview.updateAll();
       	 setDate.dismiss();    
        }
    }); 
}
	private void loadDatesV(){
		   Log.i("Main", "MainLoadDB");
			db.open();
			Cursor V = db.getRecord("dates",1);
			String[] StartDate= V.getString(1).split("/");
			Sday = Integer.parseInt(StartDate[0]);
			Smonth = Integer.parseInt(StartDate[1]);
			Syear = Integer.parseInt(StartDate[2]);
			
			String[] EndDate= V.getString(2).split("/");
			Eday = Integer.parseInt(EndDate[0]);
			Emonth = Integer.parseInt(EndDate[1]);
			Eyear = Integer.parseInt(EndDate[2]);
			db.close();	
		}
	public boolean onTouchEvent(MotionEvent event){
			int eventaction = event.getAction();
			switch(eventaction){
			case MotionEvent.ACTION_UP:
				if(info){
					setContentView(vgp1);
					info=false;
				}else{
					configDetails();
					info=true;
				}
				break;
			}
	    	return false; 		    	
	   }
	   private void configDetails(){
		   Intent myIntent = new Intent(context, Details.class);
		   startActivity(myIntent);	   
	   }
	   private void myTitleDialog(){
			final Dialog setTitle = new Dialog(this);
			setTitle.setContentView(R.layout.titlesetter);
			setTitle.setTitle("Set Title Text");
			db.open();
			Cursor V = db.getRecord("title",1);
			String title=V.getString(1);
			db.close();
			final EditText newTitle = (EditText)setTitle.findViewById(R.id.setNewTitle);
			newTitle.setText(title);
			newTitle.requestFocus();
			setTitle.show();
			
			Button submitTitle = (Button) setTitle.findViewById(R.id.button2);
			submitTitle.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View view){
	        	String input = newTitle.getText().toString();
	        	Log.i("titleTest", "-->"+input);
	    		try{
	    			db.open();
	    			db.updateTitle(1,input);
			}catch(SQLException sqle){
				throw sqle;
			}
	       	 db.close();
	       	 vgp1.updateMain(); //mview.updateAll();
	       	 setTitle.dismiss();    
	        }
	    }); 
		   }
}
