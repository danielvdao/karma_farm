package com.cs371m.ads.karma_farm;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class KFCommentsRequester {

    private static final String TAG = "KFCommentsRequester";

    private static final String HOST = "http://104.131.71.174";
    private static final String CURRENT_API_VERSION = "/api/v0";
    private static final String ENDPOINT = "/comments";


    protected String mSubmissionId;
    private String mUrl;

    public KFCommentsRequester(String id) {
        mSubmissionId = id;
        generateUrl();
    }

    private void generateUrl() {
        mUrl = HOST + CURRENT_API_VERSION + ENDPOINT + "/" + mSubmissionId;
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
                    JSONObject cur = comments.getJSONObject(i);

                    // If MoreComments, just add placeholder class
                    if (cur.has("body")) {
                        KFComment.KFMoreComments moreComment = new KFComment.KFMoreComments();
                        moreComment.depth = depth[0];
                        result.add(moreComment);
                    } else {

                        KFComment comment = new KFComment();

                        comment.author = cur.getString("author");
                        comment.text = cur.getString("text");
                        comment.KFscore = cur.getInt("rank");
                        comment.score = cur.getInt("score");
                        comment.depth = depth[0];

                        result.add(comment);

                        if ( cur.has("replies")) {
                           requestCommentsHelper((JSONArray) cur.getJSONArray("replies").get(0), result, depth);
                        }
                    }
                }catch(JSONException je){
                    Log.d(TAG, "JSONException while requesting comments");
                }
            }
        }

        depth[0]--;
    }
}
