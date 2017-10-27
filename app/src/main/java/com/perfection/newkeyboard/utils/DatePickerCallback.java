package com.perfection.newkeyboard.utils;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>DatePickerCallback</H1>
 *
 * <p>Provides a callback method to return the selected date from the Date Picker dialog</p>
 *
 * @author Divya Thakur
 * @since 1/13/17
 * @version 1.0
 */
public interface DatePickerCallback {

	/**
	 * Callback method that gets the date from the date picker dialog
	 * @param date Holds the date
	 * @param requestCode Holds the request code
	 */
	void setDate(String date, int requestCode);
}
