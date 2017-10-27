package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;
import com.perfection.newkeyboard.Helpers.Constants;

/**
 * Created by mrhic on 8/27/2017.
 */

public class AppSettingsRequest {

    @SerializedName(value = Constants.USERID, alternate = "userid")
    public int userId;

    @SerializedName("username")
    public String username;

    //@SerializedName(Constants.IS_DOWNLOAD_WIFI)
    @SerializedName("DOWNLOADONLYONWIFI")
    public int dwnldOnWifi;

    //@SerializedName(Constants.IS_UPLOAD_WIFI)
    @SerializedName("UPLOADONLYONWIFI")
    public int uploadOnWifi;

    //@SerializedName(Constants.SUGGEST_ON_WIFI)
    @SerializedName("SUGGESTONLYONWIFI")
    public int suggestOnWifi;

    //@SerializedName(Constants.AUTODELETE_UNLIKE)
    @SerializedName("AUTOMATICDELETEONUNLIKE")
    public int autoDeleteUnlike;

    //@SerializedName(Constants.ENABLE_KEYBOARD)
    @SerializedName("ENABLEKEYBOARD")
    public int enableKeyboard;

    //@SerializedName(Constants.ADD_FROM_INSTAGRAM)
    @SerializedName("ADDFROMINSTAGRAM")
    public int addFromInstagram;

    //@SerializedName(Constants.EXTERNAL_INTERNAL)
    @SerializedName("EXTERNALINTERNAL")
    public int externalInternal;
}
