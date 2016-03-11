package com.example.myairmouseclient1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;

@SuppressLint("ClickableViewAccessibility")
public class ControlActivity extends Activity {
	private float current_x;// 鼠标现在的坐标
	private float current_y;
	private float new_x;// 更新后的坐标
	private float new_y;
	private float delta_x;// 坐标变化
	private float delta_y;
	private LinearLayout touch;
	private Button btn_left;
	private Button btn_right;
	private Button btn_middle;
	private MessageSender sender;
	private static float leftbutton_x = 0; // 鼠标左键移动初始化坐标
	private static float leftbutton_y = 0;
	private static float fingerx;// 手指触摸的坐标
	private static float fingery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		sender = new MessageSender();
		sender.execute();
		initTouch();
		initleftbutton();
		initrightbutton();
		initmiddlebutton();
	}

	private void initmiddlebutton() {
		btn_middle = (Button) findViewById(R.id.btn_middle);
		btn_middle.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
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
		btn_right = (Button) findViewById(R.id.btn_right);
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

	private void initTouch() {
		touch = (LinearLayout) findViewById(R.id.touch);
		touch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (ev.getAction() == MotionEvent.ACTION_MOVE)
					onMouseMove(ev);
				if (ev.getAction() == MotionEvent.ACTION_DOWN)
					onMouseDown(ev);
				if (ev.getAction() == MotionEvent.ACTION_UP)
					onMouseUp(ev);
				return true;
			}
		});
	}

	private void initleftbutton() {
		btn_left = (Button) findViewById(R.id.btn_left);//鼠标左键+定义操作
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
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					moveMouseWithTowFinger(event);
				}
				return false;
			}

			private void moveMouseWithTowFinger(MotionEvent event) {
				int count = event.getPointerCount();
				if (count == 2) {
					if (leftbutton_x == 0 && leftbutton_y == 0) {
						leftbutton_x = event.getX(1);
						leftbutton_y = event.getY(1);
						return;
					}
					float x = event.getX(1);
					float y = event.getY(1);
					sendMouseMessage("mouse", x - leftbutton_x, y - leftbutton_y);
					leftbutton_x = x;
					leftbutton_y = y;
				}
				if (count == 1) {
					leftbutton_x = 0;
					leftbutton_y = 0;
				}
			}
		});
	}

	protected void onMouseUp(MotionEvent event) {
		if (fingerx == event.getX() && fingery == event.getY()) {
			sender.send("leftButton:down");
			sender.send("leftButton:release");
		}

	}
	protected void onMouseDown(MotionEvent event) {
		current_x = event.getX();// 鼠标指针初始化
		current_y = event.getY();
		fingerx = event.getX();
		fingery = event.getY();

	}

	protected void onMouseMove(MotionEvent event) {
		new_x = event.getX();
		new_y = event.getY();
		delta_x = new_x - current_x;
		delta_y = new_y - current_y;
		current_x = new_x;
		current_y = new_y;
		if (delta_x != 0 || delta_y != 0) {
			this.sendMouseMessage("mouse", delta_x, delta_y);
		}

	}
	private void sendMouseMessage(String type, float delta_x2, float delta_y2) {
		String str = type + ":" + delta_x2 + "," + delta_y2;
		Log.i("main", str);
		sender.send(str);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
