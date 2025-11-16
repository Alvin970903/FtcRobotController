package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ArcadeDrive {
    private DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    public void init(HardwareMap hwMap){
        frontLeftMotor = hwMap.get(DcMotor.class, "left_front_motor");
        backLeftMotor = hwMap.get(DcMotor.class, "left_back_motor");
        frontRightMotor = hwMap.get(DcMotor.class, "right_front_motor");
        backRightMotor = hwMap.get(DcMotor.class, "right_back_motor");

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void drive(double throttle, double spin){ // throttle = up, down; spin = left, right
        double leftPower = throttle + spin;
        double rightPower = throttle - spin;
        double largest = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (largest > 1.0) {
            leftPower /= largest;
            rightPower /= largest;
        }

        frontLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
        backLeftMotor.setPower(leftPower);
        backRightMotor.setPower(rightPower);
    }
}
