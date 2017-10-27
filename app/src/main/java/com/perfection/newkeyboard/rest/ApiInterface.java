package com.perfection.newkeyboard.rest;

import com.google.gson.annotations.SerializedName;
import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.Model.AppSettingsRequest;
import com.perfection.newkeyboard.Model.InstagramRequest;
import com.perfection.newkeyboard.Model.ServerResult;
import com.perfection.newkeyboard.Model.SignInRequest;
import com.perfection.newkeyboard.Model.SignUpRequest;
import com.perfection.newkeyboard.Model.UserData;
import com.perfection.newkeyboard.Model.UserId;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>ApiInterface</H1>
 * <p>
 * <p>Represents the main interface that declares all the methods for API interaction</p>
 *
 * @author Divya Thakur
 * @version 1.0
 * @since 8/23/16
 */
public interface ApiInterface {


    @POST(Constants.LOGIN_URL)
    Call<UserData> performLogin(
            @Body SignInRequest signInRequest);

    @POST(Constants.SIGN_UP_URL)
    Call<ServerResult> signUp(@Body SignUpRequest signUpRequest);

    @POST(Constants.ADD_INSTAGRAM_INFO_URL)
    Call<ServerResult> addInstagramInfo(@Body InstagramRequest instagramRequest);

    @POST(Constants.SAVE_APP_SETTINGS)
    Call<ServerResult> saveAppSettings(@Body AppSettingsRequest appSettingsRequest);

    @POST(Constants.GET_INSTAGRAM_INFO_URL)
    Call<ServerResult> getInstagramInfo(@Body UserId userId);

    @POST(Constants.DELETE_INSTAGRAM_INFO_URL)
    Call<ServerResult> deleteInstagramUser(@Body InstagramRequest instagramRequest);

    @GET(Constants.GET_APP_SETTINGS)
    Call<ServerResult> getAppSettings(@Query("userid") int userid);


    @GET(Constants.Update_Num_Of_Times_Used_Vidoes_audios)
    Call<String> updateVideo(@Query("vid") int videoId);

    @GET(Constants.Update_Num_Of_Times_Used_Photos)
    Call<String> updatePic(@Query("pic") int videoId);

    /*@FormUrlEncoded
    @POST(Constants.LOGIN_URL)
    Call<ServerResult> performLogin(
            @Field(Constants.USERNAME) String username,
            @Field(Constants.PASSWORD) String password);

    @FormUrlEncoded
    @POST(Constants.SIGN_UP_URL)
    Call<ServerResult> signUp(
            @Field(Constants.SIGNUP_USER_NAME) String username,
            @Field(Constants.SIGNUP_PASSWORD) String password,
            @Field(Constants.EMAIL) String email,
            @Field(Constants.COUNTRY) String country,
            @Field(Constants.BIRTHDAY) String birthday,
            @Field(Constants.GENDER) String gender);*/
}
