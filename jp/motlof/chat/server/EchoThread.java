package jp.motlof.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;

import jp.motlof.chat.util.ChatLogger;
import jp.motlof.chat.util.Command;

public class EchoThread extends Thread {
	private Socket socket;
	private ChatUser user;
	private PrintWriter out;
	private BufferedReader in;
	
	public EchoThread(Socket socket) throws IOException {
		this.socket = socket;
		this.user = new ChatUser("", socket.getInetAddress().getHostAddress());
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String property = in.readLine();
		String name = Command.getProperty(property, "name");
		if(name != null && !name.equalsIgnoreCase("None"))
			user.setName(name);
		String version = Command.getProperty(property, "version");
		if(name == null || !version.equalsIgnoreCase(Main.version)) {
			out.println(Command.createProperty("shutdown", "バージョンが違います"));
			socket.close();
			return;
		}
		System.out.println("接続されました "+ socket.getRemoteSocketAddress());
		systemsendAll(user.getName()+"さんが参加しました", false);
	}
	
	@Override
	public void run() {
		try {
			String line;
			while((line = in.readLine()) != null) {
				if(Command.getProperty(line, "name") != null) {
					line = Command.getProperty(line, "name");
					String beforename = user.getName();
					user.setName(line);
					systemsendAll(Command.createProperty("beforename", beforename)+","+Command.createProperty("newname", user.getName()), true);
					systemsendAll(beforename+"→"+user.getName()+" に変更されました", false);
					continue;
				}
				System.out.println(user.getName()+ " 受信: "+line);
				sendAll(user, line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null)
					socket.close();
				systemsendAll(user.getName()+"さんが退出しました。", false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("切断されました "+ socket.getRemoteSocketAddress());
			Main.clients.remove(this);
		}
	}
	
	public String setText(String username, String message) {
		return (username.equalsIgnoreCase("System") ? "" : ChatLogger.getInstance().getNum())+"	["+username+"]:	"+ replaceCodes(message);
	}
	
	public void send(String text) throws IOException {
		if(!socket.isClosed())
			out.println(text);
	}
	
	public void sendAll(ChatUser author, String text) throws IOException {
		ChatLogger.getInstance().addChatLog(author.getName(), text, true);
		for (EchoThread echoThread : Main.clients)
			echoThread.send(setText(author.getName(), text));
	}
	
	public void systemsendAll(String message, boolean command) throws IOException {
		if(!command)
			ChatLogger.getInstance().addChatLog("System", message, false);
		for (EchoThread echoThread : Main.clients)
			echoThread.send(command ? message : setText("System", message));
	}
	
	public ChatUser getUser() {
		return user;
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	/*
	 * %date%
	 * %time%
	 * %name%
	 * %uuid%
	 * 
	 */
	private String replaceCodes(String text) {
		Calendar cal = new GregorianCalendar();
		String date = cal.get(Calendar.YEAR)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH);
		String time = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
		return text.replaceAll("%date%", date)
				.replaceAll("%time%", time)
				.replaceAll("%name%", user.getName())
				.replaceAll("%uuid%", user.getUuid().toString());
	}
}
