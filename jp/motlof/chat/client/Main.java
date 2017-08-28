package jp.motlof.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	
	public static void main(String... args) {
		System.out.println("Started");
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
