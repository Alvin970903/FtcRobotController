package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestBenchColor;
import org.firstinspires.ftc.teamcode.mechanisms.TestBenchIMU;
@Disabled
@TeleOp
public class IMUPractice extends OpMode {
    TestBenchIMU bench = new TestBenchIMU();

    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        telemetry.addData("Heading", bench.getHeading());
    }
}
