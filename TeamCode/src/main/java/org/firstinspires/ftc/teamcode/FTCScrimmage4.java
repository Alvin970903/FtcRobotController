// FTCScrimmage4.java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.IntakeShooting2;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

@TeleOp
public class FTCScrimmage4 extends OpMode {

    private final StrafeDriving drive = new StrafeDriving();
    private final IntakeShooting2 shooting = new IntakeShooting2();

    // Shooter toggle buttons (gamepad2 bumpers)
    private boolean lbLast = false;
    private boolean rbLast = false;
    private boolean forwardOn = false;
    private boolean reverseOn = false;

    // Tuning buttons (gamepad2 A/B)
    private boolean aLast = false;
    private boolean bLast = false;

    // Spin-up gating for intake
    private final ElapsedTime spinTimer = new ElapsedTime();
    private static final double MIN_SPINUP_TIME_SEC = 0.30;
    private static final double SPEED_READY_RATIO   = 0.90;

    @Override
    public void init() {
        drive.init(hardwareMap);
        shooting.init(hardwareMap);
    }

    @Override
    public void loop() {
        // ---------------- Driving ----------------
        double throttle = gamepad1.left_stick_y;
        double spin     = -gamepad1.right_stick_x;
        double strafe   = -gamepad1.left_stick_x;

        spin *= 0.3;
        drive.drive(throttle, spin, strafe);

        // ---------------- Shooter percent tuning ----------------
        boolean aNow = gamepad2.a;
        boolean bNow = gamepad2.b;

        if (aNow && !aLast) shooting.increaseShooterPercent();
        if (bNow && !bLast) shooting.decreaseShooterPercent();

        aLast = aNow;
        bLast = bNow;

        // ---------------- Shooter toggles ----------------
        boolean lbNow = gamepad2.left_bumper;
        boolean rbNow = gamepad2.right_bumper;

        // ✅ RIGHT bumper = FORWARD shooting
        if (rbNow && !rbLast) {
            boolean wasOff = (!forwardOn && !reverseOn);
            forwardOn = !forwardOn;
            if (forwardOn) reverseOn = false;

            if (wasOff && forwardOn) spinTimer.reset();
        }

        // ✅ LEFT bumper = REVERSE (unjam)
        if (lbNow && !lbLast) {
            reverseOn = !reverseOn;
            if (reverseOn) forwardOn = false;
        }

        lbLast = lbNow;
        rbLast = rbNow;

        // ---------------- Apply shooter + intake behavior ----------------
        if (forwardOn) {
            shooting.setShooterMode(IntakeShooting2.ShooterMode.FORWARD);
        } else if (reverseOn) {
            shooting.setShooterMode(IntakeShooting2.ShooterMode.REVERSE);
            shooting.intakeReverse(); // immediate unjam
        } else {
            shooting.setShooterMode(IntakeShooting2.ShooterMode.OFF);
            shooting.stopIntake();
        }

        // ---------------- Forward-only intake gating ----------------
        if (shooting.getShooterMode() == IntakeShooting2.ShooterMode.FORWARD) {
            boolean timeReady = spinTimer.seconds() >= MIN_SPINUP_TIME_SEC;

            double target  = shooting.getTargetSpeedAbsTPS();
            double current = shooting.getShooterSpeedAbsTPS();
            boolean speedReady = (target > 0) && (current >= target * SPEED_READY_RATIO);

            if (timeReady && speedReady) shooting.intakeForward();
            else shooting.stopIntake();
        }

        // ---------------- Failsafe ----------------
        if (gamepad1.start && gamepad1.back) {
            drive.stop();
            shooting.stopAll();
        }

        // ---------------- Telemetry ----------------
        telemetry.addData("ShooterMode", shooting.getShooterMode());
        telemetry.addData("Shooter % (forward)", "%.0f%%", shooting.getShooterPercent() * 100.0);
        telemetry.addData("Forward Target TPS", "%.0f", shooting.getTargetSpeedAbsTPS());
        telemetry.addData("Current TPS", "%.0f", shooting.getShooterSpeedAbsTPS());
        telemetry.addData("SpinTime (s)", "%.2f", spinTimer.seconds());
        telemetry.update();
    }
}
