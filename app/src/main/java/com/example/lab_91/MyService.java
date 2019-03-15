package com.example.lab_91;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyService extends IntentService
{
    private HttpURLConnection urlConnection;
    private boolean bDestory = false;

    public MyService() { super("MyService"); }

    public MyService(String name)
    {
        super(name);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        bDestory = true;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        StringBuilder result = new StringBuilder();
        final String URLName = "";

        try
        {
            URL url = new URL(URLName);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null)
            {
                result.append(line);
            }
            urlConnection.disconnect();
        }
        catch( Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result.toString());

                // Broadcast to MainActivity
                Intent intentBroadCast = new Intent(MainActivity.WEATHER);
                intentBroadCast.putExtra("id", jsonObject.getString("id"));
                intentBroadCast.putExtra("timestamp", jsonObject.getString("timestamp"));
                intentBroadCast.putExtra("temperature", jsonObject.getString("temperature"));
                intentBroadCast.putExtra("pressure", jsonObject.getString("pressure"));
                intentBroadCast.putExtra("humidity", jsonObject.getString("humidity"));
                intentBroadCast.putExtra("intent-filter-name","WEATHER");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadCast);

                if (!intent.hasExtra("bFirstStartService") && !intent.getBooleanExtra("bFirstStartService", false))
                {
                    intentBroadCast = new Intent(MainActivity.COUNTING);
                    intentBroadCast.putExtra("intent-filter-name","COUNTING");
                    // Sleep 10 sec
                    final int count = 0;
                    for (int x = 10; count < x; x--)
                    {
                        if (bDestory) { break; }
                        intentBroadCast.putExtra("updateCounter", Integer.toString(x));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadCast);
                        Thread.sleep(1000);
                    }

                    // eternal loop
                    if (!bDestory)
                    {
                        onHandleIntent(intent);
                    }
                }
                else
                {
                    this.stopSelf();
                }
            }
            catch (JSONException Ex)
            {
                Ex.printStackTrace();
            }
            catch (InterruptedException interruptEx)
            {
                interruptEx.printStackTrace();
            }
        }
    }
}