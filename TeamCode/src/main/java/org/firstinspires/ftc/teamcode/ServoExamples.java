package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoExamples extends OpMode {

    private Servo servoPos;

    @Override
    public void init() {
        servoPos = hardwareMap.get(Servo.class, "servo_pos");
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            servoPos.setPosition(0.2);
        } else {
            servoPos.setPosition(0.8);
        }

        telemetry.addData("A Button", gamepad1.a);
        telemetry.update();
    }
}
