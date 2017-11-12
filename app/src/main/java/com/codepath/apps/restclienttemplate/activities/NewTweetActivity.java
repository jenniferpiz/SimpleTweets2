package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;

/**
 * Created by jennifergodinez on 11/11/17.
 */

public class NewTweetActivity  extends AppCompatActivity {
    private EditText etTweetMsg;
    private TextView tvCounter;
    private static int maxLength = 140;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_tweet);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -5;
        params.height = 390;
        params.width = 610;
        params.y = -20;

        this.getWindow().setAttributes(params);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowTitleEnabled(true);
        aBar.setTitle("Compose New Tweet");
        aBar.setElevation(8f);
        aBar.setDisplayHomeAsUpEnabled(true);

        tvCounter = (TextView) findViewById(R.id.tvCounter);
        etTweetMsg = (EditText) findViewById(R.id.etTweetMsg);

        // Show soft keyboard automatically and request focus to field
        etTweetMsg.requestFocus();

        etTweetMsg.addTextChangedListener(
                new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //This sets a textview to the current length
                        int length = maxLength - s.length();
                        if (length<0) {
                            tvCounter.setTextColor(Color.RED);
                        } else {
                            tvCounter.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        }
                        tvCounter.setText(String.valueOf(length));
                    }

                    public void afterTextChanged(Editable s) {
                    }
                });


        Button btn = (Button)findViewById(R.id.btnTweet);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSubmit(view);
            }
        });

    }


    void onSubmit(View v) {
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("message", etTweetMsg.getText().toString());
        data.putExtra("code", TimelineActivity.REQUEST_CODE); // ints work too
        setResult(RESULT_OK, data);
        finish();
    }
    
}
