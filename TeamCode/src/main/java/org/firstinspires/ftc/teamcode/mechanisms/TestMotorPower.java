package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestMotorPower {
    private DcMotor motor;

    private static final double MAX_POWER = 0.7;

    public void init(HardwareMap hwMap){
        motor = hwMap.get(DcMotor.class, "motor");

        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setPower(double power){
        double shooterPower = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

        motor.setPower(shooterPower);
    }
}
