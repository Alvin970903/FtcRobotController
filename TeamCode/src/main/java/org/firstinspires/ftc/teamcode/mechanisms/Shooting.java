package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Shooting {
//    public DcMotor shootingMotor1;
//    public DcMotor shootingMotor2;
//
//    public void init(HardwareMap hwMap){
//        shootingMotor1 = hwMap.get(DcMotor.class, "shooting_motor1");
//        shootingMotor2 = hwMap.get(DcMotor.class, "shooting_motor2");
//
//        shootingMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        shootingMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        shootingMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        shootingMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        shootingMotor2.setDirection(DcMotorSimple.Direction.REVERSE);
//    }

    public DcMotorEx shootingMotor1;
    public DcMotorEx shootingMotor2;
    public DcMotor intakeMotor;
    public CRServo servoRot;

    // The maximum ticks per second for 6000 RPM motors
    // 6000 RPM = 2800 ticks/sec (28 TPR motors)
    private static final double MAX_TICKS_PER_SECOND = 7000;

    public void init(HardwareMap hwMap){
        shootingMotor1 = hwMap.get(DcMotorEx.class, "shooting_motor1");
        shootingMotor2 = hwMap.get(DcMotorEx.class, "shooting_motor2");
        intakeMotor = hwMap.get(DcMotor.class, "intake_motor");
        servoRot = hwMap.get(CRServo.class, "servo_con");

        // Use encoders so velocity control works
        shootingMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shootingMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Reverse one motor so both spin same direction
        shootingMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        // Brake mode (optional, but good)
        shootingMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shootingMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    public void setPower(double power){
        double target = MAX_TICKS_PER_SECOND * 0.7;
        shootingMotor1.setVelocity(target);
        shootingMotor2.setVelocity(target);
        intakeMotor.setPower(power);
        servoRot.setPower(power);
    }
//    public void setVelocity(double speed){
//        shootingMotor1.setVelocity(speed);
//        shootingMotor2.setVelocity(speed);
//    }
    public void setShooterOn(boolean on) {
    if (on) {
        double target = MAX_TICKS_PER_SECOND * 0.7;
        shootingMotor1.setVelocity(target);
        shootingMotor2.setVelocity(target);
    } else {
        shootingMotor1.setVelocity(0);
        shootingMotor2.setVelocity(0);
    }
}

    public void setIntakePower(double power) {
        intakeMotor.setPower(power);
        servoRot.setPower(power);
    }

    public void setVelocity70() {
        double target = MAX_TICKS_PER_SECOND * 0.7;
        shootingMotor1.setVelocity(target);
        shootingMotor2.setVelocity(target);
    }

    public void setVelocityPercent(double percent){
        double targetVelocity = MAX_TICKS_PER_SECOND * percent;

        shootingMotor1.setVelocity(targetVelocity);
        shootingMotor2.setVelocity(targetVelocity);

    }

    // Full stop
    public void stopAll() {
        shootingMotor1.setVelocity(0);
        shootingMotor2.setVelocity(0);
        intakeMotor.setPower(0);
        servoRot.setPower(0);
    }

}

