package com.cs371m.ads.karma_farm;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class KFCommentsListFragment extends ListFragment {

    KFCommentsListAdapter mAdapter;
    Handler mHandler;
    List<KFComment> mComments;
    KFCommentsRequester mCommentsRequester;
    private ProgressBar spinner;

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
        mComments = new ArrayList<KFComment>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.comments, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        initialize();
        super.onActivityCreated(savedInstanceState);

    }

    private void initialize(){
        // This should run only once for the fragment as the
        // setRetainInstance(true) method has been called on
        // this fragment
        Log.d(TAG, "initializing list");
        spinner = (ProgressBar) getView().findViewById(R.id.comments_progress_bar);
        spinner.setVisibility(View.VISIBLE);

        if(mComments.size() == 0){

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
                            setListAdapter(mAdapter);
                            spinner.setVisibility(View.GONE);
                        }
                    });

                }
            }.start();
        } else {
            mAdapter = new KFCommentsListAdapter(getActivity(), R.layout.comment_item, mComments);
            setListAdapter(mAdapter);
            spinner.setVisibility(View.GONE);
        }
    }
}
