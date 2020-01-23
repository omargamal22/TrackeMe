package com.example.trackeme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trackeme.Model.Response;
import com.example.trackeme.Networking.Retrofit_Service;

import org.w3c.dom.Text;

public class LogIN extends AppCompatActivity {

    EditText UserName , PassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        UserName = findViewById(R.id.use_name);
        PassWord = findViewById(R.id.use_Pass);
    }

    public void Log(View view) {
        Retrofit_Service.Get_Retrofit_Service(UserName.getText().toString(),PassWord.getText().toString());
        Retrofit_Service.res.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("OK")){
                    Intent i =new Intent(LogIN.this ,InfoActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(),"The UserName OR Password is WRONG",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
