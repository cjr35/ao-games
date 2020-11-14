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
	}

	private static int parsePort(String port) {
		return Integer.parseInt(port);
	}
}
