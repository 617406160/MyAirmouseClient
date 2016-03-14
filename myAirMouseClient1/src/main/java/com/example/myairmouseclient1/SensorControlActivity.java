package com.example.myairmouseclient1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class SensorControlActivity extends Activity {
	private double delta_x;// 坐标变化
	private double delta_y;
	private float currentangle_x;// 鼠标现在的坐标
	private float currentangle_y;
	private float newangle_x;// 更新后的坐标
	private float newangle_y;
	private float deltaangle_x;// 坐标变化
	private float deltaangle_y;
	private float r = 15;
	private MessageSender sender;
	SensorManager manager;
	Sensor gryo;
	Sensor acceler;
	Sensor magnetic;
	SensorEventListener gryolistener;
	float[] acclerValues = new float[3];
	float[] magneticValues = new float[3];
	SensorEventListener mListener;
	int sensorflag = 1;
	protected int leftbutton_x;
	protected int leftbutton_y;
	private float fingerx;
	private float fingery;
	private float current_x;
	private float current_y;
	static int flage = 0;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_control);
		sender = new MessageSender();
		sender.execute();
		initleftbutton();
		initrightbutton();
		initmiddlebutton();
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);// 注册系统传感器监听权限
		gryo = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		final Sensor rotation = manager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		View pressarea = findViewById(R.id.pressarea);

		acceler = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gryolistener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {// 谭东修改
				newangle_x = event.values[0];// z轴坐标映射为鼠标左右
				newangle_y = event.values[1];// x轴坐标映射为鼠标上下
				float newangle_z = event.values[0];
				deltaangle_x = newangle_x - currentangle_x;
				deltaangle_y = newangle_y - currentangle_y;
				delta_x = deltaangle_x * r;
				delta_y = deltaangle_y* r;
				currentangle_x = newangle_x;
				currentangle_y = newangle_y;

				if (flage !=  0){
					String message = "mouse:" + delta_x + "," + delta_y;
					sender.send(message);
				}
				flage = 1;

				
				
				try {
					String outx = newangle_x + "\r\n";
					FileOutputStream stream = openFileOutput("rotationx.txt",
							MODE_APPEND);
					stream.write(outx.getBytes());
					
					String outy = newangle_y + "\r\n";
					FileOutputStream streamy = openFileOutput("rotationy.txt",
							MODE_APPEND);
					streamy.write(outy.getBytes());
					
					String outz = newangle_z + "\r\n";
					FileOutputStream streamz = openFileOutput("rotationz.txt",
							MODE_APPEND);
					streamz.write(outz.getBytes());

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}
		};
		pressarea.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case (MotionEvent.ACTION_DOWN):
					manager.registerListener(gryolistener, rotation,
							SensorManager.SENSOR_DELAY_FASTEST);
					return true;
				case MotionEvent.ACTION_UP:
					manager.unregisterListener(gryolistener);
//					currentangle_x = null;
//					currentangle_y = null;

					return false;
				default:

					return false;
				}
			}
		});

	}

	@Override
	protected void onPause() {
		manager.unregisterListener((SensorListener) this);
		super.onPause();
		flage = 0;
	}
	private void initmiddlebutton() {
		Button btn_middle = (Button) findViewById(R.id.btn_middle);
		btn_middle.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float current_y = 0;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					current_y = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					float y = event.getY();
					delta_y = y -current_y;
					current_y = y;
					if (delta_y > 3 || delta_y < -3) {
						String string = "mousewheel" + ":" + delta_y;
						sender.send(string);
					}
				}
				return false;
			}
		});
	}
	private void initrightbutton() {
		View btn_right = (Button) findViewById(R.id.btn_right);
		btn_right.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sender.send("rightButton:down");
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					sender.send("rightButton:release");
				}
				return false;
			}
		});
		
	}
	private void initleftbutton() {
		Button btn_left = (Button) findViewById(R.id.btn_left);//鼠标左键+定义操作
		btn_left.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					String str = "leftButton" + ":" + "down";
					sender.send(str);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					String string = "leftButton" + ":" + "release";
					sender.send(string);
					leftbutton_x = 0;
					leftbutton_y = 0;
				}
				
				return false;
			}

		
		});
	}

	


}
