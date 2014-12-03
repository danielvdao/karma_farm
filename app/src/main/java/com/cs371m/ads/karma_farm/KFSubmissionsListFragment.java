package com.cs371m.ads.karma_farm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;

public class KFSubmissionsListFragment extends ListFragment implements SwipeVoteable {

    KFSubmissionsListAdapter mAdapter;
    Handler mHandler;
    String mSubreddit;
    List<KFSubmission> mKFSubmissions;
    KFSubmissionsRequester mKFSubmissionsRequester;
    SubmissionListListener mListener;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    private ProgressBar spinner;
    private HorizontalSwipeDetector swipeDetector;

    private static final String ARG_SUBREDDIT = "subreddit";
    private static final String TAG = "KFSubmissionsListFragment";
    private HashSet<String> mOld_posts;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mLoading;

    public KFSubmissionsListFragment(){

        mHandler = new Handler();
        mKFSubmissions = new ArrayList<KFSubmission>();
        mOld_posts = new HashSet<String>();
        mLoading = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        swipeDetector = new HorizontalSwipeDetector();

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static Fragment newInstance(String subreddit){

        KFSubmissionsListFragment listFragment = new KFSubmissionsListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_SUBREDDIT, subreddit);
        listFragment.setArguments(args);
        listFragment.mSubreddit = subreddit;
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
                if (!mLoading) {
                    mLoading = true;
                    mSwipeRefreshLayout.setRefreshing(true);
                    new Thread() {
                        public void run() {
                            final ArrayList<KFSubmission> more =
                                    (ArrayList<KFSubmission>) mKFSubmissionsRequester.requestSubmissionList();

                            if (more.size() > 0) {
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        try {
                                            addMoreSubmissionsToFront(more);
                                            mSwipeRefreshLayout.setRefreshing(false);

                                        } catch (NullPointerException e) {
                                            // this happens when activity is destroyed during load
                                            Log.d(TAG, "NPE after load. Attempt to fail gracefully");
                                        }
                                    }
                                });
                            }
                        }
                    }.start();
                }
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

                                if (more.size() > 0) {
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
                            }
                        }.start();
                    }
                }
            }
        });


        // Vote by swiping setup listeners
        getListView().setOnTouchListener(swipeDetector);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                Log.d(TAG, "item clicked");
                KFSubmission submission = mKFSubmissions.get(position);
                if (swipeDetector.swipeDetected()) {

                    TextView scoreTextView = (TextView)view.findViewById(R.id.score_board)
                            .findViewById(R.id.post_score);
                    int originalValue = submission.score;

                    int score = Integer.parseInt(scoreTextView.getText().toString());

                    if (swipeDetector.getAction() == HorizontalSwipeDetector.Action.RL) {
                        if(score <= originalValue) {
                            Log.d(TAG, "upvote swipe on" + position);
                            submission.isUpVoted = true;
                            submission.isDownVoted = false;

                            int newScore = (score == originalValue) ? score + 1 : score + 2;

                            scoreTextView.setText(Integer.toString(newScore));
                            scoreTextView.setTextColor(getResources().getColor(R.color.upvote));
                            scoreTextView.setTextAppearance(getActivity().getApplicationContext(),
                                    R.style.boldText);
                            flashOrange(view);
                        }

                    } else {
                        if(score >= originalValue) {
                            Log.d(TAG, "downvote swipe on " + position);
                            submission.isUpVoted = false;
                            submission.isDownVoted = true;

                            int newScore = (score == originalValue) ? score - 1 : score - 2;

                            scoreTextView.setText(Integer.toString(newScore));
                            scoreTextView.setTextColor(getResources().getColor(R.color.downvote));
                            scoreTextView.setActivated(true);
                            scoreTextView.setTextAppearance(getActivity().getApplicationContext(),
                                    R.style.boldText);
                            flashBlue(view);
                        }
                    }
                }
                else {
                    onSubmissionClick(view);
                }

            }
        };
        getListView().setOnItemClickListener(listener);

        AdapterView.OnItemLongClickListener longListener = new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {

                TextView scoreTextView = (TextView)view.findViewById(R.id.score_board)
                        .findViewById(R.id.post_score);
                Bundle bundle = (Bundle) scoreTextView.getTag();
                int originalValue = bundle.getInt("originalValue");
                int score = Integer.parseInt(scoreTextView.getText().toString());
                if (score != originalValue){
                    scoreTextView.setText(Integer.toString(originalValue));
                    scoreTextView.setTextColor(Color.parseColor("#000000"));
                    scoreTextView.setTextAppearance(getActivity().getApplicationContext(),
                            R.style.normalText);
                    return true;
                }
                return false;
            }
        };
        getListView().setOnItemLongClickListener(longListener);
    }

    public void flashOrange(View view) {
        final int orig = view.getSolidColor();
        final float[] from = new float[3];
        final float[] to = new float[3];

        final View list_item = view;

        Color.colorToHSV(Color.parseColor("#eeeeee"), from);   // from white
        Color.colorToHSV(Color.parseColor("#ff8b60"), to);     // to orange

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(200);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                list_item.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                list_item.setBackgroundColor(orig);
            }
        });

        anim.start();

    }

    public void flashBlue(View view) {
        final int orig = view.getSolidColor();
        final float[] from = new float[3];
        final float[] to = new float[3];

        final View list_item = view;
        Color.colorToHSV(Color.parseColor("#ffffff"), from);   // from white
        Color.colorToHSV(Color.parseColor("#9494ff"), to);     // to blue

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(200);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        hsv[0] = to[0];                                     // hold hue steady
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                list_item.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                list_item.setBackgroundColor(orig);
            }
        });

        anim.start();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    public interface SubmissionListListener {

        public void onSubmissionSelected(String url);
        public void onSubmissionCommentsSelected(String id);
    }


    public void onSubmissionClick(View view) {
        Bundle bundle = null;
        try {
            bundle = (Bundle) view.getTag();
        } catch (ClassCastException e) {
            bundle = (Bundle) view.findViewById(R.id.post_title).getTag();
        }
        if (bundle != null) {
            String url = bundle.getString("url");
            Log.d(TAG, "submission clicked");
            mListener.onSubmissionSelected(url);
        }
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
        mKFSubmissionsRequester = new KFSubmissionsRequester(mSubreddit);
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

                        for(KFSubmission sub : mKFSubmissions) {
                            mOld_posts.add(sub.title);
                        }

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

        for (KFSubmission sub : more) {
            if (!mOld_posts.contains(sub.title)) {
                mAdapter.add(sub);
                mOld_posts.add(sub.title);
            }
        }

        mAdapter.notifyDataSetChanged();
        mLoading = false;
    }

    public void addMoreSubmissionsToFront(ArrayList<KFSubmission> more) {

        int i = 0;
        for (KFSubmission sub : more) {
            if (!mOld_posts.contains(sub.title)) {
                Log.d(TAG, "found new post " + sub.title);
                mAdapter.insert(sub, i++);
                mOld_posts.add(sub.title);
            }
        }
        Log.d(TAG, "looking at " + mKFSubmissions.size() + " posts");
        mAdapter.notifyDataSetChanged();
        mLoading = false;
    }
}