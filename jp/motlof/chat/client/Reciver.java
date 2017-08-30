package jp.motlof.chat.client;

import java.io.BufferedReader;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import jp.motlof.chat.util.Command;

public class Reciver extends Thread {
	BufferedReader in;
	TextArea out;
	
	private boolean run = true;
	
	public Reciver(BufferedReader in, TextArea out) {
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		String line;
		try {
			while(run) {
				line = in.readLine();
				if(Command.getProperty(line, "shutdown") != null) {
					Controller.instance.disconnected(line.split(" ")[1]);
					continue;
				}
				final String text = line;
				Platform.runLater(() -> {
					out.appendText(text+"\r\n");
				});
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
