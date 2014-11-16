package com.cs371m.ads.karma_farm;


import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;


public class KFLoginTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "LoginTask";
    private static final String REDDIT_LOGIN_URL = "https://ssl.reddit.com/api/login";
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

    /*
    *  On success stores session cookie and modhash in your "RedditSettings"
    *  On failure does not modify "RedditSettings".
    *  Should be called from a background thread.
    *
    *  @return Error message, or null on success
    * */

    private boolean doLogin(String username, String password, HttpClient client, Context context) {
        String status = "";
        String userError = "A login error has occurred, please try again.";
        HttpEntity entity = null;
        try {
            //Construct the data that we're going to use

            List<NameValuePair> user_information = new ArrayList<NameValuePair>();
            user_information.add(new BasicNameValuePair("user", username.toString()));
            user_information.add(new BasicNameValuePair("password", password.toString()));
            user_information.add(new BasicNameValuePair("api_type", "json"));

            HttpPost httpPost = new HttpPost(REDDIT_LOGIN_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(user_information, HTTP.UTF_8));

            HttpParams params = httpPost.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 45000);
                HttpConnectionParams.setSoTimeout(params, 45000);

            //Make the HTTP POST
            HttpResponse response = client.execute(httpPost);
            status = response.getStatusLine().toString();

            // If the status doesn't return 200
            if (!status.contains("OK")){
                throw new HttpException(status);
            }

            entity = response.getEntity();

            BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line = in.readLine();
            in.close();
            entity.consumeContent();

            // Failed login attempt from what it seems
           // if (StringUtils.isEmpty(line)){
           //     throw new HttpException("No content returned from login POST");
           // }

            if (line.equals("")){
                throw new HttpException("No content returned from login POST");
            }

            final JsonFactory jsonFactory = new JsonFactory();
            final JsonParser jp = jsonFactory.createJsonParser(line);

            while (jp.nextToken() != JsonToken.FIELD_NAME){
                if (jp.nextToken() != JsonToken.START_ARRAY)
                    throw new IllegalStateException("Login: expecting errors START_ARRAY");
                if (jp.nextToken() != JsonToken.END_ARRAY){
                    if (line.contains("WRONG_PASSWORD")){
                        userError = "Bad password.";
                        throw new Exception("Wrong password.");
                    }

                    else {
                        throw new Exception(line);
                    }
                }
            }


        } catch (Exception e) {
            mUserError = userError;
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (Exception e2) {
                    Log.e(TAG, "entity.consumeContent()", e);
                }

                Log.e(TAG, "doLogin()", e);
            }

            return false;

        }
        return true;
    }
}
