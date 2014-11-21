package com.cs371m.ads.karma_farm;

import com.androauth.api.RedditApi;
import com.androauth.oauth.OAuth20Request;
import com.androauth.oauth.OAuth20Service;
import com.androauth.oauth.OAuth20Token;
import com.androauth.oauth.OAuthRequest;
import com.androauth.oauth.OAuthRequest.OnRequestCompleteListener;
import com.androauth.oauth.OAuthService;
import com.twotoasters.android.hoot.HootResult;
import com.androauth.oauth.OAuth20Service.OAuth20ServiceCallback;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class KFLoginTask extends Activity {
    OAuth20Service service;
    public final static String APIKEY = "2Q4Ul-I8YNTSlQ";
    public static final String APISECRET = "yQpcp5uWSt_Z073GSGieCgqVxg8";
    public final static String CALLBACK = "https://oauth.reddit";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //setContentView();

        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Button button = (Button) findViewById(R.id.action_login);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v){
                startAuthentication();
            }
        });


    }


    private void startAuthentication(){
        service = OAuthService.newInstance(new RedditApi(), APIKEY, APISECRET, new OAuth20ServiceCallback() {
            @Override
            public void onOAuthAccessTokenReceived(OAuth20Token token) {
                editor.putString("access_token", token.getAccessToken());
                editor.putString("refresh_token", token.getRefreshToken());
                editor.commit();
                getInfo(token);
            }

            @Override
            public void onAccessTokenRequestFailed(HootResult hootResult) {

            }
        });

        service.setApiCallback(CALLBACK);
        service.setScope("identity");
        service.setDuration("permanent");
        getUserVerification();


    }

    private void getUserVerification(){
        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.startsWith(CALLBACK)){
                    webview.setVisibility(View.GONE);
                    service.getOAuthAccessToken(url);
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webview.loadUrl(service.getAuthorizeUrl());
    }

    private void getInfo(OAuth20Token token){
        OAuth20Request request = OAuthRequest.newInstance("https://oauth.reddit.com/api/v1/me", token, service, new OnRequestCompleteListener() {
            @Override
            public void onSuccess(HootResult hootResult) {
                Log.v("into", "final on success: " + hootResult.getResponseString());
            }

            @Override
            public void onNewAccessTokenReceived(OAuth20Token oAuth20Token) {

            }

            @Override
            public void onFailure(HootResult hootResult) {

            }
        });

        request.get();
    }
}
