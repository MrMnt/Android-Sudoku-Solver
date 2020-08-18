package com.example.sudokusolver.Backend;
import static com.example.sudokusolver.Backend.MySudokuUtils.*;

public class SudokuSolver {

    int[][] gridToSolve;

    public SudokuSolver(int[][] grid){
        gridToSolve = grid; // not copying it, so we can initialize this class immediately
    }

    /* Solves the sudoku, first applies some rules, then recursively backtracking			 */
    public boolean solve() {

        boolean atleastOneChanged = false;

        while(atleastOneChanged) {
            atleastOneChanged = singleCandidate();
            if(!atleastOneChanged) atleastOneChanged = onlyOnePossibleInSameRowColSection();
        }

        boolean solved = backtrackingSolve();

        return solved;
    }

    /* Solves the sudoku by backtracking
     * Pseudocode:
     *
     * Find row, col of an unassigned cell
     * If there is none, return true
     *
     * For digits from 1 to 9
     * 	if there is no conflict for digit at row,col
     * 		assign digit to row,col and recursively try fill in rest of grid
     * 		if recursion successful, return true
     * 		if !successful, remove digit and try another
     * if all digits have been tried and nothing worked, return false to trigger backtracking
     */
    private boolean backtrackingSolve() {

        int[] unassignedRowCol = new int[2];
        if(!findEmptyCell(unassignedRowCol)) return true; // Could not find an empty cell, means we solved the sudoku
        int row = unassignedRowCol[0]; int col = unassignedRowCol[1];

        for(int value = 1; value <= SUDOKU_SIZE; value++) {

            if(isOk(gridToSolve, value, row, col)) {
                gridToSolve[row][col] = value;

                if(backtrackingSolve()) {
                    return true;
                } else {
                    gridToSolve[row][col] = UNASSIGNED;
                }

            }

        }

        return false;
    }

    /* One of the methods for solving sudoku
     *
     * If the cell can only be one value, determined
     * by row, column and section constraints,
     * we make it that value 					 */
    private boolean singleCandidate() {

        boolean changed = false;

        for(int row = 0; row < SUDOKU_SIZE; row++) {
            for(int col = 0; col < SUDOKU_SIZE; col++) {

                if(gridToSolve[row][col] != 0) continue;

                int possibleValue = 0, numOfPossibleValues = 0;

                for(int value = 1; value <= SUDOKU_SIZE; value++) {
                    if(isOk(gridToSolve, value, row, col)) {
                        possibleValue = value; numOfPossibleValues++;
                    }
                }

                if(numOfPossibleValues == 1) {
                    gridToSolve[row][col] = possibleValue;
                    changed = true;
                }

            }
        }

        return changed;
    }

    /* One of the methods for solving sudoku
     *
     * If the number in this cell is possible and
     * In every other cell in the same row/column/section it is not
     * We make the cells value this number 						*/
    private boolean onlyOnePossibleInSameRowColSection() {

        boolean changed = false;

        for(int row = 0; row < SUDOKU_SIZE; row++) {
            for(int col = 0; col < SUDOKU_SIZE; col++) {

                if(gridToSolve[row][col] != 0) continue;

                for(int value = 1; value <= SUDOKU_SIZE; value++) {

                    if(!isOk(gridToSolve, value, row, col)) continue;

                    if(col == 1 && row == 1 && value == 6) {
                        int a = 0;
                    }

                    if(!isPossibleInSameRow(value, row, col) ||
                            !isPossibleInSameCol(value, row, col) ||
                            !isPossibleInSameSection(value, row, col)) {
                        gridToSolve[row][col] = value;
                        changed = true;
                    }

                }

            }
        }


        return changed;
    }


    /* Returns true, if we COULD put this value, in the same row, otherwise false */
    private boolean isPossibleInSameRow(int value, int row, int col_) {

        for(int col = 0; col < SUDOKU_SIZE; col++) {

            // Dont't compare the same cell
            if(col == col_ || gridToSolve[row][col] != 0) continue;

            if(isOk(gridToSolve, value, row, col)) return true;

        }

        return false;
    }

    /* Returns true, if we COULD put this value, in the same column, otherwise false */
    private boolean isPossibleInSameCol(int value, int row_, int col) {

        for(int row = 0; row < SUDOKU_SIZE; row++) {

            if(row == row_ || gridToSolve[row][col] != 0) continue;

            if(isOk(gridToSolve, value, row, col)) return true;

        }

        return false;
    }
    /* Returns true, if we found an empty cell 			*/
    private boolean findEmptyCell(int[] rowCol) {

        for(int row = 0; row < SUDOKU_SIZE; row++) {
            for(int col = 0; col < SUDOKU_SIZE; col++) {

                if(gridToSolve[row][col] == UNASSIGNED) {
                    rowCol[0] = row; rowCol[1] = col;
                    return true;
                }

            }
        }
        // We did not find an empty cell
        return false;
    }

    /* Returns true, if we COULD put this value, in the same column, otherwise false */
    private boolean isPossibleInSameSection(int value, int row_, int col_) {
        int startRow = (row_ / SECTION_SIZE) * SECTION_SIZE;
        int startCol = (col_ / SECTION_SIZE) * SECTION_SIZE;

        int endRow = startRow + SECTION_SIZE;
        int endCol = startCol + SECTION_SIZE;

        for(int row = startRow; row < endRow; row++) {
            for(int col = startCol; col < endCol; col++) {

                if((row == row_ && col == col_) || gridToSolve[row][col] != 0) continue;

                if(isOk(gridToSolve, value, row, col)) return true;
            }
        }

        return false;
    }

}
