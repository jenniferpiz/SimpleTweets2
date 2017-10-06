package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jennifergodinez on 10/2/17.
 */

public class HomeTimelineFragment extends TweetsListFragment {
    TwitterClient client;
    User user;
    User friend;
    HashMap<String, User> friends;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        getMyProfile();
        //getFriendsProfile();
        //getFriendProfile("jennpg233");
        friends = new HashMap<String, User>();
        populateTimeline(1);

    }


    private void getMyProfile() {
        client.getProfileInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = User.fromJSON(response);
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

    void getFriendsProfile()  { //TODO delete
        client.getFriendsList(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User f;
                try {
                    /*  TODO move this outside
                    int cursor = -1;
                    String cursor_param = "";
                    do {
                        //friend = User.fromJSON(response);
                        url_with_cursor = api_path + "&cursor=" + cursor
                        cursor = response.getInt("next_cursor");
                    } while (cursor != 0);
                    */

                    JSONArray list = response.getJSONArray("users");
                    if (list == null) return;
                    for (int i=0; i< list.length(); i++) {
                        f = User.fromJSON((JSONObject)list.get(i));
                        friends.put(f.screenName, f);

                    }
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


    public void getFriendProfile(final String screenName)  {

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



    void populateTimeline (final long id) {
/*
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), "No internet detected!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        */

        client.getTimeline(TwitterClient.GetType.HOME, id, "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());

                addItems(response, id > 0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                new Throwable().printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                new Throwable().printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                new Throwable().printStackTrace();
            }
        });
    }

    public void postNewTweet(String s) {

        // update first before posting new tweet
        populateTimeline(- getId(0));

        client.postNewTweet(s, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                insertToFirst(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

        });

    }


    public User getUser() {
        return this.user;
    }

    public User getFriend(String screenName) {
        return friends.get(screenName);
    }

}
