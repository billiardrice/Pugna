package dev.wrice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Pugna.Map.Territories.Territory;
import Pugna.Troops.Troop;
import javafx.scene.shape.Polygon;

public class PolyTerrain extends Polygon {

	private String id;
	private String region;
	private List<PolyTerrain> connections;
	private Territory territory;
	
	public PolyTerrain() {
		territory = new Territory(null, null, null, null, null);
	}

	/**
	 * @return String return the id
	 */
	public String getTId() {
		return id;
	}

	public Map<Integer, String> getPointMap() {

		HashMap<Integer, String> points = new HashMap<>();

		for (int i = 0; i < this.getPoints().size(); i+=2) {
			points.put(i, this.getPoints().get(i).toString());
			points.put(i+1, this.getPoints().get(i+1).toString());
		}

		return points;

	}

	/**
	 * @param id the id to set
	 */
	public void setTId(String id) {
		this.id = id;
		territory.setName(id);
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
	public List<PolyTerrain> getConnections() {
		return connections;
	}

	/**
	 * @param connections the connections to set
	 */
	public void setConnections(List<PolyTerrain> connections) {
		this.connections = connections;
	}

	public List<Troop> getTroops() {
		return territory.getTroops();
	}

	public List<Troop> reinforce() {
		return territory.reinforce();
	}

	public Integer getReinforcements() {
		return territory.getReinforcements();
	}

	public void setReinforcements(int reinforcements) {
		territory.setReinforcements(reinforcements);
	}

}
