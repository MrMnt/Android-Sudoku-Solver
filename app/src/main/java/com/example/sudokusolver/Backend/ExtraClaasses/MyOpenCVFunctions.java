package com.example.sudokusolver.Backend.ExtraClaasses;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class MyOpenCVFunctions {
    private static String TAG = "OpenCV";

    /* static means, that I don't need to instantiate a class */


    /* Returns the image gray scale */
    public static Mat getGray(Mat src){
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
        return gray;
    }

    /* Removes some noise out of the image */
    public static Mat getGaussianBlur(Mat src){
        Mat gaussianBlurred = new Mat();
        Imgproc.GaussianBlur(src, gaussianBlurred, new Size(5, 5), 0);
        return gaussianBlurred;
    }

    /* Makes the image binary, by applying a global
     *  Threshold (value, after which, the pixel is made white) */
    public static Mat getSimpleThreshold(Mat src){
        Mat thresholded = new Mat();
        Imgproc.threshold(src, thresholded, 0, 255, THRESH_BINARY);
        return thresholded;
    }

    /* Makes the image binary (i.e. black and white)
     * Adaptive --> Illumination independent       */
    public static Mat getAdaptiveThreshold(Mat src){
        Mat thresholded = new Mat();
        Imgproc.adaptiveThreshold(src, thresholded, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 8);
        return thresholded;
    }

    /* Returns the contour with biggest area */
    public static MatOfPoint getBiggestContour(Mat src){
        List<MatOfPoint> contours = getContours(src);

        // There might not be any contours, so we do this to avoid a crash
        if(contours.size() == 0) {
            return null;
        }

        int biggestContourIndex = getBiggestContourIndex(contours);

        if(biggestContourIndex <= 0) return null;

        return contours.get(biggestContourIndex);
    }

    /* Returns all the contours from an binary image */
    public static List<MatOfPoint> getContours(Mat src){
        final List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(src, contours, new Mat(src.size(), src.type()), Imgproc.CV_SHAPE_RECT, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    /* Returns the contour index, with the biggest area */
    public static int getBiggestContourIndex(List<MatOfPoint> contours){
        // Made maxArea more than 0, so we ignore the small contours
        int maxIndex = -99; double maxArea = 5000;

        // Loop through all the contours
        for(int i = 0; i < contours.size(); i++) {
            // Take the contour at index (i) and compare it to the biggest one
            MatOfPoint contour = contours.get(i);

            if(Imgproc.contourArea(contour) > maxArea){
                maxIndex = i; maxArea = Imgproc.contourArea(contour);
                //Log.d(TAG, "getBiggestContourIndex: " + i + " === " + Imgproc.contourArea(contour));
            }
        }

        //Log.d(TAG, "getBiggestContourIndex: ==============================================================");

        return maxIndex;
    }

    /* Makes a contour (which might have lots of points) into a shape
     *  With less vertices                                           */
    public static MatOfPoint getApproxContour(MatOfPoint originalContour){
        // Convert the original contour points to floats,
        // Because that's what approxPolyDP takes as an input
        MatOfPoint2f originalContour2f = new MatOfPoint2f();
        originalContour.convertTo(originalContour2f, CvType.CV_32F);

        // Where the approximated points will be stored
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        // The maximum distance from contour to the approximated contour
        Double epsilon = Imgproc.arcLength(originalContour2f, true) * 0.1;
        // approxPolyDP approximates a contour shape to another shape with less
        // number of vertices depending upon the precision we specify
        Imgproc.approxPolyDP(originalContour2f, approxCurve, epsilon, true);

        // Now approxCurve holds approximated point values in floats
        // So we have to convert the back to (Signed) integers
        MatOfPoint approxContour = new MatOfPoint();
        approxCurve.convertTo(approxContour, CvType.CV_32S);

        return approxContour;
    }

    /* Returns the list of corner points, in unpredictable order */
    public static List<Point> getCornerPoints(MatOfPoint src){
        return src.toList();
    }

    /* Returns the list of corner points in an order of:
     *  Top-Left, Top-Right, Bottom-Right, Bottom-Left  */
    public static List<Point> getArrangedCornerPoints(MatOfPoint src, Size srcSize) {
        // Conversion from MatOfPoint to list of points
        List<Point> srcPoints = getCornerPoints(src);

        // We only care, if its a rectangle, otherwise its not a sudoku
        if(srcPoints.size() != 4) return null;

        Point centroid = getCentroid(src);

        return getArrangedPoints(srcPoints, centroid, srcSize);
    }

    /* returns a list with points arranged in an order of:
     *  Top-Left, Top-Right, Bottom-Right, Bottom-Left  */
    public static List<Point> getArrangedPoints(List<Point> cornerPoints, Point centroid, Size srcSize){
        /* First we check which ones are at the lower part, which ones at the upper part
         *  Once we know that, we compare which one is the left and which is the right in both parts */

        List<Point> upperCorners = new ArrayList<Point>(), lowerCorners = new ArrayList<Point>();

        // Loop through all the points, find which are at the top, which at the bottom
        for (Point p: cornerPoints) {
            // The y axis on images go down
            if(p.y <= centroid.y) {
                upperCorners.add(p);
            } else {
                lowerCorners.add(p);
            }
        }

        // Double check, if we still have a rectangle
        if(upperCorners.size() != 2 || lowerCorners.size() != 2) return null;

        upperCorners = arrangeThePointsHorizontally(upperCorners);
        lowerCorners = arrangeThePointsHorizontally(lowerCorners);


        List<Point> arrangedPoints = new ArrayList<Point>();
        // Top-Left, Top-Right, Bottom-Right, Bottom-Left
        arrangedPoints.add(upperCorners.get(0));
        arrangedPoints.add(upperCorners.get(1));
        arrangedPoints.add(lowerCorners.get(1));
        arrangedPoints.add(lowerCorners.get(0));

        if(!areCornerPointsNotOnEdge(arrangedPoints, srcSize)) return null;

        return arrangedPoints;
    }

    /* Checks if all the point are "inside" the frame
     *  i.e. they should not be extremely close to the edge */
    public static boolean areCornerPointsNotOnEdge(List<Point> arrangedPoints, Size srcSize){

        if(arrangedPoints.get(0).x <= 1 || arrangedPoints.get(0).y <= 1) return false; // Top-Left
        if(arrangedPoints.get(1).x >= srcSize.width-1 || arrangedPoints.get(1).y <= 1) return false; // Top-Right
        if(arrangedPoints.get(2).x >= srcSize.width-1 || arrangedPoints.get(2).y >= srcSize.height-1) return false; // Bottom-Right
        if(arrangedPoints.get(3).x <= 1 || arrangedPoints.get(3).y >= srcSize.height-1) return false; // Bottom-Left

        return true;
    }

    /* Returns two points, arranged in an order of:
     *  Left-Most and Right-Most                   */
    public static List<Point> arrangeThePointsHorizontally(List<Point> corners){
        Point leftMost, rightMost;

        leftMost = corners.get(0).x <= corners.get(1).x ? corners.get(0) : corners.get(1);
        rightMost = corners.get(0).x > corners.get(1).x ? corners.get(0) : corners.get(1);

        List<Point> arrangedPointsHorizontally = new ArrayList<Point>();
        arrangedPointsHorizontally.add(leftMost);
        arrangedPointsHorizontally.add(rightMost);

        return arrangedPointsHorizontally;
    }

    /* Centroid is the arithmetic mean position of all the points in the figure */
    public static Point getCentroid(MatOfPoint src) {
        Moments M = getImageMoments(src);
        return new Point((M.m10 / M.m00), (M.m01 / M.m00));
    }

    /* Image moments help you to calculate some features like center of mass of the object, area of the object etc.
     *  The function moments() gives a dictionary of all moment values calculated */
    public static Moments getImageMoments(MatOfPoint src) {
        return Imgproc.moments(src);
    }

    /* Returns the perspective transformation, used in warpPerspective */
    public static Mat getPerspectiveTransform(List<Point> cornerPoints, Size size, boolean forward) {
        MatOfPoint2f src_mat = new MatOfPoint2f(
                cornerPoints.get(0), // Top left
                cornerPoints.get(1), // Top right
                cornerPoints.get(2), // Bottom right
                cornerPoints.get(3));// Bottom left

        MatOfPoint2f dst_mat = new MatOfPoint2f(
                new Point(0, 0),
                new Point(size.width - 0, 0),
                new Point(size.width - 0, size.height - 0),
                new Point(0, size.height - 0));

        if(forward){
            return Imgproc.getPerspectiveTransform(src_mat, dst_mat);
        } else {
            return Imgproc.getPerspectiveTransform(dst_mat, src_mat);
        }
    }

    /* We assume, that the second image, has all black pixels,
     *  Except the ones, we want to add                      */
    public static Mat addTwoImages(Mat baseImage, Mat imageToAdd){
        /* We make a mask from imageToAdd
         *  Non zero mask pixels, say which pixels need to be copied */

        // Create all the masks
        Mat maskForImageToAdd = new Mat(), maskForBaseImage = new Mat();;
        maskForImageToAdd = getMaskFromImageToAdd(imageToAdd);
        // For the base image, the mask needs to be the opposite
        Core.bitwise_not(maskForImageToAdd, maskForBaseImage);

        // For storing the images, that have been masked.
        Mat baseImageMasked = new Mat(), imageToAddMasked = new Mat();
        Core.bitwise_and(baseImage, baseImage, baseImageMasked, maskForBaseImage);
        Core.bitwise_and(imageToAdd, imageToAdd, imageToAddMasked, maskForImageToAdd);

        Mat addedImages = new Mat(baseImage.width(), baseImage.height(), CvType.CV_8UC1);
        Core.bitwise_or(baseImageMasked, imageToAddMasked, addedImages);

        return addedImages;
    }

    /* We assume, that the image, has all black pixels,
     *  Except the ones, we want to add               */
    public static Mat getMaskFromImageToAdd(Mat src){
        // Mask - if the pixel has any value, makes it white, otherwise black
        Mat mask = getGray(src);
        mask = getSimpleThreshold(mask);
        return mask;
    }

}