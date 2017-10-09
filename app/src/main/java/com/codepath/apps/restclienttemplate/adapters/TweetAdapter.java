package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ShowProfileActivity;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by jennifergodinez on 9/25/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    List<Tweet> mTweets;
    Context context;
    AdapterCallback callback;
    OnItemClickListener listener;

    public interface AdapterCallback{
        void onTweetUserClicked(String screenName);
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public TweetAdapter(List<Tweet> mTweets, AdapterCallback callback, OnItemClickListener listener) {

        this.mTweets = mTweets;
        this.callback = callback;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        ViewHolder viewHolder = new ViewHolder(tweetView, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvUserName.setText(tweet.user.name);

        //String body = changeHashTagColor(tweet.body);
        //holder.tvBody.setText(Html.fromHtml(body));
        setTags(holder.tvBody,tweet.body);

        holder.tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));

        final String screenName = tweet.user.screenName;
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
        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onTweetUserClicked(screenName);
                }
            }

        });

    }

    private void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '@' || pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));

                            if (tag.startsWith("@")) {
                                User user = TweetsListFragment.friends.get(tag.substring(1));

                                Intent intent = new Intent(context, ShowProfileActivity.class);
                                intent.putExtra("user", Parcels.wrap(user));

                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#33b5e5"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }


    private String getRelativeTimeAgo(String rawJsonDate) {
        boolean sameYr = true;
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            Calendar c = Calendar.getInstance();
            int currentYr = c.get(Calendar.YEAR);
            c.setTimeInMillis(dateMillis);
            sameYr =  (c.get(Calendar.YEAR) - currentYr) == 0;
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
        } else if (!relativeDate.startsWith("in ") && sameYr){
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


        public ViewHolder (final View itemView, final OnItemClickListener listener) {
            super(itemView);

            ivProfileImage = (ImageView)itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView)itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvTimeStamp = (TextView)itemView.findViewById(R.id.tvTimeStamp);
            tvScreenName = (TextView)itemView.findViewById(R.id.tvScreenName);
            ivDisplay = (ImageView)itemView.findViewById(R.id.ivDisplay);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });

        }

    }
}
