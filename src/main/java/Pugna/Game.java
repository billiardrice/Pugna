package Pugna;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import Pugna.Map.GameMap;
import Pugna.Map.Territories.Connection;
import Pugna.Map.Territories.Territory;

public class Game {
	public static void main(String[] args) {

		try {

			Scanner json = new Scanner(new File("filename.json"));

			String temp = "";
			while (json.hasNextLine()) {
				temp += json.nextLine().replaceAll("\t|\n", "");
			}
			JSONObject jsonObject = (JSONObject) JSONValue.parse(temp);
			json.close();

			GameMap g = new GameMap(jsonObject);

			for (Entry<Territory, List<Connection>> t : g.getConnections().entrySet()) {
				System.out.println(t.getKey() + "   :   " + t.getValue());
			}

			try {
				FileWriter myWriter = new FileWriter("imperator.json");
				myWriter.write(g.toJSON());
				myWriter.close();
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
