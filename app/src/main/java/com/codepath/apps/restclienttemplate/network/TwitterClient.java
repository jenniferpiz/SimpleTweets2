package com.codepath.apps.restclienttemplate.network;

import android.content.Context;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with Twitter REST API.
  *
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "0NJ5ZIcj0nucAgLRTHuBKNCT5";       // Change this
	public static final String REST_CONSUMER_SECRET = "AWbVnRDHZB57uzetxokbX1gmhnPA5to4vBfCh2vvhbYN2qAeA4"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public static final int maxTweets = 25;

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public enum GetType {HOME, MENTIONS, USERTIMELINE, USERPROFILE}


	public void getTimeline(GetType type, long id, String screenName, AsyncHttpResponseHandler handler) {
        String apiStr;

        switch (type)  {
            case HOME:
                apiStr = "statuses/home_timeline.json";
                break;
            case MENTIONS:
                apiStr = "statuses/mentions_timeline.json";
                break;
            case USERTIMELINE:
                apiStr = "statuses/user_timeline.json";
                break;
            case USERPROFILE:
                apiStr = "users/show.json";
                break;
            default:
                return;
        }

		String apiUrl = getApiUrl(apiStr);
		RequestParams params = new RequestParams();
		params.put("count", maxTweets);
		String idParam = (id <= 1) ? "since_id" : "max_id";
        if (type == GetType.USERTIMELINE || type == GetType.USERPROFILE) {
            params.put("screen_name", screenName);
        }
		params.put(idParam, Long.toString(Math.abs(id)));
		client.get(apiUrl, params, handler);
	}

	public void postNewTweet(String post, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", post);
		client.post(apiUrl, params, handler);
	}

	public void getProfileInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
		client.get(apiUrl, params, handler);
	}




}
