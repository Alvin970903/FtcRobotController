package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Conveyer;
import org.firstinspires.ftc.teamcode.mechanisms.Driving;

@TeleOp
public class FTCScrimmage extends OpMode {

    Driving drive = new Driving();
    Conveyer conveyer = new Conveyer();

    boolean bumperPressedLast = false;
    boolean conveyorToggle = false;


    @Override
    public void init() {
        drive.init(hardwareMap);
        conveyer.init(hardwareMap);
    }

    @Override
    public void loop() {

        //Driving
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

        drive.setPower(leftPower, rightPower);

        //Conveyor
        boolean bumperCurrent = gamepad1.left_bumper;

        // Toggle when button goes from NOT pressed → pressed
        if (bumperCurrent && !bumperPressedLast) {
            conveyorToggle = !conveyorToggle;
        }

        // Run motor based on toggle
        if (conveyorToggle) {
            conveyer.setPower(1);
        } else {
            conveyer.setPower(0);
        }

        bumperPressedLast = bumperCurrent;   // update previous state
        telemetry.addData("")

        telemetry.addData("Drive", drivePower);
        telemetry.addData("Turn", turnPower);
        telemetry.addData("Left", leftPower);
        telemetry.addData("Right", rightPower);
        telemetry.update();
    }
}
