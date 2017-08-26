package GUI;


import java.util.Scanner;

import integracao.IntegracaoHaskell;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.shape.Rectangle;
import preset.Preset;
import preset.PresetFileRepository;
import javafx.scene.paint.Color;

public class MatrizJogoController {
	private EventHandler<MouseEvent> cellControl = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			round.setText("0");
			double x = event.getSceneX();
			double y = event.getSceneY();
			int[] coordpoint = coordSceneToPoint(x, y);
			setPreset(coordpoint[0], coordpoint[1]);
			lastSelectedPoint = coordpoint;
		}
	};
	@FXML AnchorPane pane;
	private String sourceCode = "\\jogodavida.hs";
	private String presetsFile = "\\presets.dat";
	private int x, y;
	private int defX = 10, defY = 10;
	private Rectangle[] hor, vert; //espaco
	private Rectangle[][] cellsMatrix;
	private int[] lastSelectedPoint;
	private double cellW, cellH;
	private double space;
	private double height, width;
	private ToolBar toolbar;
	private Button next, reset, limpar, newPresetButton, remPresetButton;
	private final Preset defaultPreset = new Preset("Cel. Unica", "0 0");
	private PresetFileRepository presets = new PresetFileRepository(presetsFile, defaultPreset);
//	private Label displayX, displayY;
	private Label round = new Label("0");
	private TextField skip = new TextField(), sizeX = new TextField(), sizeY = new TextField();
	private ComboBox<Preset> presetsBox;
	public MatrizJogoController(double width, double height) {
		sourceCode = System.getProperty("user.dir")+sourceCode;
		this.x = defX;
		this.y = defY;
		this.height = height - 37;
		this.width = width;
		space = 10 / Math.sqrt(x*y);
	}
	private void init_ToolBar() {
		EventHandler<KeyEvent> numericChar =new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				try {
					Integer.parseInt(event.getCharacter());
				}catch(NumberFormatException e) {
					event.consume();
				}
			}
		}; 
		skip.setPromptText("Pular");
		skip.setPrefWidth(50);
		skip.setOnKeyTyped(numericChar);
		
		sizeX.setText(x+"");
		sizeX.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("changed");
			};
		});
		sizeX.setPrefWidth(50);
		sizeX.setOnKeyTyped(numericChar);
		
		sizeY.setText(y+"");
		sizeY.setPrefWidth(50);
		sizeY.setOnKeyTyped(numericChar);
		
/*		displayX = new Label("XX");
		displayY = new Label("YY");
*/
		
		reset = new Button("Reset");
		reset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					x = Integer.parseInt(sizeX.getText());
					y = Integer.parseInt(sizeY.getText());
					pane.getChildren().removeAll(hor);
					pane.getChildren().removeAll(vert);
					for (Rectangle[] r : cellsMatrix) {
						pane.getChildren().removeAll(r);
					}
					
					initMatrix();
				} catch(NumberFormatException e) {
					
				}
			}
		});
		
		next = new Button("Avançar");
		next.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				nextRound();
			};
		});
		limpar = new Button("Limpar");
		limpar.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clear();
			}
		});
		presetsBox = new ComboBox<Preset>((ObservableList<Preset>) presets.getPresets());
		presetsBox.setValue(defaultPreset);
		
		newPresetButton = new Button("Salvar");
		newPresetButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				newPreset();
			}
		});
		remPresetButton = new Button("Remover");
		remPresetButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				removePreset();
			}
		});
		
		toolbar = new ToolBar(skip, next, /*displayX, displayY,*/new Label("Geração"), round, new Label("Tamanho"), sizeX, new Label("x"), sizeY, reset, limpar, presetsBox, newPresetButton, remPresetButton);
		toolbar.setPrefSize(width+space, 37);
		toolbar.setLayoutX(0);
		toolbar.setLayoutY(height+space);
	}
	private void initMatrix() {
		setCellW();
		setCellH();
		initSpacers(cellControl);
		cellsMatrix = new Rectangle[y][x];
		round.setText("0");
	}
	@FXML private void initialize () {
		System.out.println("source "+sourceCode);
		
		init_ToolBar();
		pane.getChildren().add(toolbar);
		pane.setPrefSize(width, height);
		pane.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
		pane.setOnMousePressed(cellControl);
		pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double x = event.getSceneX();
				double y = event.getSceneY();
				int[] coordpoint = coordSceneToPoint(x, y);
				if (lastSelectedPoint== null || (lastSelectedPoint[0] != coordpoint[0] || lastSelectedPoint[1] != coordpoint[1])) {
					manageCell(coordpoint[0], coordpoint[1]);
					lastSelectedPoint = coordpoint;
				}
			}
		});
