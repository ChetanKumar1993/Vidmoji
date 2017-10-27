package com.perfection.newkeyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.perfection.newkeyboard.Helpers.Constants;
import com.perfection.newkeyboard.Model.InstagramRequest;
import com.perfection.newkeyboard.Model.ServerResult;
import com.perfection.newkeyboard.Model.SignUpRequest;
import com.perfection.newkeyboard.controller.AppSettingsActivity;
import com.perfection.newkeyboard.controller.instagram.InstagramApp;
import com.perfection.newkeyboard.rest.ApiClientConnection;
import com.perfection.newkeyboard.rest.ApiInterface;
import com.perfection.newkeyboard.utils.DatePickerCallback;
import com.perfection.newkeyboard.utils.DatePickerFragment;
import com.perfection.newkeyboard.utils.DialogListCallback;
import com.perfection.newkeyboard.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class signUpActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
        OnClickListener, DatePickerCallback {

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
    private String email, password, username, gender, birthday, country;
    private EditText etBirthday, etConfirmPassword, etCountry;
    private TextView mSignInButton;
    private Button btnRegister, btnInstagram;
    private CheckBox cbTerms;
    private RadioGroup rgGender;
    private AutoCompleteTextView usernameView;

    private InstagramApp mApp;

    /**
     * To get the user info from the Instagram session
     */
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Utils.printLogs("signUpActivity", mApp.getTOken());
                Utils.printLogs("signUpActivity", mApp.getId());
                Utils.printLogs("signUpActivity", mApp.getUserName());
                Utils.printLogs("signUpActivity", mApp.getName());
                signUpActivity.this.addInstagramInfo("1", mApp.getTOken(), mApp.getId(),
                        mApp.getUserName());
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(signUpActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_signup);
        this.initializeInstagram();
        this.setWidgetReferences();
        this.setListenersOnWidgets();
    }

    /**
     * Sets the References of the widgets using their IDs
     */
    private void setWidgetReferences() {

        mEmailView = (AutoCompleteTextView) findViewById(R.id.tilEmail);
        mPasswordView = (EditText) findViewById(R.id.userPassEntry);
        mSignInButton = (TextView) findViewById(R.id.loginPageButton);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        usernameView = (AutoCompleteTextView) findViewById(R.id.userNameEntry);
        etConfirmPassword = (EditText) findViewById(R.id.tilConfirmPassword);
        etBirthday = (EditText) findViewById(R.id.tilBirthday);
        etCountry = (EditText) findViewById(R.id.tilCountry);
        cbTerms = (CheckBox) findViewById(R.id.cbTerms);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        btnInstagram = (Button) findViewById(R.id.btnInstagram);
    }

    /**
     * Sets the various Listeners on the widgets
     */
    private void setListenersOnWidgets() {

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


        mSignInButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    Intent mInHome = new Intent(signUpActivity.this, LoginActivity.class);
                    signUpActivity.this.startActivity(mInHome);
                    signUpActivity.this.finish();
                }
                return true; // return is important...
            }
        });

        btnRegister.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);
        etBirthday.setOnClickListener(this);
        etCountry.setOnClickListener(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        username = usernameView.getText().toString();
        birthday = etBirthday.getText().toString();
        country = etCountry.getText().toString();
        if(rgGender.getCheckedRadioButtonId() == R.id.rbFemale)
            gender = "Female";
        else if(rgGender.getCheckedRadioButtonId() == R.id.rbMale)
            gender = "Male";

        boolean cancel = false;
        View focusView = null;

        // Check if the username is empty or not
        if(TextUtils.isEmpty(username)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Checks if both the passwords are the same or not
        if(!password.equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(signUpActivity.this, "Both the passwords should be same",
                    Toast.LENGTH_SHORT).show();
            focusView = etConfirmPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check if the username is empty or not
        if(TextUtils.isEmpty(birthday)) {
            etBirthday.setError(getString(R.string.error_field_required));
            focusView = etBirthday;
            cancel = true;
        }

        // Check if the username is empty or not
        if(TextUtils.isEmpty(country)) {
            etCountry.setError(getString(R.string.error_field_required));
            focusView = etCountry;
            cancel = true;
        }

        if(!cbTerms.isChecked()) {
            Toast.makeText(signUpActivity.this, "Please agree to the Terms and Conditions",
                    Toast.LENGTH_SHORT).show();
            focusView = cbTerms;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            /*showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
            this.performSignup();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnRegister:
                this.attemptLogin();
                break;
            case R.id.tilBirthday:
                DatePickerFragment dateOfferFromFragment = new DatePickerFragment(
                        DatePickerFragment.REQUEST_CODE_DATE, this);
                Bundle args1 = new Bundle();
                args1.putString(Constants.DATE_FORMAT,
                        Constants.DATE_FORMAT_DD_MM_YYYY_SLASH);
                dateOfferFromFragment.setArguments(args1);
                dateOfferFromFragment.show(getFragmentManager(), "");
                break;
            case R.id.tilCountry:
                Dialog countryCodeDialog = Utils.showCountryListDialog(this,
                        Constants.countryArray, new DialogListCallback() {
                            @Override
                            public void getSelectedItem(String countryCode) {
                                etCountry.setText(countryCode);
                                country = countryCode;
                            }
                        });
                countryCodeDialog.show();
                break;
            case R.id.btnInstagram:
                this.connectOrDisconnectUser();
                break;
        }
    }

    @Override
    public void setDate(String date, int requestCode) {

        switch (requestCode) {

            case DatePickerFragment.REQUEST_CODE_DATE:
                etBirthday.setText(date);
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
     * Performs Registration
     */
    private void performSignup() {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {
                Utils.hideSoftKeyboard(this);
                final ProgressDialog progressDialog = Utils.showProgressDialog(this,
                        "Signing Up...");

                service = ApiClientConnection.getInstance().createService();

                SignUpRequest signUpRequest = new SignUpRequest();
                signUpRequest.email = email;
                signUpRequest.password = password;
                signUpRequest.username = username;
                signUpRequest.birthdate = birthday;
                signUpRequest.country = country;
                signUpRequest.gender = gender;
                signUpRequest.loginType = Constants.KEYBOARD;
                Call<ServerResult> call = service.signUp(signUpRequest);

                call.enqueue(new Callback<ServerResult>() {
                    @Override
                    public void onResponse(Call<ServerResult> call,
                                           Response<ServerResult> response) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if(apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("signUpActivity", "onResponse : Failure : -- " +
                                            response.body());
                                    Toast.makeText(signUpActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.printLogs("signUpActivity", "onResponse : Success : -- " + apiResponse);
                                    Toast.makeText(signUpActivity.this, "Sign Up Successful!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent mInHome = new Intent(signUpActivity.this, AppSettingsActivity.class);
                                    mInHome.putExtra(Constants.USERID,
                                            apiResponse.data.userId);
                                    //mInHome.putExtra("username", apiResponse.data.users[0].username);
                                    signUpActivity.this.startActivity(mInHome);
                                    signUpActivity.this.finish();
                                }
                            } else {
                                Utils.printLogs("signUpActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(signUpActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("signUpActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(signUpActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
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
                Toast.makeText(signUpActivity.this, error, Toast.LENGTH_SHORT)
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
                    signUpActivity.this);
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
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                ServerResult apiResponse = response.body();
                                if(apiResponse != null && apiResponse.status != null &&
                                        apiResponse.status.equals("error")) {
                                    Utils.printLogs("signUpActivity",
                                            "onResponse : Failure : -- " + response.body());
                                    Toast.makeText(signUpActivity.this, apiResponse.message,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.printLogs("signUpActivity",
                                            "onResponse : Success : -- " + apiResponse);
                                    Toast.makeText(signUpActivity.this,
                                            apiResponse.message, Toast.LENGTH_SHORT).show();

                                    Intent mInHome = new Intent(signUpActivity.this, AppSettingsActivity.class);
                                    mInHome.putExtra(Constants.USERID,
                                            "1");
                                    signUpActivity.this.startActivity(mInHome);
                                    signUpActivity.this.finish();
                                }
                            } else {
                                Utils.printLogs("signUpActivity", "onResponse : Failure : -- " +
                                        response.body());
                                Toast.makeText(signUpActivity.this, "Some error occurred.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResult> call, Throwable t) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.printLogs("signUpActivity", "onFailure : -- " + t.getCause());
                        Toast.makeText(signUpActivity.this, "Some error occurred.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

