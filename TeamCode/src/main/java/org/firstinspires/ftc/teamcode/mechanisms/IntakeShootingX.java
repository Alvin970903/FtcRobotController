// IntakeShooting3.java
package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeShootingX {

    public enum ShooterMode { OFF, FORWARD, REVERSE }

    // bottom flywheel = shooting_motor_1
    // top flywheel    = shooting_motor_2
    private DcMotorEx shootingMotor1; // bottom
    private DcMotorEx shootingMotor2; // top

    private DcMotor intakeMotor;
    private CRServo servoRot;

    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_RPM = 6000.0;
    private static final double MAX_TPS = (MAX_RPM / 60.0) * TICKS_PER_REV;

    // A/B controls BOTTOM wheel percent
    private double bottomPercent = 0.35;      // default (you said working percent is 35%)
    private static final double STEP = 0.05;  // 5% per tap

    // Top is a fixed ratio of bottom
    private static final double TOP_RATIO = 0.65;

    // Reverse fixed (unjam)
    private static final double REVERSE_PERCENT = 0.10;

    private ShooterMode shooterMode = ShooterMode.OFF;

    public void init(HardwareMap hwMap) {
        shootingMotor1 = hwMap.get(DcMotorEx.class, "shooting_motor1");
        shootingMotor2 = hwMap.get(DcMotorEx.class, "shooting_motor2");
        intakeMotor    = hwMap.get(DcMotor.class,  "intake_motor");
        servoRot       = hwMap.get(CRServo.class,  "servo_con");

        shootingMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shootingMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Keep if needed so both wheels spin same physical direction
        shootingMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        shootingMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shootingMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stopAll();
    }

    // ---- Percent tuning (BOTTOM only) ----
    public void increaseShooterPercent() {
        bottomPercent = clamp01(bottomPercent + STEP);
        applyShooterVelocity();
    }

    public void decreaseShooterPercent() {
        bottomPercent = clamp01(bottomPercent - STEP);
        applyShooterVelocity();
    }

    public double getBottomPercent() {
        return bottomPercent;
    }

    public double getTopPercent() {
        return clamp01(bottomPercent * TOP_RATIO);
    }

    private static double clamp01(double x) {
        if (x < 0.0) return 0.0;
        if (x > 1.0) return 1.0;
        return x;
    }

    public void setShooterMode(ShooterMode mode) {
        shooterMode = mode;
        applyShooterVelocity();
    }

    public ShooterMode getShooterMode() {
        return shooterMode;
    }

    // Per-wheel targets/actuals for gating + telemetry
    public double getBottomTargetTPS() {
        return getBottomPercent() * MAX_TPS;
    }

    public double getTopTargetTPS() {
        return getTopPercent() * MAX_TPS;
    }

    public double getBottomActualTPS() {
        return Math.abs(shootingMotor1.getVelocity());
    }

    public double getTopActualTPS() {
        return Math.abs(shootingMotor2.getVelocity());
    }

    private void applyShooterVelocity() {
        switch (shooterMode) {
            case OFF: {
                shootingMotor1.setVelocity(0);
                shootingMotor2.setVelocity(0);
                break;
            }
            case FORWARD: {
                double bottomTPS = getBottomTargetTPS();
                double topTPS    = getTopTargetTPS();

                // Keep your sign convention: FORWARD uses negative velocities
                shootingMotor1.setVelocity(-bottomTPS); // bottom motor1
                shootingMotor2.setVelocity(-topTPS);    // top motor2
                break;
            }
            case REVERSE: {
                double tps = REVERSE_PERCENT * MAX_TPS;
                shootingMotor1.setVelocity(tps);
                shootingMotor2.setVelocity(tps);
                break;
            }
        }
    }

    // ---------------- Intake / Servo control (split) ----------------
    // Gamepad1 wants intake ONLY (no servo)
    public void intakeOnlyForward() {
        intakeMotor.setPower(-1.0);
    }

    public void intakeOnlyReverse() {
        intakeMotor.setPower(1.0);
    }

    public void stopIntakeOnly() {
        intakeMotor.setPower(0.0);
    }

    // Gamepad2 shooting wants intake + servo together
    public void feedForward() {
        intakeMotor.setPower(-1.0);
        servoRot.setPower(1.0);
    }

    public void feedReverse() {
        intakeMotor.setPower(1.0);
        servoRot.setPower(-1.0);
    }

    public void stopFeed() {
        intakeMotor.setPower(0.0);
        servoRot.setPower(0.0);
    }
    // ---------------------------------------------------------------

    public void stopAll() {
        setShooterMode(ShooterMode.OFF);
        stopFeed();
    }
}
