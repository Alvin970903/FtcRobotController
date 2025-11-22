package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.Driving;

@Disabled
@TeleOp
public class DrivingTest extends OpMode {

    Driving drive = new Driving();

    @Override
    public void init() {
        drive.init(hardwareMap);
    }

    @Override
    public void loop() {
        double drivePower = -gamepad1.left_stick_y;   // forward/back
        double turnPower  =   gamepad1.right_stick_x;  // turning

        double leftPower  = drivePower - turnPower;
        double rightPower = drivePower + turnPower;

        // Optional: speed limit
//        double speed = 0.8;
//        leftPower  *= speed;
//        rightPower *= speed;

        // Clamp
        leftPower  = Math.max(-1, Math.min(1, leftPower));
        rightPower = Math.max(-1, Math.min(1, rightPower));

        //drive.setPower(leftPower, rightPower);

        telemetry.addData("Drive", drivePower);
        telemetry.addData("Turn", turnPower);
        telemetry.addData("Left", leftPower);
        telemetry.addData("Right", rightPower);
        telemetry.update();
    }
}
