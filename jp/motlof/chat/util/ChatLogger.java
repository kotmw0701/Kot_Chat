package jp.motlof.chat.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import jp.motlof.chat.server.ChatUser;
import jp.motlof.chat.server.EchoThread;
import jp.motlof.chat.server.Main;

public class ChatLogger {
	
	private static ChatLogger instance = new ChatLogger();
	private String path;
	private File logfile;
	private int num;
	private String pattern = "%num%	%time%	%uuid%	%name%	%text%";
	
	private ChatLogger() {
		Calendar cal = new GregorianCalendar();
		String date = cal.get(Calendar.YEAR)
				+ "-" + (cal.get(Calendar.MONTH) + 1)
				+ "-" + cal.get(Calendar.DAY_OF_MONTH);
		path = ".\\logs\\"+date+".log";
		logfile = new File(path);
		if(!new File(".\\logs").exists()) new File(".\\logs").mkdir();
	}
	
	public static ChatLogger getInstance() {
		return instance;
	}
	
	public void addChatLog(String name, String text, boolean add) {
		try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logfile, true),"UTF-8"))){
			Calendar cal = new GregorianCalendar();
			String time = cal.get(Calendar.HOUR_OF_DAY)+":"
					+(String.valueOf(cal.get(Calendar.MINUTE)).length()<2 ? "0"+cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE))+":"
					+(String.valueOf(cal.get(Calendar.SECOND)).length()<2 ? "0"+cal.get(Calendar.SECOND) : cal.get(Calendar.SECOND));
			String convert = pattern;
			writer.println(convert.replaceAll("%num%", String.valueOf((add ? ++num : "")))
					.replaceAll("%time%", time)
					.replaceAll("%name%", name)
					.replaceAll("%uuid%", (getUser(name) == null ? UUID.fromString("0000000-0000-0000-0000-000000000000") : getUser(name).getUuid()).toString())
					.replaceAll("%text%", text));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ChatUser getUser(String name) {
		for(EchoThread thread : Main.clients) {
			if(name == thread.getUser().getName())
				return thread.getUser();
		}
		return null;
	}
	
	public int getNum() {
		return num;
	}
}
