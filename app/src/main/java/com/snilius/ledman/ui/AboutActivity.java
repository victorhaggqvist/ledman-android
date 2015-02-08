package com.snilius.ledman.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.snilius.aboutit.AboutIt;
import com.snilius.aboutit.L;
import com.snilius.ledman.BuildConfig;
import com.snilius.ledman.R;

public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new AboutIt(this).description(R.string.description).app(R.string.app_name)
                .copyright("Snilius")
                .buildInfo(BuildConfig.DEBUG,BuildConfig.VERSION_CODE,BuildConfig.VERSION_NAME)
                .year(2015)
                .libLicense("Retrofit", "Square", L.AP2, "https://github.com/square/retrofit")
                .libLicense("Gson","Google", L.AP2, "https://code.google.com/p/google-gson/")
                .libLicense("Timber", "Jake Wharton", L.AP2, "https://github.com/JakeWharton/timber")
                .toTextView(R.id.about_text);
    }
}
