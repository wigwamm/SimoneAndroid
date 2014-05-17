package com.sferrini.simone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

//public class MainActivity extends ActionBarActivity implements SensorEventListener {
public class MainActivity extends ActionBarActivity implements SurfaceHolder.Callback, SensorEventListener {

    //Camera
    private Camera camera;
    static private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    ImageView flash;
    ImageView shutter;
    //-----

    Bitmap bmp;
    //------

    //SoundManager for shutter
    SoundManager soundManager;
    //-----

    //Sensor
    SensorManager sm = null;
    Boolean isPerfect;
    TextView message = null;
    //-----

    //Info
    FrameLayout infoPopup;
    ImageView infoButton;

    final String tag = "AccLogger";

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

        message = (TextView) findViewById(R.id.message);

        soundManager.init(MainActivity.this);

        shutter = (ImageView)findViewById(R.id.takeButton);
        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPerfect) {
                    flash = (ImageView)findViewById(R.id.flash);
                    flash.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                    message.setVisibility(View.INVISIBLE);
                    shutter.setVisibility(View.INVISIBLE);
                    camera.takePicture(null, null, after_photo);
                    soundManager.play();
                }
            }
        });

        // PopUp
        infoPopup = (FrameLayout)findViewById(R.id.info);
        infoPopup.setVisibility(View.INVISIBLE);
        infoPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoPopup.setVisibility(View.INVISIBLE);
            }
        });

        infoButton = (ImageView)findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoPopup.setVisibility(View.VISIBLE);
            }
        });
    }

    //Camera
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release();
    }
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        try {
            camera.setPreviewDisplay(arg0);
            camera.startPreview();
        }
        catch (IOException e){}
    }

    Camera.PictureCallback after_photo = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            flash.setBackgroundColor(Color.parseColor("#00000000"));

            //Save picture
            try {
                //Save in media
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length, null);
                //bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);

                MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "photoSimone" , "SimonePhotos");
                bmp = null;

                shutter.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        	//Have to restart the camera
            camera.startPreview();
        }
    };
    //------

    //Sensor
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float [] values = event.values;
        synchronized (this) {
            //Log.d(tag, "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
            //Log.d(tag, " x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);

            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {

                fm = (FrameLayout)findViewById(R.id.FrameLayout1);
                Drawable borderGreen = getResources().getDrawable(R.drawable.border );
                Drawable borderRed = getResources().getDrawable(R.drawable.border_red );

                if ((values[1] > -1 && values[1] < 1) && (values[2] > -2 && values[2] < 2)) {
                    isPerfect = true;
                    message.setText("");
                    message.setVisibility(View.INVISIBLE);

                    fm.setBackground(borderGreen);
                } else {
                    isPerfect = false;
                    fm.setBackground(borderRed);
                    message.setVisibility(View.VISIBLE);

                    //Rotate
                    if (values[1] > 1) {
                        message.setText("Rotate the phone left");
                    }
                    if (values[1] < 1) {
                        message.setText("Rotate the phone right");
                    }

                    //Tilt
                    if (values[2] > 2) {
                        message.setText("Tilt the phone back");
                    }
                    if (values[2] < -2) {
                        message.setText("Tilt the phone forwards");
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Log Accuracy
        //Log.d(tag, "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Sensor Accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_UI); //SENSOR_DELAY_FASTEST
    }
    @Override
    protected void onStop() {
        // unregister listener
        sm.unregisterListener(this);
        super.onStop();
    }
    //-----

}
