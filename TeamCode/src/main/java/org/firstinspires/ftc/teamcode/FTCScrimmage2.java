package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.mechanisms.Conveyor;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.ServoCon;
import org.firstinspires.ftc.teamcode.mechanisms.Shooting;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

// Control hub 0 front right
// control hub 1 back right
// control hub 2 intake
// control hub 3 back right shooting
// control hub servo 0 continuous servo 9

// expansion 0 front left
// expansion 1 back left
// expansion 2 conveyor
// expansion 3 back left shooting

@TeleOp
public class FTCScrimmage2 extends OpMode {
    StrafeDriving drive = new StrafeDriving();
    Conveyor conveyor = new Conveyor();

    Intake intake = new Intake();
    Shooting shooting = new Shooting();
    ServoCon servo = new ServoCon();

    // intake
    boolean bumperLeftPressedLast = false;
    boolean intakeLeftToggle = false;
    boolean bumperRightPressedLast = false;
    boolean intakeRightToggle = false;

    // shooting
    boolean triggerLeftPressedLast = false;
    boolean conveyorLeftToggle = false;
    boolean triggerRightPressedLast = false;
    boolean conveyorRightToggle = false;

    // conveyor
    boolean bumperLeftPressedLast2 = false;
    boolean shootingLeftToggle = false;
    boolean bumperRightPressedLast2 = false;
    boolean shootingRightToggle = false;


    @Override
    public void init() {
        drive.init(hardwareMap);
        conveyor.init(hardwareMap);
        intake.init(hardwareMap);
        shooting.init(hardwareMap);
        servo.init(hardwareMap);
    }

    @Override
    public void loop() {

        // Driving
        double throttle = gamepad1.left_stick_y;    // forward/back
        double spin     =  -gamepad1.left_stick_x;    // turning
        double strafe   =  -gamepad1.right_stick_x;   // horizontal move

        spin *= 0.3;

        drive.drive(throttle, spin, strafe);

        // intake work
        boolean bumperLeftCurrent  = gamepad1.left_bumper;
        boolean bumperRightCurrent = gamepad1.right_bumper;

        // LEFT toggle – forward
        if (bumperLeftCurrent && !bumperLeftPressedLast) {
            intakeLeftToggle = !intakeLeftToggle;
            // If we turn forward ON, force reverse OFF
            if (intakeLeftToggle) {
                intakeRightToggle = false;
            }
        }
        // RIGHT toggle – reverse
        if (bumperRightCurrent && !bumperRightPressedLast) {
            intakeRightToggle = !intakeRightToggle;
            // If we turn reverse ON, force forward OFF
            if (intakeRightToggle) {
                intakeLeftToggle = false;
            }
        }
        // Decide final power once, based on both toggles
        double intakePower = 0;
        if (intakeLeftToggle && !intakeRightToggle) {
            intakePower = 1;
        } else if (intakeRightToggle && !intakeLeftToggle) {
            intakePower = -1;
        } else {
            intakePower = 0;
        }
        intake.setPower(intakePower);
        // Update "last" states
        bumperLeftPressedLast  = bumperLeftCurrent;
        bumperRightPressedLast = bumperRightCurrent;

        // conveyor work
        //boolean bumperLeftCurrent  = gamepad2.left_bumper;
        //boolean bumperRightCurrent = gamepad2.right_bumper;
        boolean bumperLeftCurrent2  = gamepad2.left_bumper;
        boolean bumperRightCurrent2 = gamepad2.right_bumper;
//        boolean button2A  = gamepad2.a;
//        boolean button2B = gamepad2.b;

        // LEFT toggle – forward
        if (bumperLeftCurrent2 && !bumperLeftPressedLast2) {
            conveyorLeftToggle = !conveyorLeftToggle;
            // If we turn forward ON, force reverse OFF
            if (conveyorLeftToggle) {
                conveyorRightToggle = false;
            }
        }
        // RIGHT toggle – reverse
        if (bumperRightCurrent2 && !bumperRightPressedLast2) {
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
        bumperLeftPressedLast2  = bumperLeftCurrent2;
        bumperRightPressedLast2 = bumperRightCurrent2;

        // Shooting
        boolean triggerRightCurrent = gamepad2.right_trigger > 0; // 0.2
        boolean triggerLeftCurrent = gamepad2.left_trigger > 0; // 0.2


        // LEFT toggle – forward
        if (triggerLeftCurrent && !triggerLeftPressedLast) {
            shootingLeftToggle = !shootingLeftToggle;
            // If we turn forward ON, force reverse OFF
            if (shootingLeftToggle) {
                shootingRightToggle = false;
            }
        }
        // RIGHT toggle – reverse
        if (triggerRightCurrent && !triggerRightPressedLast) {
            shootingRightToggle = !shootingRightToggle;
            // If we turn reverse ON, force forward OFF
            if (shootingRightToggle) {
                shootingLeftToggle = false;
            }
        }
        // Decide final power once, based on both toggles
        double shootingPower = 0;
        //double shootingPercent = 0;
        if (shootingLeftToggle && !shootingRightToggle) {
            shootingPower = 1;
            // shootingPercent = 0.7;
        } else if (shootingRightToggle && !shootingLeftToggle) {
            shootingPower = -1;
            // shootingPercent = -0.7;
        } else {
            shootingPower = 0;
            // shootingPercent = 0.0;
        }
        shooting.setPower(shootingPower);
        // shooting.setVelocityPercent(shootingPercent);
        
        // Update "last" states
        triggerLeftPressedLast  = triggerLeftCurrent;
        triggerRightPressedLast = triggerRightCurrent;


        // Servo Continuous
            if (gamepad2.a) {
            servo.setServoRot(-1.0);
        } else {
            servo.setServoRot(0);
        }

    }
}
