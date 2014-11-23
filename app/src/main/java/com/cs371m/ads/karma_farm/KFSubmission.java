package com.cs371m.ads.karma_farm;

public class KFSubmission {

    String subreddit;
    String title;
    String author;
    String permalink;
    String url;
    String domain;
    String id;

    int points;
    int numComments;

    String getDetails() {
        String details = author
                + " posted in /r/" + subreddit + " with " + numComments + " replies";
        return details;
    }

    String getTitle(){
        return title;
    }

    String getScore(){
        return Integer.toString(points);
    }
}