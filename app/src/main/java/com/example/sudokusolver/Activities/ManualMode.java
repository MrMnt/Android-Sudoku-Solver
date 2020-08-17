package com.example.sudokusolver.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.sudokusolver.Backend.MyCords;
import com.example.sudokusolver.R;
import com.example.sudokusolver.ViewModel.MySudokuViewModel;
import com.example.sudokusolver.Views.ManualSudokuGridView;

public class ManualMode extends AppCompatActivity implements ManualSudokuGridView.myCanvasInterface {

    // ViewModel is lifecycle aware component, stays even if we flip our phone etc.
    MySudokuViewModel mySudokuViewModel;

    ManualSudokuGridView myManualSudokuGridView;

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
    }

    @Override
    public void onTouchEventOccurred(float x, float y) {
        mySudokuViewModel.updateSelectedCell(x, y);
    }
}