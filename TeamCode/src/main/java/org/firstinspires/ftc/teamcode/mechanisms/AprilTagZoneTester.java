package org.firstinspires.ftc.teamcode.mechanisms;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class AprilTagZoneTester {
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private List<AprilTagDetection> detectedTags = new ArrayList<>();

    private Telemetry telemetry;

    // ---- Tune these ----
    // If you are using the DECODE goal tags:
    // Red goal tag id = 24, Blue goal tag id = 20
    // Change this based on which goal you’re testing.
    private int targetTagId = 24;

    // "In range" definition (cm). Start wide; tighten later.
    private double minRangeCm = 80.0;
    private double maxRangeCm = 140.0;

    // Optional: require you're roughly aimed at the tag (degrees)
    // Using ftcPose.bearing (left/right angle to tag).
    private double maxAbsBearingDeg = 6.0;

    // If you don't want angle checking yet, set to false.
    private boolean useBearingGate = true;

    public void init(HardwareMap hwMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hwMap.get(WebcamName.class, "Webcam 1"));
        builder.setCameraResolution(new Size(640, 480));
        builder.addProcessor(aprilTagProcessor);

        visionPortal = builder.build();
    }

    /** Call this every loop. */
    public void update() {
        detectedTags = aprilTagProcessor.getDetections();
    }

    public List<AprilTagDetection> getDetectedTags() {
        return detectedTags;
    }

    public AprilTagDetection getTagBySpecificId(int id) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) return detection;
        }
        return null;
    }

    /** Returns true if the target tag is visible and within your range thresholds. */
    public boolean isInRange() {
        AprilTagDetection tag = getTagBySpecificId(targetTagId);
        if (tag == null) return false;

        // range is distance from camera to tag (cm)
        double range = tag.ftcPose.range;

        if (range < minRangeCm || range > maxRangeCm) return false;

        if (useBearingGate) {
            double bearing = tag.ftcPose.bearing; // deg
            if (Math.abs(bearing) > maxAbsBearingDeg) return false;
        }

        return true;
    }

    /** Convenience: returns the target tag (or null) so TeleOp can print pose. */
    public AprilTagDetection getTargetTag() {
        return getTagBySpecificId(targetTagId);
    }

    /** Optional: print basic status for quick testing. */
    public void telemetryStatus() {
        AprilTagDetection tag = getTargetTag();

        telemetry.addData("TargetTagId", targetTagId);
        telemetry.addData("TagVisible", tag != null);

        if (tag != null) {
            telemetry.addData("Range(cm)", "%.1f", tag.ftcPose.range);
            telemetry.addData("Bearing(deg)", "%.1f", tag.ftcPose.bearing);
        }

        telemetry.addData("InRange", isInRange());
    }

    public void displayDetectionTelemetry(AprilTagDetection detectedId) {
        if (detectedId == null) return;

        if (detectedId.metadata != null) {
            telemetry.addLine(String.format("\n==== (ID %d) %s", detectedId.id, detectedId.metadata.name));
            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (cm)",
                    detectedId.ftcPose.x, detectedId.ftcPose.y, detectedId.ftcPose.z));
            telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
                    detectedId.ftcPose.pitch, detectedId.ftcPose.roll, detectedId.ftcPose.yaw));
            telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (cm, deg, deg)",
                    detectedId.ftcPose.range, detectedId.ftcPose.bearing, detectedId.ftcPose.elevation));
        } else {
            telemetry.addLine(String.format("\n==== (ID %d) Unknown", detectedId.id));
            telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)",
                    detectedId.center.x, detectedId.center.y));
        }
    }

    // ---- Setters so you can tune without editing constants ----
    public void setTargetTagId(int id) {
        this.targetTagId = id;
    }

    public void setRangeWindowCm(double minRangeCm, double maxRangeCm) {
        this.minRangeCm = minRangeCm;
        this.maxRangeCm = maxRangeCm;
    }

    public void setBearingGate(boolean enabled, double maxAbsBearingDeg) {
        this.useBearingGate = enabled;
        this.maxAbsBearingDeg = maxAbsBearingDeg;
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
            visionPortal = null;
        }
    }
}
