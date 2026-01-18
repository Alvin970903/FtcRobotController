// FTCQualifier2.java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.IntakeShooting4;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

@TeleOp
public class FTCQualifier2 extends OpMode {

    private final StrafeDriving drive = new StrafeDriving();
    private final IntakeShooting4 shooting = new IntakeShooting4();

    // gamepad2 shooter toggles (tap)
    private boolean g2lbLast = false;
    private boolean g2rbLast = false;
    private boolean forwardOn = false;
    private boolean reverseOn = false;

    // gamepad2 shot mode toggle (tap)
    private boolean backLast = false;

    // gamepad1 intake-only toggles (tap)
    private boolean g1lbLast = false;
    private boolean g1rbLast = false;
    private boolean g1IntakeForwardOn = false;
    private boolean g1IntakeReverseOn = false;

    // spin-up + settle gating (simple, no separate ratios)
    private final ElapsedTime spinTimer = new ElapsedTime();
    private static final double MIN_SPINUP_TIME_SEC = 0.30;

    private static final double LOW_RATIO  = 0.95;
    private static final double HIGH_RATIO = 1.10;
    private static final double SETTLE_SEC = 0.15;

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

        // ---------------- gamepad2 shot mode toggle ----------------
        boolean backNow = gamepad2.back;
        if (backNow && !backLast) {
            shooting.toggleShotMode();
        }
        backLast = backNow;

        // ---------------- gamepad2 shooter toggles ----------------
        boolean g2lbNow = gamepad2.left_bumper;   // LB = reverse unjam toggle
        boolean g2rbNow = gamepad2.right_bumper;  // RB = forward shoot toggle

        if (g2rbNow && !g2rbLast) {
            boolean wasOff = (!forwardOn && !reverseOn);
            forwardOn = !forwardOn;
            if (forwardOn) reverseOn = false;

            if (wasOff && forwardOn) {
                spinTimer.reset();
                settleTimer.reset();
                inWindowLast = false;

                // Cancel gamepad1 intake toggles so preloading can't accidentally feed
                g1IntakeForwardOn = false;
                g1IntakeReverseOn = false;
                shooting.stopIntakeOnly();
            }
        }

        if (g2lbNow && !g2lbLast) {
            reverseOn = !reverseOn;
            if (reverseOn) forwardOn = false;
        }

        g2lbLast = g2lbNow;
        g2rbLast = g2rbNow;

        // Apply shooter mode
        if (forwardOn) shooting.setShooterMode(IntakeShooting4.ShooterMode.FORWARD);
        else if (reverseOn) shooting.setShooterMode(IntakeShooting4.ShooterMode.REVERSE);
        else shooting.setShooterMode(IntakeShooting4.ShooterMode.OFF);

        // ---------------- Shooter feeding control ----------------
        boolean shooterFeedingThisLoop = false;

        if (shooting.getShooterMode() == IntakeShooting4.ShooterMode.FORWARD) {
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
                shooting.feedForward();
                shooterFeedingThisLoop = true;
            } else {
                shooting.stopFeed();
            }

            inWindowLast = inWindowNow;

        } else if (shooting.getShooterMode() == IntakeShooting4.ShooterMode.REVERSE) {
            shooting.feedReverse(); // immediate unjam feed
            shooterFeedingThisLoop = true;
        } else {
            shooting.stopFeed();
        }

        // ---------------- gamepad1 intake-only TOGGLE ----------------
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

            if (g1IntakeForwardOn) shooting.intakeOnlyForward();
            else if (g1IntakeReverseOn) shooting.intakeOnlyReverse();
            else shooting.stopIntakeOnly();

            g1lbLast = g1lbNow;
            g1rbLast = g1rbNow;
        }

        // ---------------- Telemetry ----------------
        telemetry.addData("ShotMode", shooting.getShotMode());
        telemetry.addData("ShooterMode", shooting.getShooterMode());
        telemetry.addData("IntakeOnly", g1IntakeForwardOn ? "FORWARD" : (g1IntakeReverseOn ? "REVERSE" : "OFF"));

        telemetry.addData("Bottom %", "%.0f%%", shooting.getBottomPercent() * 100.0);
        telemetry.addData("Top %", "%.0f%%", shooting.getTopPercent() * 100.0);

        telemetry.addData("Bottom TPS", "%.0f / %.0f", shooting.getBottomActualTPS(), shooting.getBottomTargetTPS());
        telemetry.addData("Top TPS", "%.0f / %.0f", shooting.getTopActualTPS(), shooting.getTopTargetTPS());

        telemetry.addData("PIDF Bottom (P/F)", "%.1f / %.1f", shooting.getBotP(), shooting.getBotF());
        telemetry.addData("PIDF Top (P/F)", "%.1f / %.1f", shooting.getTopP(), shooting.getTopF());

        telemetry.addData("SpinTime", "%.2f", spinTimer.seconds());
        telemetry.addData("SettleTime", "%.2f", settleTimer.seconds());
        telemetry.update();
    }
}
