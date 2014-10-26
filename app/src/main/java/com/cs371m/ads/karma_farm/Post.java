package com.cs371m.ads.karma_farm;

/**
 * Created by Andy on 10/25/2014.
 * Adapted from Hathy
 * (http://www.whycouch.com/2012/12/how-to-create-android-client-for-reddit.html)
 *
 * (Not yet compatible with backend)
 *
 * This is a class that holds the data of the JSON objects
 * returned by the Reddit API.
 */
public class Post {

    String subreddit;
    String title;
    String author;
    int points;
    int numComments;
    String permalink;
    String url;
    String domain;
    String id;

    String getDetails(){
        String details=author
                +" posted this and got "
                +numComments
                +" replies";
        return details;
    }

    String getTitle(){
        return title;
    }

    String getScore(){
        return Integer.toString(points);
    }
}