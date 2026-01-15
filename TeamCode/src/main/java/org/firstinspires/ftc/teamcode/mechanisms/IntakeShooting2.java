package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeShooting2 {

    public enum ShooterMode { OFF, FORWARD, REVERSE }

    private DcMotorEx shootingMotor1;
    private DcMotorEx shootingMotor2;
    private DcMotor intakeMotor;
    private CRServo servoRot;

    // Encoder constants
    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_RPM       = 6000.0;
    private static final double MAX_TPS       = (MAX_RPM / 60.0) * TICKS_PER_REV; // ~2800 tps

    // Runtime-tunable FORWARD percent (0.0 to 1.0)
    private double shooterPercent = 0.70;     // default 70% (forward only)
    private static final double STEP = 0.10;  // 10% per tap

    // Fixed REVERSE percent (unjam only)
    private static final double REVERSE_PERCENT = 0.40; // 40% of max velocity

    private ShooterMode shooterMode = ShooterMode.OFF;

    public void init(HardwareMap hwMap) {
        shootingMotor1 = hwMap.get(DcMotorEx.class, "shooting_motor1");
        shootingMotor2 = hwMap.get(DcMotorEx.class, "shooting_motor2");
        intakeMotor    = hwMap.get(DcMotor.class,  "intake_motor");
        servoRot = hwMap.get(CRServo.class, "servo_con");

        shootingMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shootingMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Reverse one shooter motor so both wheels spin same physical direction
        shootingMotor2.setDirection(DcMotorSimple.Direction.REVERSE);


        shootingMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shootingMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stopAll();
    }

    // Shooter percent tuning (affects FORWARD mode only)
    public void increaseShooterPercent() {
        shooterPercent = clamp01(shooterPercent + STEP);
        applyShooterVelocity();
    }

    public void decreaseShooterPercent() {
        shooterPercent = clamp01(shooterPercent - STEP);
        applyShooterVelocity();
    }

    public double getShooterPercent() {
        return shooterPercent;
    }

    private double clamp01(double x) {
        if (x < 0.0) return 0.0;
        if (x > 1.0) return 1.0;
        return x;
    }

    // Shooter control
    public void setShooterMode(ShooterMode mode) {
        shooterMode = mode;
        applyShooterVelocity();
    }

    public ShooterMode getShooterMode() {
        return shooterMode;
    }

    // Forward-only target (for forward gating + telemetry)
    public double getTargetSpeedAbsTPS() {
        return shooterPercent * MAX_TPS;
    }

    private void applyShooterVelocity() {
        switch (shooterMode) {
            case OFF: {
                shootingMotor1.setVelocity(0);
                shootingMotor2.setVelocity(0);
                break;
            }
            case FORWARD: {
                // CHANGED: invert sign so "FORWARD" matches physical forward shooting
                double targetAbsTPS = shooterPercent * MAX_TPS;
                shootingMotor1.setVelocity(-targetAbsTPS);
                shootingMotor2.setVelocity(-targetAbsTPS);
                break;
            }
            case REVERSE: {
                // CHANGED: invert sign so "REVERSE" matches physical reverse (unjam)
                double targetAbsTPS = REVERSE_PERCENT * MAX_TPS;
                shootingMotor1.setVelocity(targetAbsTPS);
                shootingMotor2.setVelocity(targetAbsTPS);
                break;
            }
        }
    }

    public double getShooterSpeedAbsTPS() {
        return (Math.abs(shootingMotor1.getVelocity()) + Math.abs(shootingMotor2.getVelocity())) / 2.0;
    }

    // Intake control
    public void intakeForward() {
        // CHANGED: invert sign so intake "forward feed" matches physical direction
        intakeMotor.setPower(-1.0);
        servoRot.setPower(1.0);
    }

    public void intakeReverse() {
        // CHANGED: invert sign so intake "reverse unjam" matches physical direction
        intakeMotor.setPower(1.0);
        servoRot.setPower(-1.0);
    }

    public void stopIntake() {
        intakeMotor.setPower(0.0);
        servoRot.setPower(0);
    }
    //
    public void setServoRot(double power){
        servoRot.setPower(power);
    }
    // Safety
    public void stopAll() {
        setShooterMode(ShooterMode.OFF);
        stopIntake();
    }
}
