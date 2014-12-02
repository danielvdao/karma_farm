package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class KFSubmissionsListAdapter extends ArrayAdapter<KFSubmission> {

    private static final String TAG = "KFSubmissionsListAdapter";
    Context mContext;
    int mLayoutResourceId;
    List<KFSubmission> mData = null;
    Handler mHandler;
    KFSubmissionsListFragment mCallingFragment;

    public KFSubmissionsListAdapter(Context context, int layoutResourceId, List<KFSubmission> data,
                                    KFSubmissionsListFragment fragment) {
        super(context, layoutResourceId, data);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = data;
        mCallingFragment = fragment;
        mHandler = new Handler();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        SubmissionHolder holder;

        // if new row initialize child views, use a view we've scrolled past
        if(row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new SubmissionHolder();
            holder.score = (TextView)row.findViewById(R.id.post_score);
            holder.title = (TextView)row.findViewById(R.id.post_title);
            holder.details = (TextView)row.findViewById(R.id.post_details);
            holder.thumb = (ImageView)row.findViewById(R.id.thumb);
            holder.comment_button = (ImageButton)row.findViewById(R.id.comment_icon);

            holder.num_comments = (TextView)row
                    .findViewById(R.id.score_board)
                    .findViewById(R.id.comments)
                    .findViewById(R.id.num_comments);

            row.setTag(holder);

        } else {
            holder = (SubmissionHolder)row.getTag();
        }

        // grab submission from array
        KFSubmission submission = mData.get(position);

        // set textviews
        holder.details.setText(submission.getDetails());

        holder.score.setText(submission.getScore());
        Bundle bundle = new Bundle();
        bundle.putBoolean("canVoteUp", true);
        bundle.putBoolean("canVoteDown", true);
        bundle.putInt("originalValue", submission.score);
        holder.score.setTag(bundle);

        holder.title.setText(submission.title);
        holder.num_comments.setText(submission.getNumComments());


        // configure clickable areas
        bundle = new Bundle();

        bundle.putString("url", submission.url);
        holder.title.setTag(bundle);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallingFragment.onSubmissionClick(view);
            }
        });

        bundle.putString("id", submission.id);
        holder.comment_button.setTag(bundle);
        holder.comment_button.setFocusable(false) ;

        holder.comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallingFragment.onSubmissionCommentsClick(view);
            }
        });


        // change comment buttons hit area
        Rect delegateArea = new Rect();
        holder.comment_button.setEnabled(true);
        holder.comment_button.getHitRect(delegateArea);
        delegateArea.right += 100;
        delegateArea.bottom += 100;

        TouchDelegate touchDelegate = new TouchDelegate(delegateArea,
                holder.comment_button);

        if (View.class.isInstance(parent)) {
            parent.setTouchDelegate(touchDelegate);
        }

        // position number nicely
        if (submission.numComments > 999) {
            holder.num_comments.setTranslationX(-30);
        }
        else if (submission.numComments > 99) {
            holder.num_comments.setTranslationX(-20);
        }
        else if (submission.numComments > 9) {
            holder.num_comments.setTranslationX(-10);
        }

        // if no thumbnail remove the view
        if (submission.thumb != null)
            holder.thumb.setImageBitmap(submission.thumb);
        else {
            holder.thumb.setImageResource(R.drawable.placeholder);
        }

        return row;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    static class SubmissionHolder {
        TextView score;
        TextView num_comments;
        TextView title;
        TextView details;
        ImageView thumb;
        ImageButton comment_button;
    }
}
