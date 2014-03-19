package com.Stu.chuffchart3;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BackgroundHandler {
	
	private static Bitmap bg1;
	private static int iscale;
	
	public BackgroundHandler(){}
	public static Bitmap getBackground(int screenW, int screenH){
		Context context = MainActivity.context;
		myDatabaseAdapter db = new myDatabaseAdapter(context);
		Log.i("BgHandler","Read DB");
		db.open();
			Cursor V = db.getRecord("background",1);
			String out=V.getString(1);
		db.close();
		Log.i("BgHandler","Find image: "+ out);
		
		BitmapFactory.Options bgOptions = new BitmapFactory.Options();
		bgOptions.inJustDecodeBounds=true;
		try{
			@SuppressWarnings("unused")
			Bitmap test = BitmapFactory.decodeFile(out, bgOptions);
		}catch(Exception ex){ex.printStackTrace();}
		if(bgOptions.outHeight>0){
			Log.i("BgHandler","Image found---Loading selected picture");
			loadSelectedImage(out,screenW,screenH);
		}else{
			Log.i("BgHandler", "Image not found---Loading default backgorund");
			loadDefaultImage(context,screenW,screenH);
		}
		return bg1;
	}
	private static Bitmap loadSelectedImage (String out, int screenW, int screenH){
		boolean landscape=true;
		BitmapFactory.Options bgOptions = new BitmapFactory.Options();
	
		//========Check size and get sample size======================
		bgOptions.inJustDecodeBounds=true;
	    bg1 = BitmapFactory.decodeFile(out, bgOptions);
	    int imageHeight = bgOptions.outHeight;
	    int imageWidth = bgOptions.outWidth;
	    float ScreenAspect = (float)screenH/(float)screenW;
	    float ImageAspect =(float)bgOptions.outHeight/(float)bgOptions.outWidth;
	    if(ImageAspect<ScreenAspect){
	    	landscape=true;
	    }
		Log.i("BgHandler","Origional Width= "+bgOptions.outWidth);
		Log.i("BgHandler","Origional Height= "+bgOptions.outHeight);
		Log.i("BgHandler","Landscape= "+landscape);
		
		
		if(landscape){
			ImageAspect=(float)bgOptions.outWidth/(float)bgOptions.outHeight;
			if(imageHeight>screenH){
				iscale= (int) ((float)imageHeight/(float)screenH)+2;
				bgOptions.inSampleSize=iscale;
			}else{
				iscale=1;
				bgOptions.inSampleSize=iscale;
			}
		}else{
			ImageAspect=(float)bgOptions.outHeight/(float)bgOptions.outWidth;
			if(imageWidth>screenW){
				iscale= (int) ((float)imageWidth/(float)screenW)+2;
				bgOptions.inSampleSize=iscale;
			}else{
				iscale=1;
				bgOptions.inSampleSize=iscale;				
			}
			
		}
		Log.i("BgHandler", "Sample Size= "+iscale);
		Log.i("BgHandler", "Image aspect ratio= "+ImageAspect);
		Log.i("BgHandler", "Screen aspect ratio= "+ScreenAspect);

		//=============Get full image and resize=====================
	    bgOptions.inJustDecodeBounds=false;
	    bg1 = BitmapFactory.decodeFile(out, bgOptions);
		
		Log.i("BgHandler","New Width= "+bg1.getWidth());
		Log.i("BgHandler","New Height= "+bg1.getHeight());
		if(landscape){
			bg1 = Bitmap.createScaledBitmap(bg1, (int) (screenH*ImageAspect), screenH, true);
		}else{
			bg1 = Bitmap.createScaledBitmap(bg1, screenW, (int) (screenW*ImageAspect), true);
			Log.i("BgHandler","Size="+screenW+" x "+(int) (screenW*ImageAspect));
		}
		return bg1;
	}
	private static Bitmap loadDefaultImage(Context context, int screenW, int screenH){
		bg1=BitmapFactory.decodeResource(context.getResources(), R.drawable.pic1);
		bg1=Bitmap.createScaledBitmap(bg1, screenW, screenH, true);
		return bg1;
	}
	public static String getTitle(){
		myDatabaseAdapter db = new myDatabaseAdapter(MainActivity.context);
		db.open();
		Cursor V = db.getRecord("title",1);
		String title=V.getString(1);
		db.close();
	
		return title;
	}
}
