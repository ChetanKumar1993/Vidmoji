package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Divya Thakur on 05-09-2017.
 */

public class Data {

    @SerializedName("Data")
    public User[] users;

    @SerializedName("AppSettings")
    public AppSettingsResponse[] appSettingsResponses;

    @SerializedName("Userid")
    public String userId;
}
