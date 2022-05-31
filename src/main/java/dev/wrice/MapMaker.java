package dev.wrice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
// import java.util.logging.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import Pugna.Map.GameMap;
import Pugna.Map.Region;
import Pugna.Map.Territories.Territory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MapMaker implements Initializable {

	private ArrayList<Line> polyLines = new ArrayList<>();
	private ArrayList<Arrow> connLines = new ArrayList<>();
	private ArrayList<Point2D> polyPoints = new ArrayList<>();
	private Stack<Shape> undoBuffer = new Stack<>();
	private boolean polyMaking = false;
	private Point2D pan = new Point2D(0, 0);
	private Boolean panning = false;
	private Arrow connectLine = null;
	private PolyTerrain start = null;
	private Map<PolyTerrain, LinkedList<PolyTerrain>> cons = new HashMap<>();

	private ArrayList<PolyTerrain> polys = new ArrayList<>();
	private Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	enum Mode {
		DRAW,
		PAN,
		POLYGONS,
		SLICE,
		CONNECT
	}

	private Mode curMode = Mode.DRAW;

	@FXML
	private Pane mapPane;

	@FXML
	private RadioButton panTool;
	@FXML
	private RadioButton drawTool;
	@FXML
	private RadioButton sliceTool;
	@FXML
	private RadioButton polyTool;
	@FXML
	private RadioButton connectTool;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		LOGGER.log(Level.INFO, "Creating Grid");
		createGrid(0.75, 20);
		LOGGER.log(Level.INFO, "Creating Handlers");
		createHandlers();
	}

	public void createHandlers() {
		Parent scene = mapPane.getParent();
		scene.addEventHandler(KeyEvent.KEY_PRESSED, keyBoard -> {
			if (keyBoard.getCode() == KeyCode.BACK_SPACE) {
				if (polyLines.size() > 0) {
					mapPane.getChildren().remove(polyLines.get(polyLines.size() - 1));
					undoBuffer.add(polyLines.get(polyLines.size() - 1));
					polyLines.remove(polyLines.size() - 1);
					polyPoints.remove(polyPoints.size() - 1);
				} else {
					try {
						mapPane.getChildren().remove(polys.get(polys.size() - 1));
						undoBuffer.add(polys.get(polys.size() - 1));
						polys.remove(polys.size() - 1);
					} catch (Exception e) {
					}
				}
			}
			if (keyBoard.getCode() == KeyCode.Z) {
				try {
					LOGGER.log(Level.INFO, "Undid Delete");
					Shape s = undoBuffer.pop();

					if (s.getClass() == new PolyTerrain().getClass()) {
						polys.add((PolyTerrain) s);
						mapPane.getChildren().add(s);
						s.toBack();
					}
					if (s.getClass() == new Line().getClass()) {
						Line last = polyLines.get(polyLines.size() - 1);
						last.setEndX(((Line) s).getStartX());
						last.setEndY(((Line) s).getStartY());
						polyLines.add((Line) s);
						polyPoints.add(new Point2D(((Line) s).getStartX(), ((Line) s).getStartY()));
						mapPane.getChildren().add(s);
						s.toBack();
					}

				} catch (Exception e) {
				}
			}
		});
		scene.addEventFilter(MouseEvent.ANY, mouseEvent -> {
			if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED && curMode == Mode.PAN) {
				pan = new Point2D(mouseEvent.getX(), mouseEvent.getY());
				panning = true;
			}
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && panning) {
				mapPane.setTranslateX(mapPane.getTranslateX() - (pan.getX() - mouseEvent.getX()));
				mapPane.setTranslateY(mapPane.getTranslateY() - (pan.getY() - mouseEvent.getY()));
				pan = new Point2D(mouseEvent.getX(), mouseEvent.getY());
			}
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && panning) {
				panning = false;
			}
		});
		scene.addEventHandler(ScrollEvent.ANY, scroll -> {

			if (mapPane.getScaleX() + scroll.getDeltaY() / 40 > 0.0 && curMode == Mode.PAN) {
				mapPane.setScaleX(mapPane.getScaleX() + scroll.getDeltaY() / 40);
				mapPane.setScaleY(mapPane.getScaleY() + scroll.getDeltaY() / 40);
			}

		});
		mapPane.addEventHandler(MouseEvent.MOUSE_PRESSED, click -> {
			List<Node> temp = new LinkedList<>();
			for (Node n : ((Pane) scene).getChildren()) {
				try {
					if (n.getId().equals("polyMenu")) {
						temp.add(n);
					}
				} catch (Exception e) {
				}
			}
			for (Node n : temp) {
				((Pane) scene).getChildren().remove(n);
			}
		});
		mapPane.addEventHandler(MouseEvent.MOUSE_MOVED, moved -> {
			if (connectLine != null) {
				connectLine.setEndX(moved.getX());
				connectLine.setEndY(moved.getY());
			}
		});
	}

	public void createGrid(Double radius, int spacing) {

		for (int y = spacing / 2; y * Math.sqrt(3) <= 1500; y += (spacing / 2)) {
			for (double x = 0.5 + ((y % spacing) / (double) spacing); x * spacing <= 1500; x++) {
				Circle point = new Circle(x * spacing, (y * Math.sqrt(3)), radius, Color.BLACK);
				Circle collider = new Circle(x * spacing, (y * Math.sqrt(3)), spacing / 1.9, new Color(1, 0, 0,
						0.0));
				collider.addEventHandler(MouseEvent.ANY, event -> {
					switch (curMode) {
						case DRAW:
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
								newLine(new Point2D(point.getCenterX(), point.getCenterY()));
							}
							break;
						default:
							break;
					}

				});
				mapPane.getChildren().add(point);
				mapPane.getChildren().add(collider);
			}
		}

	}

	public void newLine(Point2D pos) {

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
			mapPane.getChildren().add(l);
			l.toBack();
		} else {

			if (polyPoints.indexOf(pos) == 0 && polyLines.size() >= 3) {
				polyMaking = false;
				Line last = polyLines.get(polyLines.size() - 1);
				last.setEndX(pos.getX());
				last.setEndY(pos.getY());
				LOGGER.log(Level.INFO, "Creating Polygon");
				PolyTerrain poly = new PolyTerrain();
				poly.setFill(new Color(0, 0, 0, 0.1));
				poly.setStroke(Color.BLACK);
				poly.setStrokeWidth(5);
				poly.setStrokeLineJoin(StrokeLineJoin.ROUND);
				poly.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					switch (curMode) {
						case POLYGONS:
							try {
								LOGGER.log(Level.INFO, "Opening Polygon Config");
								Pane polyMenu = (Pane) App.loadFXML("polyMenu");
								((Pane) mapPane.getParent()).getChildren().add(polyMenu);
								polyMenu.setVisible(true);
								polyMenu.setTranslateY(100);
								for (Node n : ((VBox) polyMenu.getChildren().get(0)).getChildren()) {
									try {
										if (n.getId().equals("terrainName")) {
											((TextField) n).setText(poly.getTId());
											n.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
												poly.setTId(((TextField) n).getText());
											});
										} else if (n.getId().equals("regionName")) {
											((TextField) n).setText(poly.getRegion());
											n.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
												poly.setRegion(((TextField) n).getText());
											});
										} else if (n.getId().equals("reinforcements")) {
											try {
												((TextField) n).setText(Integer.toString(poly.getReinforcements()));
											} catch (Exception e) {
											}
											n.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
												poly.setReinforcements(Integer.parseInt(((TextField) n).getText()));
											});
										}
									} catch (Exception e) {
									}
								}
							} catch (IOException e) {
								LOGGER.log(Level.WARNING, "Error Creating Polygon Config");
							}
							break;
						case DRAW:
							break;
						case PAN:
							break;
						case SLICE:
							undoBuffer.add(poly);
							mapPane.getChildren().remove(poly);
							break;
						case CONNECT:
							LOGGER.log(Level.INFO, "Connecting Polygon");

							Map<Integer, LinkedList<Double>> p = new HashMap<>();
							p.put(0, new LinkedList<>());
							p.put(1, new LinkedList<>());

							for (int i = 0; i < poly.getPoints().size(); i++) {
								p.get(i % 2).add(poly.getPoints().get(i));
							}

							Double xes = 0.0;
							Double yes = 0.0;
							for (Double d : p.get(0)) {
								xes += d;
							}

							for (Double d : p.get(1)) {
								yes += d;
							}

							xes /= p.get(0).size();
							yes /= p.get(1).size();

							if (connectLine == null) {
								connectLine = new Arrow();
								connectLine.setStartX(xes);
								connectLine.setStartY(yes);
								connectLine.setEndX(pos.getX());
								connectLine.setEndX(pos.getY());
								start = poly;
								mapPane.getChildren().add(connectLine);
								connectLine.toBack();
							} else if (start != poly) {
								if (!cons.keySet().contains(start)) {
									cons.put(start, new LinkedList<>());
								}
								connLines.add(connectLine);
								cons.get(start).add(poly);
								connectLine.setEndX(xes);
								connectLine.setEndY(yes);
								start = null;
								connectLine = null;
							}

							break;
					}
				});
				for (Point2D p : polyPoints) {
					poly.getPoints().addAll(new Double[] { p.getX(), p.getY() });
				}
				mapPane.getChildren().add(poly);
				polys.add(poly);
				poly.toBack();
				mapPane.getChildren().removeAll(polyLines);
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
				mapPane.getChildren().add(l);
				l.toBack();
			}

		}

	}

	@FXML
	private void changeTool(ActionEvent event) {
		if (event.getSource() == panTool) {
			curMode = Mode.PAN;
		} else if (event.getSource() == drawTool) {
			curMode = Mode.DRAW;
			for (Polygon p : polys) {
				p.toBack();
			}
		} else if (event.getSource() == polyTool) {
			curMode = Mode.POLYGONS;
			for (Polygon p : polys) {
				p.toFront();
			}
			// polyMenu.setVisible(true);
		} else if (event.getSource() == sliceTool) {
			curMode = Mode.SLICE;
		} else if (event.getSource() == connectTool) {
			curMode = Mode.CONNECT;
			for (Polygon p : polys) {
				p.toFront();
			}
		}

	}

	@FXML
	private void export() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(App.stage);

		GameMap g = new GameMap();

		Map<String, Region> r = new HashMap<>();
		Map<String, Territory> t = new HashMap<>();

		int color = 0;
		Color[] colors = new Color[]{Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE, Color.CORAL};
		for (PolyTerrain p : polys) {
			r.put(p.getRegion(), new Region(p.getRegion(), colors[color++%5]));
		}

		for (PolyTerrain p : polys) {
			t.put(p.getTId(), new Territory(p.getTId(), r.get(p.getRegion()), true, p.getPointMap(), p.getReinforcements()));
		}

		for (Region reg : r.values()) {
			g.addRegion(reg);
		}

		for (Territory ter : t.values()) {
			g.addTerritory(ter);
		}

		for (PolyTerrain p : cons.keySet()) {
			for (PolyTerrain d : cons.get(p)) {
				g.addConnection(t.get(p.getTId()), t.get(d.getTId()));
			}
		}

		try {
			System.out.println(selectedFile.getCanonicalPath());
			FileWriter myWriter = new FileWriter(selectedFile.getCanonicalPath());
			myWriter.write(g.toJSON());
			myWriter.close();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Could not export to file");
		}
	}

	@FXML
	public void exit() throws IOException {
		App.setRoot("mainMenu");
	}

	@FXML
	public void importFile() {

		return;

	}

}