package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.mechanisms.Conveyor;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeShooting;
import org.firstinspires.ftc.teamcode.mechanisms.ServoCon;
import org.firstinspires.ftc.teamcode.mechanisms.ServoPos;
import org.firstinspires.ftc.teamcode.mechanisms.Shooting;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

// Control hub 0 front right
// control hub 1 back right
// control hub 2 intake
// control hub 3 back down shooting
// control hub servo 0 continuous servo 9

// expansion 0 front left
// expansion 1 back left
// expansion 2 conveyor (no need)
// expansion 3 back up shooting

@TeleOp
public class FTCScrimmage3 extends OpMode {
    StrafeDriving drive = new StrafeDriving();
    IntakeShooting shooting = new IntakeShooting();


    // shooting
    boolean bumperLeftPressedLast = false;
    boolean shootingLeftToggle = false;
    boolean bumperRightPressedLast = false;
    boolean shootingRightToggle = false;


    @Override
    public void init() {
        drive.init(hardwareMap);
        shooting.init(hardwareMap);
    }

    @Override
    public void loop() {

        // Driving
        double throttle = gamepad1.left_stick_y;    // forward/back
        double spin     =  -gamepad1.right_stick_x;    // turning
        double strafe   =  -gamepad1.left_stick_x;   // horizontal move

        spin *= 0.3;

        drive.drive(throttle, spin, strafe);

        // shooting + intake + conveyor
        boolean bumperLeftCurrent  = gamepad2.left_bumper;
        boolean bumperRightCurrent = gamepad2.right_bumper;

        // LEFT toggle – forward
        if (bumperLeftCurrent && !bumperLeftPressedLast) {
            shootingLeftToggle = !shootingLeftToggle;
            // If we turn forward ON, force reverse OFF
            if (shootingLeftToggle) {
                shootingRightToggle = false;
            }
        }
        // RIGHT toggle – reverse
        if (bumperRightCurrent && !bumperRightPressedLast) {
            shootingRightToggle = !shootingRightToggle;
            // If we turn reverse ON, force forward OFF
            if (shootingRightToggle) {
                shootingLeftToggle = false;
            }
        }
        // Decide final power once, based on both toggles
        double shootingPower = 0;
        if (shootingLeftToggle && !shootingRightToggle) {
            shootingPower = 1;
        } else if (shootingRightToggle && !shootingLeftToggle) {
            shootingPower = -1;
        } else {
            shootingPower = 0;
        }
        shooting.intakeShoot(shootingPower);
        // Update "last" states
        bumperLeftPressedLast  = bumperLeftCurrent;
        bumperRightPressedLast = bumperRightCurrent;

    }
}
