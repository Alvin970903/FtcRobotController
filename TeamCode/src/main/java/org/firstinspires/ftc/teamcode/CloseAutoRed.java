// CloseAuto.java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.IntakeShooting4;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

@Autonomous
public class CloseAutoRed extends LinearOpMode {

    private final StrafeDriving drive = new StrafeDriving();
    private final IntakeShooting4 shooting = new IntakeShooting4();

    // ---- TeleOp-equivalent gating ----
    private static final double MIN_SPINUP_TIME_SEC = 0.30;
    private static final double LOW_RATIO  = 0.93;
    private static final double HIGH_RATIO = 1.12;
    private static final double SETTLE_SEC = 0.25;

    // ---- Driving ----
    private static final double DRIVE_TO_SHOT_SEC = 1.2;
    private static final double DRIVE_TO_SHOT_POWER = 0.35;

    private static final double BACK_UP_SEC   = 0.55;
    private static final double BACK_UP_POWER = 0.30;

    private static final double STRAFE_SEC   = 0.65;
    private static final double STRAFE_POWER = -0.40;

    // ---- Feed pattern (run-time based, not wall-clock) ----
    private static final double TOTAL_FEED_RUN_SEC = 10.0;
    private static final double FIRST_RUN_SEC = 4.0;
    private static final double RUN_SEC  = 1.5;
    private static final double PAUSE_SEC = 1.5;

    // ---- IMU heading hold tuning ----
    private static final double HEADING_KP = 2.5;      // start here; tune
    private static final double MAX_ROTATE = 0.35;     // cap correction

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);
        shooting.init(hardwareMap);

        shooting.applyShotMode(IntakeShooting4.ShotMode.CLOSE);
        shooting.setShooterMode(IntakeShooting4.ShooterMode.OFF);
        shooting.stopAll();

        telemetry.addLine("Ready: preload 3, aim robot, press START");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        // --------------------------------------------------
        // 1) Drive to shooting pose
        // --------------------------------------------------
        drive.drive(DRIVE_TO_SHOT_POWER, 0, 0);
        sleep((long)(DRIVE_TO_SHOT_SEC * 1000));
        drive.stop();

        // Lock the heading at the start of shooting
        double targetYaw = drive.getYawRadians();

        // --------------------------------------------------
        // 2) Spin up + settle ONCE (with heading hold)
        // --------------------------------------------------
        ElapsedTime spinTimer = new ElapsedTime();
        ElapsedTime settleTimer = new ElapsedTime();
        boolean inWindowLast = false;

        shooting.setShooterMode(IntakeShooting4.ShooterMode.FORWARD);
        spinTimer.reset();
        settleTimer.reset();

        while (opModeIsActive()) {
            // hold heading while spinning up (no translation)
            drive.driveHoldHeading(0, 0, targetYaw, HEADING_KP, MAX_ROTATE);

            boolean timeReady = spinTimer.seconds() >= MIN_SPINUP_TIME_SEC;

            double topTarget = shooting.getTopTargetTPS();
            double botTarget = shooting.getBottomTargetTPS();
            double topNow    = shooting.getTopActualTPS();
            double botNow    = shooting.getBottomActualTPS();

            boolean topInWindow =
                    topTarget > 0 &&
                            topNow >= topTarget * LOW_RATIO &&
                            topNow <= topTarget * HIGH_RATIO;

            boolean botInWindow =
                    botTarget > 0 &&
                            botNow >= botTarget * LOW_RATIO &&
                            botNow <= botTarget * HIGH_RATIO;

            boolean inWindowNow = timeReady && topInWindow && botInWindow;

            if (inWindowNow) {
                if (!inWindowLast) settleTimer.reset();
            } else {
                settleTimer.reset();
            }

            boolean settled = inWindowNow && settleTimer.seconds() >= SETTLE_SEC;
            inWindowLast = inWindowNow;

            shooting.stopFeed();

            telemetry.addData("Phase", "Spin + Settle");
            telemetry.addData("Yaw(rad)", "%.3f", drive.getYawRadians());
            telemetry.addData("Settled", settled);
            telemetry.update();

            if (settled) break;
            idle();
        }

        // Stop robot motion after settle loop
        drive.stop();

        // --------------------------------------------------
        // 3) Feed pattern (run-time based, not wall-clock) + heading hold
        // --------------------------------------------------
        ElapsedTime phaseTimer = new ElapsedTime();
        double feedRunAccum = 0.0;

        boolean feedOn = true;
        boolean firstRun = true;
        phaseTimer.reset();

        while (opModeIsActive() && feedRunAccum < TOTAL_FEED_RUN_SEC) {
            // hold heading while shooting (no translation)
            drive.driveHoldHeading(0, 0, targetYaw, HEADING_KP, MAX_ROTATE);

            double t = phaseTimer.seconds();

            if (firstRun) {
                shooting.feedForward();

                if (t >= FIRST_RUN_SEC) {
                    feedRunAccum += FIRST_RUN_SEC;
                    firstRun = false;
                    feedOn = false;
                    phaseTimer.reset();
                    shooting.stopFeed();
                }

            } else if (feedOn) {
                shooting.feedForward();

                if (t >= RUN_SEC) {
                    feedRunAccum += RUN_SEC;
                    feedOn = false;
                    phaseTimer.reset();
                    shooting.stopFeed();
                }

            } else {
                shooting.stopFeed();

                if (t >= PAUSE_SEC) {
                    feedOn = true;
                    phaseTimer.reset();
                }
            }

            telemetry.addData("Phase", "Feed Pattern");
            telemetry.addData("Yaw(rad)", "%.3f", drive.getYawRadians());
            telemetry.addData("FeedRun", "%.2f / %.2f", feedRunAccum, TOTAL_FEED_RUN_SEC);
            telemetry.addData("Segment", firstRun ? "RUN 4.0" : (feedOn ? "RUN 1.5" : "PAUSE 1.5"));
            telemetry.update();

            idle();
        }

        // Stop shooter + feed
        shooting.stopFeed();
        shooting.setShooterMode(IntakeShooting4.ShooterMode.OFF);
        drive.stop();

        // --------------------------------------------------
        // 4) Back up (with heading hold)
        // --------------------------------------------------
        ElapsedTime moveTimer = new ElapsedTime();
        moveTimer.reset();
        while (opModeIsActive() && moveTimer.seconds() < BACK_UP_SEC) {
            drive.driveHoldHeading(-BACK_UP_POWER, 0, targetYaw, HEADING_KP, MAX_ROTATE);
            idle();
        }
        drive.stop();

        sleep(1000);

        // --------------------------------------------------
        // 5) Strafe (with heading hold)
        // --------------------------------------------------
        moveTimer.reset();
        while (opModeIsActive() && moveTimer.seconds() < STRAFE_SEC) {
            drive.driveHoldHeading(0, -STRAFE_POWER, targetYaw, HEADING_KP, MAX_ROTATE);
            idle();
        }
        drive.stop();

        shooting.stopAll();
    }
}
