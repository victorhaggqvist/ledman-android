package com.snilius.ledman.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.snilius.ledman.BuildConfig;

/**
 * @author victor
 * @since 2/8/15
 */
public class Preferences {

    public static final String APIKEY = "apikey";
    private static final String ENDPOINT = "endpoint";
    private SharedPreferences mPref;

    public Preferences(Context context) {

        mPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", Context.MODE_PRIVATE);
    }

    public String getApikey(){
        return mPref.getString(APIKEY, "");
    }

    public String getEndpoint() {
        return mPref.getString(ENDPOINT, "");
    }
}
