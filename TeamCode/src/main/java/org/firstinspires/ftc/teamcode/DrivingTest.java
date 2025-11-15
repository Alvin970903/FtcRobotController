package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Driving;

@TeleOp
public class DrivingTest extends OpMode {

    Driving drive = new Driving();

    @Override
    public void init() {
        drive.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {

        // Arcade drive using only left stick
        double drivePower = gamepad1.left_stick_y;  // forward/back
        double turnPower  = gamepad1.left_stick_x;   // left/right

        // Combine for arcade drive
        double rightPower  = drivePower + turnPower;
        double leftPower = drivePower - turnPower;

        double speed = 0.2;
        leftPower *= speed;
        rightPower *= speed;

        // Clamp to [-1, 1]
        leftPower  = Math.max(-1, Math.min(1, leftPower));
        rightPower = Math.max(-1, Math.min(1, rightPower));

        drive.drive(leftPower, rightPower);

        telemetry.addData("Drive", drivePower);
        telemetry.addData("Turn", turnPower);
        telemetry.addData("Left Power", leftPower);
        telemetry.addData("Right Power", rightPower);

    }
}
