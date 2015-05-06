package com.totirrapp.cc;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
		private int Syear = 2013;
		private int Smonth=02;
		private int Sday=25;
		private int Eyear= 2013;
		private int Emonth= 7;
		private int Eday= 3;
		private int monthsTLeft;
		private int monthsTDone;
		private int weeksTLeft;
		private int weeksTDone;
		private int daysTLeft;
		private long daysTDone;
		private long hoursTLeft;
		private long hoursTDone;
		private long minsTLeft;
		private long minsTDone;
		private long secsTLeft;
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

		public void updateCounterAndDates() {
			Log.i("SetCounter", "Update Counter Dates");
			String[] StartDate= DBV.Sstart.split("/");
			Sday = Integer.parseInt(StartDate[0]);
			Smonth = Integer.parseInt(StartDate[1])-1;
			Syear = Integer.parseInt(StartDate[2]);
			
			String[] EndDate=DBV.sEnd.split("/");
			Eday = Integer.parseInt(EndDate[0]);
			Emonth = Integer.parseInt(EndDate[1])-1;
			Eyear = Integer.parseInt(EndDate[2]);
			updateCounter();
		}
		public void updateCounter() {
			Log.i("SetCounter", "Update Counter");
			calCurrent.setTime(new Date());
			calTarget.set(Eyear,Emonth,Eday,0,0,0);
			calStart.set(Syear,Smonth,Sday,0,0,0);

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
			minsTDone=secsTDone/60;
			hoursTDone=minsTDone/60;
			daysTDone=(hoursTDone/24);
			weeksTDone=(int) (daysTDone/7);
			if(daysTDone>29){
				monthsTDone = calCurrent.get(Calendar.MONTH)-Smonth-1;
			}else{monthsTDone=0;}
			DBV.daysDone = (int) daysTDone;
			DBV.hoursDone = (int) hoursTDone;
//----Time left TOTALS
			compLeft.setTimeInMillis(compareLeft);
			secsTLeft=compareLeft/1000;
			minsTLeft=secsTLeft/60;
			hoursTLeft=minsTLeft/60;
			daysTLeft=(int) (hoursTLeft/24);
			weeksTLeft = daysTLeft/7;
			if(daysTLeft>29){
				monthsTLeft=(int) Math.floor(daysTLeft/30.14667);
			}else{monthsTLeft=0;}
			DBV.daysLeft = daysTLeft;
			DBV.hoursLeft = (int) hoursTLeft;
//----Time Done breakdown
			monthsDone = monthsTDone;
			weeksDone=(int) ((compareDone/1000/60/60/24/7)-(monthsTDone*4));
			daysDone=(int) ((compareDone/1000/60/60/24)-(weeksTDone*7));
			hoursDone=(int) ((compareDone/1000/60/60)-(daysTDone*24));
			minsDone=(int) ((compareDone/1000/60)-(hoursTDone*60));
			secsDone=(int) ((compareDone/1000)-(minsTDone*60));
//----Time Left breakdown
			monthsLeft=monthsTLeft;
			weeksLeft=(int) ((compareLeft/1000/60/60/24/7)-(monthsTLeft*4));
			daysLeft=(int) ((compareLeft/1000/60/60/24)-(weeksTLeft*7));
			hoursLeft=(int) ((compareLeft/1000/60/60)-(daysTLeft*24));
			minsLeft=(int) ((compareLeft/1000/60)-(hoursTLeft*60));
			secsLeft=(int) ((compareLeft/1000)-(minsTLeft*60));
		
		}
		public int getPercentRemaining(){
			return percent1;
		}
		public int getPercentCompleted(){
			return percent2;
		}
		public String getStartDate(){
			String x = Sday+"/"+(1+Smonth)+"/"+Syear;
			return x;
		}
		public String getEndDate(){
			String x = Eday+"/"+(1+Emonth)+"/"+Eyear;
			return x;
		}
		public int getDaysTLeft(){
			return daysTLeft;
		}
		public int getDaysTDone(){
			return (int)daysTDone;
		}	
		public long getHoursTLeft(){
			return hoursTLeft;
		}
		public long getHoursTDone(){
			return hoursTDone;
		}
		public long getMinsTLeft(){
			return minsTLeft;
		}
		public long getMinsTDone(){
			return minsTDone;
		}
		public long getSecsTLeft(){
			return secsTLeft;
		}
		public long getSecsTDone(){
			return secsTDone;
		}
		public int getWeeksTLeft() {
			return weeksTLeft;
		}
		public int getWeeksTDone() {
			return weeksTDone;
		}
		public int getMonthsTLeft() {
			return monthsTLeft;
		}
		public int getMonthsTDone() {
			return monthsTDone;
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
