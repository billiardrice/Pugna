package dev.wrice;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

	private static Scene scene;
	public static Stage stage;

	@Override
	public void start(Stage stage) throws IOException {
		scene = new Scene(loadFXML("mainMenu"), 1080, 720);
		stage.setScene(scene);
		stage.setTitle("Pugna");
		App.stage = stage;
		stage.show();
	}

	public static void resize() {
		stage.setResizable(false);
		stage.setWidth(1080);
		stage.setHeight(720);
	}

	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	public static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void main(String[] args) {
		launch();
	}

}