package com.cs371m.ads.karma_farm;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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


    public ArrayList<KFComment> requestComments() {
        String raw = RemoteData.readContents(mUrl);
        ArrayList<KFComment> result = new ArrayList<KFComment>();

        try {
            JSONObject data = new JSONObject(raw);
            JSONArray comments = (JSONArray) data.getJSONArray("comments").get(0);

            int depth[] = new int[]{0};

            requestCommentsHelper(comments, result, depth);

        } catch (Exception e) {
            Log.e("requestSubmissionList()", e.toString());
        }


        return result;
    }

    private void requestCommentsHelper(JSONArray comments, ArrayList<KFComment> result, int[] depth) {

        if (comments != null) {
            depth[0]++;

            for(int i = 0; i < comments.length(); i++ ) {
                try {


                    KFComment comment = new KFComment();
                    JSONObject cur = comments.getJSONArray(0).getJSONObject(i);

                    comment.author = cur.optString("author");
                    comment.text = cur.optString("text");
                    comment.KFscore = cur.optInt("rank");
                    comment.karma = cur.optInt("score");
                    comment.depth = depth[0];
                    requestCommentsHelper((JSONArray) cur.getJSONArray("replies").get(0), comment.replies, depth);

                    result.add(comment);

                    Log.d(TAG, comment.author + " said " + comment.text + " and received "
                            + comment.karma + " karma.");

                } catch (JSONException je) {
                    Log.d(TAG, "JSONException:");
                    je.printStackTrace();
//                    Log.d(TAG, je.getLocalizedMessage());
                }

            }
            depth[0]--;

        }
    }
}
