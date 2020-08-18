package com.example.sudokusolver.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.sudokusolver.Backend.MyCords;
import com.example.sudokusolver.ViewModel.MySudokuViewModel;

import static com.example.sudokusolver.Backend.MySudokuUtils.*;

public class ManualSudokuGridView extends View {

    private static String TAG = "SudokuSolver";

    // What to draw, handled by Canvas
    // How to draw, handled by Paint.
    Paint thinLinePaint, thickLinePaint, selectedCellPaint, highlightedCellPaint;


    MyCords selectedCell = new MyCords(4, 4);
    int[][] startingGrid = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    int[][] solvedGrid = new int[SUDOKU_SIZE][SUDOKU_SIZE];

    public ManualSudokuGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        highlightCells(canvas);
        drawGrid(canvas);

        super.onDraw(canvas);
    }

    /* Draws the "skeleton"/grid of the sudoku */
    private void drawGrid(Canvas canvas){
        for(int i = 0; i <= SUDOKU_SIZE; i++){
            Paint thisLinePaint = (i % SECTION_SIZE == 0) ? thickLinePaint : thinLinePaint;
            int tempFixedPoint = i*CELL_SIZE;
            canvas.drawLine(0, tempFixedPoint, getMeasuredWidth(), tempFixedPoint, thisLinePaint); // Horizontal line
            canvas.drawLine(tempFixedPoint, 0, tempFixedPoint, getMeasuredHeight(), thisLinePaint); // Vertical lines
        }
    }

    /* Highlights the cells in same row, column and section */
    private void highlightCells(Canvas canvas){
        int sRow = selectedCell.getRow(), sCol = selectedCell.getCol();

        for(int row = 0; row < SUDOKU_SIZE; row++){
            for(int col = 0; col < SUDOKU_SIZE; col++){
                if( shouldBeHighlighted(row, col, sRow, sCol)){
                    canvas.drawRect(col*CELL_SIZE, row*CELL_SIZE, (col+1)*CELL_SIZE, (row+1)*CELL_SIZE, highlightedCellPaint);
                }
            }
        }
        // Fills the selected cell
        canvas.drawRect(sCol*CELL_SIZE, sRow*CELL_SIZE, (sCol+1)*CELL_SIZE, (sRow+1)*CELL_SIZE, selectedCellPaint);
    }

    /* Responsible for passing the event to the "backend" */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouchEvent(event);
        return super.onTouchEvent(event);
    }
    private void handleTouchEvent(MotionEvent event){
        if(myCanvasInterfaceListener != null) { // if there is someone listening
            myCanvasInterfaceListener.onTouchEventOccurred(event.getX(), event.getY());
        }
    }
    myCanvasInterface myCanvasInterfaceListener = null;
    public void setListener(myCanvasInterface listener){
        myCanvasInterfaceListener = listener;
    }
    public interface myCanvasInterface {
        void onTouchEventOccurred(float x, float y);
    }
    /* Once the "backend" has set the LiveData values, it is automatically observed
    *  In the main activity, and calls this update function, to redraw the UI    */
    public void updateSelectedCellUI(MyCords selectedCell_){
        selectedCell = selectedCell_;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min = Math.min(widthMeasureSpec, heightMeasureSpec); // To make both vertical and horizontal lines the same (i.e. a square)
        min -= 48;                                              // To account for margin *Yes, hardcoded, fuck you :)))*
        initializeAllPaints();                                 // Defines paint colors, thickness style and so on
        setMeasuredDimension(min, min);                       // MUST CALL THIS
        CELL_SIZE = getMeasuredHeight() / SUDOKU_SIZE;       // Calculates the cell size in pixels
    }

    /* Initializes all the Paint objects
    *  Paint objects handle HOW to draw */
    private void initializeAllPaints(){
        // Used for non dominant lines
        thinLinePaint = new Paint();
        thinLinePaint.setColor(Color.BLACK);
        thinLinePaint.setStyle(Paint.Style.STROKE);
        thinLinePaint.setStrokeWidth(5f);
        // Used for dominant lines, the ones, that separate sections
        thickLinePaint = new Paint();
        thickLinePaint.setColor(Color.BLACK);
        thickLinePaint.setStyle(Paint.Style.STROKE);
        thickLinePaint.setStrokeWidth(12f);
        // Used for filling the selected cell
        selectedCellPaint = new Paint();
        selectedCellPaint.setColor(Color.DKGRAY);
        selectedCellPaint.setStyle(Paint.Style.FILL);
        // Used for filling the cells in the same row, column and section as selected cell
        highlightedCellPaint = new Paint();
        highlightedCellPaint.setColor(Color.GRAY);
        highlightedCellPaint.setStyle(Paint.Style.FILL);
    }

}
//        long a1 = System.nanoTime();
//        Log.d(TAG, "Time: " + (System.nanoTime() - a1) / 1 + " nano seconds\n");
