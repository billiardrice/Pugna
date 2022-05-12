package Pugna.Map.Territories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Pugna.Team;
import Pugna.Map.Region;
import Pugna.Troops.Troop;

public class Territory {

	private String name;
	private Region region;
	private List<Troop> mobileTroops;
	private List<Troop> immobileTroops;
	private Boolean claimable;
	private Team team;
	private Map<String, String> points;

	public Territory(String name, Region region, Boolean claimable, Map<String, String> points) {
		this.name = name;
		this.region = region;
		this.claimable = claimable;
		this.points = points;
	}

	/**
	 * Returns the list of troops in the territory.
	 * 
	 * @return List of the troops.
	 */
	public List<Troop> getTroops() {

		List<Troop> temp = new ArrayList<>();
		temp.addAll(mobileTroops);
		temp.addAll(immobileTroops);

		return temp;
	}

	/**
	 * Returns the region the territory is in
	 * 
	 * @return Region - the region that the territory is in.
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * Returns the team that controls the territory.
	 * 
	 * @return Team - current controlling team.
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Reinforces the territory by adding new troops
	 * 
	 * @return List of new troops
	 */
	public List<Troop> reinforce() {
		return null;
	}

	/**
	 * Returns if troops can travel to this territory.
	 * 
	 * @return True - if troops can travel to this territory.
	 */
	public boolean canTravel() {
		return claimable;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return String return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(Region region) {
		this.region = region;
	}

	/**
	 * @return List<Troop> return the mobileTroops
	 */
	public List<Troop> getMobileTroops() {
		return mobileTroops;
	}

	/**
	 * @param mobileTroops the mobileTroops to set
	 */
	public void setMobileTroops(List<Troop> mobileTroops) {
		this.mobileTroops = mobileTroops;
	}

	/**
	 * @return List<Troop> return the immobileTroops
	 */
	public List<Troop> getImmobileTroops() {
		return immobileTroops;
	}

	/**
	 * @param immobileTroops the immobileTroops to set
	 */
	public void setImmobileTroops(List<Troop> immobileTroops) {
		this.immobileTroops = immobileTroops;
	}

	/**
	 * @return Boolean return the claimable
	 */
	public Boolean isClaimable() {
		return claimable;
	}

	/**
	 * @param claimable the claimable to set
	 */
	public void setClaimable(Boolean claimable) {
		this.claimable = claimable;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(Team team) {
		this.team = team;
	}

	/**
	 * @return Map<String, String> return the points
	 */
	public Map<String, String> getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(Map<String, String> points) {
		this.points = points;
	}

}
