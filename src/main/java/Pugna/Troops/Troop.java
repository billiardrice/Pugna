package Pugna.Troops;

import Pugna.Team;
import Pugna.Map.Territories.Territory;

public interface Troop {
	
	/**
	 * Returns the distance the given troop traveled.
	 * @return int - Distance traveled
	 */
	public int getTraveled();

	public void attack();

	/**
	 * Returns the current territory of the troop.
	 * @return Territory - the current territory the troop is in.
	 */
	public Territory getTerritory();

	/**
	 * Returns the team that owns the troop.
	 * @return Team - of the troop.
	 */
	public Team getTeam();

	/**
	 * Returns if the troop can move another territory.
	 * @return True - if the troop can move.
	 */
	public boolean canMove();

	/**
	 * Returns if the troop can attack.
	 * @return True - if the troop can attack.
	 */
	public boolean canAttack();

	/**
	 * Moves the troop to a new troop and returns the old territory.
	 * @param newTerritory - The new Territory to move to.
	 * @return Territory - the previous Territory.
	 */
	public Territory move(Territory newTerritory);

}
