package dev.wrice;

import java.io.IOException;

import javafx.fxml.FXML;

public class MainMenu {
	
	@FXML
	public void changeToMap() throws IOException {
		App.setRoot("map");
	}

}
