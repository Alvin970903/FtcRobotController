package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TestBench {
    private DigitalChannel touchSensor; // descriptive name
    //private DcMotor motor;

    private DistanceSensor distance;

    private double ticksPerRev; // revolution

    public void init(HardwareMap hwMap){
        // touch sensor
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor");
        touchSensor.setMode(DigitalChannel.Mode.INPUT);

        // motor
//        motor = hwMap.get(DcMotor.class, "motor");
//        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        ticksPerRev = motor.getMotorType().getTicksPerRev();
//        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // stop immediately
//        // motor.setDirection(DcMotorSimple.Direction.REVERSE);

        // distance sensor
        distance = hwMap.get(DistanceSensor.class, "sensor_distance");
    }


    // Touch Sensor
    public boolean getTouchSensorState() {
        return !touchSensor.getState();
    }

    public boolean isTouchSensorReleased(){
        return touchSensor.getState();
    }

    //DC motor
    public void setMotorSpeed(double speed){
        // accepts values from -1.0 to 1.0
        // motor.setPower(speed);
    }

//    public double getMotorRevs(){
//        //return motor.getCurrentPosition() / ticksPerRev; // normalizing ticks to revolutions
//    }

    public void setMotorZeroBehaviour(DcMotor.ZeroPowerBehavior zeroBehaviour){
        //motor.setZeroPowerBehavior(zeroBehaviour);
    }

    // distance sensor
    public double getDistance(){
        return distance.getDistance(DistanceUnit.CM);
    }
}
