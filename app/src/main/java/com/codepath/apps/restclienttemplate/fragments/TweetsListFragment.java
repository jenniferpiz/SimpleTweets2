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
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by jennifergodinez on 10/2/17.
 */

public class TweetsListFragment extends Fragment {
    private TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        rvTweets = (RecyclerView) view.findViewById(R.id.rvTweet);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);

        rvTweets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTweets.setAdapter(tweetAdapter);

        return view;

    }

    public void addItems(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            Tweet tweet = null;
            try {
                tweet = Tweet.fromJSON(response.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tweets.add(tweet);
            tweetAdapter.notifyItemInserted(tweets.size()-1);
        }
    }

}
