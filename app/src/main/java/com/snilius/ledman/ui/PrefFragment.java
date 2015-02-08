package com.snilius.ledman.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.preference.PreferenceFragment;
import android.support.v4.preference.PreferenceManagerCompat;

import com.snilius.ledman.R;

/**
 * @author victor
 * @since 2/8/15
 */
public class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PreferencesFragment.
     */
    public static PrefFragment newInstance() {
        PrefFragment fragment = new PrefFragment();
        return fragment;
    }

    public PrefFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        // set texts correctly
        onSharedPreferenceChanged(getPreferenceManager().getSharedPreferences(), "endpoint");
        onSharedPreferenceChanged(getPreferenceManager().getSharedPreferences(), "apikey");

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("endpoint")) {
            Preference exercisesPref = findPreference(key);
            exercisesPref.setSummary(sharedPreferences.getString(key, ""));
        } else if (key.equals("apikey")) {
            Preference exercisesPref = findPreference(key);
            String apikey = sharedPreferences.getString(key, "");
            exercisesPref.setSummary(apikey.length()>0?"Key is set":"No key set");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

}
