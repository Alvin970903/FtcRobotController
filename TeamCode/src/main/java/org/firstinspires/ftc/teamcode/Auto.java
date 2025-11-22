package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.Conveyor;
import org.firstinspires.ftc.teamcode.mechanisms.Driving;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.ServoCon;
import org.firstinspires.ftc.teamcode.mechanisms.Shooting;
import org.firstinspires.ftc.teamcode.mechanisms.StrafeDriving;

@Autonomous
public class Auto extends LinearOpMode {
    StrafeDriving drive = new StrafeDriving();
    Conveyor conveyor = new Conveyor();

    Intake intake = new Intake();
    Shooting shooting = new Shooting();
    ServoCon servo = new ServoCon();

    public void runOpMode() throws InterruptedException{
        drive.init(hardwareMap);
        conveyor.init(hardwareMap);
        intake.init(hardwareMap);
        shooting.init(hardwareMap);
        servo.init(hardwareMap);

        telemetry.addLine("Initialized");
        telemetry.update();

        waitForStart();

        drive.drive(-1, 0, 0);
        sleep(2000);
        drive.drive(0, 0, 0);
        shooting.setPower(1);


    }

}
