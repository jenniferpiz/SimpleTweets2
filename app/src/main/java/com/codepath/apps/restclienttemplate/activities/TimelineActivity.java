package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.adapters.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import java.io.IOException;

public class TimelineActivity extends AppCompatActivity implements TweetAdapter.AdapterCallback {

    TweetsPagerAdapter pagerAdapter;
    ViewPager viewPager;
    boolean newTweetExists;
    static int REQUEST_CODE=200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        newTweetExists = false;

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);

        // forces to refresh Mentions tab if there is a new tweet to make sure we get latest mentions
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (newTweetExists && (position==1)) {
                    viewPager.getAdapter().notifyDataSetChanged();
                    newTweetExists = false;
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

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
                    Intent i = new Intent(this, NewTweetActivity.class);
                    startActivityForResult(i, REQUEST_CODE);
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


    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String msg = data.getExtras().getString("message");

            Fragment page = pagerAdapter.getRegisteredFragment(0);

            if (page != null && page instanceof HomeTimelineFragment) {
                ((HomeTimelineFragment)page).postNewTweet(msg);
            }

            viewPager.setCurrentItem(0);

            newTweetExists = true;
        }
    }


    @Override
    public void onTweetUserClicked(String screenName) {

        User user = TweetsListFragment.friends.get(screenName);

        Intent intent = new Intent(this, ShowProfileActivity.class);
        intent.putExtra("user", Parcels.wrap(user));

        startActivity(intent);
    }

}