/*
		pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				displayX.setText( event.getSceneX()+"" );
				displayY.setText( event.getSceneY()+"" );
			}
		});
*/
		initMatrix();
	}
	
	private void setCellW() {
		cellW = ( width - (x)*space) / x;
	}
	private void setCellH() {
		cellH = ( height - (y)*space) / y;
	}
	private void initSpacers (EventHandler<MouseEvent> cellControl) {
		hor = new Rectangle[y];
		vert = new Rectangle[x];
		int c;
		for (c = 0; c < hor.length; c++) {
			hor[c] = new Rectangle(width, space, Color.BLACK);
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
			vert[c] = new Rectangle(space, height, Color.BLACK);
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
				newCell(x,y);
			else
				deleteCell(x, y);
			}catch (ArrayIndexOutOfBoundsException e) {}
	}
	private boolean isPopulated (int x, int y) {
		return cellsMatrix[y][x] != null;
	}
	private void clear() {
		int i,j;
		for (i = 0; i < y; i++) {
			for(j = 0; j < x; j++) {
				if (isPopulated(j, i)) {
					deleteCell(j, i);
				}
			}
		}
		round.setText("0");
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
	private String formatFromHaskell (String s) {
		return s.replace(',', ' ').replaceAll("[\\[\\(\\)\\]]", "");
	}
	private void nextRound () {
		long totalTime = System.currentTimeMillis();
		String s = formatCoord();
		int skip = this.skip.getText().equals("") ? 1 : Integer.parseInt(this.skip.getText());
		System.out.println("skip "+s+ " "+ skip);
		long HaskellTime = 0;
		try {
			HaskellTime = System.currentTimeMillis();
			s = IntegracaoHaskell.writeArg(sourceCode, s, skip+"");
			HaskellTime = System.currentTimeMillis() - HaskellTime;
		}catch (Exception e) {e.printStackTrace();}
		System.out.println(s);
		s = formatFromHaskell(s);
		Scanner scanner = new Scanner(s);
		while (scanner.hasNextInt()) {
			manageCell(scanner.nextInt(), scanner.nextInt());
		}
		scanner.close();
		round.setText( (Integer.parseInt(round.getText()) + skip)+ "" );
		totalTime = System.currentTimeMillis() - totalTime;
		System.out.println("Haskell time: "+ (double)HaskellTime / 1000 + "s Total time: "+ (double)totalTime/1000+"s");
	}
	private void setPreset(int mouseX, int mouseY) {
		Preset presets = presetsBox.getValue() != null ? presetsBox.getValue() : defaultPreset;
		for (int[] p : presets.getPontos()) {
			int x = mouseX + p[0], y = mouseY + p[1];
			manageCell(x, y);
		}
	}
	private void newPreset() {
		String s = formatCoord();
		s = formatFromHaskell(s);
		if (s.length() < 3) { // Formato " 'Inteiro', ' ', 'Inteiro' "
			Alert a = new Alert(AlertType.ERROR);
			a.setContentText("O Preset não pode ser vazio");
			a.showAndWait();
			return;
		}
		TextInputDialog t = new TextInputDialog();
		t.setContentText("Nome do Preset");
		String nome = t.showAndWait().orElse(null);
		if (nome == null) return;
		Preset pres = new Preset(nome, s);
		int[] pivo = pres.getPontos().get(0);
		int pivoX = pivo[0], pivoY = pivo[1];
		for (int[] p : pres.getPontos()) {
			p[0] -= pivoX;
			p[1] -= pivoY;
		}
		presets.add(pres);
	}
	private void removePreset() {
		Preset remove = presetsBox.getValue();
		if (remove != defaultPreset)
			presetsBox.getItems().remove(remove);
	}
}