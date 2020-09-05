package com.example.sudokusolver.Backend;

import android.util.Log;

public class MySudokuUtils {

    private static String TAG = "SudokuSolver";

    // For the permissions, we use them as request codes
    public static final int REQUEST_CAMERA_AND_MEMORY = 111;
    public static final int REQUEST_MEMORY = 112;
    public static final int SAVE_OPTION = 1000;
    public static final int LOAD_OPTION = 999;

    // The number of cells in whole sudoku, and sudoku section
    public static final int SUDOKU_SIZE = 9;
    public static final int SECTION_SIZE = 3;
    public static final int UNASSIGNED = 0;

    // The size of the cell in pixels
    public static int CELL_SIZE;

    /* Given this number, in that place,
     * Returns true if it complies with all the
     * Sudoku rules, otherwise, false		*/
    public static boolean isOk(int[][] sudokuGrid, int value, int row, int col) {

        if(!isOkInRow(sudokuGrid, value, row)) return false;
        if(!isOkInCol(sudokuGrid, value, col)) return false;
        if(!isOkInSection(sudokuGrid, value, row, col)) return false;

        return true;
    }
    private static boolean isOkInRow(int[][] sudokuGrid, int value, int row) {

        for(int col = 0; col < SUDOKU_SIZE; col++) {
            if(sudokuGrid[row][col] == value) return false;
        }
        return true; // None other cell in this row had the same value
    }
    private static boolean isOkInCol(int[][] sudokuGrid, int value, int col) {

        for(int row = 0; row < SUDOKU_SIZE; row++) {
            if(sudokuGrid[row][col] == value) return false;
        }
        return true; // None other cell in this row had the same value
    }
    private static boolean isOkInSection(int[][] sudokuGrid, int value, int row_, int col_) {
        int startRow = (row_ / SECTION_SIZE) * SECTION_SIZE;
        int startCol = (col_ / SECTION_SIZE) * SECTION_SIZE;

        int endRow = startRow + SECTION_SIZE;
        int endCol = startCol + SECTION_SIZE;

        for(int row = startRow; row < endRow; row++) {
            for(int col = startCol; col < endCol; col++) {
                if(sudokuGrid[row][col] == value) return false;
            }
        }
        return true; // None other cell in this section had the same value
    }

    /* Returns true, if the cell is in the same row, column or section as the selected cell */
    public static boolean shouldBeHighlighted(int row, int col, int sRow, int sCol){
        if( row == sRow ||                                                       // Same row
            col == sCol ||                                                      // Same col
            rowColToSectionIndex(row, col) == rowColToSectionIndex(sRow, sCol) // Same section
        ) return true;

        return false;
    }

    public static int rowColToSectionIndex(int row, int col) {
        return (row / SECTION_SIZE) * SECTION_SIZE + (col / SECTION_SIZE);
    }

    public static void copy2DIntArrays(int[][] src, int[][] dst) {
        for(int row = 0; row < SUDOKU_SIZE; row++) {
            for(int col = 0; col < SUDOKU_SIZE; col++) {
                dst[row][col] = src[row][col];
            }
        }
    }

    public static int[][] problem0 =
                {   {3, 0, 6,  5, 0, 8,  4, 0, 0},
                    {5, 2, 0,  0, 0, 0,  0, 0, 0},
                    {0, 8, 7,  0, 0, 0,  0, 3, 1},

                    {0, 0, 3,  0, 1, 0,  0, 8, 0},
                    {9, 0, 0,  8, 6, 3,  0, 0, 5},
                    {0, 5, 0,  0, 9, 0,  6, 0, 0},

                    {1, 3, 0,  0, 0, 0,  2, 5, 0},
                    {0, 0, 0,  0, 0, 0,  0, 7, 4},
                    {0, 0, 5,  2, 0, 6,  3, 0, 0} };

    public static int[][] problem1 = {
            { 0, 0, 4,   0, 0, 0,   0, 6, 7 },
            { 3, 0, 0,   4, 7, 0,   0, 0, 5 },
            { 1, 5, 0,   8, 2, 0,   0, 0, 3 },

            { 0, 0, 6,   0, 0, 0,   0, 3, 1 },
            { 8, 0, 2,   1, 0, 5,   6, 0, 4 },
            { 4, 1, 0,   0, 0, 0,   9, 0, 0 },

            { 7, 0, 0,   0, 8, 0,   0, 4, 6 },
            { 6, 0, 0,   0, 1, 2,   0, 0, 0 },
            { 9, 3, 0,   0, 0, 0,   7, 1, 0 } };

    public static int[][] problem2 = {
            { 8, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 3, 6, 0, 0, 0, 0, 0 },
            { 0, 7, 0, 0, 9, 0, 2, 0, 0 },
            { 0, 5, 0, 0, 0, 7, 0, 0, 0 },
            { 0, 0, 0, 0, 4, 5, 7, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 3, 0 },
            { 0, 0, 1, 0, 0, 0, 0, 6, 8 },
            { 0, 0, 8, 5, 0, 0, 0, 1, 0 },
            { 0, 9, 0, 0, 0, 0, 4, 0, 0 }
    };

}
