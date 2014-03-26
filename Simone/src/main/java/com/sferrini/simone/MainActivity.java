package com.sferrini.simone;

import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

//public class MainActivity extends ActionBarActivity implements SensorEventListener {
public class MainActivity extends ActionBarActivity implements SurfaceHolder.Callback, SensorEventListener {
    //Camera
    private Camera camera;
    static private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    //-----


    final String tag = "AccLogger";

    SensorManager sm = null;
    TextView xAccView = null;
    TextView yAccView = null;
    TextView zAccView = null;
    FrameLayout fm = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView)findViewById(R.id.camera_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);


        // get reference to SensorManager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        xAccView = (TextView) findViewById(R.id.xbox);
        yAccView = (TextView) findViewById(R.id.ybox);
        zAccView = (TextView) findViewById(R.id.zbox);
    }

    //Camera
    public void surfaceCreated(SurfaceHolder holder) {
        camera=Camera.open();
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release();
    }
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        try{
            camera.setPreviewDisplay(arg0);
            camera.startPreview();}
        catch (IOException e){}
    }
    //------


    //Sensor
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float [] values = event.values;
        synchronized (this) {
            //Log.d(tag, "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);

            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
                xAccView.setText("Sway: " + values[0]);
                yAccView.setText("Surge: " + values[1]);
                zAccView.setText("Heave: " + values[2]);

                fm = (FrameLayout)findViewById(R.id.FrameLayout1);
                Drawable borderGreen = getResources().getDrawable(R.drawable.border );
                Drawable borderRed = getResources().getDrawable(R.drawable.border_red );

                if (values[1] > -1 && values[1] < 1) {
                    fm.setBackground(borderGreen);
                } else {
                    fm.setBackground(borderRed);
                }
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(tag, "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Sensor Accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor Orient = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // register this class as a listener for the orientation and accelerometer sensors
        sm.registerListener(this, Accel, SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(this, Orient, SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    protected void onStop() {
        // unregister listener
        sm.unregisterListener(this);
        super.onStop();
    }
    //-----

}
