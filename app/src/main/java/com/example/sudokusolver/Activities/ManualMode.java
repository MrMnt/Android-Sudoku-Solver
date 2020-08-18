package com.example.sudokusolver.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.sudokusolver.Backend.MyCords;
import com.example.sudokusolver.R;
import com.example.sudokusolver.ViewModel.MySudokuViewModel;
import com.example.sudokusolver.Views.ManualSudokuGridView;

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
    }
}