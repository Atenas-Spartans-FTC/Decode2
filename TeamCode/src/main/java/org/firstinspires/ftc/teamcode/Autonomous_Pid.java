package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Autonomous_Pid extends LinearOpMode {
    private DcMotor dd,de,td,te;
    private double kp = 0.5;//verificar no robo
    double odometria = 0;
    @Override
    public void runOpMode() throws InterruptedException {
        dd = hardwareMap.get(DcMotor.class, "dd");
        de = hardwareMap.get(DcMotor.class, "de");
        td = hardwareMap.get(DcMotor.class, "td");
        te = hardwareMap.get(DcMotor.class, "te");

        de.setDirection(DcMotorSimple.Direction.REVERSE);
        te.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()){
            double setpoint = 1000;

            double error = odometria - setpoint;
            double speed = kp * error;

            dd.setPower(speed);
            de.setPower(speed);
            td.setPower(speed);
            te.setPower(speed);

            telemetry.addData("Odometria X",odometria);
        }
    }
}
