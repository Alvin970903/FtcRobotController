package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeShooting {
    private DcMotorEx shootingMotor1;
    private DcMotorEx shootingMotor2;
    public DcMotor intakeMotor;

    //private static final double MAX_POWER = 0.7;

    // goBILDA 312 RPM: ~537.6 ticks per rev
    // 6000 RPM motor
    private static final double TICKS_PER_REV = 28.0;     // encoder ticks per output shaft rev
    private static final double MAX_RPM       = 6000.0;
    private static final double MAX_TPS       = (MAX_RPM / 60.0) * TICKS_PER_REV;   // ≈ 2800


    public void init(HardwareMap hwMap){
        shootingMotor1 = hwMap.get(DcMotorEx.class, "shooting_motor1");
        shootingMotor2 = hwMap.get(DcMotorEx.class, "shooting_motor2");
        intakeMotor = hwMap.get(DcMotor.class, "intake_motor");

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

    public void intakeShoot(double power){
        if(power == 0){
            shootingMotor1.setVelocity(0);
            shootingMotor2.setVelocity(0);
            intakeMotor.setPower(0);
        }

        else if(power > 0){
            double target  = MAX_TPS * 0.4;
            shootingMotor1.setVelocity(target);
            shootingMotor2.setVelocity(target);

            intakeMotor.setPower(1);
        }

        else {
            double target = -(MAX_TPS * 0.6);
            shootingMotor1.setVelocity(target);
            shootingMotor2.setVelocity(target);

            intakeMotor.setPower(-1);
        }
    }

    public void stop(){
        intakeShoot(0);
    }
}
