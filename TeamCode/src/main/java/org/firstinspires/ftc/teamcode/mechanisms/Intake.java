package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    private DcMotor intakeMotor;
    //private CRServo servoRot;


    public void init(HardwareMap hwMap){
        intakeMotor = hwMap.get(DcMotor.class, "intake_motor");
        //servoRot = hwMap.get(CRServo.class, "servo_con");

        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setPower(double power){
        intakeMotor.setPower(power);
        //servoRot.setPower(power);
    }
}
