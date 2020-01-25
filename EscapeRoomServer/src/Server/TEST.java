package Server;
import com.fazecast.jSerialComm.SerialPort;

public class TEST {

	public static void main(String[] args) {
//		String a = "0,1,1,1]2";
//		AnswerScanner aa = new AnswerScanner(a);
//		System.out.println(aa.getlastMsg());
//		String[] ls = aa.getlastMsg().split("]");
//		System.out.println(ls[0]+ " " + ls[1]);
//		
//		
//		
		
      SerialPort[] ports = SerialPort.getCommPorts();
      System.out.println("Select a port:");
      int i = 1;
      for(SerialPort port : ports) {
          System.out.println(i++ +  ": " + port.getSystemPortName());
      }
		
//		
//			String k="ACK";
//			Character l = k.charAt(0); 
//			if(l >= 'A' || l<= 'Z') {
//				System.out.println("TRUE");
//			}
		
	}
}
