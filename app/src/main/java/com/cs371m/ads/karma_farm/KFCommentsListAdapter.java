package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by stipton on 10/28/14.
 */
public class KFCommentsListAdapter extends ArrayAdapter<KFComment>{

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
        CommentHolder holder;

        // if new row initialize child views, use a view we've scrolled past
        if(row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new CommentHolder();
            holder.author = (TextView)row.findViewById(R.id.author);
            holder.text = (TextView)row.findViewById(R.id.text);
            holder.karma = (TextView)row.findViewById(R.id.karma);
            holder.KFscore = (TextView)row.findViewById(R.id.score);

            row.setTag(holder);

        } else {
            holder = (CommentHolder)row.getTag();
        }

        KFComment comment = mData.get(position);
        holder.author.setText(comment.author);
        holder.text.setText(comment.text);
        holder.karma.setText(comment.karma);
        holder.KFscore.setText(comment.KFscore);

        return row;
    }

    static class CommentHolder {
        TextView author;
        TextView text;
        TextView karma;
        TextView KFscore;
    }


}
