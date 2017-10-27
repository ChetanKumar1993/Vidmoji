package com.perfection.newkeyboard.utils;

import android.annotation.SuppressLint;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>DateHelper</H1>
 *
 * <p>Provides the utility methods for dates</p>
 *
 * @author Divya Thakur
 * @since 1/13/17
 * @version 1.0
 */
public class DateHelper {
	
	private static final String TAG 										= "DateHelper";
	
	/**
	 * Checks if the two dates passed as arguments are same or not
	 * @param date1 holds the first date
	 * @param date2 holds the second date
	 * @return true or false
	 */
	public static boolean isSameDate(Date date1, Date date2) {
		
		Utils.printLogs(TAG, "Inside isSameDate()");

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		
		Utils.printLogs(TAG, "Outside isSameDate()");
		
		return sameDay;
	}
	
	/**
	 * Returns the date object after parsing it from string format
	 * @param dateString holds the date in String format
	 * @return Date object
	 */
	public static Date getDateFromString(String dateString, String currentFormat) {
		
		Utils.printLogs(TAG, "Inside getDateFromString()");

		SimpleDateFormat dateFormat = new SimpleDateFormat(currentFormat, Locale.US);
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Utils.printLogs(TAG, "Outside getDateFromString()");
		
		return convertedDate;
	}

	/**
	 * Converts date from one format to another
	 * @param date holds the date in string form
	 * @param currentFormat holds the current format
	 * @param convertedFormat holds the format in which the date is to be converted
	 * @return converted format date
	 */
	@SuppressLint("SimpleDateFormat")
	public static String convertFormatOfDate(String date , String currentFormat ,
											 String convertedFormat) {
		
		Utils.printLogs(TAG, "Inside convertFormatOfDate()");

		SimpleDateFormat sdf1 = new SimpleDateFormat(currentFormat, Locale.US);
		SimpleDateFormat sdf2 = new SimpleDateFormat(convertedFormat, Locale.US);

		try {
			return sdf2.format(sdf1.parse(date));
		} catch (ParseException e) {

			e.printStackTrace();
		}
		
		Utils.printLogs(TAG, "Outside convertFormatOfDate()");
		
		return null;
	}

	/**
	 * Changes the date into the desired format
	 * @param date holds the date object
	 * @param format holds the format in which the date is to be converted
	 * @return date in string
	 */
	public static String parseDateToDesiredFormat(Date date, String format) {

		Utils.printLogs(TAG, "Inside parseDateToDesiredFormat()");
                            //new SimpleDateFormat(format);
		SimpleDateFormat df = new SimpleDateFormat(format, Locale.US);

		String formattedDate = df.format(date);

		Utils.printLogs(TAG, "Outside parseDateToDesiredFormat()");
		return formattedDate;
	}

	/**
	 * Changes the integer value into month name either short or full
	 * @param month holds the integer value for month
	 * @return month name
	 */
	public static String getMonthName(int month) {							// Very useful

		Utils.printLogs(TAG, "Inside getMonthName()");

		//return new DateFormatSymbols().getShortMonths()[month - 1];
		return new DateFormatSymbols().getMonths()[month-1];		
	}

	/**
	 * Changes the integer value into month name either short or full
	 * @param month holds the integer value for month
	 * @return month name
	 */
	public static String getShortMonthName(int month) {							// Very useful

		Utils.printLogs(TAG, "Inside getMonthName()");

		return new DateFormatSymbols().getShortMonths()[month - 1];
		//return new DateFormatSymbols().getMonths()[month-1];
	}

	public static String getDateStringFromDate(Date date) {

        //String dayOfTheWeek =
        // (String) android.text.format.DateFormat.format("EEEE", date);//Thursday
        String stringMonth = (String) android.text.format.DateFormat.format("MMM", date); //Jun
        //String intMonth = (String) android.text.format.DateFormat.format("MM", date); //06
        String year = (String) android.text.format.DateFormat.format("yyyy", date); //2013
        String day = (String) android.text.format.DateFormat.format("dd", date); //20

        return day + " " + stringMonth + " " + year;
    }

    /**
     * Returs Time extracted from date
     * @param date holds the date object
     * @return time in string
     */
    public static String getTimeStringFromDate(Date date) {

        String seconds = (String) android.text.format.DateFormat.format("ss", date); //25
        String minutes = (String) android.text.format.DateFormat.format("mm", date); //33
        String hours = (String) android.text.format.DateFormat.format("HH", date); //16

        return hours + ":" + minutes; //+ ":" + seconds;
    }

    /**
     * Converts the UTC 
     * @param dateString
     * @return
     */
	public static String convertServerTimeToLocalTime(String dateString) {
		try {

			Calendar cal = Calendar.getInstance();
			TimeZone tz = cal.getTimeZone();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = simpleDateFormat.parse(dateString);

			TimeZone destTz = tz;
			simpleDateFormat.setTimeZone(destTz);
			String result = simpleDateFormat.format(date);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Changes the integer value into day name either short or full
	 * @param day holds the integer value for day
	 * @return day name
	 */
	public static String getDayName(int day) {							// Very useful

		Utils.printLogs(TAG, "Inside getDayName()");

		return new DateFormatSymbols().getWeekdays()[day];
		//new DateFormatSymbols().getShortWeekdays()[day-1];
	}
}
