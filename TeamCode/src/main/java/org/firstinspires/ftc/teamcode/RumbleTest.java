package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@Disabled
@TeleOp
public class RumbleTest extends OpMode {

    double endGameStart;
    boolean isEndGame = false;

    @Override
    public void init() {
        telemetry.addData("Status", "Waiting for start...");
        telemetry.update();
    }

    @Override
    public void start() {
        endGameStart = getRuntime() + 90;   // rumble at 90 seconds
    }

    @Override
    public void loop() {
        if (getRuntime() >= endGameStart && !isEndGame) {
            gamepad1.rumbleBlips(3);
            isEndGame = true;
        }

        telemetry.addData("Time", getRuntime());
        telemetry.addData("Endgame at", endGameStart);
        telemetry.addData("Triggered", isEndGame);
        telemetry.update();
    }
}
