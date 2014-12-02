package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class KFCommentsListAdapter extends ArrayAdapter<KFComment> {

    public static final String TAG = "KFCommentsListAdapter";

    Context mContext;
    int mLayoutResourceId;
    List<KFComment> mData = null;

    public KFCommentsListAdapter(Context context, int layoutResourceId, List<KFComment> data) {
        super(context, layoutResourceId, data);

        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CommentHolder commentHolder;
        KFComment comment = mData.get(position);

        Class clazz = comment.getClass();

        // if new row initialize child views, use a view we've scrolled past
        if (row == null) {

            commentHolder = new CommentHolder();
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);


            commentHolder.author = (TextView) row.findViewById(R.id.author);
            commentHolder.text = (TextView) row.findViewById(R.id.text);
            commentHolder.score = (TextView) row.findViewById(R.id.score);
            commentHolder.KFscore = (TextView) row.findViewById(R.id.KFscore);

            commentHolder.score.setText(Integer.toString(comment.score));

            row.setTag(commentHolder);
        } else {
            commentHolder = (CommentHolder) row.getTag();
            if (comment.downVoted) {
                commentHolder.score.setText(Integer.toString(comment.score - 1));
                commentHolder.score.setTextColor(getContext().getResources().getColor(R.color.downvote));
                commentHolder.score.setTextAppearance(getContext(),
                        R.style.boldText);
            }
            else if (comment.upVoted) {
                commentHolder.score.setText(Integer.toString(comment.score + 1));
                commentHolder.score.setTextColor(getContext().getResources().getColor(R.color.upvote));
                commentHolder.score.setTextAppearance(getContext(),
                        R.style.boldText);
            }
            else {
                commentHolder.score.setText(Integer.toString(comment.score));
                commentHolder.score.setTextColor(Color.parseColor("#000000"));
                commentHolder.score.setTextAppearance(getContext(),
                        R.style.normalText);
            }
        }

        commentHolder.author.setText(comment.author);
        commentHolder.text.setText(comment.text);

        Bundle bundle = new Bundle();
        bundle.putInt("originalValue", comment.score);
        commentHolder.score.setTag(bundle);

        commentHolder.KFscore.setText("Karma Potential: " + Integer.toString(comment.KFscore));
        commentHolder.author.setVisibility(View.VISIBLE);
        commentHolder.text.setVisibility(View.VISIBLE);
        commentHolder.score.setVisibility(View.VISIBLE);
        commentHolder.KFscore.setVisibility(View.VISIBLE);

        row.setPadding(30 * comment.depth, 0, 0, 0);
        row.setFocusable(false);
        row.setEnabled(true);
        return row;

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    static class CommentHolder {
        TextView author;
        TextView text;
        TextView score;
        TextView KFscore;
    }



}

