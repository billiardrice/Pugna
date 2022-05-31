package dev.wrice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Pugna.Map.Territories.Territory;
import Pugna.Troops.Troop;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PolyTerrain extends Polygon {

	private String id;
	private String region;
	private List<PolyTerrain> connections;
	private Territory territory;
	private Text label;
	
	public PolyTerrain() {
		territory = new Territory(null, null, null, null, null);
	}
	public PolyTerrain(Territory t) {
		territory = t;
	}

	public Text addLabel() {
		label = new Text(this.id + "\n" +Integer.toString(territory.getTroops().size()));

		System.out.println(this.getPoints());

		double minX = this.getPoints().get(0);
		double maxX = 0;
		double minY = this.getPoints().get(1);
		double maxY = 0;
		for (int i = 0; i < this.getPoints().size(); i+=2) {
			if (this.getPoints().get(i) < minX) {
				minX = this.getPoints().get(i);
			}
			else if (this.getPoints().get(i) > maxX) {
				maxX = this.getPoints().get(i);
			}
			if (this.getPoints().get(i+1) < minY) {
				minY = this.getPoints().get(i+1);
			}
			else if (this.getPoints().get(i+1) > maxY) {
				maxY = this.getPoints().get(i+1);
			}
		}

		label.setTranslateX((minX+maxX)/2-20);
		label.setTranslateY((minY+maxY)/2);

		label.setFont(new Font("Fira Code Regular", 20));

		return label;
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

	public Territory getTerritory() {
		return territory;
	}

	public List<Troop> reinforce() {
		List<Troop> temp = territory.reinforce();
		label.setText(this.id + "\n" + territory.getTroops().size());
		return temp;
	}

	public Integer getReinforcements() {
		return territory.getReinforcements();
	}

	public void setReinforcements(int reinforcements) {
		territory.setReinforcements(reinforcements);
	}

	public void addTroop(Troop t) {
		territory.addTroop(t);
		label.setText(this.id + "\n" + territory.getTroops().size());
	}

	public void updateLable() {
		label.setText(this.id + "\n" + territory.getTroops().size());
	}

}
