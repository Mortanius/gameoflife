package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			double height = 640;
			double width = 600;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MatrizJogo.fxml"));
			MatrizJogoController controller = new MatrizJogoController(width, height);
			loader.setController(controller);
			AnchorPane root = (AnchorPane)loader.load();
			Scene scene = new Scene(root,width,height);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Jogo da Vida");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
