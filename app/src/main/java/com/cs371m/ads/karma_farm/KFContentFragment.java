package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.Toast;

public class KFContentFragment extends Fragment {

    private final static String TAG = "KFContentFragment";

    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private String mUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getArguments().getString("url");


        final Activity activity = getActivity();

        if (activity == null)
            Log.d(TAG, "null activity found");

        if (mUrl == null || mUrl.equals(""))
            Log.d(TAG, "null uri");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = new WebView(getActivity());

        if (mWebView == null)
            Log.d(TAG, "mWebView is null");
        else
            mIsWebViewAvailable = true;

        if (mUrl == null || mUrl.equals(""))
            Log.d(TAG, "url is null");

        if (mIsWebViewAvailable)
            mWebView.loadUrl(mUrl);

        return mWebView;
    }


    public static Fragment newInstance(String url){
        KFContentFragment content = new KFContentFragment();
        // supply arguments to content fragment
        Bundle args = new Bundle();
        args.putString("url", url);
        content.setArguments(args);
        return content;
    }

    public void loadUrl(String url) {
        if (mIsWebViewAvailable)
            getWebView().loadUrl(mUrl = url);
        else
            Log.d(TAG, "WebView cannot be found. Check the view and fragment have been loaded.");
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }

    /* To ensure links open within the application */
    private class InnerWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


    }
}
