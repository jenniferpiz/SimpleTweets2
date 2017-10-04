package com.codepath.apps.restclienttemplate.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.fragments.ProfileTweetsFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

/**
 * Created by jennifergodinez on 10/3/17.
 */

public class ShowProfileActivity extends AppCompatActivity {
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        client = TwitterApp.getRestClient();

        //TODO  directly assign in setters
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

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ProfileTweetsFragment fragmentDemo = ProfileTweetsFragment.newInstance(user);
        ft.replace(R.id.tweetsList_placeholder, fragmentDemo);
        ft.commit();


    }

}
