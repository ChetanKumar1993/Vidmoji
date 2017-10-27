package com.perfection.newkeyboard.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.Model.User;
import com.perfection.newkeyboard.Model.UserData;
import com.perfection.newkeyboard.R;
import com.perfection.newkeyboard.database.UserDao;
import com.perfection.newkeyboard.utils.PrefUtils;
import com.perfection.newkeyboard.utils.Utils;
import com.thin.downloadmanager.DefaultRetryPolicy;

import java.io.File;
import java.util.List;

/**
 * Created by Mayank on 10/2/2017.
 */

public class DownloadService extends Service {
    List<UserData.DataBeanX.DataBean> list;
    public DownloadManager dm;
    public File path = Environment.getDataDirectory();
    public String exPath = Environment.getExternalStorageDirectory().getPath();
    String downloadLoc = path.getAbsolutePath();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isExternalStorage = PrefUtils.getSharedPrefBoolean(this, PrefUtils.IS_EXTERNAL_STORAGE);

        if (isExternalStorage && !TextUtils.isEmpty(exPath))
            downloadLoc = exPath;
        else
            downloadLoc = exPath;

        list = UserDao.getInstance(this).queryAll();


        ThumbReceiver();
//        videoReceiver();
        downloadThumbs();
        downloadVideo();


        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void downloadVideo() {
        final String destination = downloadLoc + "/" + getResources().getString(R.string.app_name) + "/";
        Utils.deleteFile(downloadLoc + destination);
        for (int k = 0; k < list.size(); k++) {
            Uri uri = Uri.parse(list.get(k).getVideoFileName().replace(" ", "%20"));
            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);


            final String subPath = "media" + list.get(k).getVideoID() + Math.abs(list.get(k).getVideoFileName().hashCode()) + "." + Utils.getMimeType(this, uri);

            if (!new File(list.get(k).getLocalVideo().replace("file://", "")).exists()) {
                request.setDestinationInExternalPublicDir(destination, subPath);
                request.setDescription(list.get(k).getVideoID() + "");
                request.setTitle(k + "");
                request.setDescription("media");
                dm.enqueue(request);


                UserData.DataBeanX.DataBean dataBean = UserDao.getInstance(DownloadService.this).queryWithId(list.get(k));
                dataBean.setLocalVideo("file://" + downloadLoc + destination + subPath);
                UserDao.getInstance(DownloadService.this).insertOrReplace(dataBean);
            }
        }


        Utils.deleteFile(downloadLoc + destination);
    }

/*    public void videoReceiver() {

        BroadcastReceiver videoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {

                            String uriString = c
                                    .getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            int videoID = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                            int pos = c.getColumnIndex(DownloadManager.COLUMN_TITLE);

                            UserData.DataBeanX.DataBean dataBean = UserDao.getInstance(DownloadService.this).queryWithVideoId(list.get(pos));
                            dataBean.setLocalVideo(uriString);
                            UserDao.getInstance(DownloadService.this).insertOrReplace(dataBean);


                            Toast.makeText(DownloadService.this, "downloded", Toast.LENGTH_LONG).show();
                        }
                        if (DownloadManager.STATUS_FAILED == c.getInt(columnIndex)) {
                            Toast.makeText(DownloadService.this, "failed", Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }
        };

        registerReceiver(videoReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }*/


    public void downloadThumbs() {
        final String destination = downloadLoc + "/" + getResources().getString(R.string.app_name) + "/";
        Utils.deleteFile(downloadLoc + destination);

        for (int i = 0; i < list.size(); i++) {
            Uri uri = Uri.parse(list.get(i).getThumbFileName().replace(" ", "%20"));
            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);


            final String subPath = "thumb" + list.get(i).getVideoID() + Math.abs(list.get(i).getVideoFileName().hashCode()) + "." + Utils.getMimeType(this, uri);

            if (!new File(list.get(i).getLocalThumb().replace("file://", "")).exists()) {
                request.setDestinationInExternalPublicDir(destination, subPath);
                request.setTitle(i + "");
                request.setDescription("thumb");

                dm.enqueue(request);


                UserData.DataBeanX.DataBean dataBean = UserDao.getInstance(DownloadService.this).queryWithId(list.get(i));
                dataBean.setLocalThumb("file://" + downloadLoc + destination + subPath);
                UserDao.getInstance(DownloadService.this).insertOrReplace(dataBean);
            }


        }


    }

    public void ThumbReceiver() {
        BroadcastReceiver thumbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {

                            String thumb = c.getString(c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                            if (thumb.equalsIgnoreCase("thumb")) {
                                String uriString = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                int pos = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TITLE));

                                Log.d("uri string", uriString);


//                               Toast.makeText(DownloadService.this, "downloded", Toast.LENGTH_SHORT).show();
                            } else if (thumb.equalsIgnoreCase("media")) {
                                String uriString = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                int videoID = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                                int pos = c.getColumnIndex(DownloadManager.COLUMN_TITLE);


//                                Toast.makeText(DownloadService.this, "downloded", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (DownloadManager.STATUS_FAILED == c.getInt(columnIndex)) {

//                            Toast.makeText(DownloadService.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        registerReceiver(thumbReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

}
