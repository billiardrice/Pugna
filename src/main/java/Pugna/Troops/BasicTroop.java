package Pugna.Troops;

import Pugna.Team;
import Pugna.Map.Territories.Territory;

public class BasicTroop implements Troop {

	private Team team;
	private Territory territory;
	private Boolean attacked;

	public BasicTroop(Team team, Territory territory) {
		this.team = team;
		this.territory = territory;
	}

	@Override
	public int getTraveled() {
		return 0;
	}

	@Override
	public Territory getTerritory() {
		return territory;
	}

	@Override
	public Team getTeam() {
		return team;
	}

	@Override
	public boolean canMove() {
		return true;
	}

	@Override
	public boolean canAttack() {
		return true;
	}

	@Override
	public Territory move(Territory newTerritory) {
		newTerritory.addTroop(this);
		territory.getMobileTroops().remove(this);
		territory = newTerritory;
		return territory;
	}

	@Override
	public void attack() {
		// TODO Auto-generated method stub
		this.attacked = true;
	}

}
