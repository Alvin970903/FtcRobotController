package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.mechanisms.ServoCon;


@TeleOp
public class ServoExamples extends OpMode {

    private ServoCon servoCon;

    @Override
    public void init() {
        servoCon = new ServoCon();
        servoCon.init(hardwareMap);
    }

    @Override
    public void loop() {
        servoCon.setServoRot(1.0);
    }
}
