package com.cs371m.ads.karma_farm;

/**
 * Created by Andy on 10/25/2014.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Adapted from Hathy
 * (http://www.whycouch.com/2012/12/how-to-create-android-client-for-reddit.html)
 *
 * While this looks like a lot of code, all this class
 * actually does is load the posts in to the listview.
 *
 * @author Hathy
 */

public class KFSubmissionListFragment extends Fragment{

    ListView mPostsListView;
    KFSubmissionListAdapter mAdapter;
    Handler mHandler;
    String mSubreddit;
    List<KFSubmission> mKFSubmissions;
    KFSubmissionRequester mKFSubmissionRequester;

    private static final String ARG_SUBREDDIT = "subreddit";
    private static final String TAG = "KFSubmissionListFragment";

    public KFSubmissionListFragment(){
        mHandler = new Handler();
        mKFSubmissions = new ArrayList<KFSubmission>();
    }

    public static Fragment newInstance(String subreddit){

        KFSubmissionListFragment listFragment = new KFSubmissionListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_SUBREDDIT, subreddit);
        listFragment.setArguments(args);
        listFragment.mSubreddit = subreddit;
        listFragment.mKFSubmissionRequester = new KFSubmissionRequester(listFragment.mSubreddit);

        return listFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.posts, container, false);

        Log.d(TAG, "found " + mKFSubmissions.size() + " submissions");
        mPostsListView =(ListView)v.findViewById(R.id.posts_list);

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
        if(mKFSubmissions.size()==0){

            // Must execute network tasks outside the UI
            // thread. So create a new thread.
            new Thread(){
                public void run(){
                    mKFSubmissions.addAll(mKFSubmissionRequester.fetchPosts());

                    // UI elements should be accessed only in
                    // the primary thread, so we must use the
                    // handler here.
                    mHandler.post(new Runnable(){
                        public void run(){
                            mAdapter = new KFSubmissionListAdapter(getActivity(), R.layout.post_item, mKFSubmissions);
                            mPostsListView.setAdapter(mAdapter);
                        }
                    });

                }
            }.start();
        } else {
            mAdapter = new KFSubmissionListAdapter(getActivity(), R.layout.post_item, mKFSubmissions);
            mPostsListView.setAdapter(mAdapter);
        }
    }
}