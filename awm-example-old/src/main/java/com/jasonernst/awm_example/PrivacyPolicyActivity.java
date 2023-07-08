package com.jasonernst.awm_example;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;

import io.rightmesh.awm_lib_example.R;

public class PrivacyPolicyActivity extends Activity {
    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        web = findViewById(R.id.webView);
        web.loadUrl("file:///android_asset/privacy_policy.html");
    }

    public void accept(View v) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("policy", true);
        editor.commit();
        setResult(RESULT_OK);
        finish();
    }

    public void decline(View v) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("policy", false);
        editor.commit();
        setResult(RESULT_OK);
        finish();
    }
}
