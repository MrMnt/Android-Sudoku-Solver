package com.example.sudokusolver.Backend.ExtraClaasses;

import android.graphics.Bitmap;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static com.example.sudokusolver.Backend.ExtraClaasses.MyOpenCVFunctions.*;

public class MyImageProcessing {

    public static int[][] grid;
    public static MyTesseractOCR mOcr;

    public MyImageProcessing() {}
    public MyImageProcessing(MyTesseractOCR myTesseractOCR, int[][] sudokuGrid){
        mOcr = myTesseractOCR;
        grid = sudokuGrid;
    }

    /* Do ALL the image processing, + OCR + solving */
    public static Mat getFinalImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat src = inputFrame.rgba();

        List<Point> sudokuCornerPoints = getSudokuCornerPoints(src);

        // Double check, to avoid a crash
        if(sudokuCornerPoints == null) return src;

        Mat backwardPerspective = getFinalBackwardPerspective(src, sudokuCornerPoints);

        Mat finalImage = addTwoImages(src, backwardPerspective);

        return finalImage;
    }

    /* Warps the image inside the corner points into a "top-down view", so we
     *  Have a perfect square to deal with. Then we apply OCR, to take out the numbers,
     *  Do the solving, put the solved sudoku numbers in the warped image,
     *  And then return the warped out image, putted back into (source image sized) Mat */
    public static Mat getFinalBackwardPerspective(Mat src, List<Point> cornerPoints){

        // For storing the warped out image
        Mat forwardPerspective = new Mat();
        // For storing the warped out image, putted back into (source image sized) image
        Mat backwardPerspective = new Mat();

        Size warpedImageSize = new Size(720, 720);
        Mat forwardPerspectiveTransform = getPerspectiveTransform(cornerPoints, warpedImageSize, true);
        Mat backwardPerspectiveTransform = getPerspectiveTransform(cornerPoints, warpedImageSize, false);

        Imgproc.warpPerspective(src, forwardPerspective, forwardPerspectiveTransform, warpedImageSize);

        doOcrOnWarpedImage(forwardPerspective);

        forwardPerspective = getPaintedIsolatedImage(forwardPerspective);

        Imgproc.warpPerspective(forwardPerspective, backwardPerspective, backwardPerspectiveTransform, src.size());

        return backwardPerspective;
    }

    public static void doOcrOnWarpedImage(Mat src){
        int cellSize = src.width() / 9;
        int padding = cellSize / 5;
        int cellSize2 = cellSize - padding*2;

        Mat ocrImage = getGray(src);
        ocrImage = getGaussianBlur(ocrImage);
        ocrImage = getAdaptiveThreshold(ocrImage);
        Core.bitwise_not(ocrImage, ocrImage);

        Bitmap ocrImageBitmap = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ocrImage, ocrImageBitmap);

        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
                Bitmap temp = Bitmap.createBitmap(ocrImageBitmap, col*cellSize + padding, row*cellSize + padding , cellSize2, cellSize2);
                String tempText = mOcr.getOCRResult(temp);
                if(tempText.equals("")) {
                    grid[row][col] = 0;
                } else {
                    grid[row][col] = Integer.parseInt(tempText);
                }

            }
        }

    }

    /* Does all the "preprocessing" and returns the
     *  Found sudoku corner points in an order      */
    public static List<Point> getSudokuCornerPoints(Mat src) {
        Mat gray = getGray(src);
        Mat gaussianBlurred = getGaussianBlur(gray);
        Mat thresholded = getAdaptiveThreshold(gaussianBlurred);

        MatOfPoint biggestContour = getBiggestContour(thresholded);

        // In case there were no contours
        if (biggestContour == null) return null;

        MatOfPoint approxContour = getApproxContour(biggestContour);

        return getArrangedCornerPoints(approxContour, src.size());
    }

    /* Do whatever you want, with perspective transformed image */
    public static Mat getPaintedIsolatedImage(Mat srcImage) {
        Mat dst = srcImage.clone();
        int fullSize = dst.width();
        int gapSize = (fullSize / 9);
        int cellPadding = gapSize/4;

        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
//                Imgproc.putText(dst, ""+((row*9+col)+1), new Point((row*gapSize) + cellPadding, ((col+1)*gapSize) - cellPadding),
//                        Imgproc.FONT_HERSHEY_PLAIN, Imgproc.getFontScaleFromHeight(Imgproc.FONT_HERSHEY_PLAIN, cellPadding), new Scalar(0, 255, 0));
                if(grid[row][col] == 0) continue;

                Imgproc.putText(dst, "" + grid[row][col], new Point((col * gapSize) + cellPadding, ((row+1)*gapSize) - cellPadding),
                        Imgproc.FONT_HERSHEY_PLAIN, Imgproc.getFontScaleFromHeight(Imgproc.FONT_HERSHEY_PLAIN, cellPadding), new Scalar(0, 255, 0), 2);
            }
        }

        return dst;
    }

}
