package dev.wrice;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import Pugna.Map.GameMap;
import Pugna.Map.Territories.Territory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;

public class GameScreen implements Initializable {

	private Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@FXML
	Pane mapPane;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fillMap();
	}

	public void fillMap() {
		try {
			Scanner json = new Scanner(new File("imperator.json"));

			String temp = "";
			while (json.hasNextLine()) {
				temp += json.nextLine().replaceAll("\t|\n", "");
			}
			JSONObject jsonObject = (JSONObject) JSONValue.parse(temp);
			json.close();

			GameMap g = new GameMap(jsonObject);

			for (Territory t : g.getTerritories()) {

				PolyTerrain p = new PolyTerrain();

				for (int i = 0; i < t.getPoints().size(); i++) {
					p.getPoints().add(Double.parseDouble(t.getPoints().get((Object) Integer.valueOf(i).toString())));
				}
				p.setTId(t.getName());
				p.setFill(new Color(0, Math.random() / 8.0 + 0.25, 0, Math.random() / 10.0 + 0.5));
				p.setStroke(Color.BLACK);
				p.setStrokeLineJoin(StrokeLineJoin.BEVEL);
				p.setStrokeWidth(3);
				p.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					p.setFill(new Color(0, Math.random() / 10.0 + 0.25, 0, Math.random() / 10.0 + 0.5));
					LOGGER.log(Level.INFO, "Polygon " + p.getTId() + " Clicked");
				});

				mapPane.getChildren().add(p);
			}

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error Filling Map");
		}
	}

	public void exit() throws IOException {
		App.setRoot("mainMenu");
	}

}
