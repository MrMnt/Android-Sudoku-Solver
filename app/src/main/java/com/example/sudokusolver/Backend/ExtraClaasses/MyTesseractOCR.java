package com.example.sudokusolver.Backend.ExtraClaasses;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyTesseractOCR {

    private static String TAG = "ActivityCamera";

    private final TessBaseAPI mTessOCR;

    // The path, to the directory, where I want the files to go
    String DST_PATH_DIR;
    // The path to the file in the directory
    String DST_PATH_FILE;
    String DST_INIT_PATH;

    public MyTesseractOCR(Context context, String language){

        mTessOCR = new TessBaseAPI();
        if(copyTessData(context, "eng.traineddata")){
            Log.d(TAG, "MyTesseractOCR: 1 - copy was suceeded");
            if(mTessOCR.init(DST_INIT_PATH, "eng", TessBaseAPI.OEM_TESSERACT_ONLY)){
                mTessOCR.setVariable("classify_bln_numeric_mode", "1");
                mTessOCR.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, ",.!?@#$%&*()<>_-+=/:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
                mTessOCR.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");
                mTessOCR.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
            }

        }

    }

    private boolean copyTessData(Context context, String fileName){
        // To check, if maybe, we have already copied the tessData
        boolean tessDataExists = false, success = false;

        // The asset manager (where we keep the tessdata)
        AssetManager assetManager = context.getAssets();

        // The path, to the directory, where I want the files to go
        DST_PATH_DIR = context.getExternalFilesDir(null).toString() + "/TesseractOCR/tessdata/";
        // The path to the file in the directory
        DST_PATH_FILE = DST_PATH_DIR + fileName;
        // The path, where tessdata folder is kept
        DST_INIT_PATH = context.getExternalFilesDir(null).toString() + "/TesseractOCR/";

        // The streams, for file transfer
        InputStream inputStream = null; OutputStream outputStream = null;

        try {
            // Try to get the Input file, from asset manager
            inputStream = assetManager.open("Standart/" + fileName);

            // File to the directory, where I want the files to go
            File dir = new File(DST_PATH_DIR);

            if(!dir.exists()){
                // If the directory does not exist, we try to create it
                if(!dir.mkdirs()){
                    // If we could not make the directory
                    Log.d(TAG, fileName + " could not be created");
                } else {
                    // If it was possible to create the directory
                    outputStream = new FileOutputStream(new File(DST_PATH_FILE));
                    Log.d(TAG, "OutputStream: created");
                }
            } else {
                // If the directory already exists, we check if it has the tessdata we need
                File[] contents = dir.listFiles();

                if(contents != null){
                    if(contents.length >= 1) {
                        // Its okay, we have the tessdata.
                        tessDataExists = true;
                    } else {
                        outputStream = new FileOutputStream(new File(DST_PATH_FILE));
                    }
                }

            }
        } catch (IOException e) {
            // We catch the exception if something wrong happens
            e.printStackTrace();
        } finally {
            // Regardless whether there was an exception, or not, we do this code
            if(tessDataExists){
                Log.d(TAG, "TessData exists:");
                if(inputStream != null) {
                    // If the inputStream is not null, we try to clean it
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    success = true;
                }
            }

            if (inputStream != null && outputStream != null){
                // If we have both, the input and output streams, we try to copy them
                try {
                    copyFileUsingStream(inputStream, outputStream);
                    inputStream.close(); outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                success = true;
            }
        }

        return success;
    }

    private static void copyFileUsingStream(InputStream is, OutputStream os) throws IOException {
        // Copies the files in chunks *to my understanding*
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0){
            os.write(buffer, 0, length);
        }
    }


    // Checks if a volume containing external storage is available
    // for read and write.
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
    }

    // Checks if a volume containing external storage is available to at least read.
    private boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ||
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY;
    }

    public String getOCRResult(Bitmap bitmap) {
        mTessOCR.clear();
        mTessOCR.setImage(bitmap);
        String text = mTessOCR.getUTF8Text();
        return text;
    }

    public void onDestroy() {
        if (mTessOCR != null) mTessOCR.end();
    }

}

//        long a1 = System.nanoTime();
//        Log.d(TAG, "setting: " + (System.nanoTime() - a1) / 1000000  + " ms");
