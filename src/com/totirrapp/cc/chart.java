package com.totirrapp.cc;

import android.util.Log;

import java.util.ArrayList;

public class chart {
	private int chartNo= 99;

	private ArrayList<String> values;
	private String chartName = "Chart Name Test";
	private String chartHeader = "I Can't Wait...";
	private String chartTitle = "StaticTitle";
	private String chartStart = "02/02/2002";
	private String chartEnd = "02/02/2020";
	private String chartBgUrl = "StaticURL";

	private int sWidth = 720;
	private int sHeight = 1280;
//		private boolean sTheme = true;

	private int daysLeft = 25;
	private int daysDone = 30;
	private int hoursLeft =10;
	private int hoursDone = 10;
	private int percentDone = 50;

	public chart(int x){
		chartNo = x;
		readChart();
	}
	public void readChart(){
		String y = "Chart"+chartNo+">ReadChart()";
		values = databaseReader.getChartInfo(chartNo,y);
		chartName = values.get(0);
		chartHeader = values.get(1);
		chartTitle = values.get(2);
		chartStart = values.get(3);
		chartEnd = values.get(4);
		chartBgUrl = values.get(5);
	}
	public ArrayList getValues(){
		Log.e("chart TEST", "getValues() called");
		return values;
	}
	public String getChartName(){
		return chartName;
	}
	public String getChartHeader(){
		return chartHeader;
	}
	public String getChartTitle(){
		return chartTitle;
	}
	public String getChartStart(){
		return chartStart;
	}
	public String getChartEnd(){
		return chartEnd;
	}
	public String getChartBgUrl(){
		return chartBgUrl;
	}
}
