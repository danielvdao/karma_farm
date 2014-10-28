package com.cs371m.ads.karma_farm;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class KFCommentsListFragment extends Fragment{

    KFCommentsListView mCommentsListView;
    KFCommentsListAdapter mAdapter;
    Handler mHandler;
    String mSubreddit;
    JSONArray mComments;
    KFCommentsRequester mCommentsRequester;

    private static final String ARG_SUBREDDIT = "subreddit";
    private static final String TAG = "KFSubmissionsListFragment";

    public KFCommentsListFragment(){
        mHandler = new Handler();
    }

    public static Fragment newInstance(String id){

        KFCommentsListFragment commentsFragment = new KFCommentsListFragment();
        commentsFragment.mCommentsRequester = new KFCommentsRequester(id);

        return commentsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.comments, container, false);

        mCommentsListView = (KFCommentsListView) v.findViewById(R.id.comments_list);

        return v;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initialize(){
        // This should run only once for the fragment as the
        // setRetainInstance(true) method has been called on
        // this fragment
        Log.d(TAG, "initializing list");
        if(mComments.isNull(0)){

            // Must execute network tasks outside the UI
            // thread. So create a new thread.
            new Thread(){
                public void run(){
                    mComments = mCommentsRequester.requestComments();
                    // UI elements should be accessed only in
                    // the primary thread, so we must use the
                    // handler here.
                    mHandler.post(new Runnable(){
                        public void run(){
                            mAdapter = new KFCommentsListAdapter(getActivity(), R.layout.comment_item, mComments);
                            mCommentsListView.setAdapter(mAdapter);
                        }
                    });

                }
            }.start();
        } else {
            mAdapter = new KFCommentsListAdapter(getActivity(), R.layout.comment_item, mComments);
            mCommentsListView.setAdapter(mAdapter);
        }
    }
}
