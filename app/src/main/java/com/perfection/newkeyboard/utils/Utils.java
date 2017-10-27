package com.perfection.newkeyboard.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>Utils</H1>
 * <p>
 * <p>Provides utility methods that are commonly used in the application</p>
 *
 * @author Divya Thakur
 * @version 1.0
 * @since 9/1/16
 */
public class Utils {

    private static final String TAG = "Utils";
    private static final boolean LOG_ON = true;

    /**
     * Hides the keyboard if the screen is tapped
     * anywhere else then the edit texts
     *
     * @param activity holds the object for Activity
     */
    public static void hideSoftKeyboard(Activity activity) {

        Utils.printLogs(TAG, "Inside hideSoftKeyboard()");

        try {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.printLogs(TAG, "Outside hideSoftKeyboard()");
    }

    /**
     * Prints the logs if the logs are on
     *
     * @param TAG     holds the name of the class or the
     * @param message holds the string of message to be printed
     */
    public static void printLogs(String TAG, String message) {

        if (Utils.LOG_ON)
            Log.d(String.valueOf(TAG), String.valueOf(message));
    }

    /**
     * Checks if the Internet Connection is available or not
     *
     * @param context holds the object for Context
     * @return true or false
     */
    public static boolean isNetworkAvailable(Context context) {

        Utils.printLogs(TAG, "Inside isNetworkAvailable()");

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Utils.printLogs(TAG, "Outside isNetworkAvailable()");

        return activeNetworkInfo != null && activeNetworkInfo.isAvailable()
                && activeNetworkInfo.isConnected();
    }

    /**
     * Displays progress dialog
     *
     * @param context holds the object of context
     * @param message holds the message
     * @return object of Progress Dialog
     */
    public static ProgressDialog showProgressDialog(Context context, String message) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        if (message != null && message.length() > 0)
            progressDialog.show();
        return progressDialog;
    }

    /**
     * Displays country code dialog
     *
     * @param context     holds the context
     * @param countryList holds the country list with codes
     * @return Dialog object
     */
    public static Dialog showCountryListDialog(final Context context,
                                               final String[] countryList,
                                               final DialogListCallback dialogListCallback) {

        final AlertDialog.Builder builderCountry = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layoutCountryCode = inflater.inflate(R.layout.country_list_dialog,
                (ViewGroup) ((Activity) context).findViewById(R.id.rlCountryCode), false);

        builderCountry.setTitle("Please select country");

        EditText etSearch = (EditText) layoutCountryCode.findViewById(R.id.etSearch);
        ListView lvCountryCodes = (ListView) layoutCountryCode.findViewById(R.id.lvCountryCodes);

        final CountryListAdapter countryAdapter = new CountryListAdapter(context,
                countryList);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                countryAdapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        lvCountryCodes.setAdapter(countryAdapter);
        builderCountry.setView(layoutCountryCode);
        final Dialog dialog = builderCountry.create();

        lvCountryCodes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogListCallback.getSelectedItem(Constants.countryArray[position]);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * Returns the DisplayImageOptions object
     *
     * @return DisplayImageOptions
     */
    @SuppressWarnings("deprecation")
    public static DisplayImageOptions getDisplayImageOptions() {

        Utils.printLogs(TAG, "Inside getDisplayImageOptions()");

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();

        Utils.printLogs(TAG, "Outside getDisplayImageOptions()");

        return displayImageOptions;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public static boolean isFileExists(String path, String filename) {

        File folder1 = new File(path + filename);
        return folder1.exists();


    }

    // Glide Images
    public static void glide(Context context, ImageView iv, int drawable, String url) {
        try {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().placeholder(drawable).into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Glide Images
    public static void glide(Context context, ImageView iv, String url) {
        try {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getBitmap(Context context, String url, final ImageLoadedCallback imageLoadedCallback, final int i) {
        Bitmap theBitmap = null;
        SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                // do something with the bitmap
                // for demonstration purposes, let's just set it to an ImageView
                imageLoadedCallback.imageSuccessfullyLoaded(bitmap, i);

            }
        };
        Glide.
                with(context).
                load(url).
                asBitmap().
                into(target);
    }


    public static void openSetting(Context context) {
        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);


    }

    public static void deleteFile(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                boolean deleteFlag = false;
                for (int k = 1; k < 10; k++) {
                    if (files[i].getAbsolutePath().contains("-" + k)) {
                        deleteFlag = true;
                        break;
                    }
                }

                if (deleteFlag) {
                    files[i].delete();
                }
            }
    }


}
