package com.perfection.newkeyboard.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.LoginActivity;
import com.perfection.newkeyboard.Model.SignInRequest;
import com.perfection.newkeyboard.Model.User;
import com.perfection.newkeyboard.Model.UserData;
import com.perfection.newkeyboard.controller.AppSettingsActivity;
import com.perfection.newkeyboard.database.UserDao;
import com.perfection.newkeyboard.rest.ApiClientConnection;
import com.perfection.newkeyboard.rest.ApiInterface;
import com.perfection.newkeyboard.utils.PrefUtils;
import com.perfection.newkeyboard.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mayank on 10/14/2017.
 */

public class EmojiGettingService extends Service {

    Handler delayhandler = new Handler();
    public static final int TIME_DELAY = 1000 * 60 * 5;// 5 minutes
    Runnable run;
    private Timer mTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        callTimer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }


/*    public void getEmogi() {

        run = new Runnable() {
            @Override
            public void run() {
                loop();

            }
        };
        delayhandler.postDelayed(run, TIME_DELAY);
    }


    void loop() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        PrefUtils.setSharedPrefBooleanData(this, PrefUtils.WIFI_ENABLE, wifi.isWifiEnabled());

        if (!TextUtils.isEmpty(PrefUtils.getSharedPrefString(EmojiGettingService.this, PrefUtils.PASSWORD))) {
            if (PrefUtils.getSharedPrefBoolean(this, PrefUtils.IS_SETTING_WIFI) && PrefUtils.getSharedPrefBoolean(this, PrefUtils.WIFI_ENABLE))
                performLogin();
            else
                getEmogi();

            if (!PrefUtils.getSharedPrefBoolean(this, PrefUtils.IS_SETTING_WIFI))
                performLogin();
            else
                getEmogi();


        } else
            getEmogi();

    }*/

    public void callPerformLogin() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        PrefUtils.setSharedPrefBooleanData(this, PrefUtils.WIFI_ENABLE, wifi.isWifiEnabled());

        if (!TextUtils.isEmpty(PrefUtils.getSharedPrefString(EmojiGettingService.this, PrefUtils.PASSWORD))) {
            if (PrefUtils.getSharedPrefBoolean(this, PrefUtils.IS_SETTING_WIFI) && PrefUtils.getSharedPrefBoolean(this, PrefUtils.WIFI_ENABLE))
                performLogin();

            if (!PrefUtils.getSharedPrefBoolean(this, PrefUtils.IS_SETTING_WIFI))
                performLogin();
        }
    }


    /**
     * Performs Login Action
     */

    private void performLogin() {
        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {

                service = ApiClientConnection.getInstance().createService();

                SignInRequest signInRequest = new SignInRequest();
                signInRequest.username = PrefUtils.getSharedPrefString(this, PrefUtils.USER_NAME);
                signInRequest.password = PrefUtils.getSharedPrefString(this, PrefUtils.PASSWORD);
                signInRequest.loginType = Constants.KEYBOARD;
                Call<UserData> call = service.performLogin(signInRequest);

                call.enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call,
                                           Response<UserData> response) {
                        try {
                            if (response.isSuccessful()) {
                                UserData apiResponse = response.body();
                                if (apiResponse != null && apiResponse.getData().getStatus() != null &&
                                        apiResponse.getData().getStatus().equals("error")) {
                                    Utils.printLogs("LoginActivity", "onResponse : Failure : -- " +
                                            response.body());
                           /*         Toast.makeText(LoginActivity.this, apiResponse.getData(,
                                            Toast.LENGTH_SHORT).show();*/
                                } else {
                                    Utils.printLogs("LoginActivity", apiResponse.getData().getUserid());
                                    saveUserDataInDb(response.body());


                                }
                            } else {
                                Utils.printLogs("LoginActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(EmojiGettingService.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
                        Utils.printLogs("LoginActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(EmojiGettingService.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveUserDataInDb(UserData userData) {
        if (userData != null && userData.getData() != null && userData.getData().getData() != null) {

            List<UserData.DataBeanX.DataBean> deletableList = new ArrayList<>();
            List<UserData.DataBeanX.DataBean> dataBeanList = UserDao.getInstance(this).queryAll();

            boolean shouldDownload = false;

            for (int i = 0; i < userData.getData().getData().size(); i++) {
                boolean isSame = false;


                for (int k = 0; k < dataBeanList.size(); k++) {

                    // for first time
                    if (dataBeanList.get(k).getLocalVideo().isEmpty()) {
                        shouldDownload = true;
                    } else if (!new File(dataBeanList.get(k).getLocalVideo().replace("file://", "")).exists()) {
                        shouldDownload = true;
                    }


                    if ((dataBeanList.get(k).getVideoFileName()).equalsIgnoreCase(userData.getData().getData().get(i).getVideoFileName())) {

                        isSame = true;
                        break;
                    } else {
                        isSame = false;

                    }


                }

                if (!isSame) {
                    UserDao.getInstance(this).insertOrReplace(userData.getData().getData().get(i));
                    shouldDownload = true;

                }


            }


            if (shouldDownload) {
                if (PrefUtils.getSharedPrefBoolean(this, PrefUtils.DOWNLOAD_ON_WIFI)) {
                    Intent intent = new Intent(this, DownloadService.class);
                    startService(intent);
                }
            }

            // delete video from db

            for (int i = 0; i < dataBeanList.size(); i++) {
                boolean isDeletable = false;
                for (int k = 0; k < userData.getData().getData().size(); k++) {


                    if (dataBeanList.get(i).getVideoFileName().hashCode() == userData.getData().getData().get(k).getVideoFileName().hashCode()) {
                        isDeletable = false;
                        break;
                    } else {
                        isDeletable = true;
                    }
                }

                if (isDeletable) {
                    UserDao.getInstance(this).deleteRow(dataBeanList.get(i).getVideoFileName().hashCode() + "");

                }

            }

        }


    }


    public void callTimer() {

        mTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                callPerformLogin();

            }
        };


        mTimer.schedule(doAsynchronousTask, 0, TIME_DELAY);
    }
}
