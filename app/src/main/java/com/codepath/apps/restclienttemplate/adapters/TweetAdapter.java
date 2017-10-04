package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by jennifergodinez on 9/25/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;

    public TweetAdapter(List<Tweet> mTweets) {
        this.mTweets = mTweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvUserName.setText(tweet.user.name);

        String body = changeHashTagColor(tweet.body);
        holder.tvBody.setText(Html.fromHtml(body));

        holder.tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));

        holder.tvScreenName.setText("@"+tweet.user.screenName);
        holder.tvScreenName.setTextColor(Color.DKGRAY);

        if (tweet.displayURL  != null) {
            // only for media but can be extended to support unfurling
            holder.ivDisplay.setVisibility(View.VISIBLE);
            Glide.with(context).load(tweet.displayURL).into(holder.ivDisplay);
        } else {
            holder.ivDisplay.setVisibility(View.GONE);
        }


        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);

    }

    private String changeHashTagColor(String s) {
        String colorCodeStart = "<font color='#2DB7EF'>";  // use any color as  your want
        String colorCodeEnd = "</font>";

        // change color of tags, this was before I found out API has offset values

        int start = s.indexOf("#") + 1;
        String prefix = s.substring(0, start);
        String suffix = s.substring(start);
        return prefix.replace("#", colorCodeStart+"#") + suffix.replaceFirst(" ", colorCodeEnd+" ");
    }

    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // shorten relative time
        String str = relativeDate;
        if ("Yesterday".equals(relativeDate)) {
            str = "1d";
        } else if (relativeDate.substring(0, 1).matches("[0-9]")) {
            int cutIndex = relativeDate.indexOf(' ') + 1;
            str = relativeDate.replace(" ", "").substring(0, cutIndex);
        } else if (!relativeDate.startsWith("in ")){
            int pos = relativeDate.indexOf(",");
            str = relativeDate.substring(0, pos);
        }

        return str;
    }


    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTimeStamp;
        public TextView tvScreenName;
        public ImageView ivDisplay;


        public ViewHolder (View itemView) {
            super(itemView);

            ivProfileImage = (ImageView)itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView)itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvTimeStamp = (TextView)itemView.findViewById(R.id.tvTimeStamp);
            tvScreenName = (TextView)itemView.findViewById(R.id.tvScreenName);
            ivDisplay = (ImageView)itemView.findViewById(R.id.ivDisplay);

        }

    }
}
