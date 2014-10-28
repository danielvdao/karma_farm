package com.cs371m.ads.karma_farm;

import android.content.Context;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by stipton on 10/28/14.
 */
public class KFCommentsListAdapter {

    Context mContext;
    int mLayoutResourceId;
    JSONArray mData = null;

    public KFCommentsListAdapter(Context context, int layoutResourceId, JSONArray data) {
        super(context, layoutResourceId, data);

        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
    }
}
