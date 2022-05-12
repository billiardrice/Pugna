package dev.wrice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.shape.Polygon;

public class polyTerrain extends Polygon {

	private String id;
	private String region;
	private List<polyTerrain> connections;

	/**
	 * @return String return the id
	 */
	public String getTId() {
		return id;
	}

	public Map<String, String> getPointMap() {

		HashMap<String, String> points = new HashMap<>();

		for (int i = 0; i < this.getPoints().size(); i+=2) {
			points.put(this.getPoints().get(i).toString(), this.getPoints().get(i+1).toString());
		}

		return points;

	}

	/**
	 * @param id the id to set
	 */
	public void setTId(String id) {
		this.id = id;
	}

	/**
	 * @return String return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return List<polyTerrain> return the connections
	 */
	public List<polyTerrain> getConnections() {
		return connections;
	}

	/**
	 * @param connections the connections to set
	 */
	public void setConnections(List<polyTerrain> connections) {
		this.connections = connections;
	}

}
