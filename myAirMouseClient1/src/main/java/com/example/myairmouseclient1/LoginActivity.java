package com.example.myairmouseclient1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {
	EditText input_IP;
	EditText input_port;
	Button btn_login;
	Button btn_clear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ininView();
        //LoginInfo.port = Integer.parseInt(input_port.getText().toString());
        btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginInfo.IP = input_IP.getText().toString();
			    LoginInfo.port = input_port.getText().toString();
				Intent intent = new Intent(LoginActivity.this, SensorControlActivity.class);
				startActivity(intent);
			}
		});
        btn_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				input_IP.setText("");
				input_port.setText("");
				
			}
		});
    }

	private void ininView() {
		 input_IP = (EditText) findViewById(R.id.et_ip);
		 input_port = (EditText) findViewById(R.id.et_port);
		 btn_login = (Button) findViewById(R.id.btn_login);
		 btn_clear = (Button) findViewById(R.id.btn_clear);
	}


}
