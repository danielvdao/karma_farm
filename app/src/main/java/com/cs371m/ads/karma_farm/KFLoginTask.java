package com.cs371m.ads.karma_farm;


import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.client.HttpClient;

public class KFLoginTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "LoginTask";

    protected String mUsername;
    private String mPassword;
    protected String mUserError = null;

    private HttpClient mClient;
    private Context mContext;

    protected KFLoginTask(String username, String password, HttpClient client, Context context) {
        mUsername = username;
        mPassword = password;
        mClient = client;
        mContext = context;
    }

    @Override
    public Boolean doInBackground(Void... v) {
        return doLogin(mUsername, mPassword, mClient, mContext);
    }

    private boolean doLogin(String username, String password, HttpClient client, Context context) {
        return true;
    }
}
