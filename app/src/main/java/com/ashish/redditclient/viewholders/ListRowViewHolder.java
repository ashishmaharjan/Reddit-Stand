package com.ashish.redditclient.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.ashish.redditclient.R;

/**
 * Author: Ashish
 */
public class ListRowViewHolder extends RecyclerView.ViewHolder {
    public NetworkImageView thumbnail;
    public TextView title;
    public TextView url;
    public RelativeLayout recLayout;
    public TextView author;
    public TextView subreddit;
    public TextView score;

    public ListRowViewHolder(View view) {
        super(view);
        this.thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
        this.url = (TextView) view.findViewById(R.id.url);
        this.recLayout = (RelativeLayout) view.findViewById(R.id.recLayout);
        this.subreddit = (TextView) view.findViewById(R.id.subreddit);
        this.author = (TextView) view.findViewById(R.id.author);
        this.score = (TextView) view.findViewById(R.id.score);
        view.setClickable(true);
    }

}