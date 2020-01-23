package com.example.trackeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.trackeme.Model.Response;

public class InfoActivity extends AppCompatActivity {

    TextView Name , CarNumber , Stops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Name = findViewById(R.id.NameValue);
        CarNumber = findViewById(R.id.CarNumberValue);
        Stops = findViewById(R.id.StopsValue);
        Response res = Response.getInstance();
        Name.setText(res.Name);
        CarNumber.setText(res.CarNumber);
        StringBuilder SB =new StringBuilder();
        SB.append("( "+res.stopPoints.get(0).Name);
        for(int i =1;i<res.stopPoints.size();i++){
            SB.append(" - "+res.stopPoints.get(i).Name);
        }
        SB.append(" )");
        Stops.setText(SB.toString());
    }

    public void GoToMap(View view) {
        Intent i = new Intent(this , MapsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
