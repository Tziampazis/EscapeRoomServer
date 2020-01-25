package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler extends Thread {

	Socket server;
	PrintWriter out;
	BufferedReader in;
	private String me = "";
	
	public ServerHandler(Socket serv) {
		server = serv;
		try {
			out = new PrintWriter(server.getOutputStream(), true);
			
		    in = new BufferedReader(new InputStreamReader(server.getInputStream()));
	    
		} catch (IOException e) {
			System.out.println("ThreadServerHandler On Read/Write Init");
		}

	}
	 
	
	@Override
	public void run() {
		//out.println("HELLOMODAFAKA");
		try {
			readingServer();
		} catch (InterruptedException e) {
			System.out.println("CLIENT IN RUN() THREAD ");
		}
	}
	 
	
	public void readingServer() throws InterruptedException {
		String msg;
		boolean run = true ;
		try {
			 while(run) {
				 msg = in.readLine();	
				 if(msg != null) {
					 System.out.println(" MSG  IS  : " + msg);
					 AnswerScanner a = new AnswerScanner(msg);
					 if(msg.equals("3,0,0,")) {
						 out.println("0,0,0,ACK");
						 System.out.println("Server says : " + msg);
					 }else if(msg.length() > 3 && a.getGamesState().equals("0")&& a.getPuzzlesCode().equals("0")  && (a.getlastMsg().equals("1") || a.getlastMsg().equals("2") )) {
						 this.me = (a.getlastMsg().equals("1")) ? "1" : "2";
						 System.out.println("Server says : " + msg);
						 out.println("0,0,0,"+me);
						 sleep(2000);//wait a little bit
						 out.println("0,0,0,"); //Send start request.
					 }else if(msg.equals(me+",0,0,")) { //Send client to wait
						 sleep(4000);
						 System.out.println("me " + msg);
						 out.println("0,0,0,"); //Send start request again.
					 }else if(msg.equals("3,1,0,")) {//Game Start
						 System.out.println("Game can now Begin!");
						// out.println("00010000");//game beggins
						out.println("0,1,0,ACK");
					 }else if(msg.equals("3,1,1,")) {
						 System.out.println("3,1,1 GOT IT");
						 out.println("0,1,1,ACK");
					 }else if(msg.equals("3,1,2,")) {
						 out.println("0,1,2,ACK");
						 System.out.println("0,1,2,ACK");
						 sleep(2000);
						 if(me.equals("1")) {
							 out.println("0,1,2,X");
						 }
					 }else if(a.getRecepient().equals("3") && a.getGamesState().equals("1") && a.getPuzzlesCode().equals("2") && !a.getlastMsg().equals("")) {
						 System.out.println("GOT WRONG MSG");
						 if(me.equals("1")) {//ONLY FIRST PLAYER RE-SENDS
							 out.println("0,1,2,Z");
						 }
					 }else if(msg.equalsIgnoreCase("3,1,3,")) {
						 System.out.println("RIGHT LETTER PUZZLE");
						 out.println("0,1,3,ACK");
					 }else if(msg.equals("3,1,4,")) {
						 System.out.println("RESEARCH PUZZLE");
						 out.println("0,1,4,ACK");
					 }else if(a.getRecepient().equals("3") && a.getGamesState().equals("1") && a.getPuzzlesCode().equals("5") && a.getlastMsg().equals("120") ) {
						 System.out.println("GOT BPM");
						 out.println("0,1,5,ACK");
						 if(me.equals("1")){
							 sleep(2000);
							 System.out.println("Sending Wrong EQ ans");
							 out.println("0,1,5,10");//send equation answer 
						 }
					 }else if(a.getRecepient().equals("3") && a.getGamesState().equals("1") && a.getPuzzlesCode().equals("5") && a.getlastMsg().equalsIgnoreCase("wrong")) {
						 System.out.println("GOT WRONG FROM EQ");
						 if(me.equals("1")) {
							 System.out.println("SEND CORRECT");
							 out.println("0,1,5,100");//resend ans
						 }
					 }else if(msg.equals("3,1,6,")) {
						 System.out.println("NFC PUZZLE STARTS");
						 out.println("0,1,6,ACK");
						 out.println("0,1,6,TAG#");//send correct scanned TAG
					 }else if(msg.equals("3,1,7,")) {
						// System.out.println("Sending WRONG CORDINATES PLAYER");
						 out.println("0,1,7,ACK");
						 if(me.equals("1")){
							 System.out.println("Sending WRONG CORDINATES PLAYER second");
							 out.println("0,1,7,1]1");
						 }else {
							 System.out.println("Sending CORRECT CORDINATES PLAYER first");
							 out.println("0,1,7,3]4");
						 }
					 }else if(msg.equalsIgnoreCase(this.me+",1,7,wrong")){
						 if(me.equals("1")){
							 System.out.println("PLAYER 1 send CORRECT COORD");
							 out.println("0,1,7,1]2");
						 }else {
							 System.out.println("PLAYER 2 send CORRECT COORD");
							 out.println("0,1,7,3]4");
						 }
					 }else if(msg.equalsIgnoreCase("3,1,8,letter")) {
						 System.out.println("REACH AFTER LOCATION");
						 out.println("0,1,8,ACK");
						 sleep(2000);
						 if(me.equalsIgnoreCase("1")) {
							 System.out.println("Send WRONG PL 1 ");
							 out.println("0,1,8,aa");
						 }
					 }else if(msg.equalsIgnoreCase("3,1,8,wrong")) {
						 if(me.equalsIgnoreCase("1")) {
							 out.println("0,1,8,marsoupilami");
						 }
					 }else if(msg.equalsIgnoreCase("3,1,9,time")) {
						 System.out.println("REACH THE END OF THE GAME");
						 out.println("0,1,9,ACK");
						 run = false;
					 }else if(msg.equals("3,0,9,")) {
						 System.out.println("Time's up");
						 run = false;
						 out.println("0,0,9,ACK");
					 }
					 
				 }
			}
		} catch (IOException e) {
			System.out.println("RUN Error ServerHanlder");
		}
	}
	
	}
