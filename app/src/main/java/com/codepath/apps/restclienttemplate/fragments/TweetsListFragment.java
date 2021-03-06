package com.codepath.apps.restclienttemplate.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.apps.TwitterApp;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.network.TwitterClient.maxTweets;

/**
 * Created by jennifergodinez on 10/2/17.
 */

public abstract class TweetsListFragment extends Fragment {
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    SwipeRefreshLayout swipeContainer;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener scrollListener;
    TwitterClient client;
    OnRefreshListener mCallback;
    public static HashMap<String, User> friends = new HashMap<String, User>();


    abstract void populateTimeline(long id);

    // Container Activity must implement this interface
    public interface OnRefreshListener {
        public void onRefreshSelected();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof TimelineActivity) {
            try {
                mCallback = (OnRefreshListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnRefreshListener");
            }
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        client = TwitterApp.getRestClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() instanceof TimelineActivity) {
                    mCallback.onRefreshSelected();
                } else {
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvTweets = (RecyclerView) view.findViewById(R.id.rvTweet);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        tweets = new ArrayList<>();

        TweetAdapter.OnItemClickListener listener = new TweetAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Tweet tweet = tweets.get(position);

                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setMessage(tweet.body);
                b.setTitle("@"+tweet.user.screenName);
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)  {
                            dialog.dismiss();
                        }
                    }

                });
                b.show();
            }
        };

        if (getActivity() instanceof TimelineActivity) {
            tweetAdapter = new TweetAdapter(tweets, (TimelineActivity) getActivity(), listener);
        } else {
            tweetAdapter = new TweetAdapter(tweets, null, listener);
        }

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                final int curSize = tweetAdapter.getItemCount();
                populateTimeline(getId(curSize-1));

            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        return view;

    }


    public void addItems(JSONArray response, boolean append) {

        ArrayList<Tweet> newTweets = new ArrayList<Tweet>();

        for (int i = 0; i < response.length(); i++) {
            Tweet tweet = null;
            try {
                tweet = Tweet.fromJSON(response.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            newTweets.add(tweet);
            getFriendProfile(tweet.user.screenName);

        }

        if (!append) {

            int n_tweets = newTweets.size();

            // if too many new tweets, we just clear everything and start fresh
            if (n_tweets > (maxTweets - 1)) {
                int size = tweets.size();
                tweets.clear();
                tweetAdapter.notifyItemRangeRemoved(0, size );
            }

            if (n_tweets >0 ) {
                tweets.addAll(0, newTweets);
                tweetAdapter.notifyItemRangeInserted(0, n_tweets);
            }

        } else {
            //check if first = last
            if (newTweets.size() > 0 && tweets.size() > 0 &&
                    newTweets.get(0).uid == tweets.get(tweets.size()-1).uid) {
                //remove first tweet
                newTweets.remove(0);
            }

            // append new tweets to our list
            int n_tweets = newTweets.size();
            int oldSize = tweets.size();
            tweets.addAll(newTweets);
            tweetAdapter.notifyItemRangeInserted(oldSize, n_tweets);
        }

    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        tweetAdapter.notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        tweetAdapter.notifyDataSetChanged();
    }


    public void insertToFirst (JSONObject response) {
        // add new tweet
        Tweet t = null;
        try {
            t = Tweet.fromJSON(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tweets.add(0, t);
        getFriendProfile(t.user.screenName);

        tweetAdapter.notifyItemInserted(0);
        // make sure we can view it on top of home timeline
        rvTweets.scrollToPosition(0);
    }


    void getFriendProfile(final String screenName)  {

        client.getTimeline(TwitterClient.GetType.USERPROFILE, 0, screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (friends.containsKey(screenName)) {
                        return;
                    }
                    friends.put(screenName, User.fromJSON(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                new Throwable().printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                new Throwable().printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                new Throwable().printStackTrace();
            }
        });

    }

    public boolean isFragmentOnline() {
        if (getActivity() instanceof TimelineActivity) {
            return ((TimelineActivity) getActivity()).isOnline();
        } else {
            return true;
        }
    }

    public long getId (int pos) {
        return tweets.get(pos).uid;
    }

}
