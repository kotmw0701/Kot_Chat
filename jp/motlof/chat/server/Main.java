package jp.motlof.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {
	
	static final int ECHO_PORT = 25566;
	
	public static String version;
	public static List<EchoThread> clients = new ArrayList<>();
	
	public static void main(String... args) throws IOException {
		System.setProperty("file.encoding", "UTF-8");
		Properties properties = new Properties();
		InputStream inputStream;
		try {
			inputStream = Main.class.getClassLoader().getResourceAsStream("serversys.ini");
			properties.load(inputStream);
			inputStream.close();
			version = properties.getProperty("version", "1.0.0");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServerSocket serverSocket = null;
		/*Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					Main.systemsendAll("サーバーがシャットダウンしました。", false);
					Main.systemsendAll("%shutdown", true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});*/
		try {
			serverSocket = new ServerSocket(ECHO_PORT);
			System.out.println("鯖が立ち上がったで (port="+serverSocket.getLocalPort()+")");
			while(true) {
				Socket socket = serverSocket.accept();
				EchoThread client = new EchoThread(socket);
				if(client.isClosed())
					continue;
				client.start();
				clients.add(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(serverSocket != null) serverSocket.close();
		}
	}
}
