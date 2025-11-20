package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Conveyor;
import org.firstinspires.ftc.teamcode.mechanisms.Driving;
import org.firstinspires.ftc.teamcode.mechanisms.Shooting;

@TeleOp
public class FTCScrimmage extends OpMode {

    Driving drive = new Driving();
    Conveyor conveyor = new Conveyor();

    Shooting shooting = new Shooting();

    boolean bumperLeftPressedLast = false;
    boolean conveyorLeftToggle = false;
    boolean bumperRightPressedLast = false;
    boolean conveyorRightToggle = false;


    @Override
    public void init() {
        drive.init(hardwareMap);
        conveyor.init(hardwareMap);
        shooting.init(hardwareMap);
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
//        boolean bumperLeftCurrent = gamepad1.left_bumper;
//        boolean bumperRightCurrent = gamepad1.right_bumper;
//
//        // Toggle when button goes from NOT pressed → pressed
//        if (bumperLeftCurrent && !bumperLeftPressedLast) {
//            conveyorLeftToggle = !conveyorLeftToggle;
//        }
//
//        if (bumperRightCurrent && !bumperRightPressedLast) {
//            conveyorRightToggle = !conveyorRightToggle;
//        }
//
//        // Run motor based on toggle
//        if(gamepad1.right_trigger > 0){
//            conveyer.setPower(0);
//            conveyorLeftToggle = false;
//            conveyorRightToggle = false;
//        }
//        if (conveyorLeftToggle) {
//            conveyer.setPower(1);
//            conveyorRightToggle = false;
//            bumperRightPressedLast = false;
//        }
//        if(conveyorRightToggle){
//            conveyer.setPower(-1);
//            conveyorLeftToggle = false;
//            bumperLeftPressedLast = false;
//        }
//
//        bumperLeftPressedLast = bumperLeftCurrent;   // update previous state
//
//        bumperRightPressedLast = bumperRightCurrent;   // update previous sta
        // Read buttons
        boolean bumperLeftCurrent  = gamepad1.left_bumper;
        boolean bumperRightCurrent = gamepad1.right_bumper;

        // LEFT toggle – forward
        if (bumperLeftCurrent && !bumperLeftPressedLast) {
            conveyorLeftToggle = !conveyorLeftToggle;

            // If we turn forward ON, force reverse OFF
            if (conveyorLeftToggle) {
                conveyorRightToggle = false;
            }
        }

        // RIGHT toggle – reverse
        if (bumperRightCurrent && !bumperRightPressedLast) {
            conveyorRightToggle = !conveyorRightToggle;

            // If we turn reverse ON, force forward OFF
            if (conveyorRightToggle) {
                conveyorLeftToggle = false;
            }
        }

        // Decide final power once, based on both toggles
        double conveyorPower = 0;
        if (conveyorLeftToggle && !conveyorRightToggle) {
            conveyorPower = 1;
        } else if (conveyorRightToggle && !conveyorLeftToggle) {
            conveyorPower = -1;
        } else {
            conveyorPower = 0;
        }
        conveyor.setPower(conveyorPower);

    // Update "last" states
        bumperLeftPressedLast  = bumperLeftCurrent;
        bumperRightPressedLast = bumperRightCurrent;

    // Shooting
        if(gamepad2.right_bumper){
            shooting.setPower(1);
        }
        else if(gamepad2.left_bumper){
            shooting.setPower(-0.1);
        }
        else{
            shooting.setPower(0);
        }

    // Telemetry (now truly shows previous vs current if you want)
        telemetry.addData("Left Bumper", bumperLeftCurrent);
        telemetry.addData("Right Bumper", bumperRightCurrent);

        telemetry.addData("Drive", drivePower);
        telemetry.addData("Turn", turnPower);
        telemetry.addData("Left", leftPower);
        telemetry.addData("Right", rightPower);
        telemetry.update();
    }
}
