package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class ControlePID extends LinearOpMode {
    private Limelight3A limelight; // Variável da câmera
    private LLResult result;
    private DcMotor dd, de, td, te;// Variaveis dos motores
    private DcMotor la; //lancador
    private DcMotor lk;   //lock
    private double vel;// Variavel da velocidade chassis
    private Boolean lado = null; // null = não lido // true = azul // false = vermelho
    @Override
    public void runOpMode() throws InterruptedException {
        // Mapear os motores
        dd = hardwareMap.get(DcMotor.class, "dd"); // 0
        de = hardwareMap.get(DcMotor.class, "de"); // 1
        td = hardwareMap.get(DcMotor.class, "td"); // 2
        te = hardwareMap.get(DcMotor.class, "te"); // 3

        la = hardwareMap.get(DcMotor.class, "la"); //
        lk = hardwareMap.get(DcMotor.class, "lk"); //

        //Corrige a direção dos motores
        de.setDirection(DcMotor.Direction.REVERSE);
        te.setDirection(DcMotor.Direction.REVERSE);

        // Inicia e configura a câmera
        initCamera();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

        }

    }
    public void initCamera(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }
}
