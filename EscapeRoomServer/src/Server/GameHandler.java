package Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class GameHandler extends Thread {

	//Socket clientSocket;
	private PrintWriter out;
	private ClientHandler cl;

	public GameHandler(PrintWriter out,ClientHandler c) {
		this.out = out;
		this.cl = c;
//		try {
//			out = new PrintWriter(clientSocket.getOutputStream() ,true );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}


	public void hello() {
		String prot =constructMessage(3,0, 0, "");
		out.println(prot);
	}

	public void playerIndication(String numofPl) {
		String prot = constructMessage(3, 0, 0, numofPl);
		out.println(prot);
	}


	//Say that one of the players are not ready yet
	public void gameNotReady(String num) {
		int rec = (num.equals("1")) ? 1 :2 ;
		String prot =constructMessage(rec,0, 0, "");
		out.println(prot);
	}

	public void startGame() {
		String prot = constructMessage(3,1, 0, "");
		out.println(prot);

	}

	//after both clients are ready
	public void gameStarted() {
		String prot = constructMessage(3,1, 1, "");
		out.println(prot);
	}

	//PUZZLE 1
	public void laptopPuzzle() {
		String prot = constructMessage(3,1, 2, "");
		out.println(prot);
	}

	public void sendLetterWrong() {
		String prot = constructMessage(3,1, 2, "wrong");
		out.println(prot);
	}


	public void checkedLetter() {
		String prot = constructMessage(3,1, 3, "");
		out.println(prot);
	}


	//PUZZLE 2
	public void researchPuzzle() {
		String prot = constructMessage(3,1, 4, "");
		out.println(prot);
	}

	//PUZZLE 3

	public void sendBPM(String bpm) {
		String prot = constructMessage(3,1, 5, bpm);
		out.println(prot);
	}


	public void wrongEqResult() {
		String prot = constructMessage(3,1, 5, "wrong");
		out.println(prot);
	}

	//PUZZLE 4

	public void nfcTagStart() {
		String prot = constructMessage(3,1, 6, "");
		out.println(prot);
	}
	//PUZLLE 5

	public void mapPuzzle() {
		String prot = constructMessage(3,1, 7, "");
		out.println(prot);
	}

	public void correctLocation(String plNum){
		int pl = Integer.parseInt(plNum);
		System.out.println(" NUM  INN CORRECT " + pl + "  " + plNum);
		String prot = constructMessage(pl,1, 7, "");
		out.println(prot);
	}

	public void wrongLocation(String plNum) {
		int pl = Integer.parseInt(plNum);
		System.out.println(" NUM  INN WRRONG " + pl + "  " + plNum);
		String prot = constructMessage(pl,1, 7, "wrong");
		out.println(prot);
	}


	//PUZZLE 6


	public void startCaesar() {
		String prot = constructMessage(3,1, 8, "letter");
		out.println(prot);
	}


	public void checkedCaesar(String time) {
		String prot =constructMessage(3,1, 9, time);
		out.println(prot);
	}


	public void wrongCaesar() {
		String prot =constructMessage(3,1, 8, "wrong");
		out.println(prot);
	}



	public void timesUP() {
		String prot = constructMessage(3,0, 9, "");
		out.println(prot);
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
		this.cl.setRun(false);
	}

	public void sendB() {
		String prot = constructMessage(3,1, 11, "ACK");
		out.println(prot);
	}

	/*
	 * This method is used to send back ack for ending the game. Thus when server receives that
	 * the game has end from the phone it acknowledge back end terminate the connection
	 */
	public void end() {
		String prot =constructMessage(3,0, 0, "");
		out.println(prot);
	}

	public void sendHint() {
		String prot =constructMessage(3,1,10, "ACK");
		out.println(prot);
	}

	// Create Message for Client
	public String constructMessage(int rec,int state ,int puzzle ,String msg) {
		Protocol prot = new Protocol(rec, state, puzzle, msg);
		return prot.getCompleteMessage();
	}

}
