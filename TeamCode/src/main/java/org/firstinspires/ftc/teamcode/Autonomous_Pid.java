package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous
public class Autonomous_Pid extends LinearOpMode {
    private DcMotor dd,de,td,te;
    GoBildaPinpointDriver pinpointer;
    private double kp = 0.000072;//verificar no robo
    @Override
    public void runOpMode() throws InterruptedException {
        dd = hardwareMap.get(DcMotor.class, "dd");
        de = hardwareMap.get(DcMotor.class, "de");
        td = hardwareMap.get(DcMotor.class, "td");
        te = hardwareMap.get(DcMotor.class, "te");

        pinpointer = hardwareMap.get(GoBildaPinpointDriver.class, "pn");

        de.setDirection(DcMotorSimple.Direction.REVERSE);
        te.setDirection(DcMotorSimple.Direction.REVERSE);

        pinpointer.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        pinpointer.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.REVERSED
        );// x,y

        pinpointer.resetPosAndIMU();

        pinpointer.update();
        final double lastX = pinpointer.getEncoderX();
        final double lastY = pinpointer.getEncoderY();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()){
            pinpointer.update();

            double x = pinpointer.getEncoderX() - lastX;
            double y = pinpointer.getEncoderY() - lastY;
            double z = pinpointer.getHeading(AngleUnit.DEGREES);

            double setpoint = 1000;

            double error = setpoint - y;
            double speed = kp * error;

            dd.setPower(speed);
            de.setPower(speed);
            td.setPower(speed);
            te.setPower(speed);

            telemetry.addData("Odometria Y",y);
            telemetry.addData("Odometria X",x);
            telemetry.addData("Odometria Z",z);
            telemetry.update();
        }
    }
}
