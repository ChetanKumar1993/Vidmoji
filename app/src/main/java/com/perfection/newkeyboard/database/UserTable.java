package com.perfection.newkeyboard.database;

/**
 * Created by mayank on 27/9/17.
 */

public interface UserTable {

    String TABLE_NAME = "EmogiTable";


    String KEY_VIDEOID = "VideoID";
    String KEY_ID = "id";
    String KEY_VIDEOFILE = "VideoFileName";
    String KEY_THUMBFILE = "ThumbFileName";
    String KEY_TITLE = "Title";
    String KEY_CATEGORY = "Categories";
    String KEY_DESCRIPTION = "Description";
    String KEY_USERNAME = "UserName";
    String KEY_LOCAL_THUMB = "LocalThumbNail";
    String KEY_LOCAL_VIDEOFILE = "LocalVideo";
    String KEY_TYPE = "Type";
    String KEY_TAG = "Tag";

        /*create table*/


    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_VIDEOID + " TEXT   , "
            + KEY_ID + " TEXT  PRIMARY KEY , "
            + KEY_VIDEOFILE + " TEXT  , "
            + KEY_THUMBFILE + " TEXT  , " + KEY_TITLE + " TEXT  , "
            + KEY_CATEGORY + " TEXT , "
            + KEY_DESCRIPTION + " TEXT , "
            + KEY_LOCAL_THUMB + " TEXT , "
            + KEY_LOCAL_VIDEOFILE + " TEXT , "
            + KEY_TYPE + " TEXT , "
            + KEY_TAG + " TEXT , "
            + KEY_USERNAME + " TEXT" +
            ");";
}
