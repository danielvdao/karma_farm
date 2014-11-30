package com.cs371m.ads.karma_farm;

import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KFSubmissionsRequester {

    /**
     * We will be fetching JSON data from the API.
     */

    private final String URL_TEMPLATE=
            "http://www.reddit.com/r/SUBREDDIT_NAME/"
                    +".json"
                    +"?after=AFTER";

    private static final String TAG = "KFSubmissionsRequester";



    String mSubreddit;
    String mUrl;
    String mAfter;


    public KFSubmissionsRequester(String subreddit){
        mSubreddit = subreddit;
        mAfter = "";
        generateURL();
    }

    /**
     * This is to fetch the next set of posts
     * using the 'after' property
     * @return
     */
    List<KFSubmission> fetchMorePosts(){
        generateURL();
        return requestSubmissionList();
    }

    /**
     * Generates the actual URL from the template based on the
     * subreddit name and the 'after' property.
     */
    private void generateURL(){
        mUrl = URL_TEMPLATE.replace("SUBREDDIT_NAME", mSubreddit);
        mUrl = mUrl.replace("AFTER", mAfter);
    }

    /**
     * Returns a list of KFSubmission objects after fetching data from
     * Reddit using the JSON API.
     *
     * @return
     */
    List<KFSubmission> requestSubmissionList(){
        String raw = RemoteData.readContents(mUrl);

        List<KFSubmission> result = new ArrayList<KFSubmission>();

        try{

            JSONObject data = new JSONObject(raw).getJSONObject("data");

            JSONArray children = data.getJSONArray("children");

            //Using this property we can fetch the next set of
            //posts from the same subreddit
            mAfter = data.getString("after");

            for(int i = 0; i < children.length(); i++){

                JSONObject cur = children.getJSONObject(i).getJSONObject("data");

                KFSubmission sub = new KFSubmission();

                sub.title = cur.optString("title");
                sub.url = cur.optString("url");
                sub.numComments = cur.optInt("num_comments");
                sub.score = cur.optInt("score");
                sub.author = cur.optString("author");
                sub.subreddit = cur.optString("subreddit");
                sub.permalink = cur.optString("permalink");
                sub.domain = cur.optString("domain");
                sub.id = cur.optString("id");
                sub.thumb_url = cur.optString("thumbnail");

                if(sub.title != null)
                    result.add(sub);
            }
        }catch(Exception e){
            Log.e("requestSubmissionList()", e.toString());
        }

        Log.d(TAG, "Fetched " + result.size() + " posts");

        for(KFSubmission sub : result) {
            try {
                if(!sub.thumb_url.equals(""))
                    sub.thumb = BitmapFactory.decodeStream(new URL(sub.thumb_url).openConnection().getInputStream());
                else
                    sub.thumb = null;
            } catch (MalformedURLException e) {
                sub.thumb = null;
            } catch(Exception e){
                Log.e("requestSubmissionList()", e.toString());
            }
        }

        return result;
    }
}