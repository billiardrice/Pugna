package Pugna.Map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import Pugna.Map.Territories.Connection;
import Pugna.Map.Territories.Territory;
import javafx.scene.paint.Color;

public class GameMap {

	private Map<String, Region> regions = new HashMap<>();
	private Map<String, Territory> territories = new HashMap<>();
	private Map<Territory, List<Connection>> graph = new HashMap<>();
	private Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public GameMap() {
	}

	@SuppressWarnings("unchecked")
	public GameMap(JSONObject json) {

		try {
			// Gets Regions from JSON
			Color[] colors = new Color[]{Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE, Color.CORAL};
			for (Entry<String, Long> r : ((HashMap<String, Long>) json.get("Regions")).entrySet()) {
				regions.put(r.getKey(), new Region(r.getKey(), colors[Math.toIntExact(r.getValue())]));
			}

			// Gets Territories from JSON
			for (Entry<String, Object> i : ((HashMap<String, Object>) json.get("Territories")).entrySet()) {

				Region region = regions.get(((HashMap<String, String>) i.getValue()).get("Region"));
				Boolean claimable = ((HashMap<String, Boolean>) i.getValue()).get("Claimable");
				Map<Integer, String> points = ((HashMap<Integer, JSONObject>) i.getValue()).get((Object) "Points");
				int reinforcements = Math.toIntExact(((HashMap<String, Long>) i.getValue()).get("Reinforcements"));

				territories.put(i.getKey(), new Territory(i.getKey(), region, claimable, points, reinforcements));
			}

			// Gets Connections from JSON
			for (Entry<String, Object> i : ((HashMap<String, Object>) json.get("Territories")).entrySet()) {
				ArrayList<Connection> connections = new ArrayList<>();
				for (Entry<String, Object> j : ((HashMap<String, Object>) ((HashMap<String, Object>) i.getValue())
						.get("Connections")).entrySet()) {
					connections.add(new Connection(territories.get(j.getKey()), Integer.parseInt(j.getValue().toString())));
				}
				graph.put(territories.get(i.getKey()), connections);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public String toJSON() {

		JSONObject j = new JSONObject();

		JSONObject reg = new JSONObject();
		int color = 0;
		for (Region r : regions.values()) {
			reg.put(r, color++);
		}

		j.put("Regions", reg);

		JSONObject terr = new JSONObject();
		for (Territory t : territories.values()) {
			JSONObject parms = new JSONObject();
			parms.put("Region", t.getRegion().toString());
			parms.put("Claimable", t.canTravel());
			parms.put("Points", t.getPoints());
			parms.put("Reinforcements", t.getReinforcements());

			JSONObject c = new JSONObject();
			try {
				for (Connection con : graph.get(t)) {
					c.put(con.toString(), con.getWeight());
				}
			} catch (Exception e) {
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
