package com.example.sudokusolver.Backend;

public class MySudokuUtils {

    // The number of cells in whole sudoku, and sudoku section
    public static final int SUDOKU_SIZE = 9;
    public static final int SECTION_SIZE = 3;

    // The size of the cell in pixels
    public static int CELL_SIZE;

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

}
