package com.perfection.newkeyboard.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.perfection.newkeyboard.Model.UserData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayank on 27/9/17.
 */

public class UserDao implements UserTable {

    SQLiteDatabase mSqLiteDatabaseWritable, mSqLiteDatabaseReadable;

    private static UserDao mInstance;
    SQLiteDatabase localSQLiteDatabase = null;
    private Context mContext = null;


    private UserDao(Context paramContext) {
        this.mContext = paramContext;
        localSQLiteDatabase = new DatabaseHelper1(paramContext).getWritableDatabase();
    }

    public static synchronized UserDao getInstance(Context context) {
        if (mInstance == null)
            mInstance = new UserDao(context);
        return mInstance;
    }


    /*insert user data in table*/
    private void insert(UserData.DataBeanX.DataBean entity) {
        if (mSqLiteDatabaseWritable == null) {
            mSqLiteDatabaseWritable = DatabaseHelper1.getInstance(mContext).getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put(KEY_ID, entity.getVideoFileName().hashCode() + "");
        values.put(KEY_CATEGORY, entity.getCategories() + "");
        values.put(KEY_DESCRIPTION, entity.getDescription() + "");
        values.put(KEY_THUMBFILE, entity.getThumbFileName() + "");
        values.put(KEY_TITLE, entity.getTitle().toLowerCase().trim() + "");
        values.put(KEY_VIDEOFILE, entity.getVideoFileName() + "");

        values.put(KEY_VIDEOID, entity.getVideoID() + "");
        values.put(KEY_USERNAME, entity.getUserName() + "");
        values.put(KEY_LOCAL_THUMB, entity.getLocalThumb() + "");
        values.put(KEY_LOCAL_VIDEOFILE, entity.getLocalVideo() + "");
        values.put(KEY_TYPE, entity.getType());
        values.put(KEY_TAG, entity.getTags());


        // Inserting Row
        mSqLiteDatabaseWritable.insert(UserTable.TABLE_NAME, null, values);
    }

    /*update user data*/
    public void update(UserData.DataBeanX.DataBean entity) {
        if (mSqLiteDatabaseWritable == null) {
            mSqLiteDatabaseWritable = DatabaseHelper1.getInstance(mContext).getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        if (entity != null) {
            values.put(KEY_ID, entity.getVideoFileName().hashCode() + "");
            values.put(KEY_CATEGORY, entity.getCategories() + "");
            values.put(KEY_DESCRIPTION, entity.getDescription() + "");
            values.put(KEY_THUMBFILE, entity.getThumbFileName() + "");
            values.put(KEY_TITLE, entity.getTitle().toLowerCase().trim() + "");
            values.put(KEY_VIDEOFILE, entity.getVideoFileName() + "");

            values.put(KEY_VIDEOID, entity.getVideoID() + "");
            values.put(KEY_USERNAME, entity.getUserName() + "");
            values.put(KEY_LOCAL_THUMB, entity.getLocalThumb());
            values.put(KEY_LOCAL_VIDEOFILE, entity.getLocalVideo());
            values.put(KEY_TYPE, entity.getType());
            values.put(KEY_TAG, entity.getTags());

        }

        // Updating Row
        mSqLiteDatabaseWritable.update(TABLE_NAME, values, KEY_ID + " = ?",

                new String[]{entity.getVideoFileName().hashCode() + ""});
    }


    public void insertOrReplace(UserData.DataBeanX.DataBean entity) {

        if (mSqLiteDatabaseWritable == null) {
            mSqLiteDatabaseWritable = DatabaseHelper1.getInstance(mContext).getWritableDatabase();
        }

        if (queryWithId(entity) == null) {
            insert(entity);
        } else {
            update(entity);
        }
    }

    public UserData.DataBeanX.DataBean queryWithId(UserData.DataBeanX.DataBean entity) {
        if (mSqLiteDatabaseReadable == null) {
            mSqLiteDatabaseReadable = DatabaseHelper1.getInstance(mContext).getReadableDatabase();
        }

        UserData.DataBeanX.DataBean userData = null;
        Cursor cursor = mSqLiteDatabaseReadable.query(TABLE_NAME, null,
                KEY_ID + " = ?", new String[]{entity.getVideoFileName().hashCode() + ""}, null, null, null, null);
        if (cursor != null && cursor.getCount() >= 1) {
            cursor.moveToFirst();
            userData = getUserFromCursor(cursor);
        }
        cursor.close();
        return userData;
    }

    /* get user from cursor*/
    private UserData.DataBeanX.DataBean getUserFromCursor(Cursor cursor) {
        UserData.DataBeanX.DataBean bean = new UserData.DataBeanX.DataBean();
        bean.setCategories(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
        bean.setVideoID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_VIDEOID))));
        bean.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        bean.setThumbFileName(cursor.getString(cursor.getColumnIndex(KEY_THUMBFILE)));
        bean.setVideoFileName(cursor.getString(cursor.getColumnIndex(KEY_VIDEOFILE)));
        bean.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        bean.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
        bean.setLocalThumb(cursor.getString(cursor.getColumnIndex(KEY_LOCAL_THUMB)));
        bean.setLocalVideo(cursor.getString(cursor.getColumnIndex(KEY_LOCAL_VIDEOFILE)));
        bean.setType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_TYPE))));
        bean.setTags(cursor.getString(cursor.getColumnIndex(KEY_TAG)));

        return bean;
    }

    /*query all products*/
    public List<UserData.DataBeanX.DataBean> queryAll() {
        if (mSqLiteDatabaseReadable == null) {
            mSqLiteDatabaseReadable = DatabaseHelper1.getInstance(mContext).getReadableDatabase();
        }
        List<UserData.DataBeanX.DataBean> user = new ArrayList<>();
        Cursor cursor = mSqLiteDatabaseReadable.query(TABLE_NAME, null, null, null,
                null, null, null, null);
        while (cursor == null)
            ;
        if (cursor != null)
            while (cursor.moveToNext()) {
                user.add(getUserFromCursor(cursor));

            }
        return user;
    }

//    cur = db.rawQuery("SELECT * FROM testable WHERE Tags LIKE '%"+word+"%'", null);

    public List<UserData.DataBeanX.DataBean> queryOnWord(String word) {
        if (!TextUtils.isEmpty(word))
            word = word.toLowerCase().trim();
        if (mSqLiteDatabaseReadable == null) {
            mSqLiteDatabaseReadable = DatabaseHelper1.getInstance(mContext).getReadableDatabase();
        }
        List<UserData.DataBeanX.DataBean> user = new ArrayList<>();
        Cursor cur = mSqLiteDatabaseReadable.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_TITLE + " LIKE '%" + word + "%'", null);
        while (cur == null)
            ;
        if (cur != null)
            while (cur.moveToNext()) {
                user.add(getUserFromCursor(cur));

            }
        return user;
    }


    public boolean deleteRow(String id) {
        if (mSqLiteDatabaseWritable == null) {
            mSqLiteDatabaseWritable = DatabaseHelper1.getInstance(mContext).getWritableDatabase();
        }

        return mSqLiteDatabaseWritable.delete(TABLE_NAME, KEY_ID + " =" + "\"" + id + "\"", null) > 0;

    }

}
