package jp.motlof.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Controller implements Initializable {

	@FXML
	TextArea chat;
	@FXML
	TextField message, address, port, id;
	@FXML
	Button connect, send;
	
	public static Controller instance;
	private static Socket socket = new Socket();
	private static BufferedReader recive;
	private static PrintWriter sender;
	private boolean nowconnect = false;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		message.setDisable(true);
		send.setDisable(true);
	}
	
	@FXML
	public void onConnect(ActionEvent event){
		try {
			if(nowconnect) {
				socket.close();
				chat.appendText("切断しました\r\n");
				connect.setText("接続");
				address.setDisable(false);
				port.setDisable(false);
				message.setDisable(true);
				send.setDisable(true);
				nowconnect = false;
				return;
			}
			socket = new Socket();
			if(address.getText() == null || address.getText().isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("エラー");
				alert.setContentText("ちゃんとアドレスを入力してください");
				alert.show();
				return;
			} else if(port.getText() == null || port.getText().isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("エラー");
				alert.setContentText("ちゃんとポート番号を入力してください");
				alert.show();
				return;
			}
			InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(address.getText()), Integer.valueOf(port.getText()).intValue());
			socket.connect(socketAddress, 1000);
			recive = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sender = new PrintWriter(socket.getOutputStream(), true);
			Reciver reciver = new Reciver(recive, chat);
			reciver.start();
			chat.appendText("接続されました\r\n");
			connect.setText("切断");
			address.setDisable(true);
			port.setDisable(true);
			message.setDisable(false);
			send.setDisable(false);
			nowconnect = true;
		} catch (SocketTimeoutException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("接続失敗");
			alert.setContentText("接続がタイムアウトしました");
			alert.show();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("エラー");
			alert.setContentText("ポートには数字を入力してください");
			alert.show();
		}
	}
	
	@FXML
	public void onSend(ActionEvent event) {
		if(message.getText() == null || message.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("エラー");
			alert.setContentText("ちゃんと文字列を入力してください");
			alert.show();
			return;
		}
		sender.println(message.getText());
		chat.appendText("[me]: "+message.getText()+"\r\n");
		message.setText("");
	}
	
	@FXML
	public void onChange(ActionEvent event) {
		if(id.getText() == null || id.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("エラー");
			alert.setContentText("ちゃんと文字列を入力してください");
			alert.show();
			return;
		} else if(id.getText().length() > 10) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("エラー");
			alert.setContentText("10文字以内でお願いします");
			alert.show();
		}
		sender.println("%name "+id.getText());
		id.setText("");
	}
	
	@FXML
	public void onEnter(KeyEvent event) {
		if(event.getCode().equals(KeyCode.ENTER)) {
			if(message.getText() == null || message.getText().isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("エラー");
				alert.setContentText("ちゃんと文字列を入力してください");
				alert.show();
				return;
			}
			sender.println(message.getText());
			chat.appendText("[me]: "+message.getText()+"\r\n");
			message.setText("");
		}
	}
	
	public static void close() {
		try {
			if(socket != null && !socket.isClosed())
				socket.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnected() {
		chat.appendText("切断しました\r\n");
		connect.setText("接続");
		address.setDisable(false);
		port.setDisable(false);
		message.setDisable(true);
		send.setDisable(true);
		nowconnect = false;
	}
}
