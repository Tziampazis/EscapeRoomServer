package Server;
import arduino.*;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class PulseGame {
    public Arduino arduino;
    public SerialPort serialPort;
    public String msg;

    public PulseGame() {
        System.out.println("PulseGame");
        System.out.println("Trying to connect...");
        arduino = new Arduino();
        arduino.setPortDescription("tty.usbmodem141370");
        arduino.openConnection();
        System.out.println("Connected to arduino on port: " + arduino.getPortDescription());
        arduino.setBaudRate(115200);
        serialPort = arduino.getSerialPort();
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[serialPort.bytesAvailable()];
                serialPort.readBytes(newData, newData.length);

                msg = new String(newData);

//                System.out.println("Recieved: " + msg);
            }
        });
    }

    public void writeOutput(char c) {
        arduino.serialWrite(c);
        arduino.serialWrite(c);
        arduino.serialWrite(c);
//        arduino.serialWrite(c,20);
    }

    public String readInput() {
        System.out.println("Reading pulse..");
        return msg;
    }

    public void close() {
        arduino.closeConnection();
    }

}
