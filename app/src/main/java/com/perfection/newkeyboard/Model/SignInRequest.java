package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;
import com.perfection.newkeyboard.Helpers.Constants;

import retrofit2.http.Field;

/**
 * Created by Divya Thakur on 25-08-2017.
 */

public class SignInRequest {

    @SerializedName(Constants.USERNAME)
    public String username;

    @SerializedName(Constants.PASSWORD)
    public String password;

    @SerializedName(Constants.LoginType)
    public String loginType;
}
