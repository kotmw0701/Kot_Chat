package jp.motlof.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	static final int ECHO_PORT = 25566;
	
	static List<EchoThread> clients = new ArrayList<>();
	
	public static void main(String... args) throws IOException {
		ServerSocket serverSocket = null;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					Main.systemsendAll("サーバーがシャットダウンしました。", false);
					Main.systemsendAll("%shutdown", true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		try {
			serverSocket = new ServerSocket(ECHO_PORT);
			System.out.println("鯖が立ち上がったで (port="+serverSocket.getLocalPort()+")");
			while(true) {
				Socket socket = serverSocket.accept();
				EchoThread client = new EchoThread(socket);
				client.start();
				clients.add(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(serverSocket != null) serverSocket.close();
		}
	}
	
	public static void sendAll(ChatUser author, String message) throws IOException {
		for (EchoThread echoThread : clients) {
			echoThread.send(author.getName(), message);
		}
	}
	
	public static void systemsendAll(String message, boolean command) throws IOException {
		for (EchoThread echoThread : clients) {
			echoThread.send((command ? "" : "System"), message);
		}
	}
}
