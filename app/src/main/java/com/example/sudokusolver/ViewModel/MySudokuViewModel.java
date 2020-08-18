package com.example.sudokusolver.ViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sudokusolver.Backend.MyCords;

import static com.example.sudokusolver.Backend.MySudokuUtils.*;

public class MySudokuViewModel extends ViewModel {

    private static String TAG = "SudokuSolver";

    MutableLiveData<MyCords> selectedCell;
    MutableLiveData<int[][]> sudokuGrid;

    public MutableLiveData<MyCords> getSelectedCell() {
        if(selectedCell == null) {
            selectedCell = new MutableLiveData<MyCords>();
            selectedCell.setValue(new MyCords(4, 4));
        }
        return selectedCell;
    }
    public MutableLiveData<int[][]> getSudokuGrid(){
        if(sudokuGrid == null) {
            sudokuGrid = new MutableLiveData<int[][]>();
            //sudokuGrid.setValue(new int[SUDOKU_SIZE][SUDOKU_SIZE]);
            sudokuGrid.setValue(problem1);
        }
        return sudokuGrid;
    }

    // Updates which is the selected cell
    public void updateSelectedCell(float x, float y){
        int selectedRow = (int) y / CELL_SIZE;
        int selectedCol = (int) x / CELL_SIZE;
        selectedCell.setValue(new MyCords(selectedRow, selectedCol));
    }
    // Updates the selected cells value
    public void updateSelectedCellValue(int newValue){
        MyCords sCell = selectedCell.getValue();
        int[][] temp = sudokuGrid.getValue();
        temp[sCell.getRow()][sCell.getCol()] = newValue;
        printSudoku(temp);
        sudokuGrid.setValue(temp);
    }

}
