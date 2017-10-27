package com.perfection.newkeyboard.rest;

import com.perfection.newkeyboard.Helpers.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>ApiInterface</H1>
 *
 * <p>Represents the main interface that declares all the methods for API interaction</p>
 *
 * @author Divya Thakur
 * @version 1.0
 * @since 8/23/17
 */
public class ApiClientConnection {

    private static ApiClientConnection connect;

    public static synchronized ApiClientConnection getInstance() {

        if(connect == null) {
            connect = new ApiClientConnection();
        }
        return connect;
    }


    private ApiInterface clientService;

    public ApiInterface createService() throws Exception {

        if(clientService == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.connectTimeout(30, TimeUnit.SECONDS);

            // add logging as last interceptor
            httpClient.addInterceptor(logging);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            clientService = retrofit.create(ApiInterface.class);
        }
        return clientService;
    }
}
