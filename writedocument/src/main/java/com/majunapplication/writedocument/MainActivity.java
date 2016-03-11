package com.majunapplication.writedocument;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    TextView message;
    String mname;
    String mid;
    EditText name;
    EditText id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);


button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        try {
           // File file = new File("E:/test.txt");
            FileOutputStream fileOutputStream =

                    openFileOutput("test.txt",MODE_PRIVATE);
            message = (TextView) findViewById(R.id.message);
            name = (EditText) findViewById(R.id.name);
            id = (EditText) findViewById(R.id.id);

            mname = name.getText().toString();
            mid = id.getText().toString();
            Toast.makeText(MainActivity.this,"输入的信息为"+mname+mid,Toast.LENGTH_LONG);
            message.setText("输入的信息为："+ mname +mid);
            fileOutputStream.write(mname.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
});

    }
}
