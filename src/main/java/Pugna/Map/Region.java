package Pugna.Map;

import javafx.scene.paint.Color;

public class Region {
	
	private String name;
	private Color color;

	public Region(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String toString() {
		return name;
	}

}
