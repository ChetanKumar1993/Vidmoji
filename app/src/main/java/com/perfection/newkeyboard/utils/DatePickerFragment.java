package com.perfection.newkeyboard.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;

import com.perfection.newkeyboard.Helpers.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>DatePickerFragment</H1>
 *
 * <p>Represents a Date picker dialog to choose date from.</p>
 *
 * @author Divya Thakur
 * @version 1.0
 * @since 1/13/17
 */
@SuppressLint("SimpleDateFormat")
public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    public static final int REQUEST_CODE_DATE = 0;
    public static final int REQUEST_CODE_MONTH_YEAR = 1;
    public static final int REQUEST_CODE_YEAR = 2;
    private final String TAG = getClass().getSimpleName();
    private int requestCode;
    private DatePickerCallback datePickerCallback = null;
    private int year, month, day;
    private String dateFormat;

    public DatePickerFragment() {

    }

    @SuppressLint("ValidFragment")
    public DatePickerFragment(int requestCode, DatePickerCallback datePickerCallback) {

        this.requestCode = requestCode;
        this.datePickerCallback = datePickerCallback;
    }

    @Override
    public void setArguments(Bundle args) {

        Utils.printLogs(TAG, "Inside setArguments()");

        try {
            super.setArguments(args);
            year = args.getInt(Constants.DATE_FORMAT_YEAR);
            month = args.getInt(Constants.DATE_FORMAT_MONTH);
            day = args.getInt(Constants.DATE_FORMAT_DAY);
            dateFormat = args.getString("date_format");

        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.printLogs(TAG, "Outside setArguments()");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        Calendar calendar = Calendar.getInstance();

        Date minDate = calendar.getTime();
		
		Date minDateToBeSelected = DateHelper.getDateFromString(DateHelper.parseDateToDesiredFormat(
                minDate, Constants.DATE_FORMAT_MM_DD_YYYY),
                Constants.DATE_FORMAT_MM_DD_YYYY);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
        //DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        DatePicker view = dialog.getDatePicker();

        //view.setMinDate(minDateToBeSelected.getTime());

        view.setMaxDate(minDateToBeSelected.getTime());

        try {
            java.lang.reflect.Field[] datePickerDialogFields =
                    dialog.getClass().getDeclaredFields();

            switch (requestCode) {

                case REQUEST_CODE_DATE:
                    return dialog;
                case REQUEST_CODE_MONTH_YEAR:
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        int daySpinnerId =
                                Resources.getSystem().getIdentifier("day", "id", "android");
                        if (daySpinnerId != 0) {
                            View daySpinner = dialog.findViewById(daySpinnerId);
                            if (daySpinner != null) {
                                daySpinner.setVisibility(View.GONE);
                            }
                        }
                        int monthSpinnerId =
                                Resources.getSystem().getIdentifier("month", "id", "android");
                        if (monthSpinnerId != 0) {
                            View monthSpinner = dialog.findViewById(monthSpinnerId);
                            if (monthSpinner != null) {
                                monthSpinner.setVisibility(View.VISIBLE);
                            }
                        }
                        int yearSpinnerId =
                                Resources.getSystem().getIdentifier("year", "id", "android");
                        if (yearSpinnerId != 0) {
                            View yearSpinner = dialog.findViewById(yearSpinnerId);
                            if (yearSpinner != null) {
                                yearSpinner.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {*/
                    for (java.lang.reflect.Field datePickerDialogField :
                            datePickerDialogFields) {

                        if (datePickerDialogField.getName().equals("mDatePicker")) {

                            datePickerDialogField.setAccessible(true);
                            DatePicker datePicker = (DatePicker) datePickerDialogField.get(dialog);
                            java.lang.reflect.Field[] datePickerFields =
                                    datePickerDialogField.getType().getDeclaredFields();
                            for (java.lang.reflect.Field datePickerField : datePickerFields) {

                                if ("mDaySpinner".equals(datePickerField.getName())) {

                                    datePickerField.setAccessible(true);
                                    Object dayPicker = datePickerField.get(datePicker);
                                    ((View) dayPicker).setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    // }
                    break;
                case REQUEST_CODE_YEAR:
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        int daySpinnerId =
                                Resources.getSystem().getIdentifier("day", "id", "android");
                        if (daySpinnerId != 0) {
                            View daySpinner = dialog.findViewById(daySpinnerId);
                            if (daySpinner != null) {
                                daySpinner.setVisibility(View.GONE);
                            }
                        }
                        int monthSpinnerId =
                                Resources.getSystem().getIdentifier("month", "id", "android");
                        if (monthSpinnerId != 0) {
                            View monthSpinner = dialog.findViewById(monthSpinnerId);
                            if (monthSpinner != null) {
                                monthSpinner.setVisibility(View.GONE);
                            }
                        }
                        int yearSpinnerId =
                                Resources.getSystem().getIdentifier("year", "id", "android");
                        if (yearSpinnerId != 0) {
                            View yearSpinner = dialog.findViewById(yearSpinnerId);
                            if (yearSpinner != null) {
                                yearSpinner.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {*/
                    for (java.lang.reflect.Field datePickerDialogField :
                            datePickerDialogFields) {

                        if (datePickerDialogField.getName().equals("mDatePicker")) {

                            datePickerDialogField.setAccessible(true);
                            DatePicker datePicker = (DatePicker)
                                    datePickerDialogField.get(dialog);
                            Utils.printLogs(TAG,
                                    Resources.getSystem().getIdentifier("year", "id", "android") + "");
                            java.lang.reflect.Field[] datePickerFields =
                                    datePickerDialogField.getType().getDeclaredFields();
                            for (java.lang.reflect.Field datePickerField : datePickerFields) {
                                Utils.printLogs(TAG, datePickerField.getName());
                                if ("mDaySpinner".equals(datePickerField.getName()) ||
                                        "mMonthSpinner".equals((datePickerField.getName()))) {

                                    datePickerField.setAccessible(true);
                                    Object dayPicker = datePickerField.get(datePicker);
                                    ((View) dayPicker).setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    // }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Utils.printLogs(TAG, "Inside onDateSet()");

        try {
            switch (requestCode) {
                case REQUEST_CODE_DATE:
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day);
                    if(TextUtils.isEmpty(dateFormat)) {
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                Constants.DATE_FORMAT_DD_MMM_YYYY, Locale.US);
                        String formattedDate = sdf.format(c.getTime());

                        datePickerCallback.setDate(formattedDate, requestCode);
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                        String formattedDate = sdf.format(c.getTime());
                        datePickerCallback.setDate(formattedDate, requestCode);
                    }
                    break;
                case REQUEST_CODE_MONTH_YEAR:
                    datePickerCallback.setDate(month + " " + year, requestCode);
                    break;
                case REQUEST_CODE_YEAR:
                    datePickerCallback.setDate(year + "", requestCode);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.printLogs(TAG, "Outside onDateSet()");
    }
}  
