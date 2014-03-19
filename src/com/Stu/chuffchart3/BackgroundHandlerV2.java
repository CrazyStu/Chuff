package com.Stu.chuffchart3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BackgroundHandlerV2 {
	
	private static Drawable BGDrawable;
	private static String BGURL;
	
	public BackgroundHandlerV2(){}
	
	public static Drawable getBackground(String url){
		BGURL=url;
				
		Log.i("BgHandler2","Find image: "+ BGURL);
		BitmapFactory.Options bgOptions = new BitmapFactory.Options();
		bgOptions.inJustDecodeBounds=true;
		try{
			@SuppressWarnings("unused")
			Bitmap test = BitmapFactory.decodeFile(BGURL, bgOptions);
		}catch(Exception ex){ex.printStackTrace();}
		if(bgOptions.outHeight>0){
			Log.i("BgHandler2","Image found---Loading selected picture");
			BGDrawable=Drawable.createFromPath(BGURL);
		}else{
			Log.i("BgHandler2", "Image not found---Loading default backgorund");
			BGDrawable = Drawable.createFromPath("@drawable/pic1");
		}
		return BGDrawable;
	}
//	public static String getTitle(){
//		myDatabaseAdapter db = new myDatabaseAdapter(MainActivity.context);
//		db.open();
//		Cursor V = db.getRecord("title",1);
//		String title=V.getString(1);
//		db.close();
//	
//		return title;
//	}
}
