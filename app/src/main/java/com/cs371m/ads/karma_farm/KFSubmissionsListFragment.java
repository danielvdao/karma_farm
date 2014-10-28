package com.cs371m.ads.karma_farm;

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

public class KFSubmissionsListFragment extends Fragment{

    ListView mPostsListView;
    KFSubmissionsListAdapter mAdapter;
    Handler mHandler;
    String mSubreddit;
    List<KFSubmission> mKFSubmissions;
    KFSubmissionsRequester mKFSubmissionsRequester;

    private static final String ARG_SUBREDDIT = "subreddit";
    private static final String TAG = "KFSubmissionsListFragment";

    public KFSubmissionsListFragment(){
        mHandler = new Handler();
        mKFSubmissions = new ArrayList<KFSubmission>();
    }

    public static Fragment newInstance(String subreddit){

        KFSubmissionsListFragment listFragment = new KFSubmissionsListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_SUBREDDIT, subreddit);
        listFragment.setArguments(args);
        listFragment.mSubreddit = subreddit;
        listFragment.mKFSubmissionsRequester = new KFSubmissionsRequester(listFragment.mSubreddit);

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
                    mKFSubmissions.addAll(mKFSubmissionsRequester.requestSubmissionList());

                    // UI elements should be accessed only in
                    // the primary thread, so we must use the
                    // handler here.
                    mHandler.post(new Runnable(){
                        public void run(){
                            mAdapter = new KFSubmissionsListAdapter(getActivity(), R.layout.post_item, mKFSubmissions);
                            mPostsListView.setAdapter(mAdapter);
                        }
                    });

                }
            }.start();
        } else {
            mAdapter = new KFSubmissionsListAdapter(getActivity(), R.layout.post_item, mKFSubmissions);
            mPostsListView.setAdapter(mAdapter);
        }
    }
}