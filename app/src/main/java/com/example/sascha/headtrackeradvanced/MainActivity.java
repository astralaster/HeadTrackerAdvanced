package com.example.sascha.headtrackeradvanced;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.view.View.OnClickListener;

import android.widget.TextView;

import android.content.Intent;
import android.net.Uri;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import android.content.Context;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;



public class MainActivity extends AppCompatActivity implements SensorEventListener {

    int value;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, accelerometer, 1);
        mSensorManager.registerListener(this, gravity, 1);
        mSensorManager.registerListener(this, gyroscope, 1);

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

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ((TextView) findViewById(R.id.textView_accelerometer_x)).setText(String.valueOf(event.values[0]));
            ((TextView) findViewById(R.id.textView_accelerometer_y)).setText(String.valueOf(event.values[1]));
            ((TextView) findViewById(R.id.textView_accelerometer_z)).setText(String.valueOf(event.values[2]));
        }

        if(event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            ((TextView) findViewById(R.id.textView_gravity_x)).setText(String.valueOf(event.values[0]));
            ((TextView) findViewById(R.id.textView_gravity_y)).setText(String.valueOf(event.values[1]));
            ((TextView) findViewById(R.id.textView_gravity_z)).setText(String.valueOf(event.values[2]));
        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            ((TextView) findViewById(R.id.textView_gyroscope_x)).setText(String.valueOf(event.values[0]));
            ((TextView) findViewById(R.id.textView_gyroscope_y)).setText(String.valueOf(event.values[1]));
            ((TextView) findViewById(R.id.textView_gyroscope_z)).setText(String.valueOf(event.values[2]));
        }


/*
        String messageStr="Hello Android!";
        int server_port = 5555;
        DatagramSocket s;
        InetAddress local;
        try {
            s = new DatagramSocket();
        }catch(java.net.SocketException e){

        }

        try {
            local = InetAddress.getByName("192.168.0.2");
        }catch (java.net.UnknownHostException e){

        }
        int msg_length=messageStr.length();
        byte[] message = messageStr.getBytes();
        DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
        s.send(p);
*/


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
