package com.perfection.newkeyboard.utils;

/**
 * Created by mayank on 3/10/17.
 */

import android.graphics.Bitmap;

public interface ImageLoadedCallback {

    Bitmap imageSuccessfullyLoaded(Bitmap bitmap,int i);
}