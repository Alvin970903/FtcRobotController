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

    // Shooter gating (same as TeleOp)
    private static final double MIN_SPINUP_TIME_SEC = 0.30;
    private static final double LOW_RATIO  = 0.93;
    private static final double HIGH_RATIO = 1.12;
    private static final double SETTLE_SEC = 0.25;

    // Drive
    private static final double DRIVE_TO_SHOT_SEC = 1.2;
    private static final double DRIVE_TO_SHOT_POWER = 0.35;

    private static final double BACK_UP_SEC   = 0.8;
    private static final double BACK_UP_POWER = 0.30;

    private static final double STRAFE_SEC   = 0.3;
    private static final double STRAFE_POWER = 0.40;

    // Shooting timing
    private static final double FIRST_FEED_SEC = 4.0;
    private static final int BURST_COUNT = 4;
    private static final double BURST_SEC = 1.5;
    private static final double BURST_PAUSE_SEC = 0.15;

    // IMU hold
    private static final double HEADING_KP = 2.5;
    private static final double MAX_ROTATE = 0.35;

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);
        shooting.init(hardwareMap);

        shooting.applyShotMode(IntakeShooting4.ShotMode.CLOSE);
        shooting.stopAll();

        telemetry.addLine("Ready: preload 3, aim robot, press START");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        // Lock heading immediately
        sleep(100);
        //double targetYaw = drive.getYawRadians();

        // 1. Drive to shooting pose
        drive.drive(DRIVE_TO_SHOT_POWER, 0, 0);
        sleep((long)(DRIVE_TO_SHOT_SEC * 1000));
        drive.stop();

        sleep(150);

        // 2. Shooter ON
        shooting.setShooterMode(IntakeShooting4.ShooterMode.FORWARD);

        ElapsedTime spinTimer = new ElapsedTime();
        ElapsedTime settleTimer = new ElapsedTime();
        ElapsedTime timer = new ElapsedTime();

        boolean inWindowLast = false;

        spinTimer.reset();
        settleTimer.reset();

        // 3. First 4 seconds — normal gated shooting
        timer.reset();
        while (opModeIsActive() && timer.seconds() < FIRST_FEED_SEC) {
            //drive.driveHoldHeading(0, 0, targetYaw, HEADING_KP, MAX_ROTATE);

            boolean timeReady = spinTimer.seconds() >= MIN_SPINUP_TIME_SEC;

            double topNow = shooting.getTopActualTPS();
            double botNow = shooting.getBottomActualTPS();
            double topTarget = shooting.getTopTargetTPS();
            double botTarget = shooting.getBottomTargetTPS();

            boolean inWindowNow =
                    topNow >= topTarget * LOW_RATIO && topNow <= topTarget * HIGH_RATIO &&
                            botNow >= botTarget * LOW_RATIO && botNow <= botTarget * HIGH_RATIO &&
                            timeReady;

            if (inWindowNow) {
                if (!inWindowLast) settleTimer.reset();
            } else {
                settleTimer.reset();
            }

            boolean settled = inWindowNow && settleTimer.seconds() >= SETTLE_SEC;
            inWindowLast = inWindowNow;

            if (settled) shooting.feedForward();
            else shooting.stopFeed();

            idle();
        }
        shooting.stopFeed();

        // 4. Restart bursts
        for (int i = 0; i < BURST_COUNT && opModeIsActive(); i++) {

            // HARD STOP
            shooting.stopFeed();
            shooting.setShooterMode(IntakeShooting4.ShooterMode.OFF);
            sleep((long)(BURST_PAUSE_SEC * 1000));

            // RESTART shooter
            shooting.setShooterMode(IntakeShooting4.ShooterMode.FORWARD);
            spinTimer.reset();
            settleTimer.reset();
            inWindowLast = false;

            timer.reset();
            while (opModeIsActive() && timer.seconds() < BURST_SEC) {
                //drive.driveHoldHeading(0, 0, targetYaw, HEADING_KP, MAX_ROTATE);

                boolean timeReady = spinTimer.seconds() >= MIN_SPINUP_TIME_SEC;

                double topNow = shooting.getTopActualTPS();
                double botNow = shooting.getBottomActualTPS();
                double topTarget = shooting.getTopTargetTPS();
                double botTarget = shooting.getBottomTargetTPS();

                boolean inWindowNow =
                        topNow >= topTarget * LOW_RATIO && topNow <= topTarget * HIGH_RATIO &&
                                botNow >= botTarget * LOW_RATIO && botNow <= botTarget * HIGH_RATIO &&
                                timeReady;

                if (inWindowNow) {
                    if (!inWindowLast) settleTimer.reset();
                } else {
                    settleTimer.reset();
                }

                boolean settled = inWindowNow && settleTimer.seconds() >= SETTLE_SEC;
                inWindowLast = inWindowNow;

                if (settled) shooting.feedForward();
                else shooting.stopFeed();

                idle();
            }

            shooting.stopFeed();
        }

        shooting.setShooterMode(IntakeShooting4.ShooterMode.OFF);
        drive.stop();

        sleep(100);

        // 5. Back up
        drive.drive(-BACK_UP_POWER, 0, 0);
        sleep((long)(BACK_UP_SEC * 1000));
        drive.stop();

        sleep((long)(1000));

        sleep(300);

        // 6. Strafe
        drive.drive(0, -STRAFE_POWER, 0);
        sleep((long)(STRAFE_SEC * 1000));
        drive.stop();

        shooting.stopAll();
    }
}
