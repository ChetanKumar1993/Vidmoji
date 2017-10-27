package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Divya Thakur on 15-09-2017.
 */

public class AppSettingsResponse implements Serializable {

    @SerializedName("downloadOnlyOnWifi")
    public boolean downloadOnlyOnWifi;

    @SerializedName("uploadOnlyOnWifi")
    public boolean uploadOnlyOnWifi;

    @SerializedName("suggestOnlyOnWifi")
    public boolean suggestOnlyOnWifi;

    @SerializedName("automaticDeleteOnUnlike")
    public boolean automaticDeleteOnUnlike;

    @SerializedName("enableKeyboard")
    public boolean enableKeyboard;

    @SerializedName("externalInternal")
    public boolean externalInternal;

    @SerializedName("addFromInstagram")
    public boolean addFromInstagram;

    @SerializedName("ProfilePic")
    public String instagramProfilePic;

    @SerializedName("InstagramUsername")
    public String instagramUsername;

    @SerializedName("InstagramUserID")
    public String instagramUserId;
}
