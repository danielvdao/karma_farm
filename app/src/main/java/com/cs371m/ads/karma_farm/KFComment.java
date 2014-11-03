package com.cs371m.ads.karma_farm;

public class KFComment {

    public String author;
    public String text;
    public int karma;
    public int KFscore;
    public int depth;

    public KFComment() {

    }

     public static class KFMoreComments extends KFComment{

    }
}
