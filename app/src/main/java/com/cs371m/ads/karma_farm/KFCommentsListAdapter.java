package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

public class KFCommentsListAdapter extends ArrayAdapter<KFComment>{

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

        if (elem == KFComment.KFMoreComments.class) {
            MoreCommentsHolder moreCommentsHolder = new MoreCommentsHolder();
            moreCommentsHolder.msg = (TextView) row.findViewById(R.id.msg);
        } else {
            commentHolder = new CommentHolder();
            // if new row initialize child views, use a view we've scrolled past
            if (row == null) {

                if (elem == KFComment.class) {
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    row = inflater.inflate(mLayoutResourceId, parent, false);

                    commentHolder.author = (TextView) row.findViewById(R.id.author);
                    commentHolder.text = (TextView) row.findViewById(R.id.text);
                    commentHolder.karma = (TextView) row.findViewById(R.id.karma);
                    commentHolder.KFscore = (TextView) row.findViewById(R.id.score);


                    row.setTag(commentHolder);
                }
            } else {
                commentHolder = (CommentHolder) row.getTag();
            }

            KFComment comment = mData.get(position);
            commentHolder.author.setText(comment.author);
            commentHolder.text.setText(comment.text);
            commentHolder.karma.setText(Integer.toString(comment.karma));
            commentHolder.KFscore.setText("Karma Potential: " + Integer.toString(comment.KFscore));

            try {
                row.setPadding(15 * comment.depth, 0, 0, 0);
            } catch (NullPointerException e) {
                Log.d(TAG, "Null Pointer Exception on comment.detph");
            }
        }
        return row;
    }

    static class CommentHolder {
        TextView author;
        TextView text;
        TextView karma;
        TextView KFscore;
    }

    static class MoreCommentsHolder {
        // later should add button to request
        // another comment tree at this node
        TextView msg;
    }


}
