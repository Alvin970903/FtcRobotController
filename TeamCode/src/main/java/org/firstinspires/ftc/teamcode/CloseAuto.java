package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.IntakeShooting4;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

@Autonomous
public class CloseAuto extends LinearOpMode {

    private final StrafeDriving drive = new StrafeDriving();
    private final IntakeShooting4 shooting = new IntakeShooting4();

    // Same gating constants as your TeleOp
    private static final double MIN_SPINUP_TIME_SEC = 0.30;
    private static final double LOW_RATIO  = 0.93;
    private static final double HIGH_RATIO = 1.12;
    private static final double SETTLE_SEC = 0.25;

    // Auto tuning constants
    private static final double DRIVE_TO_SHOT_SEC = 1.2;
    private static final double DRIVE_TO_SHOT_POWER = 0.35;

    // NEW: run "RB behavior" for this long total (spin + settle + feed)
    private static final double SHOOT_TOTAL_SEC = 10.0;

    // Back up + strafe after shooting
    private static final double BACK_UP_SEC   = 0.8;
    private static final double BACK_UP_POWER = 0.30;

    private static final double STRAFE_SEC   = 0.4;
    private static final double STRAFE_POWER = -0.40;

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);
        shooting.init(hardwareMap);

        shooting.applyShotMode(IntakeShooting4.ShotMode.CLOSE);
        shooting.setShooterMode(IntakeShooting4.ShooterMode.OFF);
        shooting.stopFeed();
        shooting.stopIntakeOnly();

        telemetry.addLine("Ready: preload 3, aim robot, then press START");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        // 1) Drive to shooting pose
        drive.drive(DRIVE_TO_SHOT_POWER, 0, 0);
        sleep((long)(DRIVE_TO_SHOT_SEC * 1000));
        drive.stop();

        // 2) "Like TeleOp RB": shooter FORWARD, then use SAME window+settle logic to decide feed,
        // and keep doing that for 10 seconds total.
        ElapsedTime shootTimer = new ElapsedTime();
        ElapsedTime spinTimer = new ElapsedTime();
        ElapsedTime settleTimer = new ElapsedTime();
        boolean inWindowLast = false;

        shooting.setShooterMode(IntakeShooting4.ShooterMode.FORWARD);
        shootTimer.reset();
        spinTimer.reset();
        settleTimer.reset();

        while (opModeIsActive() && shootTimer.seconds() < SHOOT_TOTAL_SEC) {
            boolean timeReady = spinTimer.seconds() >= MIN_SPINUP_TIME_SEC;

            double topTarget = shooting.getTopTargetTPS();
            double botTarget = shooting.getBottomTargetTPS();
            double topNow    = shooting.getTopActualTPS();
            double botNow    = shooting.getBottomActualTPS();

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
            inWindowLast = inWindowNow;

            if (settled) {
                shooting.feedForward();   // same as TeleOp when settled
            } else {
                shooting.stopFeed();      // same as TeleOp when not settled
            }

            telemetry.addData("ShootTime", "%.2f / %.2f", shootTimer.seconds(), SHOOT_TOTAL_SEC);
            telemetry.addData("SpinTime", "%.2f", spinTimer.seconds());
            telemetry.addData("SettleTime", "%.2f", settleTimer.seconds());
            telemetry.addData("Bottom TPS", "%.0f / %.0f", botNow, botTarget);
            telemetry.addData("Top TPS", "%.0f / %.0f", topNow, topTarget);
            telemetry.addData("Settled", settled);
            telemetry.update();

            idle();
        }

        // Stop shooter + feed after 10 sec
        shooting.stopFeed();
        shooting.setShooterMode(IntakeShooting4.ShooterMode.OFF);

        // 3) Back up
        drive.drive(-BACK_UP_POWER, 0, 0);
        sleep((long)(BACK_UP_SEC * 1000));
        drive.stop();

        sleep((long)(1000));

        // 4) Strafe
        drive.drive(0, -STRAFE_POWER, 0);
        sleep((long)(STRAFE_SEC * 1000));
        drive.stop();

        shooting.stopAll();
    }
}
