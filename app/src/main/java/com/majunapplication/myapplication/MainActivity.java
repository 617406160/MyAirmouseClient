package com.majunapplication.myapplication;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class MainActivity extends Activity
        implements SensorEventListener, RadioGroup.OnCheckedChangeListener {

    private SensorFusion sensorFusion;
    private BubbleLevelCompass bubbleLevelCompass;
    private SensorManager sensorManager = null;

    private RadioGroup setModeRadioGroup;
    private TextView azimuthText, pithText, rollText;
    private DecimalFormat d = new DecimalFormat("#.##");


    private DataOutputStream dataOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        registerSensorManagerListeners();

        d.setMaximumFractionDigits(2);
        d.setMinimumFractionDigits(2);

        sensorFusion = new SensorFusion();
        sensorFusion.setMode(SensorFusion.Mode.ACC_MAG);

        bubbleLevelCompass = (BubbleLevelCompass) this.findViewById(R.id.SensorFusionView);
        setModeRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        azimuthText = (TextView) findViewById(R.id.azmuth);
        pithText = (TextView) findViewById(R.id.pitch);
        rollText = (TextView) findViewById(R.id.roll);
        setModeRadioGroup.setOnCheckedChangeListener(this);
    }

    public void updateOrientationDisplay() {

        final double azimuthValue = sensorFusion.getAzimuth();
        double rollValue = sensorFusion.getRoll();
        double pitchValue = sensorFusion.getPitch();


        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("/mnt/sdcard/zimuthValue.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dataOutputStream = new DataOutputStream(fileOutputStream);

        azimuthText.setText(String.valueOf(d.format(azimuthValue)));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    dataOutputStream.writeDouble(azimuthValue);
                    dataOutputStream.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();



        pithText.setText(String.valueOf(d.format(pitchValue)));
        rollText.setText(String.valueOf(d.format(rollValue)));

        bubbleLevelCompass.setPLeft((int) rollValue);
        bubbleLevelCompass.setPTop((int) pitchValue);
        bubbleLevelCompass.setAzimuth((int) azimuthValue);

    }

    public void registerSensorManagerListeners() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensorManagerListeners();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorFusion.setAccel(event.values);
                sensorFusion.calculateAccMagOrientation();
                break;

            case Sensor.TYPE_GYROSCOPE:
                sensorFusion.gyroFunction(event);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorFusion.setMagnet(event.values);
                break;
        }
        updateOrientationDisplay();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.radio0:
                sensorFusion.setMode(SensorFusion.Mode.ACC_MAG);
                break;
            case R.id.radio1:
                sensorFusion.setMode(SensorFusion.Mode.GYRO);
                break;
            case R.id.radio2:
                sensorFusion.setMode(SensorFusion.Mode.FUSION);
                break;
        }
    }

}
