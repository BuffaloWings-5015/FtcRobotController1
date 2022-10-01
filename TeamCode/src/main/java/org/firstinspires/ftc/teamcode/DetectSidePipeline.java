/*
 * Copyright (c) 2020 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

/*
 * This sample demonstrates a basic (but battle-tested and essentially
 * 100% accurate) method of detecting the skystone when lined up with
 * the sample regions over the first 3 stones.
 */

public class DetectSidePipeline extends OpenCvPipeline {
    Telemetry telemetry;
    Mat mat = new Mat();
    public enum Side {
        First,
        Second,
        Third,
        NOT_FOUND
    }
    private Side side;

    //region of interest
    static final Rect ROI = new Rect(
        new Point(60, 35),
        new Point(120, 75));
    static double PERCENT_COLOR_THRESHOLD_1 = 0.2;
    static double PERCENT_COLOR_THRESHOLD_2 = 0.4;
    static double PERCENT_COLOR_THRESHOLD_3 = 0.6;
    public DetectSidePipeline(Telemetry t) { telemetry = t; }

    @Override
    public Mat processFrame(Mat input) {
        //convert rgb to hsv
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        //range where color is yellow
        Scalar lowHSV = new Scalar(23,50,70);
        Scalar highHSV = new Scalar(32,255,255);

        //image becomes greyscale, parts within threshold turn white
        Core.inRange(mat,lowHSV, highHSV, mat);
        Mat region = mat.submat(ROI);

        double yValue = Core.sumElems(region).val[0] / ROI.area() / 255;
        region.release();

        telemetry.addData("raw value", (int) Core.sumElems(region).val[0]);
        telemetry.addData("percentage", Math.round(yValue * 100) + "%");

        boolean side1 = yValue < PERCENT_COLOR_THRESHOLD_1;
        boolean side2 = yValue < PERCENT_COLOR_THRESHOLD_2;
        boolean side3 = yValue < PERCENT_COLOR_THRESHOLD_3;

        if (side1 && side2 && side3) {
            //not found
            side = side.NOT_FOUND;
            telemetry.addData("Cone side", "not found");
        }else if (side1) {
            //park in first space
            side = side.First;
            telemetry.addData("Cone side", "1");
        }else if (side2) {
            //park in second space
            side = side.Second;
            telemetry.addData("Cone side", "2");
        }else if (side3) {
            //park in third space
            side = side.Third;
            telemetry.addData("Cone side", "3");
        } else {
            // no idea
        }
        telemetry.update();

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

        Scalar colorSide = new Scalar(255, 0, 0);
        Scalar colorSide2 = new Scalar(0,255,0);

        return mat;

    }
    public Side getSide() {
        return side;
    }
}

