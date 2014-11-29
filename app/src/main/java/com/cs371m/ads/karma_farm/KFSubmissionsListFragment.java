package com.cs371m.ads.karma_farm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class KFSubmissionsListFragment extends ListFragment {

    KFSubmissionsListAdapter mAdapter;
    Handler mHandler;
    String mSubreddit;
    List<KFSubmission> mKFSubmissions;
    KFSubmissionsRequester mKFSubmissionsRequester;
    OnSubmissionSelectedListener mListener;
    private ProgressBar spinner;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.posts, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    // KFMain must implement this interface
    public interface OnSubmissionSelectedListener {
        public void onSubmissionSelected(String id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSubmissionSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Append the clicked item's row ID with the content provider Uri
        KFSubmission sub = mKFSubmissions.get(position);
        mListener.onSubmissionSelected(sub.url);
    }

    private void initialize(){
        // This should run only once for the fragment as the
        // setRetainInstance(true) method has been called on
        // this fragment
        Log.d(TAG, "requesting submissions list");
        spinner = (ProgressBar) getView().findViewById(R.id.submissions_progress_bar);
        spinner.setVisibility(View.VISIBLE);

        if(mKFSubmissions.size() == 0){

            // Must execute network tasks outside the UI
            // thread. So create a new thread.
            new Thread(){
                public void run(){
                    try {
                        mKFSubmissions.addAll(mKFSubmissionsRequester.requestSubmissionList());
                        // UI elements should be accessed only in
                        // the primary thread, so we must use the
                        // handler here.
                    } catch (NullPointerException e) {
                        Log.d(TAG, "NPE adapting array during load. Attempt to fail gracefully");
                    }
                    mHandler.post(new Runnable(){
                        public void run(){
                            try {
                                mAdapter = new KFSubmissionsListAdapter(getActivity(), R.layout.post_item, mKFSubmissions);
                                setListAdapter(mAdapter);
                                spinner.setVisibility(View.GONE);
                            } catch (NullPointerException e) {
                                // this happens when activity is destroyed during load
                                Log.d(TAG, "NPE after load. Attempt to fail gracefully");
                            }
                        }
                    });
                }
            }.start();
        } else {
            Log.d(TAG, "using old list");
            mAdapter = new KFSubmissionsListAdapter(getActivity(), R.layout.post_item, mKFSubmissions);
            setListAdapter(mAdapter);
            spinner.setVisibility(View.GONE);
        }
    }
}