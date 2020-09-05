package com.example.sudokusolver.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sudokusolver.Activities.ManualMode;
import com.example.sudokusolver.R;

import static com.example.sudokusolver.Backend.MySudokuUtils.REQUEST_CAMERA_AND_MEMORY;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpButtons();
    }

    private void setUpButtons(){
        findViewById(R.id.manualModeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an Intent, from this activity, to the ManualMode activity
                Intent intent = new Intent(MainActivity.this, ManualMode.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cameraModeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an Intent, from this activity, to the CameraMode activity
                // if the permissions were accepted
                askForCameraModePermissions();
            }
        });
    }

    private void askForCameraModePermissions(){

        // if the build version already accepts everything no need to check for permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        // Checks all the needed permissions, if they are granted, great, go to camera activity, if not, request them
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

            // Creates an Intent, from this activity, to the ManualMode activity
            Intent intent = new Intent(MainActivity.this, CameraMode.class);
            startActivity(intent);
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                Toast.makeText(MainActivity.this, "Permissions needed for using camera mode", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA }, REQUEST_CAMERA_AND_MEMORY);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CAMERA_AND_MEMORY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && checkGrantedResults(grantResults)) {
                    // Permission is granted. Continue the action or workflow in your app.

                    // Creates an Intent, from this activity, to the ManualMode activity
                    Intent intent = new Intent(MainActivity.this, CameraMode.class);
                    startActivity(intent);
                }  else {
                    Toast.makeText(MainActivity.this, "Camera mode is not available without camera and file permissions," +
                            "\n\nif the dialog box does not appear and you want to use this feature, " +
                            "please go to app settings and allow all the permissions", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* Returns true, if all results were granted */
    public boolean checkGrantedResults(int[] grantedResults){
        for(int result : grantedResults){
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

}