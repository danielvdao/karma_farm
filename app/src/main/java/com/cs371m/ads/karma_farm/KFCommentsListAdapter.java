package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

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

        Class elem = mData.get(position).getClass();

        // if new row initialize child views, use a view we've scrolled past
        if (row == null) {

            commentHolder = new CommentHolder();
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);


            commentHolder.author = (TextView) row.findViewById(R.id.author);
            commentHolder.text = (TextView) row.findViewById(R.id.text);
            commentHolder.karma = (TextView) row.findViewById(R.id.karma);
            commentHolder.KFscore = (TextView) row.findViewById(R.id.score);
            commentHolder.moreComments = (Button) row.findViewById(R.id.more_comments_button);

            row.setTag(commentHolder);
        } else {
            commentHolder = (CommentHolder) row.getTag();
        }

        KFComment comment = mData.get(position);

        if (elem != KFComment.KFMoreComments.class) {
            commentHolder.author.setText(comment.author);
            commentHolder.text.setText(comment.text);
            commentHolder.karma.setText(Integer.toString(comment.karma));
            commentHolder.KFscore.setText("Karma Potential: " + Integer.toString(comment.KFscore));
            commentHolder.author.setVisibility(View.VISIBLE);
            commentHolder.text.setVisibility(View.VISIBLE);
            commentHolder.karma.setVisibility(View.VISIBLE);
            commentHolder.KFscore.setVisibility(View.VISIBLE);
            commentHolder.moreComments.setVisibility(View.GONE);
        } else {
            commentHolder.author.setVisibility(View.GONE);
            commentHolder.text.setVisibility(View.GONE);
            commentHolder.karma.setVisibility(View.GONE);
            commentHolder.KFscore.setVisibility(View.GONE);
            commentHolder.moreComments.setVisibility(View.VISIBLE);

//            commentHolder.moreComments.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    mCallingFragment.onMoreCommentsSelected(view);
//                }
//            });
        }

        row.setPadding(30 * comment.depth, 0, 0, 0);
        row.setFocusable(false);
        row.setEnabled(false);
        row.setOnClickListener(null);
        return row;

    }

    static class CommentHolder {
        TextView author;
        TextView text;
        TextView karma;
        TextView KFscore;
        Button moreComments;
    }

}

