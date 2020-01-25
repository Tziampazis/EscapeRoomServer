package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientHandler extends Thread {

	Socket clientSocket;
	private BufferedReader in;
	//private static PrintWriter out;
	private GameHandler game;
	private String numberofPl ="-1" ;
	private GameServer gameServer;
	private boolean flagWait = false;
	private boolean connection = true;
	private PrintWriter out;
	public double[] loc1 = {1 , 2};
	public double[] loc2 = {3 , 4};
	public boolean locCheck1 = false;
	public boolean locCheck2 = false;
	public int count =0;
	boolean run = true;
	boolean flag = false;

	boolean music = false;

	public Music mp3;
	public Music infoscreen;
	public Music victory;
	public Music wronginput;
	public Music nextpuzzle;
	public Music applauding;
	public Music hint;


	public ClientHandler(Socket clientSock,String numpl,GameServer gmServer) {
		clientSocket = clientSock;
		this.numberofPl = numpl;
		this.gameServer = gmServer;

		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream() ,true );
		} catch (IOException e) {
			System.out.println("ThreadClientHandler On Read/Write Init");
		}
		this.game = new GameHandler(out,this);
		System.out.println("GAMEHAND OBJECT " + this.game.getId());
		this.game.start();

		mp3 = new Music("background_merged.mp3");
		infoscreen = new Music("info_screen.mp3");
		wronginput = new Music("wrong_input.mp3");
		victory = new Music("victory.mp3");
		nextpuzzle = new Music("next_puzzle.mp3");
		applauding = new Music("applauding.mp3");
		hint = new Music("hint_unlocked.mp3");
	}


	@Override
	public void run() {
		//Handshake
		this.game.hello();//send hello with 3,0,0,
		System.out.println("SENT HELLO MSG");
		try {
			readingClient();
			try {
				this.in.close();
				this.out.close();
				this.game.stop();
			} catch (IOException e) {
				System.out.println("closing bufferSockets");
			}
			this.gameServer.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	public void readingClient() throws InterruptedException {
		System.out.println("IN READING CLIENT");
		try {
			while(run) {
				String msg = in.readLine();
				if (this.gameServer.getFlagCon() && this.count == 0 && this.numberofPl.equals("2")) {
					System.out.println();
					this.count++;
					this.gameServer.startTimer();
				}
				if (msg != null && !msg.equalsIgnoreCase("0,0,9,ACK")) {
					//synchronized (this.in) {


						if (!msg.equalsIgnoreCase("0,1,B,")) {

							//System.out.println("READING NEW " + this.getId());
							AnswerScanner ans = new AnswerScanner(msg);
							if (this.connection) {
								connectionSetup(msg);
								if (this.flagWait) {
									while (flagWait) {
										sleep(3000);
										System.out.println("WAITING FOR OTHER PL " + this.numberofPl);
										if (this.gameServer.getFlagCon()) {
											System.out.println("UNLOCKING REST GAME " + this.numberofPl);
											this.connection = false;
											this.flagWait = false;
											this.game.startGame();
										}
									}
								}
							} else if (this.gameServer.getFlagCon()) {
								System.out.println(" THIS MSG " + msg + " " + this.numberofPl);
								//										System.out.println("REST OF THE GAME " + this.getId());
								//										System.out.println("MSG " + msg+" " + this.getId());
								if (!music && this.numberofPl.equals("1")) {
									mp3.play();
									System.out.println("BACKGROUND MUSIC STARTED");
									music = true;
								}
								gameRunning(msg, ans);
							}
						} else {
							this.gameServer.sendNoisePen();
							this.gameServer.penalty();
						}

				}else{

					System.out.println("GOT 0,0,9,");
					sleep(20000);
					this.run = false;
				}
			}
			//}7ttt
		} catch (IOException e) {
			System.out.println("ThreadClientHandler On Read");
		}

	}


	public void gameRunning(String msg,AnswerScanner ans) throws InterruptedException, IOException {

		if(msg.equals("0,1,0,ACK")) {
			this.game.gameStarted();
			System.out.println("GAME STARTED!");
		}else if(msg.equalsIgnoreCase("0,1,1,ACK")) {
			this.gameServer.setMorseFlag(this.numberofPl);
			while(!gameServer.getMorseFlag()) { // Until both flags are true
				sleep(2000);
			}
			if(this.numberofPl.equals("1")) {
				this.gameServer.getCompHandler().setMoorseFlag(true);; // ACTIVATE LIGHT MORSE CODE by making flag true in the THREAD ComponentHandler
				System.out.println("PLAYER ONE GOT OUT");
			}
			boolean temp= true;
			while(temp) {
				String tempmsg ="0,0,0,0";
				if(in.ready()) {
					tempmsg = in.readLine();
				}
				if(!tempmsg.equalsIgnoreCase("0,0,9,ACK")) {

					if (this.numberofPl.equals("1")) {
						if (this.gameServer.getCompHandler().isMorseCompFlag()) {
							//sleep(1000);
							//activate morse code only by player 1
							this.game.laptopPuzzle();
							nextpuzzle.play();
							System.out.println("NEXTPUZZLE MUSIC STARTED");
							temp = false;
						}

					}
					if (this.numberofPl.equals("2")) {
						if (this.gameServer.getCompHandler().isMorseCompFlag()) {
							//	sleep(10000);
							this.game.laptopPuzzle();
							temp = false;
						}

					}
					if (tempmsg.equalsIgnoreCase("0,1,B,")) {
						this.gameServer.sendNoisePen();
						this.gameServer.penalty();
					}
					if (tempmsg.equals("0,1,A,") && !this.gameServer.getOneHint()) {
						hint();
						this.gameServer.setOneHint(true);
						System.out.println(" GOT A HIIIIIINNT REQUEST MADAFAKA");
					}

				}else{

					System.out.println("GOT 0,0,9,ACK");
					sleep(20000);
					temp=false;
					this.run = false;
				}
			}
			this.gameServer.setHintflag1(false);
			this.gameServer.setHintflag2(false);
			this.gameServer.setOneHint(false);



		}else if(msg.equalsIgnoreCase("0,1,2,ACK")) {
			System.out.println("LAPTOP PUZZLE " + this.numberofPl);

//			if(this.numberofPl.equals("1")) {
//				nextpuzzle.play();
//				System.out.println("NEXTPUZZLE MUSIC STARTED");
//			}

			boolean temp= true;
			while(temp) {
				String tempmsg ="0,0,0,0";
				if(in.ready()) {
					tempmsg = in.readLine();
				}
				System.out.println("2nd P msg "+ tempmsg);
				if(!tempmsg.equalsIgnoreCase("0,0,9,ACK")) {
					AnswerScanner s = new AnswerScanner(tempmsg);
					System.out.println("WAITING IN VAIN!! " + this.numberofPl);
					sleep(2000);
					if(this.gameServer.getLetterFlag()) {
						System.out.println("AM IN " + this.numberofPl);
						this.game.checkedLetter();
						temp =false;
					}
					if(s.getRecepient().equals("0") && s.getGamesState().equals("1") && s.getPuzzlesCode().equals("2") && !s.getlastMsg().equalsIgnoreCase("ACK")) {
						System.out.println(" LETTER IS :" + s.getlastMsg());
						if(s.checkLetter(s.getlastMsg())){
							//System.out.println("AM IN LETTER SEND " + this.numberofPl);
							this.gameServer.setLetterFlag();
							System.out.println("Correct LETTER " + this.numberofPl + " LETTER FLAG : " + this.gameServer.getLetterFlag());
							this.game.checkedLetter();//if letter is correct // code : 313
							nextpuzzle.play();
							temp = false;
						}else{
							System.out.println("NOT CORRECT LETTER " + this.numberofPl );
							this.game.sendLetterWrong();//if letter is wrong
							wronginput.play();
						}
					}
					if(tempmsg.equalsIgnoreCase("0,1,B,")) {
						this.gameServer.sendNoisePen();
						this.gameServer.penalty();
					}
					if(tempmsg.equals("0,1,A,")&& !this.gameServer.getOneHint()) {
						hint();
						this.gameServer.setOneHint(true);
					}


				}else{
					System.out.println("GOT 0,0,9,ACK");
					sleep(20000);
					temp=false;
					this.run = false;
				}

			}
			this.gameServer.setHintflag1(false);
			this.gameServer.setHintflag2(false);
			this.gameServer.setOneHint(false);
			//Sending Letter
		}else if(msg.equalsIgnoreCase("0,1,3,ACK")) {

			if(this.numberofPl.equals("1")) {
				nextpuzzle.play();
				System.out.println("NEXTPUZZLE MUSIC STARTED");
			}

			System.out.println("RESEARCH PUZZLE " + this.numberofPl);
			this.gameServer.setLightFlag(this.numberofPl);

			while(!gameServer.getLightFlag()) { // Until both flags are true
				sleep(2000);
			}
			if(this.numberofPl.equals("1")) {
				this.gameServer.getCompHandler().setLightFlag(true); // ACTIVATE LIGHT SENSORS
			}


			boolean temp = true;
			while(temp) {
				String tempmsg ="0,0,0,0";
				if(in.ready()) {
					tempmsg = in.readLine();
				}
				if(!tempmsg.equalsIgnoreCase("0,0,9,ACK")) {

					if (this.numberofPl.equals("1")) {
						if (this.gameServer.getCompHandler().isLightSensor()) {
							//sleep(1000);
							//activate morse code only by player 1
							this.game.researchPuzzle();
							temp = false;

							infoscreen.play();
							System.out.println("INFOSCREEN MUSIC STARTED");
						}


					}
					if (this.numberofPl.equals("2")) {
						if (this.gameServer.getCompHandler().isLightSensor()) {
							//	sleep(10000);
							this.game.researchPuzzle();
							temp = false;
						}

					}
					if (tempmsg.equalsIgnoreCase("0,1,B,")) {
						this.gameServer.sendNoisePen();
						this.gameServer.penalty();
					}
					if (tempmsg.equals("0,1,A,") && !this.gameServer.getOneHint()) {
						hint();
						this.gameServer.setOneHint(true);
					}
					if (tempmsg.equalsIgnoreCase("0,0,9,")) {
						temp = false;
						this.run = false;
					}

				}else{

					System.out.println("GOT 0,0,9,ACK");
					sleep(20000);
					temp=false;
					this.run = false;

				}
			}
			this.gameServer.setHintflag1(false);
			this.gameServer.setHintflag2(false);
			this.gameServer.setOneHint(false);


		}else if(msg.equalsIgnoreCase("0,1,4,ACK")) {

			if(this.numberofPl.equals("1")) {
				nextpuzzle.play();
				System.out.println("NEXTPUZZLE MUSIC STARTED");
			}


			System.out.println("GETTING BPM FOR EQUATION PUZZLE");
			this.gameServer.setPulseFlag(this.numberofPl);

			while(!gameServer.getPulseFlag()) { // Until both flags are true
				sleep(2000);
			}
			if(this.numberofPl.equals("1")) {
				this.gameServer.getCompHandler().setHeartFlag(true); // ACTIVATE LIGHT SENSORS
			}

			boolean temp = true;
			while(temp) {
				String tempmsg ="0,0,0,0";
				if(in.ready()) {
					tempmsg = in.readLine();
				}

				if(!tempmsg.equalsIgnoreCase("0,0,9,ACK")) {

					if (this.numberofPl.equals("1")) {
						if (this.gameServer.getCompHandler().isPulseFlag()) {
							//sleep(10000);
							//activate light code only by player 1
							this.game.sendBPM("120");
							temp = false;
						}

					}
					if (this.numberofPl.equals("2")) {
						if (this.gameServer.getCompHandler().isPulseFlag()) {
							//sleep(10000);
							this.game.sendBPM("120");
							temp = false;
							infoscreen.play();
							System.out.println("INFOSCREEN MUSIC STARTED");

						}

					}
					if (tempmsg.equalsIgnoreCase("0,1,B,")) {
						this.gameServer.sendNoisePen();
						this.gameServer.penalty();
					}
					if (tempmsg.equals("0,1,A,") && !this.gameServer.getOneHint()) {
						hint();
						this.gameServer.setOneHint(true);
					}

				}else{
					System.out.println("GOT 0,0,9,ACK");
					sleep(20000);
					temp=false;
					this.run = false;
				}
			}
			this.gameServer.setHintflag1(false);
			this.gameServer.setHintflag2(false);
			this.gameServer.setOneHint(false);

		}else if(msg.equalsIgnoreCase("0,1,5,ACK")) {

			if(this.numberofPl.equals("1")) {
				nextpuzzle.play();
				System.out.println("NEXTPUZZLE MUSIC STARTED");
			}


			System.out.println("EQUATION PUZZLE ");
			System.out.println("ACK for BPM " + this.numberofPl);
			boolean temp= true;
			while(temp) {
				String tempmsg ="0,0,0,0";
				if(in.ready()) {
					tempmsg = in.readLine();
				}
				if(!tempmsg.equalsIgnoreCase("0,0,9,ACK")) {

					AnswerScanner s = new AnswerScanner(tempmsg);
					sleep(2000);
					System.out.println("IN EQUAT MSG" + tempmsg + " pl " + this.numberofPl);
					//				if(this.gameServer.getEquation()) {
					//					System.out.println("AM IN EQuation " + this.numberofPl);
					//					//this.game.nfcTagStart();
					//					this.game.mapPuzzle();
					//					temp =false;
					//				}

					if(flag){

						temp = false;

					}
					if (s.getRecepient().equals("0") && s.getGamesState().equals("1") && s.getPuzzlesCode().equals("5") && s.getlastMsg().equals("6")) {
						//if(s.checkIfEquation(s.getlastMsg())){
						//System.out.println("AM IN LETTER SEND " + this.numberofPl);
						//	this.gameServer.setEquation();
						System.out.println("Correct Equation " + this.numberofPl);
						//this.game.nfcTagStart();//if letter is correct // code : 313
							this.gameServer.sendMapPuzzle();
							nextpuzzle.play();
							temp = false;

						//					}else{
						//						System.out.println("NOT CORRECT Equation " + this.numberofPl );
						//						this.game.wrongEqResult();//if letter is wrong
						//						wronginput.play();
						//						System.out.println("WRONG MUSIC STARTED");
						//					}
					}
					if (tempmsg.equalsIgnoreCase("0,1,B,")) {
						this.gameServer.sendNoisePen();
						this.gameServer.penalty();
					}
					if (tempmsg.equals("0,1,A,") && !this.gameServer.getOneHint()) {
						hint();
						this.gameServer.setOneHint(true);
					}
				}else{
					System.out.println("GOT 0,0,9,ACK");
					sleep(20000);
					temp=false;
					this.run = false;
				}
			}
			this.gameServer.setHintflag1(false);
			this.gameServer.setHintflag2(false);
			this.gameServer.setOneHint(false);

			//}else if(msg.equalsIgnoreCase("0,1,6,ACK")) {
			//	System.out.println("ACK FOR NFC PUZZLE START");
//		}else if(ans.getRecepient().equals("0") && ans.getGamesState().equals("1") && ans.getPuzzlesCode().equals("6") && !ans.getlastMsg().equalsIgnoreCase("ACK")) {
//			System.out.println("SCANNED QR");
//			System.out.println("START MAP PUZZLE");
//			this.game.mapPuzzle();//start received 0,1,6,[nfc tag num]
		}else if(msg.equalsIgnoreCase("0,1,7,ACK")){

			if(this.numberofPl.equals("1")) {
				nextpuzzle.play();
				System.out.println("NEXTPUZZLE MUSIC STARTED");
			}


			System.out.println("ACK for MAP");
		}else if(ans.getRecepient().equals("0") && ans.getGamesState().equals("1") && ans.getPuzzlesCode().equals("7") && !ans.getlastMsg().equalsIgnoreCase("")) {
			boolean fl1 = true;//TODO : implement hint
			boolean fl2 = true;
			System.out.println("LOCATION CHECK");
			String[] ls = ans.getlastMsg().split(";");
			double[] lsd = {Double.parseDouble(ls[0]),Double.parseDouble(ls[1])};

			if(checkLoc03(lsd[0], lsd[1]) && !locCheck1 && !this.gameServer.isLoc1()) {
				this.gameServer.setLoc1(true);
				this.locCheck1 =true;
				this.locCheck2 =true;
				this.gameServer.setLocation(this.numberofPl);
				this.game.correctLocation(this.numberofPl);
				while(fl1) {
					sleep(3000);
					if (this.gameServer.locationFlag()) { // till both players flags are true
						System.out.println("BOTH LOCATIONS VERIFIED " + this.numberofPl);
						fl1 = false;
						this.game.startCaesar();//if both correct
						infoscreen.play();
						System.out.println("INFOSCREEN MUSIC STARTED");
					}
				}
			}else if(checkLoc36(lsd[0], lsd[1]) && !locCheck2 && !this.gameServer.isLoc2()) {
				this.gameServer.setLoc2(true);
				this.locCheck2 = true;
				this.locCheck1 = true;
				this.gameServer.setLocation(this.numberofPl);
				this.game.correctLocation(this.numberofPl);
				while(fl2) {
					sleep(3000);
					if(this.gameServer.locationFlag()) { // till both players flags are true
						System.out.println("BOTH LOCATIONS VERIFIED " + this.numberofPl);
						fl2=false;
						this.game.startCaesar();//if both correct
					}
				}
			}else {//if location is wrong
				System.out.println("ONE LOCATION WRONG " + this.numberofPl);
				this.game.wrongLocation(this.numberofPl); // send to specific player that his coordinates are wrong
				System.out.println("WRONG POS NUM " + this.numberofPl);
				wronginput.play();
				System.out.println("WRONG MUSIC STARTED");
			}


//			if(lsd[0] == this.loc1[0] && lsd[1] == this.loc1[1] || lsd[0] == this.loc2[0] && lsd[1] == this.loc2[1] ) {//if location is correct FIXED FOR BOTH PLAYERS
//				this.gameServer.checkLocation(numberofPl, lsd);
//				while(fl) {
//					sleep(3000);
//					if(this.gameServer.locationFlag()) { // till both players flags are true
//						System.out.println("BOTH LOCATIONS VERIFIED " + this.numberofPl);
//						fl=false;
//						this.game.startCaesar();//if both correct
//					}
//				}
			//}else {//if location is wrong
//				System.out.println("ONE LOCATION WRONG " + this.numberofPl);
//				this.game.wrongLocation(this.numberofPl); // send to specific player that his coordinates are wrong
//			}
		}else if(msg.equalsIgnoreCase("0,1,8,ACK")) {

			if(this.numberofPl.equals("1")) {
				nextpuzzle.play();
				System.out.println("NEXTPUZZLE MUSIC STARTED");
			}


			boolean temp= true;
			while(temp) {
				String tempmsg ="0,0,0,0";
				if(in.ready()) {
					tempmsg = in.readLine();
				}

				if(!tempmsg.equalsIgnoreCase("0,0,9,ACK")) {

					AnswerScanner s = new AnswerScanner(tempmsg);
					sleep(2000);
					//System.out.println(" CAME IN WITH " + tempmsg);
					if (this.gameServer.getCaesar()) {
						System.out.println("AM IN Caesar " + this.numberofPl);
						this.game.checkedCaesar(this.gameServer.getTimeServer());
						temp = false;
					}
					if (s.getRecepient().equals("0") && s.getGamesState().equals("1") && s.getPuzzlesCode().equals("8") && !s.getlastMsg().equals("ACK")) {
						if (s.checkIfCaesar(s.getlastMsg())) {
							//System.out.println("AM IN LETTER SEND " + this.numberofPl);
							this.gameServer.setCaesar();
							System.out.println("Correct Caesar " + this.numberofPl);
							this.game.checkedCaesar(this.gameServer.getTimeServer());//if letter is correct // code : 313
							temp = false;
						} else {
							System.out.println("NOT CORRECT Caesar " + this.numberofPl);
							this.game.wrongCaesar();//if letter is wrong
							wronginput.play();
							System.out.println("WRONG MUSIC STARTED");
						}
					}
					if (tempmsg.equalsIgnoreCase("0,1,B,")) {
						this.gameServer.sendNoisePen();
						this.gameServer.penalty();
					}
					if (tempmsg.equals("0,1,A,") && !this.gameServer.getOneHint()) {
						hint();
						this.gameServer.setOneHint(true);
					}
				}else{
					System.out.println("GOT 0,0,9,ACK");
					sleep(20000);
					temp=false;
					this.run = false;
				}
			}
			this.gameServer.setHintflag1(false);
			this.gameServer.setHintflag2(false);
			this.gameServer.setOneHint(false);

		}else if(msg.equalsIgnoreCase("0,1,9,ACK")) {
			System.out.println("SERVER END OF GAME");
			this.gameServer.getTimerObj().endTimer();

			this.run = false;
			if(this.numberofPl.equals("1")) {
				mp3.close();
				System.out.println("BACKGROUND MUSIC STOPPED");
				victory.play();
				System.out.println("VICTORY MUSIC STARTED");
				applauding.play();
				System.out.println("APPLAUDING MUSIC STARTED");
			}
			sleep(20000);
		}else if(msg.equals("0,1,A,") && !this.gameServer.getOneHint()) {
			hint();
//			this.gameServer.setHintflag1(false);
//			this.gameServer.setHintflag2(false);
//			this.gameServer.setOneHint(false);
		}


	}


	//Setup Connection of players
	public void connectionSetup(String msg) throws InterruptedException {
		//	System.out.println("IN CONN METH "+ msg + " CLH " + this.numberofPl);
		if(msg.equalsIgnoreCase("0,0,0,ACK")) {
			System.out.println("CLIENT SAYS "+  this.getId() + " : " + "is Connected with code 300 " + " NUM PL " + 	this.numberofPl);
			this.game.playerIndication(this.numberofPl); // send the players number to the player
		}else if(msg.equals("0,0,0,"+ (this.numberofPl))){ //Ack that player got its number
			//Nothing to do there
			//gameServer.setFlag(true,this.numberofPl);
			System.out.println("Set flag for player : " + this.numberofPl);
		}else if(msg.equals("0,0,0,")) { //Request from Player to Start -> Start Only if both players are in
			System.out.println("Checking if game can start");
			//if(gameServer.isGameReadytoStart()) { //check both flags
			gameServer.setFlagCon(this.numberofPl);
			sleep(2000);
			System.out.println("Game ready to start " + this.numberofPl);
			synchronized (this.gameServer) {
				if(!this.gameServer.getFlagCon()) {
					flagWait = true;
				}else {
					System.out.println("TRUE BOTH FLAGS");
					this.connection = false;
					game.startGame();
				}
			}
		}
	}


	public void hint() {
		if(this.numberofPl.equals("1")) {
			this.gameServer.setHintflag1(true);
			if(!this.gameServer.isHintflag2()) {
				this.gameServer.getHint();
				System.out.println("REQUEST HINT CATCHED " + this.numberofPl);
			}

		}
		if(this.numberofPl.equals("2")) {
			this.gameServer.setHintflag2(true);
			if(!this.gameServer.isHintflag1()) {
				this.gameServer.getHint();
				System.out.println("REQUEST HINT CATCHED " + this.numberofPl );
			}

		}
		System.out.println("HINT REQUESTED");
		hint.play();
		System.out.println("HINT MUSIC STARTED");



	}

	public GameHandler getGameHandler() {
		return this.game;
	}

	public boolean checkLoc03(double x ,double y) {
		if(x>=0 && x<=3 && y>=0 && y<=3){
			return true;
		}
		return false;
	}

	public boolean checkLoc36(double x ,double y) {
		if(x>=3 && x<=6 && y>=3 && y<=6){
			return true;
		}
		return false;
	}


	public boolean isRun() {
		return run;
	}


	public void setRun(boolean run) {
		this.run = run;
	}


	public void setflagTrue(){
		this.flag = true;
	}

}

