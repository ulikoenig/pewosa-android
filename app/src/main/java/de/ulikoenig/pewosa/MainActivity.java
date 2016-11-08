package de.ulikoenig.pewosa;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import static de.ulikoenig.pewosa.R.id.action_settings;

public class MainActivity extends AppCompatActivity {

    private static final String PEWOSA_BASE_URL = "https://ulikoenig.de/pewosa/";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Toolbar mToolbar;
    public static final String PREFS_NAME = "de.ulikoenig.pewosa.settings";

    private String username;
    private String password;
    private WebView webview;
    private TextView textView;
    private MenuItem settingsbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("pewosa", "onCreate");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        username = settings.getString("username", "");
        password = settings.getString("password", "");

        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webView);
        textView = (TextView) findViewById(R.id.textView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("de.ulikoenig.pewosa/android");
        webview.clearHistory();

        //Webseite in Browser laden
        webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                Log.d("pewosa", "Splash Webview finished!");
                if (textView != null) textView.setVisibility(View.INVISIBLE);
                if (settingsbtn != null) {
                    settingsbtn.setEnabled(true);
                    settingsbtn.setVisible(true);
                }
                if (webview != null) {
                    webview.setVisibility(View.VISIBLE);
                    webview.setEnabled(true);
                    webview.setAlpha(1f);
                    //stelle History auf Null, wenn Startseite geladen.
                    if (url.equals(PEWOSA_BASE_URL)) {
                        webview.clearHistory();
                    }
                }
            }

            public void onPageStarted(WebView view,
                                      String url,
                                      Bitmap favicon) {
                waitForWebview(view);
            }
        });

//        webview.loadUrl(PEWOSA_BASE_URL + "login.php?login=1&username=" + username + "&password=" + password);

        if ((savedInstanceState == null) || (!savedInstanceState.containsKey("NotificationMessageType"))) {
            webview.loadUrl(PEWOSA_BASE_URL + "login.php?login=1&username=" + username + "&password=" + password);
        } else {
            onNewIntent(getIntent());
        }


        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else Log.e("pewosa","ManyActivity.onCreate: mToolbar == null");


        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("user" + username);
        FirebaseMessaging.getInstance().subscribeToTopic("news");


        String token = FirebaseInstanceId.getInstance().getToken();
        // Log and toast

        if (token != null) {
            Log.d("firebase", token);
        } else {
            Log.d("firebase", "token==NULL!");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadHome(v);
                }
            });
        } else Log.e("pewosa","ManyActivity.onCreate: toolbar == null");



        if ((username != null)&&(username != "")) {
            FirebaseMessaging.getInstance().subscribeToTopic("user" + username);
            Log.d("PeWoSa", "Push - listening to: " + username);
        } else {
            Log.d("PeWoSa", "Push - NOT listening - username not set");
        }
    }


    /**
     * catch Notification todos
     *
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent) {
        Log.d("pewosa", "onNewIntent");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d("pewosa", "onNewIntent - found extra data");
            if (extras.containsKey("NotificationMessageType")) {
                Log.d("pewosa", "onNewIntent - found NotificationMessageType");

                if (extras.getString("NotificationMessageType").equals("newFirstReleaseRequest")) {
                    int id = extras.getInt("NotificationMessageID");
                    Log.d("pewosa", "onNewIntent - found newFirstReleaseRequest " + id);
                    webview.loadUrl(PEWOSA_BASE_URL + "message.php#" + id);
                }

                if (extras.getString("NotificationMessageType").equals("newSecondReleaseRequest")) {
                    int id = extras.getInt("NotificationMessageID");
                    Log.d("pewosa", "onNewIntent - found newSecondReleaseRequest " + id);
                    webview.loadUrl(PEWOSA_BASE_URL + "message.php#" + id);
                }
            }
        } else {
            Log.d("pewosa", "onNewIntent extras == null");
        }
    }


    public void loadHome(View v) {
        Log.d("pewosa", "loadHome");
        WebView webview = (WebView) findViewById(R.id.webView);
        waitForWebview(webview);
        webview.loadUrl(PEWOSA_BASE_URL + "login.php?login=1&username=ukoenig&password=hugo");
    }

    private void waitForWebview(WebView webview) {
        if (webview != null) {
            webview.setAlpha(0.25f);
            webview.setEnabled(false);
        }
        if (settingsbtn != null) {
            settingsbtn.setEnabled(false);
            settingsbtn.setVisible(false);
        }
    }


    public void reloadWebview(MenuItem item) {
        Log.d("pewosa", "reloadWebview");
        waitForWebview(webview);
        webview.reload();
    }

    public void openSettings(MenuItem item) {
        Log.d("pewosa", "openSettings");
        setContentView(R.layout.activity_login);
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        this.startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if ((webview != null) && (webview.canGoBack())) {
            waitForWebview(webview);
            webview.goBack();
            return;
        }

//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);
        //super.onBackPressed();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        settingsbtn = menu.findItem(R.id.action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
