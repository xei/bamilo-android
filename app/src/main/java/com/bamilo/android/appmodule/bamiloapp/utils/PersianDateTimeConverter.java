package com.bamilo.android.appmodule.bamiloapp.utils;

/**
 * Created by Arash Hassanpour on 4/30/2017.
 */

import java.util.Calendar;
import java.util.GregorianCalendar;

public class PersianDateTimeConverter {

    protected int year = 0;
    protected int month = 0;
    protected int day = 0;
    protected int hour = 0;
    protected int minute = 0;
    protected int second = 0;

    public PersianDateTimeConverter(String date) {
        date = date.replace("/", "-");
        String[] main = date.split(" ");
        String[] dates = main[0].split("-");
        this.year = Integer.valueOf(dates[0]);
        this.month = Integer.valueOf(dates[1]);
        this.day = Integer.valueOf(dates[2]);
        if (main.length > 1) {
            String[] times = main[1].split(":");
            this.hour = Integer.valueOf(times[0]);
            this.minute = Integer.valueOf(times[1]);
            this.second = Float.valueOf(times[2]).intValue();
        }

    }

    public PersianDateTimeConverter(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public PersianDateTimeConverter(int year, int month, int day, int hour, int minute, int second) {
        this(year, month, day);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public static PersianDateTimeConverter valueOf(Calendar ed) {
        int g_days_in_month[] = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int j_days_in_month[] = new int[]{31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
        int i;
        int gy = ed.get(Calendar.YEAR) - 1600;
        int gm = ed.get(Calendar.MONTH);
        int gd = ed.get(Calendar.DAY_OF_MONTH) - 1;
        int g_day_no = 365 * gy + (int) ((gy + 3) / 4) - (int) ((gy + 99) / 100) + ((int) ((gy + 399) / 400));
        for (i = 0; i < gm; ++i) {
            g_day_no += g_days_in_month[i];
        }
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            g_day_no++;
        }
        g_day_no += gd;
        int j_day_no = g_day_no - 79;
        int j_np = (int) (j_day_no / 12053);
        j_day_no = j_day_no % 12053;
        int jy = 979 + 33 * j_np + 4 * (int) (j_day_no / 1461);
        j_day_no %= 1461;
        if (j_day_no >= 366) {
            jy += (int) ((j_day_no - 1) / 365);
            j_day_no = (j_day_no - 1) % 365;
        }
        for (i = 0; i < 11 && j_day_no >= j_days_in_month[i]; ++i) {
            j_day_no -= j_days_in_month[i];
        }
        int jm = i + 1;
        j_day_no++;

        return new PersianDateTimeConverter(jy, jm, j_day_no, ed.get(Calendar.HOUR_OF_DAY), ed.get(Calendar.MINUTE), ed.get(Calendar.SECOND));

    }

    public static PersianDateTimeConverter valueOf(Long milis) {
        Calendar dt = new GregorianCalendar();
        dt.setTimeInMillis(milis);
        return PersianDateTimeConverter.valueOf(dt);
    }

    public static PersianDateTimeConverter Now() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int second = cal.get(Calendar.SECOND);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        return PersianDateTimeConverter.valueOf(new GregorianCalendar(year, month, day, hour, minute, second));
    }

    public long getTimeStamp() {
        GregorianCalendar dt = new GregorianCalendar(year, month, day, hour, minute, second);
        return dt.getTimeInMillis();
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getMonth() {
        return month;
    }

    public int getSecond() {
        return second;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return this.toDateString();// + " " + this.toTimeString();
    }

    public String toDateString() {
        return String.format("%04d/%02d/%02d", year, month, day);
    }

    public String toTimeString() {
        return String.format("%d:%d:%d", hour, minute, second);
    }

    public boolean after(PersianDateTimeConverter dt) {
        return this.getTimeStamp() > dt.getTimeStamp();
    }

    public boolean before(PersianDateTimeConverter dt) {
        return this.getTimeStamp() < dt.getTimeStamp();
    }

    public GregorianCalendar toGregorianDate() {
        return toGregorianDate(this);
    }

    public static GregorianCalendar toGregorianDate(PersianDateTimeConverter pd) {
        GregorianCalendar dt = null;
        int miladiYear, i, dayCount, remainDay, marchDayDiff;
        // this buffer has day count of Miladi month from April to January for a none year.
        int[] miladiMonth = {30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31};
        miladiYear = pd.year + 621;
        //Detemining the Farvardin the First
        //this is a Miladi leap year so Shamsi is leap too so the 1st of Farvardin is March 20 (3/20)
        //this is not a Miladi leap year so Shamsi is not leap too so the 1st of Farvardin is March 21 (3/21)
        // If next year is leap we will add one day to Feb.
        int milady_leap_year = miladiYear + 1;
        if (((miladiYear % 100) != 0 && (miladiYear % 4) == 0) || ((miladiYear % 100) == 0 && (miladiYear % 400) == 0)) {
            marchDayDiff = 12;
        } else {
            marchDayDiff = 11;
        }
        if (((milady_leap_year % 100) != 0 && (milady_leap_year % 4) == 0) || ((milady_leap_year % 100) == 0 && (milady_leap_year % 400) == 0)) {
            miladiMonth[10] = miladiMonth[10] + 1; //Adding one day to Feb
        }
        //Calculate the day count for input shamsi date from 1st Farvadin
        if ((pd.month >= 1) && (pd.month <= 6)) {
            dayCount = ((pd.month - 1) * 31) + pd.day;
        } else {
            dayCount = (6 * 31) + ((pd.month - 7) * 30) + pd.day;
        }
        //Finding the correspond miladi month and day
        if (dayCount <= marchDayDiff) //So we are in 20(for leap year) or 21for none leap year) to 31 march
        {
            dt = new GregorianCalendar(miladiYear, 3, dayCount + (31 - marchDayDiff), pd.hour, pd.minute, pd.second);
        } else {
            remainDay = dayCount - marchDayDiff;
            i = 0; //starting from April
            while ((remainDay > miladiMonth[i])) {
                remainDay = remainDay - miladiMonth[i];
                i++;
            }
            //  miladiDate.setDate(remainDay);
            if (i > 8) // We are in the next Miladi Year
            {
                dt = new GregorianCalendar(miladiYear + 1, i - 8, remainDay, pd.hour, pd.minute, pd.second);
            } else {
                dt = new GregorianCalendar(miladiYear, i + 4, remainDay, pd.hour, pd.minute, pd.second);
            }
        }
        return dt;
    }
}
