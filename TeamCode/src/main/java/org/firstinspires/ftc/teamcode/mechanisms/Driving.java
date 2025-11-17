package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Driving {
    public DcMotor leftFrontMotor, leftBackMotor;
    public DcMotor rightFrontMotor, rightBackMotor;

    public void init(HardwareMap hwMap){
        leftFrontMotor  = hwMap.get(DcMotor.class, "left_front_motor");
        leftBackMotor   = hwMap.get(DcMotor.class, "left_back_motor");
        rightFrontMotor = hwMap.get(DcMotor.class, "right_front_motor");
        rightBackMotor  = hwMap.get(DcMotor.class, "right_back_motor");

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

    public void setPower(double left, double right){
        leftFrontMotor.setPower(left);
        leftBackMotor.setPower(left);
        rightFrontMotor.setPower(right);
        rightBackMotor.setPower(right);
    }
}
