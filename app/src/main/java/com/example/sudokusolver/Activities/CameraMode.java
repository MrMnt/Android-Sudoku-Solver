package com.example.sudokusolver.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sudokusolver.Backend.ExtraClaasses.MyImageProcessing;
import com.example.sudokusolver.Backend.ExtraClaasses.MyTesseractOCR;
import com.example.sudokusolver.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class CameraMode extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "ActivityCamera";

    // For displaying the camera
    JavaCameraView mOpenCvCameraView;

    //For displaying the solved sudoku
    ImageView myImageView;

    MyImageProcessing myImageProcessing;
    MyTesseractOCR myTesseractOCR;

    Button cameraBtn, solveBtn; // For toggling the camera and solving

    int[][] temp = new int[9][9];

    boolean stopFrames = false; // If this is true, we stop the frames form coming
    boolean frameProcessed = false; // If this is false, the frame is still being processed
    Runnable runObj; // used for running another thread, to check if the frame has finished being processed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_mode);

        setLandscapeAndStuff();
        initializeCamera();
        initializeProcessing();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if(stopFrames){
            Mat finalImage = myImageProcessing.getFinalImage(inputFrame);
            frameProcessed = true;
            return finalImage;
        }

        return inputFrame.rgba();
    }

    private void initializeProcessing(){
        myTesseractOCR = new MyTesseractOCR(CameraMode.this, "eng");
        myImageProcessing = new MyImageProcessing(myTesseractOCR, temp);

        runObj = new Runnable() {
            @Override
            public void run() {
                // While the frame is still being processed, lets wait
                while(frameProcessed == false){
                    try { Thread.sleep(10); } catch (Exception e) {}
                }
                // Once the frame has finished being processed, we "close" the camera
                mOpenCvCameraView.disableView();
                // And set the frameProcessed to false, to prepare for the next time
                frameProcessed = false;
            }
        };
    }

    private void initializeCamera(){
        mOpenCvCameraView = findViewById(R.id.myCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(CameraMode.this);
        //mOpenCvCameraView.setMaxFrameSize(1920, 1080);
    }
    private void setLandscapeAndStuff(){
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //set orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //permanent full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFrames = !stopFrames;
                if(stopFrames){
                    mOpenCvCameraView.setVisibility(View.VISIBLE);
                    myImageView.setVisibility(View.INVISIBLE);
                    new Thread(runObj).start(); // If we want to stop frames, we do this, but only after the last one is processed
                } else {
                    mOpenCvCameraView.setVisibility(View.VISIBLE);
                    myImageView.setVisibility(View.INVISIBLE);
                    mOpenCvCameraView.enableView(); // else, we continue the frames
                }
            }

        });

        myImageView = findViewById(R.id.myImageView);
        if(myImageView != null) myImageView.setVisibility(View.INVISIBLE); // Since we don't want anything to show at first

        solveBtn = findViewById(R.id.solveBtn);
        if(solveBtn != null) solveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: show the solved image
                Mat sudokuImage = myImageProcessing.getsolvedSudokuImage();
                // convert to bitmap:
                Bitmap sudokuImageBitmap = Bitmap.createBitmap(sudokuImage.cols(), sudokuImage.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(sudokuImage, sudokuImageBitmap);

                if(myImageView != null) {
                    mOpenCvCameraView.setVisibility(View.INVISIBLE);
                    myImageView.setVisibility(View.VISIBLE);
                    myImageView.setImageBitmap(sudokuImageBitmap);
                }
            }
        });
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    static { OpenCVLoader.initDebug(); }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }
}