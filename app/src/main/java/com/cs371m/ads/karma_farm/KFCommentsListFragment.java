package com.cs371m.ads.karma_farm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.getIntent;

public class KFCommentsListFragment extends ListFragment implements SwipeVoteable {

    private KFCommentsListAdapter mAdapter;
    private Handler mHandler;
    private List<KFComment> mComments;
    private KFCommentsRequester mCommentsRequester;
    private ProgressBar spinner;

    private static final String ARG_SUBREDDIT = "subreddit";

    private static final String TAG = "KFCommentsListFragment";
    private static final HorizontalSwipeDetector swipeDetector = new HorizontalSwipeDetector();

    public KFCommentsListFragment() {
        mHandler = new Handler();
    }

    public static Fragment newInstance(String id){

        KFCommentsListFragment commentsFragment = new KFCommentsListFragment();

        Bundle args = new Bundle();
        args.putString("submissionId", id);
        commentsFragment.setArguments(args);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstancesState) {
        Log.d(TAG, "view created");
        getListView().setOnTouchListener(swipeDetector);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                Log.d(TAG, "item clicked");
                KFComment comment = mComments.get(position);
                if (swipeDetector.swipeDetected()) {

                    TextView scoreTextView = (TextView)view.findViewById(R.id.score);
                    int originalValue = comment.score;

                    int score = Integer.parseInt(scoreTextView.getText().toString());

                    if (swipeDetector.getAction() == HorizontalSwipeDetector.Action.RL) {
                        if(score <= originalValue) {
                            Log.d(TAG, "upvote swipe on" + position);
                            comment.upVoted = true;
                            comment.downVoted = false;

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
                            comment.upVoted = false;
                            comment.downVoted = true;

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
            }
        };
        getListView().setOnItemClickListener(listener);

        AdapterView.OnItemLongClickListener longListener = new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                Log.d(TAG, "long press");
                TextView scoreTextView = (TextView)view.findViewById(R.id.score);
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

    public void postCommentDialog(Bundle args) {
        getActivity().showDialog(KFMain.COMMENT_DIALOG, args);
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

    private void initialize(){
        // This should run only once for the fragment as the
        // setRetainInstance(true) method has been called on
        // this fragment
        Log.d(TAG, "initializing list");
        spinner = (ProgressBar) getView().findViewById(R.id.comments_progress_bar);
        spinner.setVisibility(View.VISIBLE);

        if(mComments.size() == 0){
            final KFCommentsListFragment fragment = this;
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
                            try {
                                mAdapter = new KFCommentsListAdapter(getActivity(), R.layout.comment_item, mComments, fragment);
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
            mAdapter = new KFCommentsListAdapter(getActivity(), R.layout.comment_item, mComments, this);
            setListAdapter(mAdapter);
            spinner.setVisibility(View.GONE);
        }
    }


}
