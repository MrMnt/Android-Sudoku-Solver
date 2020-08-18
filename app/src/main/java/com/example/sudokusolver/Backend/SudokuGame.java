package com.example.sudokusolver.Backend;

import static com.example.sudokusolver.Backend.MySudokuUtils.*;

/* Has all the data about the sudoku, like the whole grid of numbers */
public class SudokuGame {

    int[][] startingSudokuGrid = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    int[][] solvedSudokuGrid = new int[SUDOKU_SIZE][SUDOKU_SIZE];

    public SudokuGame(int[][] sudokuGrid){
        copy2DIntArrays(sudokuGrid, startingSudokuGrid);
    }
    public SudokuGame(){}

}
