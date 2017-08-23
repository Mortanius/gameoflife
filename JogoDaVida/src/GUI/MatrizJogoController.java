package GUI;


import java.util.Scanner;

import Integracao.IntegracaoHaskell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class MatrizJogoController {
	@FXML Rectangle r;
	@FXML AnchorPane pane;
	private String sourceCode = "\\jogodavida.hs";
	private Scene scene;
	private int x, y;
	private Rectangle[] hor, vert; //espaco
	private Rectangle[][] cellsMatrix;
	private double cellW, cellH;
	private double space;
	private double height, width;
	private ToolBar toolbar;
	private Button button;
	private Label displayX, displayY;
	private Label round = new Label("0");
	private TextField skip = new TextField();
	public MatrizJogoController(int x, int y, double width, double height) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		space = 10 / Math.sqrt(x*y);
	}
	@FXML void initialize () {
		displayX = new Label("XX");
		displayY = new Label("YY");
		displayX.setLayoutX(10);
		displayX.setLayoutY(10);
		displayY.setLayoutX(20);
		displayY.setLayoutY(20);
		
		sourceCode = System.getProperty("user.dir")+sourceCode;
		System.out.println("source "+sourceCode);
		round.setLayoutX(30);
		button = new Button("Next");
		button.setLayoutX(0);
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				nextRound();
			};
		});
		toolbar = new ToolBar(button, displayX, displayY, round);
		toolbar.setPrefSize(width, 10);
		toolbar.setLayoutX(0);
		toolbar.setLayoutY(height-toolbar.getHeight());
		pane.getChildren().addAll(toolbar);
		pane.setPrefSize(width, height);
		
		pane.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
		//pane.getChildren().add(button);
		EventHandler<MouseEvent> cellControl = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				String roundRestart = "0";
				if (!round.getText().equals(roundRestart)) round.setText(roundRestart);
				double x = event.getSceneX();
				double y = event.getSceneY();
				int[] coordpoint = coordSceneToPoint(x, y);
				manageCell(coordpoint[0], coordpoint[1]);
			}
		};
		pane.setOnMousePressed(cellControl);
		pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				displayX.setText( event.getSceneX()+"" );
				displayY.setText( event.getSceneY()+"" );
			}
		});
		setCellW();
		setCellH();
		initSpacers(cellControl);
		cellsMatrix = new Rectangle[y][x];
	}
	
	private void setCellW() {
		cellW = ( width - (x)*space) / x;
	}
	private void setCellH() {
		cellH = ( height - (y)*space) / y;
	}
	private void initSpacers (EventHandler<MouseEvent> cellControl) {
		hor = new Rectangle[y-1];
		vert = new Rectangle[x-1];
		int c;
		for (c = 0; c < hor.length; c++) {
			hor[c] = new Rectangle(width, space, Color.WHITE);
			Rectangle r = hor[c];
			r.setX(0);
			r.setY((c+1)* (space+cellH));
			r.setOnDragDetected(event -> {
				r.startFullDrag();
			});
			r.setOnMouseDragEntered(cellControl);
			pane.getChildren().add(r);
		}
		for (c = 0; c < vert.length; c++) {
			vert[c] = new Rectangle(space, height, Color.WHITE);
			Rectangle r = vert[c];
			r.setX((c+1)* (space+cellW));
			r.setY(0);
			r.setOnDragDetected(event -> {
				r.startFullDrag();
			});
			r.setOnMouseDragEntered(cellControl);
			pane.getChildren().add(r);
		}
	}
	private int[] coordSceneToPoint (double x, double y) {
		int[] coordpoint = new int[2];
		coordpoint[0] = (int)(x / (cellW + space));
		coordpoint[1] = (int)(y / (cellH + space));
		return coordpoint;
	}
	private double[] coordPointToScene (int x, int y) {
		double[] coordsc = new double[2];
		coordsc[0] = space+ x * (cellW + space);
		coordsc[1] = space+ y * (cellH + space);
		return coordsc;
	}
	private void newCell(int x, int y) {
		double[] coordsc = coordPointToScene(x, y);
		cellsMatrix[y][x] = new Rectangle(coordsc[0], coordsc[1], cellW, cellH);
		cellsMatrix[y][x].setFill(Color.YELLOW);
		pane.getChildren().add(cellsMatrix[y][x]);
	}
	private void deleteCell(int x, int y) {
		pane.getChildren().remove(cellsMatrix[y][x]);
		cellsMatrix[y][x] = null;
	}
	private void manageCell(int x, int y) {
		try {
			if ( !isPopulated(x, y) )
				newCell(x, y);
			else
				deleteCell(x, y);
			}catch (ArrayIndexOutOfBoundsException e) {}
	}
	private boolean isPopulated (int x, int y) {
		return cellsMatrix[y][x] != null;
	}
	private String formatCoord () {
		int[][] coordList = new int[y*x][2];
		int last = 0;
		int i, j;
		for (i=0; i < y; i++) {
			for (j = 0; j < x; j++) {
				if (cellsMatrix[i][j] != null) {
					coordList[last] = coordSceneToPoint(cellsMatrix[i][j].getX(), cellsMatrix[i][j].getY());
					last++;
				}
			}
		}
		String formatted = "[";
		for (i=0; i < last; i++) {
			formatted = formatted + "("+ coordList[i][0]+","+ coordList[i][1]+ ")"+ (i < last-1 ? "," : "");
		}
		return formatted + "]";
	}
	private void nextRound () {
		long totalTime = System.currentTimeMillis();
		String s = formatCoord();
		System.out.println("nextRound "+s);
		long HaskellTime = 0;
		try {
			HaskellTime = System.currentTimeMillis();
			s = IntegracaoHaskell.writeArg(sourceCode, s);
			HaskellTime = System.currentTimeMillis() - HaskellTime;
		}catch (Exception e) {e.printStackTrace();}
		System.out.println(s);
		s = s.replaceAll("[\\[\\(\\,\\)\\]]", " ");
		Scanner scanner = new Scanner(s);
		while (scanner.hasNextInt()) {
			manageCell(scanner.nextInt(), scanner.nextInt());
		}
		scanner.close();
		round.setText( (Integer.parseInt(round.getText()) + 1) +"" );
		totalTime = System.currentTimeMillis() - totalTime;
		System.out.println("Haskell load time: "+ (double)HaskellTime / 1000 + " Total time: "+ (double)totalTime/1000);
	}
	@FXML void drag () {
		
	}
}