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

import static com.example.sudokusolver.Backend.MySudokuUtils.*;

public class ManualSudokuGridView extends View {

    private static String TAG = "SudokuSolver";

    // What to draw, handled by Canvas
    // How to draw, handled by Paint.
    Paint thinLinePaint, thickLinePaint;

    public ManualSudokuGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onDraw(Canvas canvas) {

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return super.onTouchEvent(event);
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
    }

}
//        long a1 = System.nanoTime();
//        Log.d(TAG, "Time: " + (System.nanoTime() - a1) / 1 + " nano seconds\n");
