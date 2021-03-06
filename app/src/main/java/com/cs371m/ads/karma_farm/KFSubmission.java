package com.cs371m.ads.karma_farm;

import android.graphics.Bitmap;

public class KFSubmission {

    String subreddit;
    String title;
    String author;
    String permalink;
    String url;
    String domain;
    String id;
    String thumb_url;
    Bitmap thumb;
    int numComments;
    int score;
    boolean isUpVoted;
    boolean isDownVoted;
    boolean isNSFW;

    String getDetails() {
        String details = "authored by /u/" + author;
        return details;
    }

    String getTitle(){
        return title;
    }

    String getScore(){
        return Integer.toString(score);
    }

    String getNumComments() { return Integer.toString(numComments); }
}