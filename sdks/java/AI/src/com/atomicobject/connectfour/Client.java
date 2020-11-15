package com.atomicobject.connectfour;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

import com.google.gson.Gson;

public class Client {

	BufferedReader input;
	OutputStreamWriter out;
	Gson gson = new Gson();
	AI ai;

	public Client(Socket socket) {
		try {
			ai = new AI();
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new OutputStreamWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		System.out.println("Starting client processing ...");
		boolean firstTurn = true;
		GameState state;
		try {
			while ((state = readStateFromServer()) != null) {
				if (firstTurn) {
					ai.setPlayer(state.getPlayer());
					ai.setTurnTimeLimit(state.getMaxTurnTime());
					firstTurn = false;
				}
				long start = System.currentTimeMillis();
				int move = ai.computeMove(state);
				long end = System.currentTimeMillis();
				System.out.println("----------------------------------------------time: " + (end - start));
				respondWithMove(move);
			}
			ai.printData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeStreams();
	}

	private GameState readStateFromServer() throws IOException {
		System.out.println("Reading from server ...");
		String nextLine = input.readLine();
		System.out.println("Read data: " + nextLine);
		if (nextLine == null) return null;
		return gson.fromJson(nextLine.trim(), GameState.class);
	}

	private void respondWithMove(int move) throws IOException {
		String encoded = gson.toJson(Map.of("column", move));
		System.out.println("Sending response: " + encoded);
		out.write(encoded);
		out.write("\n");
		out.flush();
	}

	private void closeStreams() {
		closeQuietly(input);
		closeQuietly(out);
	}

	private void closeQuietly(Closeable stream) {
		try {
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
