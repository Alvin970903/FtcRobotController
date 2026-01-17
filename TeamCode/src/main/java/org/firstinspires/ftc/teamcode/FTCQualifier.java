// FTCQualifier.java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.IntakeShooting3;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

@TeleOp
public class FTCQualifier extends OpMode {

    private final StrafeDriving drive = new StrafeDriving();
    private final IntakeShooting3 shooting = new IntakeShooting3();

    // ---------------- gamepad2 shooter toggles (tap) ----------------
    private boolean g2lbLast = false;
    private boolean g2rbLast = false;
    private boolean forwardOn = false;
    private boolean reverseOn = false;

    // ---------------- gamepad2 tuning (tap) ----------------
    private boolean aLast = false;
    private boolean bLast = false;

    // ---------------- gamepad1 intake-only toggles (tap) ----------------
    private boolean g1lbLast = false;
    private boolean g1rbLast = false;
    private boolean g1IntakeForwardOn = false;
    private boolean g1IntakeReverseOn = false;

    // ---------------- spin-up + settle gating ----------------
    private final ElapsedTime spinTimer = new ElapsedTime();
    private static final double MIN_SPINUP_TIME_SEC = 0.30;

    // New: upper+lower window + must be stable for SETTLE_SEC
    private static final double LOW_RATIO  = 0.95;
    private static final double HIGH_RATIO = 1.05;
    private static final double SETTLE_SEC = 0.25;
    private final ElapsedTime settleTimer = new ElapsedTime();
    private boolean inWindowLast = false;

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

        // ---------------- gamepad2 A/B tuning ----------------
        boolean aNow = gamepad2.a;
        boolean bNow = gamepad2.b;

        if (aNow && !aLast) shooting.increaseShooterPercent();
        if (bNow && !bLast) shooting.decreaseShooterPercent();

        aLast = aNow;
        bLast = bNow;

        // ---------------- gamepad2 shooter toggles ----------------
        boolean g2lbNow = gamepad2.left_bumper;
        boolean g2rbNow = gamepad2.right_bumper;

        // RB = forward shoot toggle
        if (g2rbNow && !g2rbLast) {
            boolean wasOff = (!forwardOn && !reverseOn);
            forwardOn = !forwardOn;
            if (forwardOn) reverseOn = false;

            if (wasOff && forwardOn) {
                spinTimer.reset();

                // Cancel gamepad1 intake toggles so preloading can't accidentally feed
                g1IntakeForwardOn = false;
                g1IntakeReverseOn = false;
                shooting.stopIntakeOnly();

                // Reset settle logic for this shooter start
                settleTimer.reset();
                inWindowLast = false;
            }
        }

        // LB = reverse unjam toggle
        if (g2lbNow && !g2lbLast) {
            reverseOn = !reverseOn;
            if (reverseOn) forwardOn = false;
        }

        g2lbLast = g2lbNow;
        g2rbLast = g2rbNow;

        // Apply shooter mode (gamepad2)
        if (forwardOn) {
            shooting.setShooterMode(IntakeShooting3.ShooterMode.FORWARD);
        } else if (reverseOn) {
            shooting.setShooterMode(IntakeShooting3.ShooterMode.REVERSE);
        } else {
            shooting.setShooterMode(IntakeShooting3.ShooterMode.OFF);
        }

        // ---------------- Shooter feeding control (servo tied to shooting) ----------------
        boolean shooterFeedingThisLoop = false;

        if (shooting.getShooterMode() == IntakeShooting3.ShooterMode.FORWARD) {
            boolean timeReady = spinTimer.seconds() >= MIN_SPINUP_TIME_SEC;

            double topTarget = shooting.getTopTargetTPS();
            double botTarget = shooting.getBottomTargetTPS();
            double topNow = shooting.getTopActualTPS();
            double botNow = shooting.getBottomActualTPS();

            boolean topInWindow = topTarget > 0 &&
                    topNow >= topTarget * LOW_RATIO &&
                    topNow <= topTarget * HIGH_RATIO;

            boolean botInWindow = botTarget > 0 &&
                    botNow >= botTarget * LOW_RATIO &&
                    botNow <= botTarget * HIGH_RATIO;

            boolean inWindowNow = timeReady && topInWindow && botInWindow;

            if (inWindowNow) {
                if (!inWindowLast) settleTimer.reset();
            } else {
                settleTimer.reset();
            }

            boolean settled = inWindowNow && (settleTimer.seconds() >= SETTLE_SEC);

            if (settled) {
                shooting.feedForward(); // intake + servo
                shooterFeedingThisLoop = true;
            } else {
                shooting.stopFeed();
            }

            inWindowLast = inWindowNow;

        } else if (shooting.getShooterMode() == IntakeShooting3.ShooterMode.REVERSE) {
            shooting.feedReverse(); // intake + servo
            shooterFeedingThisLoop = true;

        } else {
            shooting.stopFeed();
        }

        // ---------------- gamepad1 intake-only TOGGLE ----------------
        // Only active when shooter is NOT feeding (prevents fighting).
        if (!shooterFeedingThisLoop) {
            boolean g1lbNow = gamepad1.left_bumper;
            boolean g1rbNow = gamepad1.right_bumper;

            // RB = toggle intake forward (no servo)
            if (g1rbNow && !g1rbLast) {
                g1IntakeForwardOn = !g1IntakeForwardOn;
                if (g1IntakeForwardOn) g1IntakeReverseOn = false;
            }

            // LB = toggle intake reverse (no servo)
            if (g1lbNow && !g1lbLast) {
                g1IntakeReverseOn = !g1IntakeReverseOn;
                if (g1IntakeReverseOn) g1IntakeForwardOn = false;
            }

            // Apply intake-only state
            if (g1IntakeForwardOn) {
                shooting.intakeOnlyForward();
            } else if (g1IntakeReverseOn) {
                shooting.intakeOnlyReverse();
            } else {
                shooting.stopIntakeOnly();
            }

            g1lbLast = g1lbNow;
            g1rbLast = g1rbNow;
        }

        // ---------------- Telemetry ----------------
        telemetry.addData("Mode", shooting.getShooterMode());
        telemetry.addData("IntakeOnly", g1IntakeForwardOn ? "FORWARD" : (g1IntakeReverseOn ? "REVERSE" : "OFF"));

        telemetry.addData("Bottom %", "%.0f%%", shooting.getBottomPercent() * 100.0);
        telemetry.addData("Top % (ratio)", "%.0f%%", shooting.getTopPercent() * 100.0);

        telemetry.addData("Bottom TPS", "%.0f / %.0f", shooting.getBottomActualTPS(), shooting.getBottomTargetTPS());
        telemetry.addData("Top TPS", "%.0f / %.0f", shooting.getTopActualTPS(), shooting.getTopTargetTPS());

        telemetry.addData("SpinTime (s)", "%.2f", spinTimer.seconds());
        telemetry.addData("Settled (s)", "%.2f", settleTimer.seconds());
        telemetry.update();
    }
}
