package com.example.sudokusolver.ViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sudokusolver.Backend.MyCords;

import static com.example.sudokusolver.Backend.MySudokuUtils.CELL_SIZE;

public class MySudokuViewModel extends ViewModel {

    private static String TAG = "SudokuSolver";

    MutableLiveData<MyCords> selectedCell;

    public MutableLiveData<MyCords> getSelectedCell() {
        if(selectedCell == null) selectedCell = new MutableLiveData<MyCords>();
        return selectedCell;
    }
    public void updateSelectedCell(float x, float y){
        int selectedRow = (int) y / CELL_SIZE;
        int selectedCol = (int) x / CELL_SIZE;
        Log.d(TAG, "updateSelectedCell: " + selectedRow + ", " + selectedCol);
        selectedCell.setValue(new MyCords(selectedRow, selectedCol));
    }

}
