package Server;
import java.util.Scanner;

public class AnswerScanner {

	private String recepient;
	private String gamestate ;
	private String puzzlecode;
	private String msg;




	public AnswerScanner(String answer) {
		String[] all = answer.split(",");
		String rec = all[0];
		String gmS = all[1];
		String pzl = all[2];
		String msg ="";
		if(all.length>3) {
			msg = all[3];
		}




		setRecepient((rec));
		setGameState(gmS);
		setPuzzleCode(pzl);
		setMsg(msg);
	}



	public String getRecepient() {

		return this.recepient;
	}


	public String getGamesState() {
		return this.gamestate;
	}

	public String getPuzzlesCode() {
		return this.puzzlecode;
	}
	public String getlastMsg() {
		return msg;
	}





	public void setRecepient(String rec) {
		this.recepient = rec;
	}

	public void setGameState(String gam) {
		this.gamestate = gam;
	}

	public void setPuzzleCode(String puzz) {
		this.puzzlecode = puzz;
	}
	public void setMsg(String msg) {
		this.msg =msg;
	}


	public static void main(String[] args) {
		AnswerScanner a = new AnswerScanner("3,1,8,");
		System.out.println(a.getRecepient());
		System.out.println(a.getGamesState());
		System.out.println(a.getPuzzlesCode());
		System.out.println(a.getlastMsg());
	}


	public boolean checkLetter(String letter) {
		//char a = letter.charAt(0);
		if( letter.equalsIgnoreCase("C")) {
			return true;
		}
		return false;
	}

	public boolean checkIfLetter(String l) {
		//Character k = l.charAt(0);
		if(l.equalsIgnoreCase("c")) {
			return true;
		}
		return false;
	}

	public boolean checkIfCaesar(String code) {
		if(code.equalsIgnoreCase("END")) {
			return true;
		}
		return false;
	}

	public boolean checkIfEquation(String code) {
		if(code.equals("4")) {
			return true;
		}
		return false;
	}

}
