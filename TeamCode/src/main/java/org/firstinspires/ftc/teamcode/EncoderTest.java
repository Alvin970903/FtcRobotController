package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Shooting;

@TeleOp
public class EncoderTest extends OpMode {

        Shooting shooting = new Shooting();

        @Override
        public void init() {
            shooting.init(hardwareMap);
        }

        @Override
        public void loop() {
            shooting.setVelocity70();

            telemetry.addData("pos1", shooting.shootingMotor1.getCurrentPosition());
            telemetry.addData("vel1", shooting.shootingMotor1.getVelocity());
            telemetry.update();
        }
    }

