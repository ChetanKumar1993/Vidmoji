package com.perfection.newkeyboard;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.content.Intent;
import android.widget.Toast;

import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.Model.InstagramRequest;
import com.perfection.newkeyboard.Model.ServerResult;
import com.perfection.newkeyboard.Model.SignInRequest;
import com.perfection.newkeyboard.Model.UserData;
import com.perfection.newkeyboard.controller.AppSettingsActivity;
import com.perfection.newkeyboard.controller.instagram.InstagramApp;
import com.perfection.newkeyboard.database.UserDao;
import com.perfection.newkeyboard.rest.ApiClientConnection;
import com.perfection.newkeyboard.rest.ApiInterface;
import com.perfection.newkeyboard.service.DownloadService;
import com.perfection.newkeyboard.utils.PrefUtils;
import com.perfection.newkeyboard.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
        OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // UserViewModel ViewModel => BindingContext as UserViewModel;
    //UserViewModel ViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    private boolean isLogin;


    private InstagramApp mApp;
    private Button btnInstagram;
    private String username, password;
    private String TAG = getClass().getSimpleName();

    /**
     * To get the user info from the Instagram session
     */
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Utils.printLogs("LoginAct", mApp.getTOken());
                Utils.printLogs("LoginAct", mApp.getId());
                Utils.printLogs("LoginAct", mApp.getUserName());
                Utils.printLogs("LoginAct", mApp.getName());
                LoginActivity.this.addInstagramInfo("1", mApp.getTOken(), mApp.getId(),
                        mApp.getUserName());
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(LoginActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_login);
        this.checkForStoragePermissions();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.userNameEntry);

        mPasswordView = (EditText) findViewById(R.id.userPassEntry);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        EditText mSignUpButton = (EditText) findViewById(R.id.signupButton);
        /*mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));

            }
        });*/

        mSignUpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    Intent mInHome = new Intent(LoginActivity.this, signUpActivity.class);
                    LoginActivity.this.startActivity(mInHome);
                    LoginActivity.this.finish();
                }

                return true; // return is important...
            }
        });

        //mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);
        btnInstagram = (Button) findViewById(R.id.btnInstagram);
        this.initializeInstagram();
        btnInstagram.setOnClickListener(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (checkInputValue()) {
            isLogin = true;
            Intent mInHome = new Intent(LoginActivity.this, KeyboardSettings.class);
            LoginActivity.this.startActivity(mInHome);
            LoginActivity.this.finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private boolean checkInputValue() {
        {
            if (mAuthTask != null) {
                return false;
            }

            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            username = mEmailView.getText().toString();
            password = mPasswordView.getText().toString();

            boolean cancel = false;
            View focusView = null;


            // Check for a valid email address.
            if (TextUtils.isEmpty(username)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
                return false;
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                //showProgress(true);
                //mAuthTask = new UserLoginTask(email, password);
                //mAuthTask.execute((Void) null);
                this.performLogin();

            }
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnInstagram:
                this.connectOrDisconnectUser();
                break;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
                // SignIn(mEmail, mPassword);
                // GetAppSettings();

            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Initializes Instagram Setup
     */
    private void initializeInstagram() {

        mApp = new InstagramApp(this, Constants.CLIENT_ID,
                Constants.CLIENT_SECRET, Constants.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                btnInstagram.setText("Logout from Instagram");
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        if (mApp.hasAccessToken()) {
            // tvSummary.setText("Connected as " + mApp.getUserName());
            btnInstagram.setText("Logout from Instagram");
            mApp.fetchUserName(handler);

        }
    }

    /**
     * Checks if the user is already logged into Instagram or not
     */
    private void connectOrDisconnectUser() {

        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    LoginActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    btnInstagram.setText("Instagram");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }

    /**
     * Performs Login Action
     */
    private void performLogin() {
        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                Utils.hideSoftKeyboard(this);
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Signing In...");

                service = ApiClientConnection.getInstance().createService();

                SignInRequest signInRequest = new SignInRequest();
                signInRequest.username = username;
                signInRequest.password = password;

                PrefUtils.setSharedPrefStringData(this, PrefUtils.USER_NAME, username);
                PrefUtils.setSharedPrefStringData(this, PrefUtils.PASSWORD, password);
                signInRequest.loginType = Constants.KEYBOARD;
                Call<UserData> call = service.performLogin(signInRequest);

                call.enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call,
                                           Response<UserData> response) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                UserData apiResponse = response.body();
                                if (apiResponse != null && apiResponse.getData().getStatus() != null &&
                                        apiResponse.getData().getStatus().equals("error")) {
                                    Utils.printLogs("LoginActivity", "onResponse : Failure : -- " +
                                            response.body());
                           /*         Toast.makeText(LoginActivity.this, apiResponse.getData(,
                                            Toast.LENGTH_SHORT).show();*/
                                } else {
                                    Utils.printLogs("LoginActivity", "onResponse : Success : -- " + apiResponse);
                                    Toast.makeText(LoginActivity.this, "Login Successful!",
                                            Toast.LENGTH_SHORT).show();
                                    Utils.printLogs("LoginActivity", apiResponse.getData().getUserid());
                                    Intent mInHome = new Intent(LoginActivity.this, AppSettingsActivity.class);
                                    mInHome.putExtra(Constants.USERID,
                                            apiResponse.getData().getUserid());
                                    mInHome.putExtra("username", apiResponse.getData().getData().get(0).getUserName());
                                    saveUserDataInDb(response.body());
                                    LoginActivity.this.startActivity(mInHome);
                                    saveInPref(apiResponse);
                                    LoginActivity.this.finish();
                                }
                            } else {
                                Utils.printLogs("LoginActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(LoginActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            if (response != null)
                                if (response.body() != null)
                                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("LoginActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(LoginActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveInPref(UserData userData) {
        PrefUtils.setSharedPrefStringData(this, PrefUtils.USER_ID, userData.getData().getUserid());
        PrefUtils.setSharedPrefStringData(this, PrefUtils.USER_NAME, userData.getData().getData().get(0).getUserName());
        PrefUtils.setSharedPrefBooleanData(this, PrefUtils.IS_LOGGEDIN, true);

    }

    public void saveUserDataInDb(UserData userData) {
        if (userData != null && userData.getData() != null && userData.getData().getData() != null) {

            for (int i = 0; i < userData.getData().getData().size(); i++) {
                UserDao.getInstance(this).insertOrReplace(userData.getData().getData().get(i));
                Log.d("videoId", i + ": " + userData.getData().getData().get(i).getVideoID() + "");
                Log.d("videoId", i + ": " + userData.getData().getData().get(i).getTitle() + "");
            }

        }


    }


    /**
     * Adds Instagram Info to the existing user's account
     */
    private void addInstagramInfo(String userId, String accessToken, String igUserId,
                                  String igUsername) {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                Utils.hideSoftKeyboard(this);
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Linking...");

                service = ApiClientConnection.getInstance().createService();

                InstagramRequest appSettingsRequest = new InstagramRequest();
                appSettingsRequest.userId = userId;
                appSettingsRequest.accessToken = accessToken;
                appSettingsRequest.instagramUserId = igUserId;
                appSettingsRequest.instagramUsername = igUsername;
                Call<ServerResult> call = service.addInstagramInfo(appSettingsRequest);

                call.enqueue(new Callback<ServerResult>() {
                    @Override
                    public void onResponse(Call<ServerResult> call,
                                           Response<ServerResult> response) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if (apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("LoginActivity",
                                            "onResponse : Failure : -- " + response.body());
                                    Toast.makeText(LoginActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.printLogs("LoginActivity",
                                            "onResponse : Success : -- " + apiResponse);
                                    Toast.makeText(LoginActivity.this,
                                            apiResponse.message, Toast.LENGTH_SHORT).show();

                                    PrefUtils.setSharedPrefStringData(LoginActivity.this, PrefUtils.USER_ID, apiResponse.data.userId);
                                    PrefUtils.setSharedPrefStringData(LoginActivity.this, PrefUtils.USER_NAME, apiResponse.data.users[0].username);
                                    PrefUtils.setSharedPrefBooleanData(LoginActivity.this, PrefUtils.IS_LOGGEDIN, true);

                                    Intent mInHome = new Intent(LoginActivity.this, AppSettingsActivity.class);
                                    mInHome.putExtra(Constants.USERID,
                                            "1");
                                    LoginActivity.this.startActivity(mInHome);
                                    LoginActivity.this.finish();
                                }
                            } else {
                                Utils.printLogs("LoginActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(LoginActivity.this, "Some error occurred.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("LoginActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(LoginActivity.this, "Some error occurred.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForStoragePermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                //return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            //return true;
        }
        /*int permissionCheck = ContextCompat.checkSelfPermission(LandingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }


}

