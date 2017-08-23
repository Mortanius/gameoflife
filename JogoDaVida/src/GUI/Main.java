package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;



public class Main extends Application {
	private int x=500, y=500;
	@Override
	public void start(Stage primaryStage) {
		try {
			Rectangle2D b = Screen.getPrimary().getVisualBounds();
			double height = 600;
			double width = 600;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MatrizJogo.fxml"));
			MatrizJogoController controller = new MatrizJogoController(x, y, width, height);
			loader.setController(controller);
			AnchorPane root = (AnchorPane)loader.load();
			Scene scene = new Scene(root,width,height);
			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.setMaximized(false);
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
