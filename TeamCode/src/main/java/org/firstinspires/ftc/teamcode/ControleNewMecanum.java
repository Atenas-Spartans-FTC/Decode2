package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.List;

@TeleOp
public class ControleNewMecanum extends LinearOpMode {
    private Limelight3A limelight; // Variável da câmera
    private LLResult result;
    private DcMotor dd, de, td, te;// Variaveis dos motores
    private DcMotor la; //lancador
    private DcMotor lk;   //lock
    private double vel;// Variavel da velocidade chassis
    private Boolean lado = null; // null = não lido // true = azul // false = vermelho

    double sin, cos, teta, x, y, turn, power, max, dep, ddp, tep, tdp;

    @Override
    public void runOpMode() {
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
            result = limelight.getLatestResult();
            fullTelemetry();
            //Chassis

            x = gamepad1.left_stick_x;
            y = gamepad1.left_stick_y;
            turn = gamepad1.right_stick_x;

            teta = Math.atan2(y,x);
            power = Math.hypot(x,y);
            sin = Math.sin(teta - Math.PI/4);
            cos = Math.cos(teta - Math.PI/4);
            max = Math.max(Math.abs(sin),Math.abs(cos));

            dep = power * cos/max + turn;
            ddp = power * sin/max - turn;
            tep = power * sin/max + turn;
            tdp = power * cos/max - turn;

            if ((power + Math.abs(turn)) > 1){
                dep /= power + turn;
                ddp /= power + turn;
                tep /= power + turn;
                tdp /= power + turn;
            }

            de.setPower(dep);
            dd.setPower(ddp);
            te.setPower(tep);
            td.setPower(tdp);

            //Lançador
            if (gamepad2.y){
                la.setPower(1);
            }else{
                la.setPower(0.5);
            }
            if(gamepad2.x){
                lk.setPower(1);
            }else{
                lk.setPower(0);
            }
            if (gamepad2.a){
                leLado();
                Double[] deg = leAngulos();//esquerda aumenta direita diminui
                if(deg[0] == null){
                    parado();
                }else if(deg[0] < 0){ // x == deg[0]
                    giroE();
                }else if(deg[0] > 0){
                    giroD();
                }
            }
        }
    }
    public void fullTelemetry(){
        telemetry.addData("Status", "Running");

        if (lado == null){
            telemetry.addLine("Aliança atual: UNKNOWN");
        }else if(lado){
            telemetry.addLine("Aliança atual: AZUL");
        }else{
            telemetry.addLine("Aliança atual: VERMELHO");
        }

        telemtryCamera();

        telemetry.update();
    }
    public Double[] leAngulos (){
        Double x = null;
        Double y = null;
        if (result != null && result.isValid()){
            List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
            int tag;
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                tag = fr.getFiducialId();
                if((tag == 20 ^ tag == 24) && lado != null){ // ^ = xor
                    x = fr.getTargetXDegrees();
                    y = fr.getTargetYDegrees();
                }
            }
        }
        Double[] xy = new Double[2];
        xy[0] = x;
        xy[1] = y;
        return xy;
    }
    public void telemtryCamera () {
        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
            int tag;
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                tag = fr.getFiducialId();
                telemetry.addData("Tag Detectada", true);
                telemetry.addData("AprilTag ID", tag);

                telemetry.addData("X (°)", "%.2f", fr.getTargetXDegrees());
                telemetry.addData("Y (°)", "%.2f", fr.getTargetYDegrees());
            }
        } else {
            telemetry.addData("Tag Detectada", false);
            telemetry.addLine("Nenhuma AprilTag visível.");
        }
    }
    public void leLado () {
        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
            int tag;
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                tag = fr.getFiducialId();
                if (tag == 20 && lado == null){
                    lado = true;
                }else if (tag == 24 && lado == null){
                    lado = false;
                }
            }
        }
    }
    public void initCamera(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }
    public void frente(){
        dd.setPower(vel);
        de.setPower(vel);
        td.setPower(vel);
        te.setPower(vel);
    }
    public void tras(){
        dd.setPower(-vel);
        de.setPower(-vel);
        td.setPower(-vel);
        te.setPower(-vel);
    }
    public void giroD(){
        dd.setPower(-vel);
        de.setPower(vel);
        td.setPower(-vel);
        te.setPower(vel);
    }
    public void giroE(){
        dd.setPower(vel);
        de.setPower(-vel);
        td.setPower(vel);
        te.setPower(-vel);
    }
    public void esquerda(){
        dd.setPower(-vel - 0.1);
        de.setPower(vel + 0.1);
        td.setPower(vel);
        te.setPower(-vel);
    }
    public void direita(){
        dd.setPower(vel + 0.1);
        de.setPower(-vel - 0.1);
        td.setPower(-vel);
        te.setPower(vel);
    }
    public void diagonalFD(){// x = quad
        dd.setPower(0);
        de.setPower(vel);
        td.setPower(vel);
        te.setPower(0);
    }
    public void diagonalFE(){// b = bol
        dd.setPower(vel);
        de.setPower(0);
        td.setPower(0);
        te.setPower(vel);
    }
    public void diagonalTD(){// y = tri
        dd.setPower(-vel);
        de.setPower(0);
        td.setPower(0);
        te.setPower(-vel);
    }
    public void diagonalTE(){// a = x
        dd.setPower(0);
        de.setPower(-vel);
        td.setPower(-vel);
        te.setPower(0);
    }
    public void parado(){
        dd.setPower(0);
        de.setPower(0);
        td.setPower(0);
        te.setPower(0);
    }
}