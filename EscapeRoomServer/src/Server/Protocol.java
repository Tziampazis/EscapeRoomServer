package Server;

public class Protocol {


	String message = "";

	public Protocol(int rec,int state,int puzzle,String msg) {
		setRecepient(rec);
		setGameState(state);
		setPuzzle(puzzle);
		this.message = this.message + msg;
	}


	public void setRecepient(int rec) {
		if(rec ==0 ) {
			message = message + "0,"; // Pi
		}else if (rec == 1) {
			message = message + "1,"; // Phone 1
		}else if (rec == 2) {
			message = message + "2,"; // Phone 2
		}else if (rec == 3){
			message = message + "3,"; // Multicast
		}

	}

	// 0 = not running  | 1 = running

	public void setGameState(int state) {
		if(state != 0 && state != 1) {

		}
		message = message + Integer.toString(state) + ",";
	}

	// flag to distinguish when server talks about connection conf.
	// 1 = talk for connection | 0 = not


	//0000 | 0001 | 0010 | 0011 | 0100 | 0101 | 0110 | 0111 | 1000 | 1001 | 1010 | 1011 | 1100  (1-12) 

	public void setPuzzle(int puzzle) {

		switch(puzzle) {
			case 0:
				message = message + "0,"; //setup
				break;
			case 1:
				message = message + "1,"; //Morse
				break;
			case 2:
				message = message + "2,"; //Laptop
				break;
			case 3:
				message = message + "3,"; //Light
				break;
			case 4:
				message = message + "4,"; //research
				break;
			case 5:
				message = message + "5,"; // equation
				break;
			case 6:
				message = message + "6,"; //NFC
				break;
			case 7:
				message = message + "7,"; // MAP
				break;
			case 8:
				message = message + "8,"; // caesar
				break;
			case 9:
				message = message + "9,"; // Finished
				break;
			case 10:
				message = message + "A,"; // HINT
				break;
			case 11:
				message = message + "B,"; // HINT
				break;
		}

	}


	public String getCompleteMessage() {
		return this.message;
	}



}
