package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;
import com.perfection.newkeyboard.Helpers.Constants;

/**
 * Created by Divya Thakur on 22-08-2017.
 */

public class ServerResult {

    @SerializedName(Constants.STATUS)
    public String status;
    @SerializedName(Constants.MESSAGE)
    public String message;

    @SerializedName("data")
    public Data data;
}
