package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.adapters.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import java.io.IOException;

public class TimelineActivity extends AppCompatActivity implements TweetFragment.ComposeTweetListener, TweetAdapter.AdapterCallback {

    TweetsPagerAdapter pagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

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
                if (!isOnline()) {
                    Toast.makeText(this, "No internet detected!",
                            Toast.LENGTH_LONG).show();
                } else {
                    FragmentManager fm = getSupportFragmentManager();

                    TweetFragment tweetFragment = TweetFragment.newInstance("Some Title");
                    tweetFragment.show(fm, "fragment_tweet");
                }
                return true;

            case R.id.myprofile:
                if (!isOnline()) {
                    Toast.makeText(this, "No internet detected!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Fragment page = pagerAdapter.getRegisteredFragment(0);

                    if (page != null && page instanceof HomeTimelineFragment) {
                        User user = ((HomeTimelineFragment) page).getUser();
                        Intent intent = new Intent(this, ShowProfileActivity.class);
                        intent.putExtra("user", Parcels.wrap(user));
                        startActivity(intent);
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


    @Override
    public void onFinishComposingNewTweet(String s) {

        Fragment page = pagerAdapter.getRegisteredFragment(0);

        if (page != null && page instanceof HomeTimelineFragment) {
            ((HomeTimelineFragment)page).postNewTweet(s);
        }

        viewPager.setCurrentItem(0);
    }

    @Override
    public void onTweetUserClicked(String screenName) {

        User user = TweetsListFragment.friends.get(screenName);

        Intent intent = new Intent(this, ShowProfileActivity.class);
        intent.putExtra("user", Parcels.wrap(user));

        startActivity(intent);
    }

}
