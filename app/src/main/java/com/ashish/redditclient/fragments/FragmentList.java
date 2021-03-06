package com.ashish.redditclient.fragments;

import android.app.Fragment;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ashish.redditclient.R;
import com.ashish.redditclient.adapter.MyRecyclerAdapter;
import com.ashish.redditclient.helpers.EndlessRecyclerOnScrollListener;
import com.ashish.redditclient.models.ListItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentList extends Fragment {

    private static final String TAG = "RecyclerViewExample";

    private ProgressBar mProgressBar;

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;

    private int initialCount = 0;
    private int counter = 0;

    private String count;
    private String after_id = null;
    private String subreddit;
    private String url;
    private String subUrl;

    private static final String redditUrl = "http://www.reddit.com/";
    private static final String jsonEnd = ".json";
    private static final String qCount = "?count=";
    private static final String after = "&after=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getArguments().getString("url");
        subUrl = getArguments().getString("subUrl");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        subreddit = url + subUrl;

        //Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        //Initialize ProgressBar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

            updateList(subreddit);

            mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int current_page) {
                    Log.d("SCROLL PAST UPDATE", "You hit me");

                    Log.d("after", after_id + "__");

                    if (after_id != null && !(initialCount < 25)) {

                        //maintain scroll position
                        int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                        loadMore(subreddit);
                    }

                }
            });
        }else{
            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new MyRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
    }

    public void updateList(String subreddit) {

        // Set the counter to 0. This counter will be used to create new json urls
        // In the loadMore function we will increase this integer by 25
        counter = 0;

        // Create the reddit json url for parsing
        subreddit = redditUrl + subreddit + jsonEnd;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        showBar();

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, subreddit, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());
                hideBar();

                // Parse json data.
                // Declare the json objects that we need and then for loop through the children array.
                // Do the json parse in a try catch block to catch the exceptions
                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    if (children.length() == 0) {
                        Toast.makeText(getActivity(), "there doesn't seem to be anything here", Toast.LENGTH_LONG).show();
                    }
                    initialCount = children.length();
                    List<ListItems> list = new ArrayList<>(adapter.getData());
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject post = children.getJSONObject(i).getJSONObject("data");
                        ListItems item = new ListItems();
                        item.setTitle(post.getString("title"));
                        item.setThumbnail(post.getString("thumbnail"));
                        item.setUrl(post.getString("url"));
                        item.setSubreddit(post.getString("subreddit"));
                        item.setAuthor(post.getString("author"));
                        item.setScore(post.getInt("score")+"");
                        list.add(item);
                    }
                    // Clear the adapter because new data is being added from a new subreddit
                    //adapter.clearAdapter();
                    adapter.setData(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideBar();
            }
        });
        queue.add(jsObjRequest);
    }

    public void loadMore(String subreddit) {

        // Add 25 each time the function is called
        // Then convert it to a string to add to other strings to create the new reddit json url.
        counter = counter + 25;
        count = String.valueOf(counter);

        // Create the reddit json url for parsing
        subreddit = redditUrl + subreddit + jsonEnd + qCount + count + after + after_id;
        Log.d("abc", subreddit);

        showBar();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, subreddit, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                // Log to console the whole json string for debugging
                Log.d(TAG, response.toString());
                hideBar();

                // Parse json data.
                // Declare the json objects that we need and then forloop through the children array.
                // Do the json parse in a try catch block to catch the exceptions
                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    List<ListItems> list = new ArrayList<>(adapter.getData());
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject post = children.getJSONObject(i).getJSONObject("data");
                        ListItems item = new ListItems();
                        item.setTitle(post.getString("title"));
                        item.setThumbnail(post.getString("thumbnail"));
                        item.setUrl(post.getString("url"));
                        item.setSubreddit(post.getString("subreddit"));
                        item.setAuthor(post.getString("author"));
                        item.setScore(post.getInt("score") + "");
                        list.add(item);
                    }

                    adapter.setData(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error" + error.getMessage());
                hideBar();
            }
        });
        queue.add(jsObjRequest);
    }

    public void showBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

    public void hideBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}