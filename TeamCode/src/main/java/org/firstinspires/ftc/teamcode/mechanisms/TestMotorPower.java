package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestMotorPower {
    private DcMotorEx motor;

    // goBILDA 312 RPM: ~537.6 ticks per rev
    private static final double TICKS_PER_REV = 537.6;
    private static final double MAX_RPM       = 312.0;
    private static final double MAX_TPS       = MAX_RPM / 60.0 * TICKS_PER_REV;  // ≈ 2800

    public void init(HardwareMap hwMap){
        motor = hwMap.get(DcMotorEx.class, "motor");

        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // percent = 0.0 to 1.0  (fraction of max RPM)
    public void setVelocityPercent(double percent){
        // clamp
        percent = Math.max(-1.0, Math.min(1.0, percent));

        double targetTps = MAX_TPS * percent;
        motor.setVelocity(targetTps);
    }

    public void stop() {
        motor.setVelocity(0);
    }
}
