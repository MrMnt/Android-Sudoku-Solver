package com.example.sudokusolver.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sudokusolver.Backend.ExtraClaasses.MyCords;
import com.example.sudokusolver.R;
import com.example.sudokusolver.ViewModel.MySudokuViewModel;
import com.example.sudokusolver.Views.ManualSudokuGridView;

import static com.example.sudokusolver.Backend.MySudokuUtils.LOAD_OPTION;
import static com.example.sudokusolver.Backend.MySudokuUtils.REQUEST_MEMORY;
import static com.example.sudokusolver.Backend.MySudokuUtils.SAVE_OPTION;
import static com.example.sudokusolver.Backend.MySudokuUtils.SUDOKU_SIZE;
import static com.example.sudokusolver.Backend.MySudokuUtils.UNASSIGNED;

public class ManualMode extends AppCompatActivity implements ManualSudokuGridView.myCanvasInterface {

    private static String TAG = "SudokuSolver";

    // ViewModel is lifecycle aware component, stays even if we flip our phone etc.
    MySudokuViewModel mySudokuViewModel;

    // Our canvas, where we draw the sudoku grid
    ManualSudokuGridView myManualSudokuGridView;

    // Input Buttons, used for giving value to a cell + delete button
    Button[] inputButtons = new Button[SUDOKU_SIZE + 1];
    Button solveBtn, clearBtn, saveBtn;

    int saveLoadOption = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mode);

        myManualSudokuGridView = findViewById(R.id.myManualSudokuGridView);

        myManualSudokuGridView.setListener(this);

        mySudokuViewModel = new ViewModelProvider(this).get(MySudokuViewModel.class);
        mySudokuViewModel.getSelectedCell().observe(this, new Observer<MyCords>() {
            @Override
            public void onChanged(MyCords selectedCell) {
                myManualSudokuGridView.updateSelectedCellUI(selectedCell);
            }
        });
        mySudokuViewModel.getSudokuGrid().observe(this, new Observer<int[][]>() {
            @Override
            public void onChanged(int[][] sudokuGridSrc) {
                myManualSudokuGridView.updateSudokuGridUI(sudokuGridSrc);
            }
        });

        initializeInputButtons();
    }

    @Override
    public void onTouchEventOccurred(float x, float y) {
        mySudokuViewModel.updateSelectedCell(x, y);
    }

    public void initializeInputButtons(){
        inputButtons[0] = findViewById(R.id.deleteBtn);
        inputButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySudokuViewModel.updateSelectedCellValue(UNASSIGNED);
            }
        });

        inputButtons[1] = findViewById(R.id.button1);
        inputButtons[2] = findViewById(R.id.button2);
        inputButtons[3] = findViewById(R.id.button3);
        inputButtons[4] = findViewById(R.id.button4);
        inputButtons[5] = findViewById(R.id.button5);
        inputButtons[6] = findViewById(R.id.button6);
        inputButtons[7] = findViewById(R.id.button7);
        inputButtons[8] = findViewById(R.id.button8);
        inputButtons[9] = findViewById(R.id.button9);

        // The key pad
        for(int digit = 1; digit <= SUDOKU_SIZE; digit++){
            final int value = digit;
            inputButtons[digit].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mySudokuViewModel.updateSelectedCellValue(value);
                }
            });
        }

        solveBtn = findViewById(R.id.solveBtn);
        solveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myManualSudokuGridView.saveStartingGrid();
                mySudokuViewModel.solveAndAnimate();
            }
        });
        clearBtn = findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySudokuViewModel.setNewBoard();
            }
        });

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoadOption = SAVE_OPTION;
                // if the permissions are granted, do SAVING stuff
                askForMemoryPermissions();
            }
        });
    }

    // Created the option menu at the top, in this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.toggleDarkModeItem:
                // TODO: Toggle the dark mode
                return true;
            case R.id.openFileItem:
                // Opens a menu, to choose a sudoku to open, IF the permissions were granted
                saveLoadOption = LOAD_OPTION;
                askForMemoryPermissions();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void askForMemoryPermissions(){

        // if the build version already accepts everything no need to check for permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        // Checks all the needed permissions, if they are granted, great, do what you wanted, if not, request them
        if (ContextCompat.checkSelfPermission(ManualMode.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ManualMode.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            if(saveLoadOption == SAVE_OPTION){
                // TODO: do saving stuff
            } else if (saveLoadOption == LOAD_OPTION){
                // TODO do loading stuff
            }

        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(ManualMode.this, "Permissions needed for using saving and loading sudoku", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_MEMORY);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_MEMORY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && checkGrantedResults(grantResults)) {
                    // Permission is granted. Continue the action or workflow in your app.

                    if(saveLoadOption == SAVE_OPTION){
                        // TODO: do saving stuff
                    } else if (saveLoadOption == LOAD_OPTION){
                        // TODO do loading stuff
                    }

                }  else {
                    Toast.makeText(ManualMode.this, "Saving and loading is not available without media permissions," +
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
