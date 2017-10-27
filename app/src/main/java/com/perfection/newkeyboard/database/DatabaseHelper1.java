package com.perfection.newkeyboard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper1 extends SQLiteOpenHelper {

    private static String TAG = "DataBaseHelper";
    public static final String DBNAME = "vidEmogi";
    //    public static final String DBLOCATION = "/data/data/com.perfection.newkeyboard/databases/";
    public static String DBLOCATION = "";
    private static DatabaseHelper1 mInstance;
    private Context mContext;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper1(Context context) {
        super(context, DBNAME, null, 5);
        this.mContext = context;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DBLOCATION = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DBLOCATION = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }


    public static synchronized DatabaseHelper1 getInstance(Context context) {
        if (mInstance == null)
            mInstance = new DatabaseHelper1(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDao.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase() throws IOException {
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
//        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

//    public void closeDatabase(){
//        if(mDatabase!=null){
//            mDatabase.close();
//        }
//    }

    @Override
    public synchronized void close() {
        if (mDatabase != null)
            mDatabase.close();
        super.close();
    }

    //Create a empty database on the system
    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();
//        if(dbExist) {
//            Log.d("E", "Database exists.");
//                try {
//                    copyDataBase();
//                } catch (IOException e) {
//                    Log.e("Error", e.getMessage());
//                }
//            } else {
//            try {
//                copyDataBase();
//            } catch (IOException e) {
//                Log.e("Error1", e.getMessage());
//            }
//        }

        if (!dbExist) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }

    }

    //Check database already exist or not
    private boolean checkDataBase() {
//        boolean checkDB = false;
        File dbfile = null;
        try {
            String myPath = DBLOCATION + DBNAME;
            dbfile = new File(myPath);
//            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
//        return checkDB;
        return dbfile.exists();
    }

    private void copyDataBase() throws IOException {

        InputStream mInput = mContext.getAssets().open(DBNAME);
        String outFileName = DBLOCATION + DBNAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

}
