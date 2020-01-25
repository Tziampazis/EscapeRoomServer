package Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {

	private static Socket sock;
	private static ServerHandler svH;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		sock = new Socket("localhost",1234);
//		PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
//		out.println("MADAFAKA");
		svH = new ServerHandler(sock);
		svH.start();
	}
	

}  
