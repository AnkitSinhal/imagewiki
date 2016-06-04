package com.microsoft.wiki.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.microsoft.wiki.R;
import com.microsoft.wiki.utils.Constants;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String title = getIntent().getStringExtra(Constants.TITLE);

        TextView textDetails = (TextView) findViewById(R.id.textDetails);
        textDetails.setText(title);
    }
}
