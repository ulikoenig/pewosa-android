package de.ulikoenig.pewosa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

     /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    private static final String PREFS_NAME = "de.ulikoenig.pewosa.settings";
    public static final String RINGTONE = "ringtone";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mRingtoneView;
    private String username;
    private String password;
    private String chosenRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        username = settings.getString(USERNAME, "");
        password = settings.getString(PASSWORD, "");
        chosenRingtone = settings.getString(RINGTONE, "");

        // Set up the login form.
        mEmailView = (TextView) findViewById(R.id.username);
        assert mEmailView != null;
        mEmailView.setText(username);


        mPasswordView = (EditText) findViewById(R.id.password);
        assert mPasswordView != null;
        mPasswordView.setText(password);

        mRingtoneView = (TextView) findViewById(R.id.ringtone);
        assert mRingtoneView != null;
        if ((chosenRingtone != null) && (chosenRingtone != "")) {
            String ringToneTitle = RingtoneManager.getRingtone(this, Uri.parse(chosenRingtone)).getTitle(this);
            mRingtoneView.setText(ringToneTitle);
        }


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
        assert mEmailSignInButton != null;
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        showProgress(true);
        mAuthTask = new UserLoginTask(email, password, this);
        mAuthTask.execute((Void) null);

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private LoginActivity mLoginActivity;

        UserLoginTask(String email, String password, LoginActivity loginActivity) {
            mEmail = email;
            mPassword = password;
            mLoginActivity = loginActivity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("pewosaSettings", "doInBackground");
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(USERNAME, mEmail);
            editor.putString(PASSWORD, mPassword);
            editor.commit();

            if ((mEmail != null)&&(mEmail != "")) {
                FirebaseMessaging.getInstance().subscribeToTopic("user" + mEmail);
                Log.d("PeWoSa", "Push - listening to: " + mEmail);
            } else {
                Log.d("PeWoSa", "Push - NOT listening - username not set");
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                mLoginActivity.onBackPressed();
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


    @Override
    public void onBackPressed() {
        Log.d("pewosaSettings", "back");
        setContentView(R.layout.activity_main);
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        this.startActivity(i);
    }


    //Klingelton
    public void selectRingtone(View v) {
        Log.d("pewosaSettings", "selectRingtone");
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        Uri parse = null;
        if (this.chosenRingtone != null){
            parse = Uri.parse(this.chosenRingtone);
        }
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, parse);
        this.startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Log.d("pewosaSettings", "selectRingtone-done");
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
                Log.d("pewosaSettings", "selected Ringtone:"+this.chosenRingtone);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(RINGTONE, this.chosenRingtone);
                editor.commit();
                String ringToneTitle = RingtoneManager.getRingtone(this, Uri.parse(chosenRingtone)).getTitle(this);
                Log.d("pewosaSettings", "selected Ringtone title:"+ringToneTitle);
                mRingtoneView.setText(ringToneTitle);
            }
            else
            {
                this.chosenRingtone = null;
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove(RINGTONE);
                editor.commit();
                mRingtoneView.setText("");
            }
        }

    }


}

