package Pugna.Map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import Pugna.Map.Territories.Connection;
import Pugna.Map.Territories.Territory;

public class GameMap {

	private Map<String, Region> regions = new HashMap<>();
	private Map<String, Territory> territories = new HashMap<>();
	private Map<Territory, List<Connection>> graph = new HashMap<>();

	public GameMap() {
	}

	@SuppressWarnings("unchecked")
	public GameMap(JSONObject json) {

		for (String i : ((HashMap<String, Integer>) json.get("Regions")).keySet()) {
			regions.put(i, new Region(i));
		}

		for (Entry<String, Object> i : ((HashMap<String, Object>) json.get("Territories")).entrySet()) {

			Region region = regions.get(((HashMap<String, String>) i.getValue()).get("Region"));
			Boolean claimable = ((HashMap<String, Boolean>) i.getValue()).get("Claimable");
			Map<String, String> points = ((HashMap<String, JSONObject>) i.getValue()).get("Points");

			territories.put(i.getKey(), new Territory(i.getKey(), region, claimable, points));
		}

		for (Entry<String, Object> i : ((HashMap<String, Object>) json.get("Territories")).entrySet()) {
			ArrayList<Connection> connections = new ArrayList<>();
			for (Entry<String, Object> j : ((HashMap<String, Object>) ((HashMap<String, Object>) i.getValue())
					.get("Connections")).entrySet()) {
				connections.add(new Connection(territories.get(j.getKey()), Integer.parseInt(j.getValue().toString())));
			}
			graph.put(territories.get(i.getKey()), connections);
		}

	}

	@SuppressWarnings("unchecked")
	public String toJSON() {

		JSONObject j = new JSONObject();

		JSONObject reg = new JSONObject();
		for (Region r : regions.values()) {
			reg.put(r, 0);
		}

		j.put("Regions", reg);

		JSONObject terr = new JSONObject();
		for (Territory t : territories.values()) {
			JSONObject parms = new JSONObject();
			parms.put("Region", t.getRegion().toString());
			parms.put("Claimable", t.canTravel());
			parms.put("Points", t.getPoints());

			JSONObject c = new JSONObject();
			try {
				for (Connection con : graph.get(t)) {
					c.put(con.toString(), con.getWeight());
					System.out.println(c);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			parms.put("Connections", c);

			terr.put(t.toString(), parms);
		}
		j.put("Territories", terr);

		return j.toJSONString();
	}

	public Collection<Territory> getTerritories() {
		return territories.values();
	}

	public Map<Territory, List<Connection>> getConnections() {
		return graph;
	}

	public void addTerritory(Territory t) {
		territories.put(t.toString(), t);
	}

	public void addRegion(Region r) {
		regions.put(r.toString(), r);
	}

	public void addConnection(Territory start, Territory end) {
		if (!graph.keySet().contains(start)) {
			graph.put(start, new LinkedList<>());
		}

		graph.get(start).add(new Connection(end, 0));

	}

}
