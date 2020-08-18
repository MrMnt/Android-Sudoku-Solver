package com.example.sudokusolver.ViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sudokusolver.Backend.MyCords;
import com.example.sudokusolver.Backend.SudokuSolver;

import static com.example.sudokusolver.Backend.MySudokuUtils.*;

public class MySudokuViewModel extends ViewModel {

    private static String TAG = "SudokuSolver";

    MutableLiveData<MyCords> selectedCell;
    int selectedRow = 4, selectedCol = 4;
    MutableLiveData<int[][]> mutableSudokuGrid;
    int[][] sudokuGrid = problem0;

    public MutableLiveData<MyCords> getSelectedCell() {
        if(selectedCell == null) {
            selectedCell = new MutableLiveData<MyCords>();
            selectedCell.setValue(new MyCords(4, 4));
        }
        return selectedCell;
    }
    public MutableLiveData<int[][]> getSudokuGrid(){
        if(mutableSudokuGrid == null) {
            mutableSudokuGrid = new MutableLiveData<int[][]>();
            sudokuGrid = new int[SUDOKU_SIZE][SUDOKU_SIZE];
            mutableSudokuGrid.setValue(sudokuGrid);
        }
        return mutableSudokuGrid;
    }

    // Updates which is the selected cell
    public void updateSelectedCell(float x, float y){
        selectedRow = (int) y / CELL_SIZE;
        selectedCol = (int) x / CELL_SIZE;
        selectedCell.setValue(new MyCords(selectedRow, selectedCol));
    }
    // Updates the selected cells value
    public void updateSelectedCellValue(int newValue){

        // If we can NOT put this value in this cell, we return
        if(!isOk(sudokuGrid, newValue, selectedRow, selectedCol) && newValue != UNASSIGNED) return;

        // Otherwise set the value, and "send" it to the views
        sudokuGrid[selectedRow][selectedCol] = newValue;
        mutableSudokuGrid.setValue(sudokuGrid);
    }
    /* Solves the board */
    public void solveAndUpdate(){
        SudokuSolver solver = new SudokuSolver(sudokuGrid);

        long a1 = System.nanoTime();
        boolean solved = solver.solve();
        Log.d(TAG, "Time to solve: " + (System.nanoTime() - a1) / 1000 + " micro seconds");

        mutableSudokuGrid.setValue(sudokuGrid);
    }

    public void solveAndAnimate(){
        final SudokuSolver solver = new SudokuSolver(sudokuGrid);


        Runnable obj = new Runnable() {
            @Override
            public void run() {

                Runnable obj2 = new Runnable() {
                    @Override
                    public void run() {
                        long a1 = System.nanoTime();
                        solver.solve();
                        Log.d(TAG, "Time to solve: " + (System.nanoTime() - a1) / 1000 + " micro seconds");
                    }
                };

                Thread t2 = new Thread(obj2);
                long a1 = System.nanoTime();
                t2.setPriority(Thread.MAX_PRIORITY);
                Log.d(TAG, "Setting priority: " + (System.nanoTime() - a1) / 1000 + " micro seconds");
                t2.start();

                while(t2.isAlive()){
                    mutableSudokuGrid.postValue(sudokuGrid);
                }

                mutableSudokuGrid.postValue(sudokuGrid); // To make sure, we have the latest data
            }
        };

        // obj starts is a thread, which makes another thread...
        Thread t1 = new Thread(obj);
        t1.start();

    }

}
