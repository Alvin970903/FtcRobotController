package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestMotorPower;

import javax.crypto.spec.OAEPParameterSpec;

@TeleOp
public class TestMotorPowerExample extends OpMode {
    TestMotorPower motor = new TestMotorPower();

    @Override
    public void init() {
        motor.init(hardwareMap);
    }

    @Override
    public void loop() {
        if(gamepad1.a)
            motor.setPower(1);
        else
            motor.setPower(0);
    }
}
