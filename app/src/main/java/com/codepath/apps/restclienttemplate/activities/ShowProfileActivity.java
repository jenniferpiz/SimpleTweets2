package com.codepath.apps.restclienttemplate.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by jennifergodinez on 10/3/17.
 */

public class ShowProfileActivity extends AppCompatActivity {
    RecyclerView rvTweets;
    ArrayList<Tweet> tweets;
    TweetAdapter tweetAdapter;
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        client = TwitterApp.getRestClient();
        getTweets();

        //TODO  directly assign in setters
        String profileImgStr = user.profileImageUrl;
        String name = user.name;
        String userName = user.screenName;
        int nFollowing = user.countFollowing;
        int nFollowers = user.countFollowers;

        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(name);

        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(String.format("@" + userName));

        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        tvFollowing.setText(String.format(nFollowing + " Following")); //TODO use %1$

        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowers.setText(nFollowers + " Followers");

        ImageView ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);
        Glide.with(this).load(Uri.parse(user.profileImageUrl)).into(ivProfileImg);

        rvTweets = (RecyclerView) findViewById(R.id.rvUserTweet);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);

        rvTweets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTweets.setAdapter(tweetAdapter);

    }


    private void getTweets() {
        client.getUserTweets(user.screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                addItems(response);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
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
