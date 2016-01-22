package com.example.sascha.headtrackeradvanced;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import android.content.Context;

import com.example.sascha.headtrackeradvanced.services.UDPService;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    int value;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor gyroscope;
    private boolean serviceRunning;

    UDPService sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText ipAddress = (EditText)findViewById(R.id.ip_address);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        ipAddress.setFilters(filters);
        final EditText port = (EditText) findViewById(R.id.port);

        sender = new UDPService();
        serviceRunning = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, accelerometer, 1);
        mSensorManager.registerListener(this, gravity, 1);
        mSensorManager.registerListener(this, gyroscope, 1);

        final Button startStopButton = (Button) findViewById(R.id.startstopbutton);
        startStopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceRunning) {
                    stopService(new Intent(getBaseContext(), UDPService.class));
                    startStopButton.setText("Start Sender");
                    serviceRunning = false;
                } else {
                    Intent startIntent = new Intent(getBaseContext(), UDPService.class);
                    startIntent.putExtra("ip",ipAddress.getText().toString());
                    startIntent.putExtra("port",Integer.parseInt(port.getText().toString()));
                    startService(startIntent);
                    startStopButton.setText("Stop Sender");
                    serviceRunning = true;
                }
            }
        });

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ((TextView) findViewById(R.id.textView_accelerometer_x)).setText(String.valueOf(event.values[0]));
            ((TextView) findViewById(R.id.textView_accelerometer_y)).setText(String.valueOf(event.values[1]));
            ((TextView) findViewById(R.id.textView_accelerometer_z)).setText(String.valueOf(event.values[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            ((TextView) findViewById(R.id.textView_gravity_x)).setText(String.valueOf(event.values[0]));
            ((TextView) findViewById(R.id.textView_gravity_y)).setText(String.valueOf(event.values[1]));
            ((TextView) findViewById(R.id.textView_gravity_z)).setText(String.valueOf(event.values[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            ((TextView) findViewById(R.id.textView_gyroscope_x)).setText(String.valueOf(event.values[0]));
            ((TextView) findViewById(R.id.textView_gyroscope_y)).setText(String.valueOf(event.values[1]));
            ((TextView) findViewById(R.id.textView_gyroscope_z)).setText(String.valueOf(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sender.stopService(new Intent(this, UDPService.class));
    }
}
