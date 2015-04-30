package com.snilius.ledman.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.snilius.ledman.R;
import com.snilius.ledman.data.LedmanService;
import com.snilius.ledman.data.LedmanServiceProvider;
import com.snilius.ledman.util.Preferences;

import io.fabric.sdk.android.Fabric;
import retrofit.RetrofitError;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Toolbar mToolbar;
    private TextView mRed;
    private TextView mGreen;
    private TextView mBlue;
    private Preferences mPreferences;
    private Button mMasterToggle;
    private SeekBar[] mSeekBar = new SeekBar[3];
    private TextView[] mSeekText = new TextView[3];
    private double[] mColorset = new double[3];
    private Button mColorToggle;
    private Button mColorReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        Timber.plant(new Timber.DebugTree());

        mRed = (TextView) findViewById(R.id.red);
        mGreen = (TextView) findViewById(R.id.green);
        mBlue = (TextView) findViewById(R.id.blue);

        mMasterToggle = (Button) findViewById(R.id.master_toggle);
        mMasterToggle.setOnClickListener(this);

        mSeekBar[0] = (SeekBar) findViewById(R.id.seekBarR);
        mSeekBar[0].setOnSeekBarChangeListener(this);
        mSeekBar[1] = (SeekBar) findViewById(R.id.seekBarG);
        mSeekBar[1].setOnSeekBarChangeListener(this);
        mSeekBar[2] = (SeekBar) findViewById(R.id.seekBarB);
        mSeekBar[2].setOnSeekBarChangeListener(this);

        mSeekText[0] = (TextView) findViewById(R.id.seek_r);
        mSeekText[1] = (TextView) findViewById(R.id.seek_g);
        mSeekText[2] = (TextView) findViewById(R.id.seek_b);

        mColorToggle = (Button) findViewById(R.id.color_toggle);
        mColorToggle.setOnClickListener(this);
        mColorReset = (Button) findViewById(R.id.color_reset);
        mColorReset.setOnClickListener(this);

        mPreferences = new Preferences(this);

        if (mPreferences.getEndpoint().length() < 1){
            startActivity(new Intent(this, PreferencesActivity.class));
        } else {
            LedmanServiceProvider.setApikey(mPreferences.getApikey());
            LedmanServiceProvider.setEndpoint(mPreferences.getEndpoint());

            new StatusTask().execute((Void) null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case R.id.action_refresh:
                new StatusTask().execute((Void) null);
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mMasterToggle)) {
            if (mMasterToggle.getText().toString().equals(getString(R.string.turnoff))) {
                new ToggleLightTask(false).execute((Void) null);
            } else {
                new ToggleLightTask(true).execute((Void) null);
            }
        } else if (v.equals(mColorToggle)) {
            new SetColorTask().execute((Void) null);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String[] color = new String[]{"R", "G", "B"};
        for (int i = 0; i < mSeekBar.length; i++) {
            if (seekBar.equals(mSeekBar[i])){
                double val = ((double)seekBar.getProgress())/10;
                mSeekText[i].setText(color[i] + ": " + Double.toString(val));
                mColorset[i] = val;
                break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // noop
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // noop
    }

    private class StatusTask extends AsyncTask<Void, Void, Integer> {

        private com.snilius.ledman.data.Status mStatus;

        private Integer OK = 0;
        private Integer AUTH_FAIL = 1;
        private Integer ENDPOINT_FAIL = 2;

        @Override
        protected Integer doInBackground(Void... params) {

            LedmanService service = LedmanServiceProvider.getService();

            try {
                mStatus = service.status();
            } catch (RetrofitError e) {
                if (null == e.getResponse()) {
                    return ENDPOINT_FAIL;
                } else if (e.getResponse().getStatus() == 403) {
                    return AUTH_FAIL;
                }
            }

            return OK;
        }

        @Override
        protected void onPostExecute(Integer status) {

            if (status == OK){
                mRed.setText(String.format(getString(R.string.status_red), mStatus.getR()));
                mGreen.setText(String.format(getString(R.string.status_green), mStatus.getG()));
                mBlue.setText(String.format(getString(R.string.status_blue), mStatus.getB()));

                if (mStatus.isOn())
                    mMasterToggle.setText(R.string.turnoff);
                else
                    mMasterToggle.setText(R.string.turnon);

                mSeekBar[0].setProgress((int)(Double.parseDouble(mStatus.getR())*10));
                mSeekBar[1].setProgress((int)(Double.parseDouble(mStatus.getG())*10));
                mSeekBar[2].setProgress((int)(Double.parseDouble(mStatus.getB())*10));

            } else if(status == AUTH_FAIL) {
                Toast.makeText(getApplicationContext(), "Auth fail, bad api key", Toast.LENGTH_LONG).show();
            } else if(status == ENDPOINT_FAIL) {
                Toast.makeText(getApplicationContext(), "Connection fail, bad endpoint", Toast.LENGTH_LONG).show();
            }

        }
    }

    private class ToggleLightTask extends AsyncTask<Void,Void,Void> {
        private boolean mLightOn;

        public ToggleLightTask(boolean lightOn) {

            mLightOn = lightOn;
        }

        @Override
        protected void onPreExecute() {
            mMasterToggle.setText("...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            LedmanService service = LedmanServiceProvider.getService();
            if (mLightOn)
                service.on();
            else
                service.off();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mLightOn)
                mMasterToggle.setText(R.string.turnoff);
            else
                mMasterToggle.setText(R.string.turnon);

            new StatusTask().execute((Void) null);
        }
    }

    private class SetColorTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            LedmanService service = LedmanServiceProvider.getService();

            String[] color = new String[]{"r", "g", "b"};
            for (int i = 0; i < mColorset.length; i++) {
                String level = mColorset[i]<1?Double.toString(mColorset[i]):"1";
                service.set(color[i], level);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new StatusTask().execute();
        }
    }
}
