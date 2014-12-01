package com.cs371m.ads.karma_farm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;

public class KFSubmissionsListFragment extends ListFragment implements SwipeVoteable {

    KFSubmissionsListAdapter mAdapter;
    Handler mHandler;
    String mSubreddit;
    List<KFSubmission> mKFSubmissions;
    KFSubmissionsRequester mKFSubmissionsRequester;
    SubmissionListListener mListener;
    private ProgressBar spinner;
    private HorizontalSwipeDetector swipeDetector;

    private static final String ARG_SUBREDDIT = "subreddit";
    private static final String TAG = "KFSubmissionsListFragment";


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mLoading;

    public KFSubmissionsListFragment(){

        mHandler = new Handler();
        mKFSubmissions = new ArrayList<KFSubmission>();
        mLoading = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        swipeDetector = new HorizontalSwipeDetector(this);
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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.posts, container, false);

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
//        mSwipeRefreshLayout.setColorScheme(
//                R.color.swipe_color_1, R.color.swipe_color_2,
//                R.color.swipe_color_3, R.color.swipe_color_4);
        // END_INCLUDE (change_colors)

        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstancesState) {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                initialize();
            }
        });

        getActivity().setProgressBarIndeterminateVisibility(false);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {



                    if (!mLoading) {
                        mLoading = true;
                        getActivity().setProgressBarIndeterminateVisibility(true);
                        new Thread() {
                            public void run() {
                                final ArrayList<KFSubmission> more = (ArrayList<KFSubmission>) mKFSubmissionsRequester.fetchMorePosts();

                                mHandler.post(new Runnable() {
                                    public void run() {
                                        try {
                                            getActivity().setProgressBarIndeterminateVisibility(false);
                                            addMoreSubmissions(more);
                                        } catch (NullPointerException e) {
                                            // this happens when activity is destroyed during load
                                            Log.d(TAG, "NPE after load. Attempt to fail gracefully");
                                        }
                                    }
                                });
                            }
                        }.start();
                    }
                }
            }
        });

        getListView().setOnTouchListener(swipeDetector);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Log.d(TAG, "item clicked");
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == HorizontalSwipeDetector.Action.RL) {
                        Log.d(TAG, "swipe right to left on " + position);

                    } else {
                        Log.d(TAG, "swipe left to right on " + position);
                    }
                }
            }
        };
        getListView().setOnItemClickListener(listener);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    // KFMain must implement this interface to handle list item click
    public interface SubmissionListListener {

        public void onSubmissionSelected(String url);
        public void onSubmissionCommentsSelected(String id);
    }
    public void onUpVote() {

    }

    public void onDownVote() {

    }

    public void onSubmissionClick(View view) {
        Bundle bundle = (Bundle) view.getTag();
        String url = bundle.getString("url");
        Log.d(TAG, "submission clicked");
        mListener.onSubmissionSelected(url);
    }

    public void onSubmissionCommentsClick(View view) {
        Bundle bundle = (Bundle) view.getTag();
        String id = bundle.getString("id");
        Log.d(TAG, "comments clicked");
        mListener.onSubmissionCommentsSelected(id);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (SubmissionListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    private void initialize(){
        // This should run only once for the fragment as the
        // setRetainInstance(true) method has been called on
        // this fragment
        Log.d(TAG, "requesting submissions list");
        spinner = (ProgressBar) getView().findViewById(R.id.submissions_progress_bar);
        spinner.setVisibility(View.VISIBLE);

        if(mKFSubmissions.size() == 0){

            final KFSubmissionsListFragment fragment = this;
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
                                mAdapter = new KFSubmissionsListAdapter(getActivity(), R.layout.post_item, mKFSubmissions, fragment);
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
            mAdapter = new KFSubmissionsListAdapter(getActivity(), R.layout.post_item, mKFSubmissions, this);
            setListAdapter(mAdapter);
            spinner.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void addMoreSubmissions(ArrayList<KFSubmission> more) {
        mKFSubmissions.addAll(more);
        mAdapter.notifyDataSetChanged();
        mLoading = false;
    }
}