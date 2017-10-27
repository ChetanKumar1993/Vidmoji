package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Divya Thakur on 05-09-2017.
 */

public class User {

    @SerializedName(value = "UserID", alternate = "userid")
    public String userId;
    @SerializedName("UserName")
    public String username;
}
