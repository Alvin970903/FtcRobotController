package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagZoneTester;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "AprilTag In-Range Tester (Iterative)", group = "Test")
public class AprilTagZoneTesterTeleOp extends OpMode {

    private AprilTagZoneTester tester;

    @Override
    public void init() {
        tester = new AprilTagZoneTester();
        tester.init(hardwareMap, telemetry);

        // ---- Tune these ----
        // DECODE goal tags (typical): Red = 24, Blue = 20
        tester.setTargetTagId(24);

        // Start wide
        tester.setRangeWindowCm(80.0, 140.0);

        // Require rough alignment (optional)
        tester.setBearingGate(true, 6.0);

        telemetry.addLine("Initialized.");
        telemetry.addLine("Press PLAY to start vision loop.");
        telemetry.update();
    }

    @Override
    public void start() {
        // Nothing required here; VisionPortal is already streaming
    }

    @Override
    public void loop() {
        tester.update();

        // Minimal requirement: print true / false
        telemetry.addData("InRange", tester.isInRange());

        // Extra telemetry for debugging / tuning
        AprilTagDetection tag = tester.getTargetTag();
        telemetry.addData("TagVisible", tag != null);

        if (tag != null) {
            telemetry.addData("TagId", tag.id);
            telemetry.addData("Range(cm)", "%.1f", tag.ftcPose.range);
            telemetry.addData("Bearing(deg)", "%.1f", tag.ftcPose.bearing);
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        tester.stop();
    }
}
