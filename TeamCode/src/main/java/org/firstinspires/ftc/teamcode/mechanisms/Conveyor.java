package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Conveyor {
    public DcMotor conveyorMotor;

    public void init(HardwareMap hwMap){
        conveyorMotor = hwMap.get(DcMotor.class, "conveyor_motor");

        conveyorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        conveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setPower(double power){
        conveyorMotor.setPower(power);
    }
}
