package com.cs371m.ads.karma_farm;

import android.content.Context;
import android.widget.ArrayAdapter;

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


}
