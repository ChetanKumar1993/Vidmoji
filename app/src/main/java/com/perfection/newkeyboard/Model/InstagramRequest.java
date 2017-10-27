package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;
import com.perfection.newkeyboard.Helpers.Constants;

import java.io.Serializable;

/**
 * Created by Divya Thakur on 04-09-2017.
 */

public class InstagramRequest implements Serializable{

    @SerializedName(value = Constants.USERID, alternate = "userid")
    public String userId;
    @SerializedName(Constants.INSTAGRAMUSERID)
    public String instagramUserId;
    @SerializedName(Constants.INSTAGRAMUSERNAME)
    public String instagramUsername;
    @SerializedName(Constants.ACCESSTOKEN)
    public String accessToken;
    @SerializedName("profilepic")
    public String profilePic;
}
