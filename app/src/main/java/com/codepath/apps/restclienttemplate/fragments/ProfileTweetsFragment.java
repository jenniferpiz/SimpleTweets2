package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.apps.TwitterApp;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jennifergodinez on 10/4/17.
 */

public class ProfileTweetsFragment extends  TweetsListFragment {
    TwitterClient client;
    User user;

    public static ProfileTweetsFragment newInstance(User user) {
        ProfileTweetsFragment fragment = new ProfileTweetsFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) Parcels.unwrap(getArguments().getParcelable("user"));

        client = TwitterApp.getRestClient();

        populateTimeline(1);

    }


    void populateTimeline(final long id) {

        if (!isFragmentOnline()) {
            Toast.makeText(getActivity(), "No internet detected!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        client.getTimeline(TwitterClient.GetType.USERTIMELINE, id, user.screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addItems(response, id > 0);
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

}
