package jp.motlof.chat.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	
	public static String version;
	
	public static void main(String... args) {
		System.setProperty("file.encoding", "UTF-8");
		Properties properties = new Properties();
		InputStream inputStream;
		try {
			inputStream = Main.class.getClassLoader().getResourceAsStream("clientsys.ini");
			properties.load(inputStream);
			inputStream.close();
			version = properties.getProperty("version", "-1");
		} catch (IOException e) {
			e.printStackTrace();
		}
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("チャット");
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(FXMLLoader.load(ClassLoader.getSystemResource("Main.fxml"))));
		primaryStage.show();
		primaryStage.setOnCloseRequest(Event -> {
			Controller.close();
		});
	}
}
