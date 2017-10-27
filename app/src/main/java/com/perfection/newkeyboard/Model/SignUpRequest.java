package com.perfection.newkeyboard.Model;

import com.google.gson.annotations.SerializedName;
import com.perfection.newkeyboard.Helpers.Constants;

/**
 * Created by Divya Thakur on 25-08-2017.
 */

public class SignUpRequest {

    @SerializedName(Constants.EMAIL)
    public String email;
    @SerializedName(Constants.SIGNUP_PASSWORD)
    public String password;
    @SerializedName(Constants.SIGNUP_USER_NAME)
    public String username;
    @SerializedName(Constants.BIRTHDAY)
    public String birthdate;
    @SerializedName(Constants.GENDER)
    public String gender;
    @SerializedName(Constants.COUNTRY)
    public String country;
    @SerializedName(Constants.LoginType)
    public String loginType;
}
