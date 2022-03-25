package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

/**
 * This is NOT an opmode.
 *
 * This class is used to define all the specific hardware for a single robot.
 * In this case that robot is a Mecanum driven Robot.
 * Mecanum robots utilize 4 motor driven wheels.
 *
 *
 *
 * Software 1 min and 45 second Elevator Speech:
 * - Github:
 *      - We utilize an open source coding platform known as github. Github adds value to our team in 3 ways:
 *          - Version Control:
 *              - Allows us to keep track of multiple files, backup our code, and share it with the world
 *          - Colaboration:
 *              - Allow us to make our code open source to the interwebs.
 *          - Backup:
 *              - We will never lose a file again
 * - Computer vision
 *      - Vuforia: 
 * - Definition File
 * - Auto
 * - Teleop
 * - Past failures
 *      - Tensorflow
 *
 */
public class Definitions
{
    public static final String VUFORIA_KEY = "AXtFr3H/////AAABmdesNJ4h10A/jsUUQYg3iZYNuybZP+xSL1rgtKZGv/eza25sSNgwWw0ZFXNVFcMED6F3OQ6RHuFGYMB58rsaDkJ5GbM7roSrP1xO0cKgkqfiBNrtN5Mi0CCSKoTKpyAuT6be8LQofpRgjpqevCkljaPPpUVVx9KWkYk7PE39YuABgbqJbh+9vHKYsfAIETxvXXxmY6rgqa84SE7BUVCB/9XeITffoYPHbr+LSM/NOps2wpc0TAIHswCBDoM5+5xLKVteViUng6d9vdWClFwFkq6VJ1vgiQxvS7i4EklqDbcJlvoqtg2RY7Kb5fc6qYml8Ab5aqJJ+Uj+ATBKlajN2jp1FhNUWMand/JgNj9sUsS6";

    public boolean stoneLeft = false;
    public boolean stoneCenter = false;
    public boolean stoneRight = false;
    public DcMotor leftBackDrive;
    public DcMotor leftFrontDrive;
    public DcMotor rightBackDrive;
    public DcMotor rightFrontDrive;
    public DcMotor leftIntakeMotor;
    public DcMotor intake;
    public CRServo leftFoundationCRServo;
    public CRServo rightFoundationCRServo;
    public DcMotor slide;
    private CRServo slideServo = null;
    private DcMotor spinner = null;


    final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;//Selects Webcam as default camera
    final boolean PHONE_IS_PORTRAIT = false;//Keeps the phone orientated Vertically. This was suggested by the SDK
    // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    // We will define some constants and conversions here
    final float mmPerInch        = 25.4f;
    final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor
    // Constant for Stone Target
    final float stoneZ = 2.00f * mmPerInch;
    // Constants for the center support targets
    final float bridgeZ = 6.42f * mmPerInch;
    final float bridgeY = 23 * mmPerInch;
    final float bridgeX = 5.18f * mmPerInch;
    final float bridgeRotY = 59;                                 // Units are degrees
    final float bridgeRotZ = 180;
    // Constants for perimeter targets
    final float halfField = 72 * mmPerInch;
    final float quadField  = 36 * mmPerInch;
    // Class Members
    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia = null;
    /**
     * This is the webcam we are to use. As with other hardware devices such as motors and
     * servos, this device is identified using the robot configuration tool in the FTC application.
     */
    WebcamName webcamName = null;
    boolean targetVisible = false;
    float phoneXRotate    = 0;
    float phoneYRotate    = 0;
    float phoneZRotate    = 0;

    final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // eg: TETRIX Motor Encoder
    final double     DRIVE_GEAR_REDUCTION    = 0.69 ;     // This is < 1.0 if geared UP
    final double     WHEEL_DIAMETER_INCHES   = 3.54331 ;     // For figuring circumference
    final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    final double     DRIVE_SPEED             = 0.6;
    final double     TURN_SPEED              = 0.5;
    boolean isAlignedWithSkystone = false;




    public void robotHardwareMapInit(HardwareMap Map)
    {
        leftBackDrive = Map.dcMotor.get("left_back_drive");
        leftFrontDrive = Map.dcMotor.get("left_front_drive");
        rightFrontDrive = Map.dcMotor.get("right_front_drive");
        rightBackDrive = Map.dcMotor.get("right_back_drive");
        intake = Map.dcMotor.get("intake");
        slide = Map.dcMotor.get("slide");
        slideServo = Map.crservo.get("slideServo");
        spinner = Map.dcMotor.get("spinner");
    }

    void driveInit()
    {
        //Stop and reset motor encoders to ensure consistent values
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Sets the motors to run through driver input instead of running to an encoder position
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Sets each motor to hold its current position while having zero power set
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftIntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //This sets the robot to drive straight by default
        leftBackDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        leftFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        leftIntakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFoundationCRServo.setDirection(CRServo.Direction.REVERSE);
        spinner.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    void autoInit(){
    }
}
    


