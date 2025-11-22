package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Driving {
    public DcMotor leftFrontMotor, leftBackMotor;
    public DcMotor rightFrontMotor, rightBackMotor;

    public void init(HardwareMap hwMap){
        leftFrontMotor  = hwMap.get(DcMotor.class, "front_left_motor");
        leftBackMotor   = hwMap.get(DcMotor.class, "back_left_motor");
        rightFrontMotor = hwMap.get(DcMotor.class, "front_right_motor");
        rightBackMotor  = hwMap.get(DcMotor.class, "back_right_motor");

        leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // IMPORTANT: Reverse ONE side so robot goes forward correctly
        rightFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        rightBackMotor.setDirection(DcMotor.Direction.REVERSE);

        leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }



    public void drive(double forward, double turn, double strafe) {
        // Basic mecanum math
        double fl = forward + strafe + turn;
        double bl = forward - strafe + turn;
        double fr = forward - strafe - turn;
        double br = forward + strafe - turn;

        // Normalize so no wheel power exceeds 1.0
        double max = Math.max(1.0,
                Math.max(Math.abs(fl),
                        Math.max(Math.abs(bl),
                                Math.max(Math.abs(fr), Math.abs(br)))));

        fl /= max;
        bl /= max;
        fr /= max;
        br /= max;

        leftFrontMotor.setPower(fl);
        leftBackMotor.setPower(bl);
        rightFrontMotor.setPower(fr);
        rightBackMotor.setPower(br);
    }

    // Optional: quick way to stop
    public void stop() {
        drive(0, 0, 0);
    }

    public void setPower(double left, double right){
        leftFrontMotor.setPower(left);
        leftBackMotor.setPower(left);
        rightFrontMotor.setPower(right);
        rightBackMotor.setPower(right);
    }
}
