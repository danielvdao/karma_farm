package com.cs371m.ads.karma_farm;

import java.util.ArrayList;

public class KFComment {

    public String author;
    public String text;
    public int karma;
    public int KFscore;
    public int depth;
    public ArrayList<KFComment> replies;

    public KFComment() {

    }

    private class KFMoreComments extends KFComment{

        public KFMoreComments() {

        }
    }
}
