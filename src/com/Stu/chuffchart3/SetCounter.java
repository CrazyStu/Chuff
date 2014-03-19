package com.Stu.chuffchart3;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class SetCounter {
		private static Locale home = new Locale("en");
		private static Calendar calCurrent = Calendar.getInstance(home);
		private static Calendar calStart = Calendar.getInstance(home);
		private static Calendar calTarget = Calendar.getInstance(home);
		private static Calendar compLeft = Calendar.getInstance(home);
		private static Calendar compDone = Calendar.getInstance(home);
		private static long compareLeft;
		private static long compareDone;
		private static int percent1;
		private static int percent2;
		private static int Syear = 2013;
		private static int Smonth=02;
		private static int Sday=25;
		private static int Eyear= 2013;
		private static int Emonth= 7;
		private static int Eday= 3;
		private static int monthsTLeft;
		private static int monthsTDone;
		private static int weeksTLeft;
		private static int weeksTDone;
		private static int daysTLeft;
		private static long daysTDone;
		private static long hoursTLeft;
		private static long hoursTDone;
		private static long minsTLeft;
		private static long minsTDone;
		private static long secsTLeft;
		private static long secsTDone;
		private static int monthsLeft;
		private static int monthsDone;
		private static int weeksLeft;
		private static int weeksDone;
		private static int daysLeft;
		private static int daysDone;
		private static int hoursLeft;
		private static int hoursDone;
		private static int minsLeft;
		private static int minsDone;
		private static int secsLeft;
		private static int secsDone;
		
//		public SetCounter() {
//		}
		public static void updateCounterAndDates(String start, String end) {
			loadDbDates(start, end);
			updateCounter();
		}
		public static void updateCounter() {
			Log.i("SetCounter", "UpdateCounter");
			calCurrent.setTime(new Date());
			calTarget.set(Eyear,Emonth,Eday,0,0,0);
			calStart.set(Syear,Smonth,Sday,0,0,0);
			
			compareLeft = calTarget.getTimeInMillis()-calCurrent.getTimeInMillis();
			compareDone = calCurrent.getTimeInMillis()-calStart.getTimeInMillis();
			if(compareLeft<0){
				compareLeft=0;
				compareDone = calTarget.getTimeInMillis()-calStart.getTimeInMillis();
			}
			percent1 = (int) ((float)compareLeft/(compareLeft+compareDone)*100);
			percent2 = (int) ((float)compareDone/(compareLeft+compareDone)*100);
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
//----Time left TOTALS
				compLeft.setTimeInMillis(compareLeft);
				secsTLeft=compareLeft/1000;
				minsTLeft=secsTLeft/60;
				hoursTLeft=minsTLeft/60;
				daysTLeft=(int) (hoursTLeft/24);
				weeksTLeft = daysTLeft/7;
				if(daysTLeft>29){
					monthsTLeft=Emonth-calCurrent.get(Calendar.MONTH)-1;
				}else{monthsTLeft=0;}
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
		private static void loadDbDates(String start, String end){
			String[] StartDate= start.split("/");
			Sday = Integer.parseInt(StartDate[0]);
			Smonth = Integer.parseInt(StartDate[1]);
			Syear = Integer.parseInt(StartDate[2]);
			
			String[] EndDate=end.split("/");
			Eday = Integer.parseInt(EndDate[0]);
			Emonth = Integer.parseInt(EndDate[1]);
			Eyear = Integer.parseInt(EndDate[2]);
	
		}
		public static int getPercentRemaining(){
			return percent1;
		}
		public static int getPercentCompleted(){
			return percent2;
		}
		public static String getStartDate(){
			String x = Sday+"/"+(1+Smonth)+"/"+Syear;
			return x;
		}
		public static String getEndDate(){
			String x = Eday+"/"+(1+Emonth)+"/"+Eyear;
			return x;
		}
		public static int getDaysTLeft(){
			return daysTLeft;
		}
		public static int getDaysTDone(){
			return (int)daysTDone;
		}	
		public static long getHoursTLeft(){
			return hoursTLeft;
		}
		public static long getHoursTDone(){
			return hoursTDone;
		}
		public static long getMinsTLeft(){
			return minsTLeft;
		}
		public static long getMinsTDone(){
			return minsTDone;
		}
		public static long getSecsTLeft(){
			return secsTLeft;
		}
		public static long getSecsTDone(){
			return secsTDone;
		}
		public static int getWeeksTLeft() {
			return weeksTLeft;
		}
		public static int getWeeksTDone() {
			return weeksTDone;
		}
		public static int getMonthsTLeft() {
			return monthsTLeft;
		}
		public static int getMonthsTDone() {
			return monthsTDone;
		}		
		public static int getDaysLeft(){
			return daysLeft;
		}
		public static int getDaysDone(){
			return daysDone;
		}	
		public static long getHoursLeft(){
			return hoursLeft;
		}
		public static long getHoursDone(){
			return hoursDone;
		}
		public static long getMinsLeft(){
			return minsLeft;
		}
		public static long getMinsDone(){
			return minsDone;
		}
		public static long getSecsLeft(){
			return secsLeft;
		}
		public static long getSecsDone(){
			return secsDone;
		}
		public static int getWeeksLeft() {
			return weeksLeft;
		}
		public static int getWeeksDone() {
			return weeksDone;
		}
		public static int getMonthsLeft() {
			return monthsLeft;
		}
		public static int getMonthsDone() {
			return monthsDone;
		}
}
