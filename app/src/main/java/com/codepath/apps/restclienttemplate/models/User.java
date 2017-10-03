package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jennifergodinez on 9/25/17.
 */

public class User {
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;
    public int countFollowers;
    public int countFollowing;
    public String tagLine;
    ArrayList<String> tweets;

    public static User fromJSON(JSONObject json) throws JSONException{
        User user = new User();

        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");
        user.tagLine = json.getString("description");
        user.countFollowers = json.getInt("followers_count");
        user.countFollowing = json.getInt("friends_count");

        return user;
    }
}
