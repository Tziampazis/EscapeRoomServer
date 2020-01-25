package Server;
import Server.Music;

public class ComponentsHandler extends Thread {

    /**
     * This class should be used to call arduino functions(connection GPIO pins to components)
     * and not for server response to the players. The response is created in GameHandler.
     */


    public boolean morseCompFlag = false;
    public boolean pulseFlag =false;
    public boolean lightSensor = false;
    public boolean runFlag = true;
    public boolean moorseFlag = false;
    public boolean lightFlag = false;
    public boolean heartFlag = false;



    public boolean stoplight = false;
    public boolean stopmorse = false;
    public boolean stopbpm = false;

    public LEDStrip strip;

    @Override
    public void run() {
        while(runFlag) {

            if(moorseFlag) {
                System.out.println("IN MORSE COMPHANDLER");
                morseLight();
                setMoorseFlag(false);
            }
            if(lightFlag) {
                System.out.println("IN LIGHT COMPHANDLER");
                lightSensors();
                setLightFlag(false);
            }
            if(heartFlag) {
                System.out.println("IN HEART COMPHANDLER");
                heartBeat();
                setHeartFlag(false);
                this.runFlag = false;
            }
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                // FIXME Auto-generated catch block
                e.printStackTrace();
            }
        }
    }





    public boolean isMoorseFlag() {
        return moorseFlag;
    }
    public void setMoorseFlag(boolean moorseFlag) {
        this.moorseFlag = moorseFlag;
    }
    public boolean isLightFlag() {
        return lightFlag;
    }
    public void setLightFlag(boolean lightFlag) {
        this.lightFlag = lightFlag;
    }
    public boolean isHeartFlag() {
        return heartFlag;
    }





    public void setHeartFlag(boolean heartFlag) {
        this.heartFlag = heartFlag;
    }





    public ComponentsHandler() {

    }

    //Hold button for 15 sec
    public void button() {

    }
    //Use of thresholds
    public void heartBeat() {
        PulseGame game2 = new PulseGame();
        game2.writeOutput('1');
        String result2 = game2.readInput();
        System.out.println("Trying to read pulse..");
        while (result2 == null) {
            result2 = game2.readInput();
            if(isStopbpm()){
                result2 = "";
            }

        }
        setPulseFlag(true);
        System.out.println("Recieved2: " + result2);
        System.out.println("Game2 completed");
        game2.writeOutput('0');
        game2.close();

        manageLight(2);

    }
    //2 Light sensors at same time
    public void lightSensors() {
        LightSensorGame game3 = new LightSensorGame();
        game3.writeOutput('1');
        String result3 = game3.readInput();
        System.out.println("Trying to read light..");
        while (result3 == null) {
            result3 = game3.readInput();
            if(isStoplight()){
                result3 = "";
            }
        }
        setLightSensor(true);
        System.out.println("Recieved3: " + result3);
        System.out.println("Game3 completed");
        game3.writeOutput('0');
        game3.close();

        manageLight(2);

    }

    public void morseLight() {
        manageLight(1);
        ButtonGame game1 = new ButtonGame();
        game1.writeOutput('1');
        String result = game1.readInput();
        System.out.println("Trying to read button..");
        while (result == null) {
            result = game1.readInput();
            if(isStopmorse()){
                result = "";
            }
        }
        setMorseCompFlag(true);
        System.out.println("Recieved1: " + result);
        System.out.println("Game1 completed");
        game1.writeOutput('0');
        game1.close();

        manageLight(0);
        manageLight(2);
    }

    //return just boolean
    public void morse() {

    }

    public void light() {

    }


    public void bpm() {

    }

    public boolean isStoplight() {
        return stoplight;
    }

    public void setStoplight(boolean stoplight) {
        this.stoplight = stoplight;
    }

    public boolean isStopmorse() {
        return stopmorse;
    }

    public void setStopmorse(boolean stopmorse) {
        this.stopmorse = stopmorse;
    }

    public boolean isStopbpm() {
        return stopbpm;
    }

    public void setStopbpm(boolean stopbpm) {
        this.stopbpm = stopbpm;
    }



    public boolean isMorseCompFlag() {
        return morseCompFlag;
    }

    public void setMorseCompFlag(boolean morseCompFlag) {
        this.morseCompFlag = morseCompFlag;
    }

    public boolean isPulseFlag() {
        return pulseFlag;
    }

    public void setPulseFlag(boolean pulseFlag) {
        this.pulseFlag = pulseFlag;
    }

    public boolean isLightSensor() {
        return lightSensor;
    }

    public void setLightSensor(boolean lightSensor) {
        this.lightSensor = lightSensor;
    }

    public void manageLight(int code) {
        switch (code) {
            case 0: strip.writeOutput('0');
//                    strip.close();
                    System.out.println("0--Strip closed");
                    break;
            case 1: strip = new LEDStrip();
                    strip.writeOutput('1');
                    System.out.println("1--Strip opened");
                    break;
            case 2: strip.writeOutput('2');
                    System.out.println("2--Strip blink green");
                    break;
            case 3: strip.writeOutput('3');
                    System.out.println("3--Strip blink white");
                    break;
        }
    }

}
