package com.example.sudokusolver.Backend.ExtraClaasses;

public class MyCords {

    private int row, col;

    public MyCords(int row_, int col_){
        row = row_; col = col_;
    }

    public void setRow(int row_) {
        row = row_;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col_) {
        col = col_;
    }

    public int getRow() {
        return row;
    }

    public void setCords(int row_, int col_){
        row = row_; col = col_;
    }

}
