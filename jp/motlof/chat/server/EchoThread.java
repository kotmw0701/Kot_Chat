package jp.motlof.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoThread extends Thread {
	private Socket socket;
	private ChatUser user;
	private PrintWriter out;
	
	public EchoThread(Socket socket) throws IOException {
		this.socket = socket;
		this.user = new ChatUser("");
		this.out = new PrintWriter(socket.getOutputStream(), true);
		System.out.println("接続されたし "+socket.getRemoteSocketAddress().toString());
		Main.systemsendAll(user.getName()+"さんが参加しました", false);
	}
	
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while((line = in.readLine()) != null) {
				if(line.startsWith("%name")) {
					line = line.split(" ")[1];
					user.setName(line);
					continue;
				}
				System.out.println(user.getName()+ " 受信: "+line);
				Main.sendAll(user, line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null)
					socket.close();
				Main.systemsendAll(user.getName()+"さんが退出しました。", false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("切断されました "+ socket.getRemoteSocketAddress());
			Main.clients.remove(this);
		}
	}
	
	public void send(String username, String message) throws IOException {
		if(username.equalsIgnoreCase(user.getName()) || socket.isClosed())
			return;
		out.println("["+username+"]: "+ message);
	}
	
	public void sendCmd(String cmd) {
		if(socket.isClosed())
			return;
		out.println(cmd);
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
}
