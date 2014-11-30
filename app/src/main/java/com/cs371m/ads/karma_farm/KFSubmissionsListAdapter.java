package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class KFSubmissionsListAdapter extends ArrayAdapter<KFSubmission> {

    private static final String TAG = "KFSubmissionsListAdapter";
    Context mContext;
    int mLayoutResourceId;
    List<KFSubmission> mData = null;
    Handler mHandler;

    public KFSubmissionsListAdapter(Context context, int layoutResourceId, List<KFSubmission> data) {
        super(context, layoutResourceId, data);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = data;
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

            row.setTag(holder);

        } else {
            holder = (SubmissionHolder)row.getTag();
        }

        KFSubmission submission = mData.get(position);

        holder.title.setText(submission.title);
        holder.details.setText(submission.getDetails());
        holder.score.setText(submission.getScore());

        // if no thumbnail remove the view
        if (submission.thumb != null)
            holder.thumb.setImageBitmap(submission.thumb);
        else {
            holder.thumb.setImageResource(R.drawable.placeholder);
        }

        return row;
    }

    static class SubmissionHolder {
        TextView score;
        TextView title;
        TextView details;
        ImageView thumb;
    }
}
