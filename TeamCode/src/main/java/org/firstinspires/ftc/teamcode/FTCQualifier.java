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

    private boolean lbLast = false;
    private boolean rbLast = false;
    private boolean forwardOn = false;
    private boolean reverseOn = false;

    private boolean aLast = false;
    private boolean bLast = false;

    private final ElapsedTime spinTimer = new ElapsedTime();
    private static final double MIN_SPINUP_TIME_SEC = 0.30;

    // NEW: gate both wheels with separate thresholds
    private static final double TOP_READY_RATIO = 0.92;
    private static final double BOT_READY_RATIO = 0.92;

    @Override
    public void init() {
        drive.init(hardwareMap);
        shooting.init(hardwareMap);
    }

    @Override
    public void loop() {
        // Driving
        double throttle = gamepad1.left_stick_y;
        double spin     = -gamepad1.right_stick_x;
        double strafe   = -gamepad1.left_stick_x;

        spin *= 0.3;
        drive.drive(throttle, spin, strafe);

        // A/B tuning (BOTTOM percent)
        boolean aNow = gamepad2.a;
        boolean bNow = gamepad2.b;

        if (aNow && !aLast) shooting.increaseShooterPercent();
        if (bNow && !bLast) shooting.decreaseShooterPercent();

        aLast = aNow;
        bLast = bNow;

        // Toggles
        boolean lbNow = gamepad2.left_bumper;
        boolean rbNow = gamepad2.right_bumper;

        // RB = forward shoot
        if (rbNow && !rbLast) {
            boolean wasOff = (!forwardOn && !reverseOn);
            forwardOn = !forwardOn;
            if (forwardOn) reverseOn = false;
            if (wasOff && forwardOn) spinTimer.reset();
        }

        // LB = reverse unjam
        if (lbNow && !lbLast) {
            reverseOn = !reverseOn;
            if (reverseOn) forwardOn = false;
        }

        lbLast = lbNow;
        rbLast = rbNow;

        // Apply modes
        if (forwardOn) {
            shooting.setShooterMode(IntakeShooting3.ShooterMode.FORWARD);
        } else if (reverseOn) {
            shooting.setShooterMode(IntakeShooting3.ShooterMode.REVERSE);
            shooting.intakeReverse(); // immediate unjam
        } else {
            shooting.setShooterMode(IntakeShooting3.ShooterMode.OFF);
            shooting.stopIntake();
        }

        // --------- NEW: Forward-only "gate both wheels" ---------
        if (shooting.getShooterMode() == IntakeShooting3.ShooterMode.FORWARD) {
            boolean timeReady = spinTimer.seconds() >= MIN_SPINUP_TIME_SEC;

            double topTarget = shooting.getTopTargetTPS();
            double botTarget = shooting.getBottomTargetTPS();

            double topNow = shooting.getTopActualTPS();
            double botNow = shooting.getBottomActualTPS();

            boolean topReady = (topTarget > 0) && (topNow >= topTarget * TOP_READY_RATIO);
            boolean botReady = (botTarget > 0) && (botNow >= botTarget * BOT_READY_RATIO);

            if (timeReady && topReady && botReady) shooting.intakeForward();
            else shooting.stopIntake();
        }
        // --------------------------------------------------------

        // Telemetry (now shows both wheels)
        telemetry.addData("Mode", shooting.getShooterMode());
        telemetry.addData("Bottom %", "%.0f%%", shooting.getBottomPercent() * 100.0);
        telemetry.addData("Top % (ratio)", "%.0f%%", shooting.getTopPercent() * 100.0);

        telemetry.addData("Bottom TPS", "%.0f / %.0f", shooting.getBottomActualTPS(), shooting.getBottomTargetTPS());
        telemetry.addData("Top TPS", "%.0f / %.0f", shooting.getTopActualTPS(), shooting.getTopTargetTPS());

        telemetry.addData("SpinTime (s)", "%.2f", spinTimer.seconds());
        telemetry.update();
    }
}
