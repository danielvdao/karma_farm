package com.cs371m.ads.karma_farm;

import android.annotation.SuppressLint;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class KFContentFragment extends Fragment {

    private final static String TAG = "KFContentFragment";

    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private String mUrl;
    protected ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getArguments().getString("url");

        if (mUrl == null || mUrl.equals(""))
            Log.d(TAG, "null uri");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mWebView != null) {
            mWebView.destroy();
        }

        // create new ProgressBar and style it
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 24));
        progressBar.setProgress(0);

        // retrieve the top view of our application
        final FrameLayout decorView = (FrameLayout) getActivity().getWindow().getDecorView();
        decorView.addView(progressBar);

        // Here we try to position the ProgressBar to the correct position by looking
        // at the position where content area starts. But during creating time, sizes
        // of the components are not set yet, so we have to wait until the components
        // has been laid out
        // Also note that doing progressBar.setY(136) will not work, because of different
        // screen densities and different sizes of actionBar
        ViewTreeObserver observer = progressBar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View contentView = decorView.findViewById(android.R.id.content);
                progressBar.setY(contentView.getY() - 10);
            }
        });

        // configure WebView
        mWebView = new WebView(getActivity());
        mWebView.setWebViewClient(new InnerWebViewClient()); // forces it to open in app
        mWebView.setScrollbarFadingEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.clearCache(true);
        mWebView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);

                if(progress == 100)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCloseWindow(WebView window) {
                progressBar.setVisibility(View.GONE);
            }
        });


        // configure web view
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);              // show zoom controls
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);                  // zoom to 100%
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUserAgentString("AndroidWebView");      

        mIsWebViewAvailable = true;
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

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null)
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
//        progressBar.setVisibility(View.GONE);
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
