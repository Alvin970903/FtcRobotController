package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.TestMotorPower;

@TeleOp
public class TestMotorPowerExample extends OpMode {
    TestMotorPower motor = new TestMotorPower();

    boolean bumperLeftPressedLast = false;
    boolean shootingLeftToggle = false;
    boolean bumperRightPressedLast = false;
    boolean shootingRightToggle = false;

    @Override
    public void init() {
        motor.init(hardwareMap);
    }

    @Override
    public void loop() {
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
            shootingPower = 0.1;
        } else if (shootingRightToggle && !shootingLeftToggle) {
            shootingPower = -0.1;
        } else {
            shootingPower = 0;
        }
        motor.setVelocityPercent(shootingPower);
        // Update "last" states
        bumperLeftPressedLast  = bumperLeftCurrent;
        bumperRightPressedLast = bumperRightCurrent;
    }
}
