package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Shooting {
    public DcMotor shootingMotor1;
    public DcMotor shootingMotor2;

    public void init(HardwareMap hwMap){
        shootingMotor1 = hwMap.get(DcMotor.class, "shooting_motor1");
        shootingMotor2 = hwMap.get(DcMotor.class, "shooting_motor2");

        shootingMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shootingMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shootingMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shootingMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        shootingMotor2.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setPower(double power){
        shootingMotor1.setPower(power);
        shootingMotor2.setPower(power);
    }
}

