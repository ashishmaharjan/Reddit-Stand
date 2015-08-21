package com.ashish.redditclient.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashish.redditclient.Listerners.OnItemSelectedListener;
import com.ashish.redditclient.R;
import com.ashish.redditclient.activities.MainActivity;
import com.ashish.redditclient.models.SearchSubreddit;

import java.util.List;

/**
 * Created by User on 7/28/2015.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchSubredditViewHolder> {

    private List<SearchSubreddit> subRedditsList;
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemSelectedListener mListener;
    private Typeface noto, notoBold;

    public SearchAdapter(Context mContext, OnItemSelectedListener mListener, List<SearchSubreddit> subRedditsList) {
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.subRedditsList = subRedditsList;
        this.mListener = mListener;
    }

    @Override
    public SearchSubredditViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mInflater.inflate(R.layout.search_row, null);
        SearchSubredditViewHolder holder = new SearchSubredditViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchSubredditViewHolder holder, int position) {
        SearchSubreddit listItems = subRedditsList.get(position);
        noto = Typeface.createFromAsset(mContext.getAssets(), "fonts/NotoSans-Regular.ttf");
        notoBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/NotoSans-Bold.ttf");

        holder.subredditUrl.setText(listItems.getUrl());
        holder.subredditUrl.setTypeface(notoBold);

        holder.publicDescription.setText(listItems.getPublicDescription());
        holder.publicDescription.setTypeface(noto);
    }

    @Override
    public int getItemCount() {
        return (null != subRedditsList ? subRedditsList.size() : 0);
    }

    class SearchSubredditViewHolder extends RecyclerView.ViewHolder {
        public TextView subredditUrl, publicDescription;

        public SearchSubredditViewHolder(View itemView) {
            super(itemView);
            this.subredditUrl = (TextView) itemView.findViewById(R.id.subreddit_url_search);
            this.publicDescription = (TextView) itemView.findViewById(R.id.public_descripton);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void clearAdapter() {
        subRedditsList.clear();
        notifyDataSetChanged();
    }

}