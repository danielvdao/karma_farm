package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by stipton on 10/25/14.
 */
public class KarmaFarmSubmissionAdapter extends ArrayAdapter<KarmaFarmSubmission> {

    Context context;
    int layoutResourceId;
    KarmaFarmSubmission data[] = null;

    public KarmaFarmSubmissionAdapter(Context context, int layoutResourceId,
                                      KarmaFarmSubmission[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        KarmaFarmSubmissionHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new KarmaFarmSubmissionHolder();
            holder.image = (ImageView)row.findViewById(R.id.imgIcon);
            holder.title = (TextView)row.findViewById(R.id.title);

            row.setTag(holder);

        } else {
            holder = (KarmaFarmSubmissionHolder)row.getTag();
        }
        KarmaFarmSubmission submission = data[position];
        holder.title.setText(submission.title);
        holder.image.setImageResource(submission.image);

        return row;
    }

    static class KarmaFarmSubmissionHolder {
        ImageView image;
        TextView title;
    }
}
