package com.perfection.newkeyboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;

import com.android.inputmethodcommon.InputMethodSettingsFragment;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Displays the IME preferences inside the input method setting.
 */
public class ImePreferences extends PreferenceActivity {
    public static final int RequestPermissionCode = 1;
    @Override
    public Intent getIntent() {
        final Intent modIntent = new Intent(super.getIntent());
        modIntent.putExtra(EXTRA_SHOW_FRAGMENT, Settings.class.getName());
        modIntent.putExtra(EXTRA_NO_HEADERS, true);
        return modIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings_name);
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();

        if(checkPermission()){
        }
        else {
            requestPermission();
        }
        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        imeManager.showInputMethodPicker();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ImePreferences.this, new String[]
                {WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, SYSTEM_ALERT_WINDOW
                }, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean WriteExternal = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadPhoneState = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean System = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if ( WriteExternal && ReadPhoneState && System) {
                    }
                    else {
                    }
                }
                break;
        }
    }
    public boolean checkPermission() {
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), SYSTEM_ALERT_WINDOW);
        return SecondPermissionResult == PackageManager.PERMISSION_GRANTED && FirstPermissionResult == PackageManager.PERMISSION_GRANTED && ThirdPermissionResult == PackageManager.PERMISSION_GRANTED ;
    }


//    private void SaveImage(Bitmap finalBitmap) {
//
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-"+ n +".jpg";
//        File file = new File (myDir, fname);
//        if (file.exists ()) file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected boolean isValidFragment(final String fragmentName) {
        return Settings.class.getName().equals(fragmentName);
    }

    public static class Settings extends InputMethodSettingsFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setInputMethodSettingsCategoryTitle(R.string.language_selection_title);
            setSubtypeEnablerTitle(R.string.select_language);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.ime_preferences);
        }
    }
}