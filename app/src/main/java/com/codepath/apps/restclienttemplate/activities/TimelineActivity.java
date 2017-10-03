package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;

public class TimelineActivity extends AppCompatActivity implements TweetFragment.ComposeTweetListener  {

    TweetsListFragment fragmentTweetsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        fragmentTweetsList = (TweetsListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                return true;

            case R.id.twit:
                FragmentManager fm = getSupportFragmentManager();
                TweetFragment tweetFragment = TweetFragment.newInstance("Some Title");
                tweetFragment.show(fm, "fragment_tweet");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPassTweetMsg(String s) {
    }

}
