package com.ashish.redditclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.ashish.redditclient.R;
import com.ashish.redditclient.activities.WebViewActivity;
import com.ashish.redditclient.helpers.MySingleton;
import com.ashish.redditclient.models.ListItems;
import com.ashish.redditclient.viewholders.ListRowViewHolder;

/**
 * Author: Ashish
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<ListRowViewHolder> {

    private List<ListItems> listItemsList;
    private Context mContext;
    private ImageLoader mImageLoader;

    private Typeface noto, notoBold, notoItalic;

    private int focusedItem = 0;

    public MyRecyclerAdapter(Context context, List<ListItems> listItemsList) {
        this.listItemsList = listItemsList;
        this.mContext = context;
    }

    public MyRecyclerAdapter(Context context) {
        this.listItemsList = new ArrayList<>();
        this.mContext = context;
    }

    public void setData(List<ListItems> listItems) {
        this.listItemsList = listItems;
        notifyDataSetChanged();
    }

    public List<ListItems> getData() {
        return listItemsList;
    }

    @Override
    public ListRowViewHolder onCreateViewHolder(final ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        ListRowViewHolder holder = new ListRowViewHolder(v);

        holder.recLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("List Size", Integer.toString(getItemCount()));

                TextView redditUrl = (TextView) v.findViewById(R.id.url);
                String postUrl = redditUrl.getText().toString();
                Log.d("THe URS", postUrl);
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("url", postUrl);
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ListRowViewHolder listRowViewHolder, int position) {
        ListItems listItems = listItemsList.get(position);
        listRowViewHolder.itemView.setSelected(focusedItem == position);

        noto = Typeface.createFromAsset(mContext.getAssets(), "fonts/NotoSans-Regular.ttf");
        notoBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/NotoSans-Bold.ttf");
        notoItalic = Typeface.createFromAsset(mContext.getAssets(), "fonts/NotoSans-Italic.ttf");

        listRowViewHolder.getLayoutPosition();

        mImageLoader = MySingleton.getInstance(mContext).getImageLoader();

        listRowViewHolder.thumbnail.setImageUrl(listItems.getThumbnail(), mImageLoader);
        listRowViewHolder.thumbnail.setDefaultImageResId(R.drawable.reddit_placeholder);

        listRowViewHolder.title.setText(Html.fromHtml(listItems.getTitle()));
        listRowViewHolder.title.setTypeface(notoBold);

        listRowViewHolder.url.setText(Html.fromHtml(listItems.getUrl()));
        listRowViewHolder.url.setTypeface(notoItalic);

        listRowViewHolder.subreddit.setText(Html.fromHtml(listItems.getSubreddit()));
        listRowViewHolder.subreddit.setTypeface(noto);

        listRowViewHolder.author.setText(Html.fromHtml(listItems.getAuthor()));
        listRowViewHolder.author.setTypeface(noto);

        listRowViewHolder.score.setText(Html.fromHtml(listItems.getScore()));
        listRowViewHolder.score.setTypeface(notoBold);
    }

    public void clearAdapter() {
        listItemsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != listItemsList ? listItemsList.size() : 0);
    }

}