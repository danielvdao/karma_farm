package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by stipton on 10/26/14.
 */
public class KFSubmissionListAdapter extends ArrayAdapter<KFSubmission> {

    Context mContext;
    int mLayoutResourceId;
    List<KFSubmission> mData = null;

    public KFSubmissionListAdapter(Context context, int layoutResourceId, List<KFSubmission> data) {
        super(context, layoutResourceId, data);

        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        SubmissionHolder holder = null;

        // if new row initialize child views, use a view we've scrolled past
        if(row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new SubmissionHolder();
            holder.score = (TextView)row.findViewById(R.id.post_score);
            holder.title = (TextView)row.findViewById(R.id.post_title);
            holder.details = (TextView)row.findViewById(R.id.post_details);

            row.setTag(holder);

        } else {
            holder = (SubmissionHolder)row.getTag();
        }

        KFSubmission p = mData.get(position);

        holder.title.setText(p.title);
        holder.details.setText(p.getDetails());
        holder.score.setText(p.getScore());
        return row;
    }

    static class SubmissionHolder {
        TextView score;
        TextView title;
        TextView details;
    }
}
