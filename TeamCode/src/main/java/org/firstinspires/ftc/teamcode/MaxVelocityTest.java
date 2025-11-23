package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.mechanisms.Shooting;

@TeleOp(name="Shooter Max Velocity Test", group="Test")
public class MaxVelocityTest extends OpMode {

    Shooting shooting = new Shooting();

    @Override
    public void init() {
        shooting.init(hardwareMap);

        telemetry.addLine("Shooter Max Velocity Test - READY");
        telemetry.addLine("Press PLAY and wait 3 seconds for full speed.");
        telemetry.update();
    }

    @Override
    public void loop() {

        // Run at full power so we can measure real max speed
        shooting.setPower(1.0);

        // Telemetry from both motors
        telemetry.addData("Motor1 Position", shooting.shootingMotor1.getCurrentPosition());
        telemetry.addData("Motor1 Velocity", shooting.shootingMotor1.getVelocity());

        telemetry.addData("Motor2 Position", shooting.shootingMotor2.getCurrentPosition());
        telemetry.addData("Motor2 Velocity", shooting.shootingMotor2.getVelocity());

        telemetry.update();
    }
}
