package com.example.sudokusolver.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sudokusolver.Activities.ManualMode;
import com.example.sudokusolver.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpButtons(this);
    }

    private void setUpButtons(final Context mainActivityContext){
        findViewById(R.id.manualModeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an Intent, from this activity, to the ManualMode activity
                Intent intent = new Intent(mainActivityContext, ManualMode.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cameraModeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an Intent, from this activity, to the ManualMode activity
                Intent intent = new Intent(mainActivityContext, CameraMode.class);
                startActivity(intent);
            }
        });
    }
}