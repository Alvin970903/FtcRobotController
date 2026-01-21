// IntakeShooting4.java
package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class IntakeShooting4 {

    public enum ShooterMode { OFF, FORWARD, REVERSE }
    public enum ShotMode   { CLOSE, FAR }

    private DcMotorEx bottomMotor; // shooting_motor_1
    private DcMotorEx topMotor;    // shooting_motor_2

    private DcMotor intakeMotor;
    private CRServo servoRot;

    // Encoder constants (your assumption)
    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_RPM = 6000.0;
    private static final double MAX_TPS = (MAX_RPM / 60.0) * TICKS_PER_REV;

    // Top wheel is a fixed ratio of bottom (keep your current style)
    private static final double TOP_RATIO = 0.65;

    // Reverse fixed (unjam)
    private static final double REVERSE_PERCENT = 0.10;

    // Current state
    private ShooterMode shooterMode = ShooterMode.OFF;
    private ShotMode shotMode = ShotMode.CLOSE;

    // Current percent (set by shot mode)
    private double bottomPercent = 0.35;

    // Current PIDF (set by shot mode)
    private double botP = 48.0, botI = 0.0, botD = 0.0, botF = 14.0;
    private double topP = 38.0, topI = 0.0, topD = 0.0, topF = 13.0;

    public void init(HardwareMap hwMap) {
        bottomMotor = hwMap.get(DcMotorEx.class, "shooting_motor1");
        topMotor    = hwMap.get(DcMotorEx.class, "shooting_motor2");
        intakeMotor = hwMap.get(DcMotor.class,  "intake_motor");
        servoRot    = hwMap.get(CRServo.class,  "servo_con");

        bottomMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        topMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        topMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        bottomMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        topMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        applyShotMode(ShotMode.CLOSE);
        stopAll();
    }

    //  Shot modes
    public void toggleShotMode() {
        applyShotMode(shotMode == ShotMode.CLOSE ? ShotMode.FAR : ShotMode.CLOSE);
    }

    public void applyShotMode(ShotMode mode) {
        shotMode = mode;

        if (mode == ShotMode.CLOSE) {
            // Close: bottom 35%
            bottomPercent = 0.35;

            // Close PIDF:
            // Bottom: P=48 F=14
            botP = 48.0; botF = 14.0; botI = 0.0; botD = 0.0;
            // Top: P=38 F=13
            topP = 38.0; topF = 13.0; topI = 0.0; topD = 0.0;

        } else {
            // Far: bottom 40%
            bottomPercent = 0.40;

            // Far PIDF: both P=48 F=14
            botP = 48.0; botF = 14.0; botI = 0.0; botD = 0.0;
            topP = 48.0; topF = 14.0; topI = 0.0; topD = 0.0;
        }

        applyVelocityPIDF();
        applyShooterVelocity();
    }

    public ShotMode getShotMode() { return shotMode; }

    public double getBottomPercent() { return bottomPercent; }
    public double getTopPercent() { return clamp01(bottomPercent * TOP_RATIO); }

    public double getBotP() { return botP; }
    public double getBotF() { return botF; }
    public double getTopP() { return topP; }
    public double getTopF() { return topF; }

    private static double clamp01(double x) {
        if (x < 0.0) return 0.0;
        if (x > 1.0) return 1.0;
        return x;
    }

    private void applyVelocityPIDF() {
        PIDFCoefficients bot = new PIDFCoefficients(botP, botI, botD, botF);
        PIDFCoefficients top = new PIDFCoefficients(topP, topI, topD, topF);

        bottomMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, bot);
        topMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, top);
    }

    // Shooter control
    public void setShooterMode(ShooterMode mode) {
        shooterMode = mode;
        applyShooterVelocity();
    }


    // getter methods
    public ShooterMode getShooterMode() { return shooterMode; }

    public double getBottomTargetTPS() { return bottomPercent * MAX_TPS; }
    public double getTopTargetTPS() { return getTopPercent() * MAX_TPS; }

    public double getBottomActualTPS() { return Math.abs(bottomMotor.getVelocity()); }
    public double getTopActualTPS() { return Math.abs(topMotor.getVelocity()); }

    private void applyShooterVelocity() {
        switch (shooterMode) {
            case OFF:
                bottomMotor.setVelocity(0);
                topMotor.setVelocity(0);
                break;

            case FORWARD: {
                double bottomTPS = getBottomTargetTPS();
                double topTPS    = getTopTargetTPS();

                // Keep your sign convention: FORWARD uses negative velocities
                bottomMotor.setVelocity(-bottomTPS);
                topMotor.setVelocity(-topTPS);
                break;
            }

            case REVERSE: {
                double tps = REVERSE_PERCENT * MAX_TPS;
                bottomMotor.setVelocity(tps);
                topMotor.setVelocity(tps);
                break;
            }
        }
    }

    // Intake / Servo control (split)
    // Gamepad1: intake ONLY (no servo)
    public void intakeOnlyForward() { intakeMotor.setPower(-1.0); }
    public void intakeOnlyReverse() { intakeMotor.setPower(1.0); }
    public void stopIntakeOnly()    { intakeMotor.setPower(0.0); }

    // Shooting feed: intake + servo together
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

    public void stopAll() {
        setShooterMode(ShooterMode.OFF);
        stopFeed();
        stopIntakeOnly();
    }
}
