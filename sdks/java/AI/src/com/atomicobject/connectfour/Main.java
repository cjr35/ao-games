package com.atomicobject.connectfour;
import java.net.Socket;

public class Main {

	public static void main(String[] args) {
		String ip = args.length > 0 ? args[0] : "127.0.0.1";
		int port = args.length > 1 ? parsePort(args[1]) : 1337;
		try {
			System.out.println("Connecting to " + ip + " at " + port);
			Socket socket = new Socket(ip, port);
			new Client(socket).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		AI ai = new AI();
//		ai.setPlayer(1);
//		GameState gs = new GameState();
////		int[][] board = {
////				{ 1, 2, 1, 0, 0, 2, 1 },
////				{ 2, 1, 2, 2, 1, 1, 2 },
////				{ 1, 2, 1, 2, 1, 2, 1 },
////				{ 1, 2, 1, 2, 1, 2, 1 },
////				{ 2, 1, 2, 1, 2, 1, 2 },
////				{ 2, 1, 2, 1, 2, 1, 2 }
////		};
//		int[][] board = {
//				{ 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 2, 1, 0, 0 },
//				{ 1, 2, 1, 2, 1, 2, 1 },
//				{ 1, 2, 1, 2, 1, 2, 1 },
//				{ 2, 1, 2, 1, 2, 1, 2 },
//				{ 2, 1, 2, 1, 2, 1, 2 }
//		};
//		gs.setBoard(board);
//		gs.setPlayer(1);
//		System.out.println("AI chose: " + ai.computeMove(gs));
//		ai.printData();
	}

	private static int parsePort(String port) {
		return Integer.parseInt(port);
	}
}
