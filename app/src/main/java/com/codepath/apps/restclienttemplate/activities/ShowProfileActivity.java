package com.codepath.apps.restclienttemplate.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.fragments.ProfileTweetsFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterClient;

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

        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(user.name);

        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(String.format("@" + user.screenName));

        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        Spanned sp = Html.fromHtml("<b>" +user.countFollowing +"</b>"+" Following");
        tvFollowing.setText(sp);

        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        sp = Html.fromHtml("<b>" +user.countFollowers +"</b>"+" Followers");
        tvFollowers.setText(sp);

        ImageView ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);
        Glide.with(this).load(Uri.parse(user.profileImageUrl.replace("_normal", ""))).into(ivProfileImg);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ProfileTweetsFragment fragmentDemo = ProfileTweetsFragment.newInstance(user);
        ft.replace(R.id.tweetsList_placeholder, fragmentDemo);
        ft.commit();


    }

}
