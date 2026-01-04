package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeShooting {
    private DcMotor shootingMotor1;
    private DcMotor shootingMotor2;
    public DcMotor intakeMotor;

    private static final double MAX_POWER = 0.7;

    public void init(HardwareMap hwMap){
        shootingMotor1 = hwMap.get(DcMotor.class, "shooting_motor1");
        shootingMotor2 = hwMap.get(DcMotor.class, "shooting_motor2");
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
    public void setPower(double power){
        double shooterPower = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

        shootingMotor1.setPower(shooterPower);
        shootingMotor2.setPower(shooterPower);
        intakeMotor.setPower(power);
    }

}
