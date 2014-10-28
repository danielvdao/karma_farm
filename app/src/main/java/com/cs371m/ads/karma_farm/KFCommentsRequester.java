package com.cs371m.ads.karma_farm;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;


public class KFCommentsRequester {

    private static final String TAG = "KFCommentsRequester";

    private static final String HOST = "http://104.131.71.174";
    private static final String PORT = "5000";
    private static final String CURRENT_API_VERSION = "/api/v0";
    private static final String ENDPOINT = "/comments";


    private String mSubmissionId;
    private String mUrl;

    public KFCommentsRequester(String id) {
        mSubmissionId = id;
        generateUrl();
    }

    private void generateUrl() {
        mUrl = HOST + ":" + PORT + CURRENT_API_VERSION + ENDPOINT + "/" + mSubmissionId;
    }


    public JSONArray requestComments() {


        String raw = RemoteData.readContents(mUrl);
        JSONArray children = null;

        try {
            JSONObject data = new JSONObject(raw).getJSONObject("data");
            children = data.getJSONArray("comments");

        } catch (Exception e) {
            Log.e("requestSubmissionList()", e.toString());
        }

        if (children == null) return null;

        Log.d(TAG, "Fetched comments for submissionID: " + mSubmissionId);

        return children;
    }
}
