package com.ashish.redditclient.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import com.ashish.redditclient.Listerners.OnItemSelectedListener;
import com.ashish.redditclient.R;
import com.ashish.redditclient.activities.MainActivity;
import com.ashish.redditclient.adapter.SearchAdapter;
import com.ashish.redditclient.helpers.EndlessRecyclerOnScrollListener;
import com.ashish.redditclient.models.SearchSubreddit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment implements OnItemSelectedListener {

    //fragment search ko declaration part haru
    private static final String TAG = "RecyclerViewExample";
    private List<SearchSubreddit> listItemsList = new ArrayList<SearchSubreddit>();

    private ProgressBar mProgressBar;

    private RecyclerView mRecyclerView;
    private SearchAdapter adapter;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;

    private int counter = 0;
    private int initialCount = 0;
    private String count;
    private String after_id;
    private String subreddit;
    private String redditUrl = "http://www.reddit.com/subreddits/search";
    private String qString = "?q=";
    private String jsonEnd = "/.json";
    private String qCount = "&count=";
    private String after = "&after=";
    //declaration part end

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        subreddit = getArguments().getString("query");

        if (subreddit.equals("/subreddits")) {
            redditUrl = "http://www.reddit.com/subreddits";
            qString = "";
            subreddit = "";
            qCount = "?count=";
        }

        //Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.searchList);

        //Initialize ProgressBar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBarSearch);

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

                    if (after_id != null && !(initialCount < 25)) {
                        //maintain scroll position
                        int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                        loadMore(subreddit);
                    }
                }
            });

        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    public void updateList(String subreddit) {

        // Set the counter to 0. This counter will be used to create new json urls
        // In the loadMore function we will increase this integer by 25
        counter = 0;

        // Create the reddit json url for parsing
        subreddit = redditUrl + jsonEnd + qString + Uri.encode(subreddit);

        Log.d("abc", subreddit);

        //declare the adapter and attach it to the recyclerview
        adapter = new SearchAdapter(getActivity(), this, listItemsList);
        mRecyclerView.setAdapter(adapter);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Clear the adapter because new data is being added from a new subreddit
        adapter.clearAdapter();

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
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject post = children.getJSONObject(i).getJSONObject("data");
                        SearchSubreddit item = new SearchSubreddit();
                        item.setUrl(post.getString("url"));
                        item.setPublicDescription(post.getString("public_description"));
                        listItemsList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();

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
        subreddit = redditUrl + jsonEnd + qString + Uri.encode(subreddit) + qCount + count + after + after_id;

        // Declare the adapter and attach it to the recyclerview
        adapter = new SearchAdapter(getActivity(), this, listItemsList);
        mRecyclerView.setAdapter(adapter);

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
                // Declare the json objects that we need and then for loop through the children array.
                // Do the json parse in a try catch block to catch the exceptions
                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    for (int i = 0; i < children.length(); i++) {
                        JSONObject post = children.getJSONObject(i).getJSONObject("data");
                        SearchSubreddit item = new SearchSubreddit();
                        item.setUrl(post.getString("url"));
                        item.setPublicDescription(post.getString("public_description"));
                        listItemsList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();
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

    @Override
    public void OnItemClick(int itemId) {
        ((MainActivity) getActivity()).showFab();

        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentList fragment = new FragmentList();
        Bundle bundle = new Bundle();
        ((MainActivity) getActivity()).setUrl(listItemsList.get(itemId).getUrl());
        bundle.putString("url", listItemsList.get(itemId).getUrl());
        bundle.putString("subUrl", "");

        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(((MainActivity) getActivity()).getSupportActionBar().getTitle().toString());

        fragmentTransaction.commit();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(listItemsList.get(itemId).getUrl());
        ((MainActivity) getActivity()).invalidateOptionsMenu();
        ((MainActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.fire);
    }
}