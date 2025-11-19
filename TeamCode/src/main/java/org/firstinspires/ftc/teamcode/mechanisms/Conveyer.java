package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Conveyer {
    public DcMotor conveyerMotor;

    public void init(HardwareMap hwMap){
        conveyerMotor = hwMap.get(DcMotor.class, "conveyer_motor");

        conveyerMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        conveyerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setPower(double power){
        conveyerMotor.setPower(power);
    }
}
