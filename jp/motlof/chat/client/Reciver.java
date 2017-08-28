package jp.motlof.chat.client;

import java.io.BufferedReader;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

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
				if(line.startsWith("%shutdown")) {
					Controller.instance.disconnected();
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
