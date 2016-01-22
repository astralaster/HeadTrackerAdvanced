package com.example.sascha.headtrackeradvanced.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.example.sascha.headtrackeradvanced.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UDPService extends Service implements SensorEventListener {
    private String host;   // Ip-Address of Receiver
    private int port;         // Port of Receiver
    public static final int VALUE_SIZE_IN_BYTE = 52;    // Temporary....

    private InetAddress server;
    private DatagramSocket socket;
    private double[] values;
    Thread sender;

    @Override
    public void onCreate() {
        super.onCreate();
        values = new double[6];

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, accelerometer, 1);
        mSensorManager.registerListener(this, gravity, 1);
        mSensorManager.registerListener(this, gyroscope, 1);

        sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server = InetAddress.getByName(host);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (socket != null) {
                    // Convert Values to Byte[]
                    ByteBuffer b;
                    while (!Thread.interrupted()) {
                        b = ByteBuffer.allocate(VALUE_SIZE_IN_BYTE).order(ByteOrder.LITTLE_ENDIAN);
                        for (double v : values) {
                            b.putDouble(v);
                        }
                        try {
                            socket.send(new DatagramPacket(b.array(), VALUE_SIZE_IN_BYTE, server, port));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            sender.interrupt();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        host = intent.getStringExtra("ip");
        port = intent.getIntExtra("port",0);
        try {
            socket = new DatagramSocket(5555);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        sender.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sender.interrupt();
        socket.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (int i = 0; i < 3; i++) {
            values[i] = event.values[i];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
