package com.cs371m.ads.karma_farm;

import java.util.ArrayList;

public class KFComment {

    public String mAuthor;
    public String mSubmissionId;
    public int mKarma;
    public int mKFscore;
    public ArrayList<? extends KFComment> mReplies;

    private class KFMoreComments extends KFComment{

    }
}
