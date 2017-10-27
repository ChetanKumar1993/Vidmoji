package com.perfection.newkeyboard.controller.application;

import android.app.Application;
import android.content.Context;
/*
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
*/

import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * <H1>Basket Business</H1>
 * <H1>BasketBusinessApplication</H1>
 *
 * <p>Represents the whole application</p>
 *
 * @author Divya Thakur
 * @since 6/18/16
 * @version 1.0
 */
public class VidmojiApplication extends Application {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {

        try {

            super.onCreate();
            Stetho.initializeWithDefaults(this);

            // Image Loader Setup
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getApplicationContext())
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .build();

            ImageLoader.getInstance().init(config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
}
