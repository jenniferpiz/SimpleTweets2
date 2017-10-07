package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
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

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.network.TwitterClient.maxTweets;

/**
 * Created by jennifergodinez on 10/2/17.
 */

public abstract class TweetsListFragment extends Fragment {
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener scrollListener;
    TwitterClient client;
    public static HashMap<String, User> friends = new HashMap<String, User>();


    abstract void populateTimeline(long id);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        client = TwitterApp.getRestClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        rvTweets = (RecyclerView) view.findViewById(R.id.rvTweet);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        tweets = new ArrayList<>();

        if (getActivity() instanceof TimelineActivity) {
            tweetAdapter = new TweetAdapter(tweets, (TimelineActivity) getActivity());
        } else {
            tweetAdapter = new TweetAdapter(tweets, null);
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

    public long getId (int pos) {
        return tweets.get(pos).uid;
    }

}
