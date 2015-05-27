package com.totirrapp.cc;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CounterFragment {
    private Locale home = new Locale("en");
    private Calendar calCurrent = Calendar.getInstance(home);
    private Calendar calStart = Calendar.getInstance(home);
    private Calendar calTarget = Calendar.getInstance(home);
    private Calendar compLeft = Calendar.getInstance(home);
    private Calendar compDone = Calendar.getInstance(home);
    private long compareLeft;
    private long compareDone;
    private int startYear = 2013;
    private int startMonth = 02;
    private int startDay = 25;
    private int endYear = 2013;
    private int endMonth = 7;
    private int endDay = 3;

    private int percentLeft;
    private int percentDone;
    private int monthsTotalLeft;
    private int monthsTotalDone;
    private int weeksTotalLeft;
    private int weeksTotalDone;
    private int daysTotalLeft;
    private long daysTotalDone;
    private long hoursTotalLeft;
    private long hoursTotalDone;
    private long minsTotalLeft;
    private long minsTotalDone;
    private long secsTotalLeft;
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

    CounterFragment(String chartStart, String chartEnd) {

        String[] StartDate = chartStart.split("/");
        startDay = Integer.parseInt(StartDate[0]);
        startMonth = Integer.parseInt(StartDate[1]) - 1;
        startYear = Integer.parseInt(StartDate[2]);

        String[] EndDate = chartEnd.split("/");
        endDay = Integer.parseInt(EndDate[0]);
        endMonth = Integer.parseInt(EndDate[1]) - 1;
        endYear = Integer.parseInt(EndDate[2]);
    }


    public void updateCounter() {
        try {
            Log.i("ChartFragCounter", "Update Counter");
            calCurrent.setTime(new Date());
            calTarget.set(endYear, endMonth, endDay, 0, 0, 0);
            calStart.set(startYear, startMonth, startDay, 0, 0, 0);

//----Check date validity
            compareLeft = calTarget.getTimeInMillis() - calCurrent.getTimeInMillis();
            compareDone = calCurrent.getTimeInMillis() - calStart.getTimeInMillis();
            if (compareLeft < 0) {
                compareLeft = 0;
                compareDone = calTarget.getTimeInMillis() - calStart.getTimeInMillis();
            }
            if (compareDone < 0) {
                compareDone = 0;
                compareLeft = calTarget.getTimeInMillis() - calStart.getTimeInMillis();
            }
            percentLeft = (int) ((float) compareLeft / (compareLeft + compareDone) * 100);
            percentDone = (int) ((float) compareDone / (compareLeft + compareDone) * 100);
//----Time done TOTALS
            compDone.setTimeInMillis(compareDone);
            secsTDone = compDone.getTimeInMillis() / 1000;
            minsTotalDone = secsTDone / 60;
            hoursTotalDone = minsTotalDone / 60;
            daysTotalDone = (hoursTotalDone / 24);
            weeksTotalDone = (int) (daysTotalDone / 7);
            if (daysTotalDone > 29) {
                monthsTotalDone = calCurrent.get(Calendar.MONTH) - startMonth - 1;
            } else {
                monthsTotalDone = 0;
            }
//            DBV.daysDone = (int) daysTotalDone;
//            DBV.hoursDone = (int) hoursTotalDone;
//----Time left TOTALS
            compLeft.setTimeInMillis(compareLeft);
            secsTotalLeft = compareLeft / 1000;
            minsTotalLeft = secsTotalLeft / 60;
            hoursTotalLeft = minsTotalLeft / 60;
            daysTotalLeft = (int) (hoursTotalLeft / 24);
            weeksTotalLeft = daysTotalLeft / 7;
            if (daysTotalLeft > 29) {
                monthsTotalLeft = (int) Math.floor(daysTotalLeft / 30.14667);
            } else {
                monthsTotalLeft = 0;
            }
//            DBV.daysLeft = daysTotalLeft;
//            DBV.hoursLeft = (int) hoursTotalLeft;
//----Time Done breakdown
            monthsDone = monthsTotalDone;
            weeksDone = (int) ((compareDone / 1000 / 60 / 60 / 24 / 7) - (monthsTotalDone * 4));
            daysDone = (int) ((compareDone / 1000 / 60 / 60 / 24) - (weeksTotalDone * 7));
            hoursDone = (int) ((compareDone / 1000 / 60 / 60) - (daysTotalDone * 24));
            minsDone = (int) ((compareDone / 1000 / 60) - (hoursTotalDone * 60));
            secsDone = (int) ((compareDone / 1000) - (minsTotalDone * 60));
//----Time Left breakdown
            monthsLeft = monthsTotalLeft;
            weeksLeft = (int) ((compareLeft / 1000 / 60 / 60 / 24 / 7) - (monthsTotalLeft * 4));
            daysLeft = (int) ((compareLeft / 1000 / 60 / 60 / 24) - (weeksTotalLeft * 7));
            hoursLeft = (int) ((compareLeft / 1000 / 60 / 60) - (daysTotalLeft * 24));
            minsLeft = (int) ((compareLeft / 1000 / 60) - (hoursTotalLeft * 60));
            secsLeft = (int) ((compareLeft / 1000) - (minsTotalLeft * 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPercentRemaining() {
        return percentLeft;
    }

    public int getPercentDone() {
        return percentDone;
    }

    public String getStartDate() {
        String x = startDay + "/" + (1 + startMonth) + "/" + startYear;
        return x;
    }

    public String getEndDate() {
        String x = endDay + "/" + (1 + endMonth) + "/" + endYear;
        return x;
    }

    public long getCalStartMills() {
        return calStart.getTimeInMillis();
    }

    public long getCalEndMills() {
        return calTarget.getTimeInMillis();
    }

    public int getDaysTotalLeft() {
        return daysTotalLeft;
    }

    public int getDaysTotalDone() {
        return (int) daysTotalDone;
    }

    public long getHoursTotalLeft() {
        return hoursTotalLeft;
    }

    public long getHoursTotalDone() {
        return hoursTotalDone;
    }

    public long getMinsTotalLeft() {
        return minsTotalLeft;
    }

    public long getMinsTotalDone() {
        return minsTotalDone;
    }

    public long getSecsTotalLeft() {
        return secsTotalLeft;
    }

    public long getSecsTotalDone() {
        return secsTDone;
    }

    public int getWeeksTotalLeft() {
        return weeksTotalLeft;
    }

    public int getWeeksTotalDone() {
        return weeksTotalDone;
    }

    public int getMonthsTotalLeft() {
        return monthsTotalLeft;
    }

    public int getMonthsTotalDone() {
        return monthsTotalDone;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public int getDaysDone() {
        return daysDone;
    }

    public long getHoursLeft() {
        return hoursLeft;
    }

    public int getHoursDone() {
        return hoursDone;
    }

    public long getMinsLeft() {
        return minsLeft;
    }

    public int getMinsDone() {
        return minsDone;
    }

    public long getSecsLeft() {
        return secsLeft;
    }

    public int getSecsDone() {
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
