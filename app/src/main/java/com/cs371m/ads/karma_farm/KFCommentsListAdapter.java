package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class KFCommentsListAdapter extends ArrayAdapter<KFComment> {

    public static final String TAG = "KFCommentsListAdapter";

    Context mContext;
    int mLayoutResourceId;
    List<KFComment> mData = null;
    KFCommentsListFragment mListFragment;

    public KFCommentsListAdapter(Context context, int layoutResourceId, List<KFComment> data,
                                 KFCommentsListFragment listFragment) {
        super(context, layoutResourceId, data);

        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
        this.mListFragment = listFragment;
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
            commentHolder.commentButton = (ImageView) row.findViewById(R.id.comment);

            commentHolder.score.setText(Integer.toString(comment.score));
            Bundle bundle = new Bundle();
            bundle.putString("id", comment.id);
            commentHolder.commentButton.setTag(bundle);
            row.setTag(commentHolder);
        } else {
            commentHolder = (CommentHolder) row.getTag();
        }

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

        Bundle bundle = (Bundle) commentHolder.commentButton.getTag();
        bundle.putInt("position", position);
        commentHolder.commentButton.setTag(bundle);

        commentHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create post dialog
                // make post to backend with username and password
                Bundle bundle = (Bundle) view.getTag();
                String id = bundle.getString("id");
                int pos = bundle.getInt("position");

                if (id != null) {
                    mListFragment.postCommentDialog(bundle, pos);
                }
            }
        });

        commentHolder.author.setText(comment.author);
        commentHolder.text.setText(comment.text);

        bundle = new Bundle();
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
        ImageView commentButton;
        TextView author;
        TextView text;
        TextView score;
        TextView KFscore;
    }



}

