package Pugna.Troops;

import Pugna.Team;
import Pugna.Map.Territories.Territory;

public class BasicTroop implements Troop {

	@Override
	public int getTraveled() {
		return 0;
	}

	@Override
	public Territory getTerritory() {
		return null;
	}

	@Override
	public Team getTeam() {
		return null;
	}

	@Override
	public boolean canMove() {
		return false;
	}

	@Override
	public boolean canAttack() {
		return false;
	}

	@Override
	public Territory move(Territory newTerritory) {
		return null;
	}

}
