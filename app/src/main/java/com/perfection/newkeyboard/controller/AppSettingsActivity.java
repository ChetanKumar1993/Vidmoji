package com.perfection.newkeyboard.controller;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.LoginActivity;
import com.perfection.newkeyboard.Model.AppSettingsRequest;
import com.perfection.newkeyboard.Model.AppSettingsResponse;
import com.perfection.newkeyboard.Model.InstagramRequest;
import com.perfection.newkeyboard.Model.ServerResult;
import com.perfection.newkeyboard.Model.UserId;
import com.perfection.newkeyboard.R;
import com.perfection.newkeyboard.SoftKeyboard;
import com.perfection.newkeyboard.YoutubeAcitivity;
import com.perfection.newkeyboard.controller.instagram.InstagramApp;
import com.perfection.newkeyboard.rest.ApiClientConnection;
import com.perfection.newkeyboard.rest.ApiInterface;
import com.perfection.newkeyboard.service.DownloadService;
import com.perfection.newkeyboard.service.EmojiGettingService;
import com.perfection.newkeyboard.utils.PrefUtils;
import com.perfection.newkeyboard.utils.Utils;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppSettingsActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private Button btnAuthenticateInstagram, btnSaveSettings, btnLogOut, btnUnlinkInstagram;
    private Switch switchAddVidmoji, switchEnableKeyboard, switchExternal, switchInternal;
    private Switch switchDwnldWiFi, switchDataConcious, switchDelVidmojis, switchUploadWiFi;
    private String dwnldOnWifi, uploadOnWifi, suggestOnWiFi, delOnUnlike, enableKeyboard, userId;
    private InstagramApp mApp;
    private String username, addFromInstagram, externalInternal;
    private ImageView ivIgImage;
    private TextView tvUserIGName, tvRefresh;
    private RelativeLayout rlIgDetails;
    private String TAG = "tag";
    private TextView tvTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        //this.initializeInstagram();
        this.getPrefValues();
        this.setWidgetReferences();
        this.setListenersOnWidgets();
        btnAuthenticateInstagram.setVisibility(View.VISIBLE);
        rlIgDetails.setVisibility(View.GONE);
        switchAddVidmoji.setEnabled(false);
        this.getAppSettings();
    }

    /**
     * Gets the Values from the Pref
     */
    private void getPrefValues() {
        userId = PrefUtils.getSharedPrefString(this, PrefUtils.USER_ID);
        username = PrefUtils.getSharedPrefString(this, PrefUtils.USER_NAME);
    }

    /**
     * Sets the References of the widgets using their IDs
     */
    private void setWidgetReferences() {

        btnAuthenticateInstagram = (Button) findViewById(R.id.btnAuthenticateInstagram);
        switchAddVidmoji = (Switch) findViewById(R.id.switchAddVidmoji);
        switchEnableKeyboard = (Switch) findViewById(R.id.switchEnableKeyboard);
        switchExternal = (Switch) findViewById(R.id.switchExternal);
        switchInternal = (Switch) findViewById(R.id.switchInternal);
        switchDwnldWiFi = (Switch) findViewById(R.id.switchDwnldWiFi);
        switchUploadWiFi = (Switch) findViewById(R.id.switchUploadWiFi);
        btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);

        switchDwnldWiFi = (Switch) findViewById(R.id.switchDwnldWiFi);
        switchDataConcious = (Switch) findViewById(R.id.switchDataConcious);
        switchDelVidmojis = (Switch) findViewById(R.id.switchDelVidmojis);

        tvTutorial = (TextView) findViewById(R.id.tvTutorial);

        ivIgImage = (ImageView) findViewById(R.id.ivIgImage);
        tvUserIGName = (TextView) findViewById(R.id.tvUserIGName);
        btnUnlinkInstagram = (Button) findViewById(R.id.btnUnlinkInstagram);
        rlIgDetails = (RelativeLayout) findViewById(R.id.rlIgDetails);
        tvRefresh = (TextView) findViewById(R.id.tvRefresh);
    }

    /**
     * Sets the various Listeners on the widgets
     */
    private void setListenersOnWidgets() {

        btnAuthenticateInstagram.setOnClickListener(this);
        btnSaveSettings.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnUnlinkInstagram.setOnClickListener(this);
        switchExternal.setOnCheckedChangeListener(this);
        switchInternal.setOnCheckedChangeListener(this);
        tvRefresh.setOnClickListener(this);
        tvTutorial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAuthenticateInstagram:
                this.initializeInstagram();
                this.connectOrDisconnectUser();
                break;
            case R.id.btnSaveSettings:
                this.getSettingsValue();
                if (checkForStoragePermissions())
                    this.saveAppSettings();
                break;
            case R.id.btnLogOut:
                mApp = new InstagramApp(this, Constants.CLIENT_ID,
                        Constants.CLIENT_SECRET, Constants.CALLBACK_URL);
                if (mApp.hasAccessToken())
                    mApp.resetAccessToken();

                PrefUtils.clearAll(this);
                Intent logoutIntent = new Intent(AppSettingsActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                this.finish();
                break;
            case R.id.btnUnlinkInstagram:
                mApp = new InstagramApp(this, Constants.CLIENT_ID,
                        Constants.CLIENT_SECRET, Constants.CALLBACK_URL);
                if (mApp.hasAccessToken())
                    mApp.resetAccessToken();
                this.deleteInstagramInfo();
                break;
            case R.id.tvRefresh:
                this.getAppSettings();
                break;
            case R.id.tvTutorial:
                playVideo();
                break;
        }
    }

    /**
     * Checks if the user is already logged into Instagram or not
     */
    private void connectOrDisconnectUser() {

        if (mApp != null) {
            if (mApp.hasAccessToken()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        AppSettingsActivity.this);
                builder.setMessage("Disconnect from Instagram?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mApp.resetAccessToken();
                                        btnAuthenticateInstagram.setText("Authenticate Instagram");
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                mApp.authorize();
            }
        } else {
            this.initializeInstagram();
            this.connectOrDisconnectUser();
        }
    }

    /**
     * Retrieves the values set by the user for the settings
     */
    private void getSettingsValue() {

        if (switchDwnldWiFi.isChecked())
            dwnldOnWifi = "1";
        else
            dwnldOnWifi = "0";

        if (switchUploadWiFi.isChecked())
            uploadOnWifi = "1";
        else
            uploadOnWifi = "0";

        if (switchDataConcious.isChecked())
            suggestOnWiFi = "1";
        else
            suggestOnWiFi = "0";

        if (switchDelVidmojis.isChecked())
            delOnUnlike = "1";
        else
            delOnUnlike = "0";

        if (switchEnableKeyboard.isChecked())
            enableKeyboard = "1";
        else
            enableKeyboard = "0";

        if (switchAddVidmoji.isChecked())
            addFromInstagram = "1";
        else
            addFromInstagram = "0";

        if (switchExternal.isChecked())
            externalInternal = "1";
        else if (switchInternal.isChecked())
            externalInternal = "0";
    }

    /**
     * Adds Instagram Info to the existing user's account
     */
    private void addInstagramInfo(String userId, String accessToken, String igUserId,
                                  String igUsername, final String profilePicture) {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                Utils.hideSoftKeyboard(this);
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Linking...");

                service = ApiClientConnection.getInstance().createService();

                InstagramRequest instagramRequest = new InstagramRequest();
                instagramRequest.userId = userId;
                instagramRequest.accessToken = accessToken;
                instagramRequest.instagramUserId = igUserId;
                instagramRequest.instagramUsername = igUsername;
                instagramRequest.profilePic = profilePicture;
                Call<ServerResult> call = service.addInstagramInfo(instagramRequest);

                call.enqueue(new Callback<ServerResult>() {
                    @Override
                    public void onResponse(Call<ServerResult> call,
                                           Response<ServerResult> response) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if (apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Failure : -- " + response.body());
                                    Toast.makeText(AppSettingsActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Success : -- " + apiResponse);
                                    Toast.makeText(AppSettingsActivity.this,
                                            apiResponse.message, Toast.LENGTH_SHORT).show();
                                    AppSettingsActivity.this.getInstagramInfo(username, profilePicture);
                                }
                            } else {
                                Utils.printLogs("AppSettingsActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("AppSettingsActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the App Settings to the user's account
     */
    private void saveAppSettings() {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Saving...");

                service = ApiClientConnection.getInstance().createService();

                AppSettingsRequest appSettingsRequest = new AppSettingsRequest();
                appSettingsRequest.userId = Integer.parseInt(userId);
                appSettingsRequest.dwnldOnWifi = Integer.parseInt(dwnldOnWifi);
                appSettingsRequest.uploadOnWifi = Integer.parseInt(uploadOnWifi);
                appSettingsRequest.suggestOnWifi = Integer.parseInt(suggestOnWiFi);
                appSettingsRequest.autoDeleteUnlike = Integer.parseInt(delOnUnlike);
                appSettingsRequest.enableKeyboard = Integer.parseInt(enableKeyboard);
                appSettingsRequest.addFromInstagram = Integer.parseInt(addFromInstagram);
                if (externalInternal != null)
                    appSettingsRequest.externalInternal = Integer.parseInt(externalInternal);
                appSettingsRequest.username = username;
                Call<ServerResult> call = service.saveAppSettings(appSettingsRequest);

                call.enqueue(new Callback<ServerResult>() {
                    @Override
                    public void onResponse(Call<ServerResult> call,
                                           Response<ServerResult> response) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if (apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Failure : -- " + response.body());
                                    Toast.makeText(AppSettingsActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {


                                    PrefUtils.setSharedPrefBooleanData(AppSettingsActivity.this, PrefUtils.DOWNLOAD_ON_WIFI, switchDwnldWiFi.isChecked());
                                    PrefUtils.setSharedPrefBooleanData(AppSettingsActivity.this, PrefUtils.SUGGEST_VIDMOGI, switchDataConcious.isChecked());
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Success : -- " + apiResponse);
                                    Toast.makeText(AppSettingsActivity.this,
                                            apiResponse.message, Toast.LENGTH_SHORT).show();
                                }
                                callDownloadService();

                            } else {
                                Utils.printLogs("AppSettingsActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("AppSettingsActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callDownloadService() {
        boolean isExternalStorage;
        isExternalStorage = switchExternal.isChecked();
        PrefUtils.setSharedPrefBooleanData(this, PrefUtils.IS_SETTING_WIFI, switchDwnldWiFi.isChecked());
        PrefUtils.setSharedPrefBooleanData(this, PrefUtils.IS_SETTING_WIFI, switchExternal.isChecked());

        Intent intent = new Intent(this, EmojiGettingService.class);
        intent.putExtra(Constants.EXTRA_ISEXTERNAL, isExternalStorage);
        startService(intent);
    }

    /**
     * To get the user info from the Instagram session
     */
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Utils.printLogs("LoginAct", mApp.getTOken());
                Utils.printLogs("LoginAct", mApp.getId());
                Utils.printLogs("LoginAct", mApp.getUserName());
                Utils.printLogs("LoginAct", mApp.getName());
                Utils.printLogs("AppSettings", mApp.getTagProfilePicture());
                AppSettingsActivity.this.addInstagramInfo(userId, mApp.getTOken(), mApp.getId(),
                        mApp.getUserName(), mApp.getTagProfilePicture());
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(AppSettingsActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    /**
     * Sets profile data such as profile picture and username
     */
    private void setProfileDetails(String username, String profilePicture) {

        if (username != null && username.length() > 0) {
            rlIgDetails.setVisibility(View.VISIBLE);
            btnAuthenticateInstagram.setVisibility(View.GONE);
            tvUserIGName.setText(username);
            tvUserIGName.setVisibility(View.VISIBLE);
            switchAddVidmoji.setEnabled(true);
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions displayImageOptions = Utils.getDisplayImageOptions();

        if (profilePicture != null && profilePicture.length() > 0) { // && !profilePicture
            //.equals("https://ig-s-a-a.akamaihd.net/h-ak-igx/t51.2885-19/11906329_960233084022564_1448528159_a.jpg")) {
            imageLoader.displayImage(profilePicture, ivIgImage, displayImageOptions,
                    new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1,
                                                      Bitmap bitmap) {
                            ((ImageView) arg1).setImageBitmap(bitmap);
                            ivIgImage.setVisibility(View.VISIBLE);
                            if (bitmap == null)
                                ivIgImage.setImageDrawable(AppSettingsActivity.this.
                                        getResources().getDrawable(R.drawable.instagram));
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                        }
                    });
        } else {
            ivIgImage.setImageDrawable(AppSettingsActivity.this.
                    getResources().getDrawable(R.drawable.instagram));
        }
    }

    /**
     * Initializes Instagram Setup
     */
    private void initializeInstagram() {

        mApp = new InstagramApp(this, Constants.CLIENT_ID,
                Constants.CLIENT_SECRET, Constants.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(AppSettingsActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        if (mApp.hasAccessToken()) {
            // tvSummary.setText("Connected as " + mApp.getUserName());
            mApp.fetchUserName(handler);
        }
    }

    /**
     * Retrieves Instagram Info from the server
     */
    private void getInstagramInfo(final String username, final String profilePicture) {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Please wait...");

                service = ApiClientConnection.getInstance().createService();

                UserId instagramRequest = new UserId();
                instagramRequest.userid = userId;
                Call<ServerResult> call = service.getInstagramInfo(instagramRequest);

                call.enqueue(new Callback<ServerResult>() {
                    @Override
                    public void onResponse(Call<ServerResult> call,
                                           Response<ServerResult> response) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if (apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Failure : -- " + response.body());
                                    Toast.makeText(AppSettingsActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Success : -- " + apiResponse);
                                    rlIgDetails.setVisibility(View.VISIBLE);
                                    btnAuthenticateInstagram.setVisibility(View.GONE);
                                    switchAddVidmoji.setEnabled(true);
                                    AppSettingsActivity.this.setProfileDetails(
                                            apiResponse.data.appSettingsResponses[0].instagramUsername,
                                            apiResponse.data.appSettingsResponses[0].instagramProfilePic);
                                }
                            } else {
                                Utils.printLogs("AppSettingsActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("AppSettingsActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes Instagram Info from the server
     */
    private void deleteInstagramInfo() {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Unlinking...");

                service = ApiClientConnection.getInstance().createService();

                InstagramRequest instagramRequest = new InstagramRequest();
                instagramRequest.userId = userId;
                Call<ServerResult> call = service.deleteInstagramUser(instagramRequest);

                call.enqueue(new Callback<ServerResult>() {
                    @Override
                    public void onResponse(Call<ServerResult> call,
                                           Response<ServerResult> response) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if (apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Failure : -- " + response.body());
                                    Toast.makeText(AppSettingsActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Success : -- " + apiResponse);
                                    Toast.makeText(AppSettingsActivity.this,
                                            apiResponse.message, Toast.LENGTH_SHORT).show();
                                    rlIgDetails.setVisibility(View.GONE);
                                    btnAuthenticateInstagram.setVisibility(View.VISIBLE);
                                    switchAddVidmoji.setEnabled(false);
                                }
                            } else {
                                Utils.printLogs("AppSettingsActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("AppSettingsActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.switchExternal) {
            if (isChecked)
                switchInternal.setChecked(false);
            else
                switchInternal.setChecked(true);
        } else if (buttonView.getId() == R.id.switchInternal) {
            if (isChecked)
                switchExternal.setChecked(false);
            else
                switchExternal.setChecked(true);
        }
    }

    /**
     * Retrieves App Settings values from the server
     */
    private void getAppSettings() {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Please wait...");

                service = ApiClientConnection.getInstance().createService();

                /*UserId instagramRequest = new UserId();
                instagramRequest.userid = userId;*/
                Call<ServerResult> call = service.getAppSettings(Integer.parseInt(userId));

                call.enqueue(new Callback<ServerResult>() {
                    @Override
                    public void onResponse(Call<ServerResult> call,
                                           Response<ServerResult> response) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if (apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Failure : -- " + response.body());
                                    Toast.makeText(AppSettingsActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.printLogs("AppSettingsActivity",
                                            "onResponse : Success : -- " + apiResponse);

                                    AppSettingsActivity.this.setAppSettingsValues(
                                            apiResponse.data.appSettingsResponses[0]);
                                }
                            } else {
                                Utils.printLogs("AppSettingsActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("AppSettingsActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(AppSettingsActivity.this, "Some error occurred.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets App Settings values fetched from the server
     *
     * @param appSettingsResponse Holds the AppSettings Response object
     */
    private void setAppSettingsValues(AppSettingsResponse appSettingsResponse) {

        if (appSettingsResponse != null) {
            switchDwnldWiFi.setChecked(appSettingsResponse.downloadOnlyOnWifi);
            switchUploadWiFi.setChecked(appSettingsResponse.uploadOnlyOnWifi);
            switchDataConcious.setChecked(appSettingsResponse.suggestOnlyOnWifi);
            switchDelVidmojis.setChecked(appSettingsResponse.automaticDeleteOnUnlike);
            switchEnableKeyboard.setChecked(appSettingsResponse.enableKeyboard);
            switchAddVidmoji.setChecked(appSettingsResponse.addFromInstagram);

            PrefUtils.setSharedPrefBooleanData(this, PrefUtils.DOWNLOAD_ON_WIFI, appSettingsResponse.downloadOnlyOnWifi);
            PrefUtils.setSharedPrefBooleanData(this, PrefUtils.SUGGEST_VIDMOGI, appSettingsResponse.suggestOnlyOnWifi);
            if (appSettingsResponse.externalInternal == true) {
                switchExternal.setChecked(true);
            } else {
                switchInternal.setChecked(true);
            }
        }

        this.setProfileDetails(appSettingsResponse.instagramUsername,
                appSettingsResponse.instagramProfilePic);
    }


    private boolean checkForStoragePermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
        /*int permissionCheck = ContextCompat.checkSelfPermission(LandingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }


    public void playVideo() {

        startActivity(new Intent(this, YoutubeAcitivity.class));

    }


}
