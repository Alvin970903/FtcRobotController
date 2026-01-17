package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Intake;

@TeleOp
public class IntakeTest extends OpMode {
    Intake intake = new Intake();
    @Override
    public void init() {
        intake.init(hardwareMap);
    }

    @Override
    public void loop() {
        intake.setPower(-1);
    }
}
