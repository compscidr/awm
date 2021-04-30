package io.rightmesh.awm_lib_example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

public class PrivacyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy, container, false);
        WebView web = view.findViewById(R.id.webView);
        web.loadUrl("file:///android_asset/privacy_policy.html");
        return view;
    }
}
