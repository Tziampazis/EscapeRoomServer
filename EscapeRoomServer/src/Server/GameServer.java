package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameServer {

	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static TimerTime timer;

	private static List<ClientHandler> listClients ; // inside goes the clientHandler thread and gameHandler thread
	private static ClientHandler client;
	private static int portNumber = 1234;
	private static ComponentsHandler compHandler;
	private static int plcounter =0;
	private int numOfhints =0;
	private long totalTime = 1200;


	public long timeStart =0;
	public long timeFinish;
	public boolean hintflag1 = false;
	public boolean hintflag2 = false;
	public boolean timeFlag = false;
	public boolean flagConn = false;
	public boolean flagcon1 = false;
	public boolean flagcon2 = false;
	public boolean locpl1 = false;
	public boolean locpl2 = false;
	public boolean loc1 = false;
	public boolean loc2 = false;
	public boolean locFlag= false;
	public boolean letterFlag= false;
	public boolean caesarFlag = false;
	public boolean equationFlag = false;
	public boolean morseFalg =false;
	public boolean morse1 =false;
	public boolean morse2 =false;
	public boolean lightFlag =false;
	public boolean light1 =false;
	public boolean light2 =false;
	public boolean pulseFlag =false;
	public boolean pulse1 =false;
	public boolean pulse2 =false;
	public boolean hintflag = false;
	public boolean noiseFlag = false;
	public boolean oneHint = false;

	Lock lock = new ReentrantLock();

	public Music gameOver;

	public static void main(String[] args) {
		String numbPl ="";
		GameServer s = new GameServer();
		init(s);

		//Support max 2 players
		while(plcounter<2) {
			try {
				clientSocket = serverSocket.accept();
				System.out.println("New Client Connected with num: " + plcounter );

			} catch (IOException e) {
				System.out.println("ClientSocket Problem On Create!");
			}

			numbPl =(plcounter == 0) ? "1" : "2";
			client  = new  ClientHandler(clientSocket,numbPl,s);
			System.out.println("CLIENT HD OBJECT " + client.getId());
			client.start();
			listClients.add(client);
			plcounter++;
		}
	}


	//Initialization of the Server with the component handler thread
	public static void init(GameServer s) {
		System.out.println("Initializing Server...");
		listClients = new ArrayList();
		timer = new TimerTime(s);
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println("ServerSocket Problem on Create!");
		}
		compHandler = new ComponentsHandler();
		compHandler.start();
		setCompHandler(compHandler);
		System.out.println("Complete!");
	}


	//Shutting down open socket before closing the server
	/**
	 *
	 */
	public void close() {
		System.out.println("closing everything BYEEE");
		try {
			for(ClientHandler i : listClients) {
				i.clientSocket.close();
			}
			serverSocket.close();

		} catch (IOException e) {
			System.out.println("Shutting down server problem");
		}

	}

