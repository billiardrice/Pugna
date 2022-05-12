package dev.wrice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import Pugna.Map.GameMap;
import dev.wrice.terrainMaker.Mode;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class terrainMaker {

	private static ArrayList<Line> polyLines = new ArrayList<>();
	private static ArrayList<Point2D> polyPoints = new ArrayList<>();
	private static Stack<Shape> undoBuffer = new Stack<>();
	private static boolean polyMaking = false;
	private static Point2D pan = new Point2D(0, 0);
	private static Boolean panning = false;

	private static ArrayList<Polygon> polys = new ArrayList<>();

	private static Mode curMode = Mode.CREATE;

	enum Mode {
		CREATE,
		EDIT
	}

	public static Scene createScene() {
		Group group = new Group();

		VBox terrainMaker = new VBox();
		HBox terrainTools = new HBox();
		HBox terrainViewer = new HBox();
		terrainViewer.getChildren().add(group);
		terrainMaker.getChildren().add(terrainTools);
		terrainMaker.getChildren().add(terrainViewer);

		Scene scene = new Scene(terrainMaker, 1080, 720, Color.GRAY);
		scene.getStylesheets().add("Stylesheet.css");

		int spacing = 15;
		double radius = 1;
		createUI(scene, terrainTools, terrainViewer);
		createGrid(scene, group, radius, spacing);
		createHandlers(scene, group);

		return scene;
	}

	public static GameMap createMap() {
		try {

			Scanner json = new Scanner(new File("filename.json"));

			String temp = "";
			while (json.hasNextLine()) {
				temp += json.nextLine().replaceAll("\t|\n", "");
			}
			JSONObject jsonObject = (JSONObject) JSONValue.parse(temp);
			json.close();

			GameMap g = new GameMap(jsonObject);

			return g;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void createUI(Scene scene, HBox toolBox, HBox contentBox) {
		Button modeButton = new Button();
		modeButton.setText("Edit");
		modeButton.setOnMouseClicked(clickEvent -> {
			switch (curMode) {
				case CREATE:
					modeButton.setText("Create");
					for (Polygon p : polys) {
						p.toFront();
					}
					curMode = Mode.EDIT;
					break;
				case EDIT:
					modeButton.setText("Edit");
					for (Polygon p : polys) {
						p.toBack();
					}
					curMode = Mode.CREATE;
					break;
			}
		});

		toolBox.getChildren().add(modeButton);

	}

	public static void createHandlers(Scene scene, Group group) {
		scene.addEventHandler(KeyEvent.KEY_PRESSED, keyBoard -> {
			if (keyBoard.getCode() == KeyCode.BACK_SPACE) {
				if (polyLines.size() > 0) {
					group.getChildren().remove(polyLines.get(polyLines.size() - 1));
					undoBuffer.add(polyLines.get(polyLines.size() - 1));
					polyLines.remove(polyLines.size() - 1);
					polyPoints.remove(polyPoints.size() - 1);
				} else {
					try {
						group.getChildren().remove(polys.get(polys.size() - 1));
						undoBuffer.add(polys.get(polys.size() - 1));
						polys.remove(polys.size() - 1);
					} catch (Exception e) {
					}
				}
			}
			if (keyBoard.getCode() == KeyCode.Z) {
				try {
					Shape s = undoBuffer.pop();

					if (s.getClass() == new Polygon().getClass()) {
						polys.add((Polygon) s);
						group.getChildren().add(s);
						s.toBack();
					}
					if (s.getClass() == new Line().getClass()) {
						Line last = polyLines.get(polyLines.size() - 1);
						last.setEndX(((Line) s).getStartX());
						last.setEndY(((Line) s).getStartY());
						polyLines.add((Line) s);
						polyPoints.add(new Point2D(((Line) s).getStartX(), ((Line) s).getStartY()));
						group.getChildren().add(s);
						s.toBack();
					}

				} catch (Exception e) {
				}
			}
		});
		scene.addEventFilter(MouseEvent.ANY, mouseEvent -> {
			if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED && curMode == Mode.EDIT) {
				pan = new Point2D(mouseEvent.getX(), mouseEvent.getY());
				panning = true;
			}
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && panning) {
				group.setTranslateX(group.getTranslateX() - (pan.getX() - mouseEvent.getX()));
				group.setTranslateY(group.getTranslateY() - (pan.getY() - mouseEvent.getY()));
				pan = new Point2D(mouseEvent.getX(), mouseEvent.getY());
			}
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && panning) {
				panning = false;
			}
		});
		scene.addEventHandler(ScrollEvent.ANY, scroll -> {

			if (group.getScaleX() + scroll.getDeltaY() / 40 > 0 && curMode == Mode.EDIT) {
				group.setScaleX(group.getScaleX() + scroll.getDeltaY() / 40);
				group.setScaleY(group.getScaleY() + scroll.getDeltaY() / 40);
			}

		});
	}

	public static void createGrid(Scene scene, Group group, Double radius, int spacing) {

		for (int y = 0; y * Math.sqrt(3) <= scene.getHeight(); y += (spacing / 2)) {
			for (double x = 0 + ((y % spacing) / (double) spacing); x * spacing <= scene.getWidth(); x++) {
				Circle point = new Circle(x * spacing, (y * Math.sqrt(3)), radius, Color.BLACK);
				Circle collider = new Circle(x * spacing, (y * Math.sqrt(3)), spacing / 1.9, new Color(1, 0, 0,
						0.0));
				collider.addEventHandler(MouseEvent.ANY, event -> {
					switch (curMode) {
						case CREATE:
							if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
								point.setFill(Color.RED);
								try {
									polyLines.get(polyLines.size() - 1).setEndX(collider.getCenterX());
									polyLines.get(polyLines.size() - 1).setEndY(collider.getCenterY());
								} catch (Exception e) {
								}
							} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
								point.setFill(Color.BLACK);
							} else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
								newLine(scene, group, new Point2D(point.getCenterX(), point.getCenterY()));
							}
							break;
						case EDIT:
							break;
					}

				});
				group.getChildren().add(point);
				group.getChildren().add(collider);
			}
		}

	}

	public static void newLine(Scene scene, Group group, Point2D pos) {

		if (polyLines.size() == 0) {
			polyPoints.clear();
			polyMaking = false;
		}

		if (!polyMaking) {
			polyMaking = true;
			Line l = new Line(pos.getX(), pos.getY(), pos.getX(), pos.getY());
			l.setStroke(Color.RED);
			l.setStrokeWidth(5);
			l.setStrokeLineCap(StrokeLineCap.ROUND);
			polyLines.add(l);
			polyPoints.add(pos);
			group.getChildren().add(l);
			l.toBack();
		} else {

			if (polyPoints.indexOf(pos) == 0 && polyLines.size() >= 3) {
				polyMaking = false;
				Line last = polyLines.get(polyLines.size() - 1);
				last.setEndX(pos.getX());
				last.setEndY(pos.getY());
				Polygon poly = new Polygon();
				poly.setFill(new Color(0, 0, 0, 0.1));
				poly.setStroke(Color.BLACK);
				poly.setStrokeWidth(5);
				poly.setStrokeLineJoin(StrokeLineJoin.ROUND);
				poly.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					switch (curMode) {
						case EDIT:
							final Stage dialog = new Stage();
							dialog.initModality(Modality.APPLICATION_MODAL);
							// dialog.initOwner(stage);
							VBox dialogVbox = new VBox(20);
							TextField territoryName = new TextField();
							territoryName.addEventHandler(KeyEvent.KEY_PRESSED, enter -> {
								if (enter.getCode() == KeyCode.ENTER) {
									System.out.println(territoryName.getText());
								}
							});
							Button submit = new Button("Submit");
							submit.setOnMouseClicked(clickEvent -> {
								System.out.println(territoryName.getText());
								dialog.close();
							});
							dialogVbox.getChildren().add(territoryName);
							dialogVbox.getChildren().add(submit);
							Scene dialogScene = new Scene(dialogVbox, 300, 200);
							dialog.setScene(dialogScene);
							dialog.show();
							break;
						case CREATE:
							break;
					}
				});
				for (Point2D p : polyPoints) {
					poly.getPoints().addAll(new Double[] { p.getX(), p.getY() });
				}
				group.getChildren().add(poly);
				polys.add(poly);
				poly.toBack();
				group.getChildren().removeAll(polyLines);
				polyLines.clear();
				polyPoints.clear();
			} else if (polyPoints.contains(pos)) {
			} else {
				Line last = polyLines.get(polyLines.size() - 1);
				last.setEndX(pos.getX());
				last.setEndY(pos.getY());
				Line l = new Line(pos.getX(), pos.getY(), pos.getX(), pos.getY());
				l.setStroke(Color.RED);
				l.setStrokeLineCap(StrokeLineCap.ROUND);
				l.setStrokeWidth(5);
				polyLines.add(l);
				polyPoints.add(pos);
				group.getChildren().add(l);
				l.toBack();
			}

		}

	}

}
