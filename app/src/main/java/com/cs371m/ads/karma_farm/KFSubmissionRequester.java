package com.cs371m.ads.karma_farm;

/**
 * Created by Andy on 10/25/2014.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KFSubmissionRequester {

    /**
     * We will be fetching JSON data from the API.
     */

    private final String URL_TEMPLATE=
            "http://www.reddit.com/r/SUBREDDIT_NAME/"
                    +".json"
                    +"?after=AFTER";

    private final String TAG = "KFSubmissionRequester";

    private final String currentApiVersion = "/api/v0";

    String subreddit;
    String url;
    String after;

    KFSubmissionRequester(String sr){
        subreddit=sr;
        after="";
        generateURL();
    }

    /**
     * Generates the actual URL from the template based on the
     * subreddit name and the 'after' property.
     */
    private void generateURL(){
        url=URL_TEMPLATE.replace("SUBREDDIT_NAME", subreddit);
        url=url.replace("AFTER", after);
    }

    /**
     * Returns a list of KFSubmission objects after fetching data from
     * Reddit using the JSON API.
     *
     * @return
     */
    List<KFSubmission> fetchPosts(){
        String raw=RemoteData.readContents(url);
        List<KFSubmission> list=new ArrayList<KFSubmission>();

        try{

            JSONObject data=new JSONObject(raw)
                    .getJSONObject("data");
            JSONArray children=data.getJSONArray("children");

            //Using this property we can fetch the next set of
            //posts from the same subreddit
            after=data.getString("after");

            for(int i=0;i<children.length();i++){
                JSONObject cur=children.getJSONObject(i)
                        .getJSONObject("data");
                KFSubmission p=new KFSubmission();
                p.title=cur.optString("title");
                p.url=cur.optString("url");
                p.numComments=cur.optInt("num_comments");
                p.points=cur.optInt("score");
                p.author=cur.optString("author");
                p.subreddit=cur.optString("subreddit");
                p.permalink=cur.optString("permalink");
                p.domain=cur.optString("domain");
                p.id=cur.optString("id");
                if(p.title!=null)
                    list.add(p);
            }
        }catch(Exception e){
            Log.e("fetchPosts()",e.toString());
        }

        Log.d(TAG, "Fetched " + list.size() + " posts");
        return list;
    }

    /**
     * This is to fetch the next set of posts
     * using the 'after' property
     * @return
     */
    List<KFSubmission> fetchMorePosts(){
        generateURL();
        return fetchPosts();
    }
}