//	public void clientsShut() {
//		boolean pl1 = true;
//		boolean pl2 = true;
//
//		if(!pl1&& !pl2) {
//			this.close();
//		}
//
//	}

	public static ComponentsHandler getCompHandler() {
		return compHandler;
	}


	public static void setCompHandler(ComponentsHandler compHandler) {
		GameServer.compHandler = compHandler;
	}


	public void setFlagCon(String num) {
		if(num.equals("1")) {
			this.flagcon1 = true;
		}else if (num.equals("2")) {
			this.flagcon2 = true;
		}
		if(this.flagcon1 && this.flagcon2) {
			this.flagConn = true;
		}
	}


	public boolean getFlagCon() {
		return this.flagConn;
	}

	public void setMorseFlag(String num) {
		if(num.equals("1")) {
			this.morse1 = true;
		}else if (num.equals("2")) {
			this.morse2 = true;
		}
		if(this.morse1 && this.morse2) {
			this.morseFalg = true;
		}
	}

	public boolean getMorseFlag() {
		return this.morseFalg;
	}

	public boolean getLightFlag() {
		return lightFlag;
	}


	public void setLightFlag(String num) {
		if(num.equals("1")) {
			this.light1 = true;
		}else if (num.equals("2")) {
			this.light2 = true;
		}
		if(this.light1 && this.light2) {
			this.lightFlag = true;
		}
	}


	public boolean getPulseFlag() {
		return pulseFlag;
	}


	public void setPulseFlag(String num) {
		if(num.equals("1")) {
			this.pulse1 = true;
		}else if (num.equals("2")) {
			this.pulse2 = true;
		}
		if(this.pulse1 && this.pulse2) {
			this.pulseFlag = true;
		}
	}


	public boolean isLoc1() {
		return loc1;
	}


	public void setLoc1(boolean loc1) {
		this.loc1 = loc1;
	}


	public boolean isLoc2() {
		return loc2;
	}


	public void setLoc2(boolean loc2) {
		this.loc2 = loc2;
	}


	public void setLocation(String name) {

		if(name.equals("1")) {
			this.locpl1 = true;
		}else {
			this.locpl2 = true;
		}


		if(this.locpl1 && this.locpl2) {
			this.locFlag = true;
		}
	}


	public void getHint() {
		synchronized (client) {
			for(ClientHandler i : listClients) {
				i.getGameHandler().sendHint();
			}
			getCompHandler().manageLight(3);
			System.out.println("BLINK WHITE FOR HINT!");
			penalty(); // so 1200 is 20 min and 1179 is 19.65 thus 35sec penalty
			this.numOfhints++;//increase the number of hints
			setNumOfhints(this.numOfhints); //set the number of hints requested
		}
	}


	public void penalty() {
		this.timer.timePenalty();
	}
	public boolean isHintflag1() {
		return hintflag1;
	}


	public void setHintflag1(boolean hintflag1) {
		this.hintflag1 = hintflag1;
	}


	public boolean isHintflag2() {
		return hintflag2;
	}


	public void setHintflag2(boolean hintflag2) {
		this.hintflag2 = hintflag2;
	}


	public int getNumOfhints() {
		return numOfhints;
	}


	public void setNumOfhints(int numOfhints) {
		this.numOfhints = numOfhints;
	}


	public boolean locationFlag() {
		return this.locFlag;
	}

	public void setLetterFlag() {
		this.letterFlag = true;
	}

	public boolean getLetterFlag() {
		return this.letterFlag;
	}

	public void setCaesar() {
		this.caesarFlag = true;
	}


	public boolean getCaesar() {
		return this.caesarFlag;
	}

	public void setEquation() {
		this.equationFlag = true;
	}

	public boolean getEquation() {
		return this.equationFlag;
	}

	public void startTimer() {
		this.timeStart = System.currentTimeMillis()/1000;
		this.timer.setStart(this.timeStart);
		this.timer.start();
	}

	public void sendNoisePen(){
		for(ClientHandler i : listClients){
			i.getGameHandler().sendB();
			System.out.println("sending noiseeee");
		}
	}

	public void stopGame() {

		listClients.get(0).mp3.close();
		System.out.println("BACKGROUND MUSIC STOPPED");
		gameOver = new Music("game_over.mp3");
		gameOver.play();
		System.out.println("GAME OVER MUSIC STARTED");
		getCompHandler().setStoplight(true);
		getCompHandler().setStopmorse(true);
		getCompHandler().setStopbpm(true);
		for(ClientHandler i : listClients) {
			System.out.println("SENDING TIME'S UP TO BOTH PLAYERS");
			i.getGameHandler().timesUP();

		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//close(); //closing the sockets
		//TODO: not waiting for ack for times up
	}

	public void sendMapPuzzle(){
		for(ClientHandler i : listClients) {
			System.out.println("SENDING MAP TO BOTH PLAYERS");
			i.getGameHandler().mapPuzzle();
			i.setflagTrue();

		}
	}

	public void setOneHint(boolean hi) {
		this.oneHint = hi;
	}

	public boolean getOneHint() {
		return this.oneHint;
	}

	public String getTimeServer() {
		return this.timer.getTime();
	}
	public TimerTime getTimerObj(){
		return this.timer;
	}
}
	
	
