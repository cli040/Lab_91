package com.example.lab_91;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    public static final String WEATHER = "com.example.lab_91.WEATHER";
    public static final String COUNTING = "com.example.lab_91.COUNTING";

    private boolean bDownloadingWeather = false;
    private BroadcastReceiver MyBroadCastReceiver;
    private Intent intentFetchWeather;
    private TableLayout tl_info;
    private Button btn_service;
    private Context ctxt;

    private TextView tv_id_ans;
    private TextView tv_timestamp_ans;
    private TextView tv_temperature_ans;
    private TextView tv_pressure_ans;
    private TextView tv_humidity_ans;
    private TextView tv_counter;

    // Get the context
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(newBase);
        ctxt = newBase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_service = findViewById(R.id.btn_service);

        // Find the different views in activity_main.xml
        tl_info = findViewById(R.id.tl_info);
        tv_id_ans = findViewById(R.id.tv_id_ans);
        tv_timestamp_ans = findViewById(R.id.tv_timestamp_ans);
        tv_temperature_ans = findViewById(R.id.tv_temperature_ans);
        tv_pressure_ans = findViewById(R.id.tv_pressure_ans);
        tv_humidity_ans = findViewById(R.id.tv_humidity_ans);
        tv_counter = findViewById(R.id.tv_counter);

        // Create new broadcast
        MyBroadCastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getStringExtra("intent-filter-name").equals("WEATHER"))
                {
                        tv_id_ans.setText(intent.getStringExtra("id"));
                        tv_timestamp_ans.setText(intent.getStringExtra("timestamp"));
                        tv_temperature_ans.setText(intent.getStringExtra("temperature"));
                        tv_pressure_ans.setText(intent.getStringExtra("pressure"));
                        tv_humidity_ans.setText(intent.getStringExtra("humidity"));
                }
                else if (intent.getStringExtra("intent-filter-name").equals("COUNTING"))
                {
                    if (tv_counter.getVisibility() == View.INVISIBLE)
                    {
                        tv_counter.setVisibility(View.VISIBLE);
                    }
                    tv_counter.setText(intent.getStringExtra("updateCounter"));
                }
            }
        };

        // Register the receivers in onCreate() -> no need in manifest.xml
        LocalBroadcastManager.getInstance(this).registerReceiver(MyBroadCastReceiver, new IntentFilter(WEATHER));
        LocalBroadcastManager.getInstance(this).registerReceiver(MyBroadCastReceiver, new IntentFilter(COUNTING));

        // Auto start downloading weather data
        getWeatherData(true);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Unregister the current receiver when the app is closed. (or if we change activity)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(MyBroadCastReceiver);
    }

    public void onServiceButtonClicked(@Nullable View view)
    {
        // Check if we are downloading
        if (!bDownloadingWeather)
        {
            bDownloadingWeather = true;
            btn_service.setText(R.string.stop_service);

            // Create new serviceIntent
            getWeatherData(false);
        }
        else
        {
            // user has canceled the service -> stop!
            stopService(intentFetchWeather);
            intentFetchWeather = null;

            // Hide the text
            hideCountingText();

            // Change the button text
            btn_service.setText(R.string.start_service);
            bDownloadingWeather = false;
        }
    }

    private void getWeatherData(boolean bFirstTime)
    {
        intentFetchWeather = new Intent(ctxt, MyService.class);

        if (bFirstTime)
        {
            intentFetchWeather.putExtra("bFirstStartService", true);
        }

        startService(intentFetchWeather);
    }

    private void hideCountingText()
    {
        tv_counter.setVisibility(View.INVISIBLE);
        tv_counter.setText("");
    }
}
