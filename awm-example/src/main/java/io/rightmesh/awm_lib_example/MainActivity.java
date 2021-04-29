package io.rightmesh.awm_lib_example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import io.rightmesh.awm.AndroidWirelessStatsCollector;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private AndroidWirelessStatsCollector awsc;
    private boolean started;
    private boolean clearUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean privacyPolicy = prefs.getBoolean("policy", false);
        if (!privacyPolicy) {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivityForResult(intent, 100);
        } else {
            init();
        }
    }

    void init() {
        setContentView(R.layout.activity_main);

        started = false;
        start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK) {
            if (requestCode == 100) {
                init();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    void stop() {
        if(started) {
            awsc.stop();
            started = false;
        }
    }

    void start() {
        if (!started) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

            boolean caching = prefs.getBoolean("caching", true);
            boolean wifiuploads = prefs.getBoolean("wifiuploads", false);
            boolean clearboot = prefs.getBoolean("clearboot", false);
            clearUpload = prefs.getBoolean("clearupload", true);
            boolean privacy = prefs.getBoolean("privacy", false);
            String url = prefs.getString("url", "https://test.jasonernst.com/awm-lib-server/index.php");

            Log.d(TAG, "CONFIGS: " + caching + " " + wifiuploads + " " + clearboot + " "
                + " " + clearUpload + " " + privacy + " " + url );

            awsc = new AndroidWirelessStatsCollector(this, caching, wifiuploads, clearboot,
                    clearUpload, privacy, url);

            awsc.start();
            started = true;
        }
    }

    public boolean clearUploads() {
        return clearUpload;
    }

    public AndroidWirelessStatsCollector getAwsc() {
        return awsc;
    }
}
