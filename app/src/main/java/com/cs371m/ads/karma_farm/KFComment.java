package com.cs371m.ads.karma_farm;

public class KFComment {

    public String author;
    public String text;
    public int score;
    public int KFscore;
    public int depth;
    public boolean upVoted;
    public boolean downVoted;
    public String id;

    public static class KFMoreComments extends KFComment{

    }
}
