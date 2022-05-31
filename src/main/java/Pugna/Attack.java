package Pugna;

import java.util.Collections;
import java.util.List;

import Pugna.Map.Territories.Territory;
import Pugna.Troops.Troop;

public class Attack {

	// Attackers Info
	private List<Troop> attackingTroops;
	private Territory attackingTerritory;

	// Defenders Info
	private Territory defendingTerritory;

	// Attack Variables
	private Boolean won;
	Integer MAX_ATTACK_ROLLS = 3;
	Integer MAX_DEFENCE_ROLLS = 2;

	/**
	 * Starts an attack.
	 * 
	 * @param attackingTroops    - List of troops attacking.
	 * @param attackingTerritory - Territory the attackers come from.
	 * @param defendingTerritory - Territory the defenders come from.
	 */
	public Attack(List<Troop> attackingTroops, Territory attackingTerritory, Territory defendingTerritory) {

		this.attackingTroops = attackingTroops;
		this.attackingTerritory = attackingTerritory;

		this.defendingTerritory = defendingTerritory;

		for (Troop t : attackingTroops) {
			t.attack();
		}

	}

	/**
	 * Begins a round of attacks.
	 * 
	 * @param attackRolls  - List of attackers dice rolls
	 * @param defenceRolls - List of defenders dice rolls
	 * @return Attack - current attack.
	 */
	public Attack attack(List<Integer> attackRolls, List<Integer> defenceRolls) {
		attackRolls = attackRolls.subList(0, MAX_ATTACK_ROLLS);
		Collections.sort(attackRolls, Collections.reverseOrder());
		defenceRolls = defenceRolls.subList(0, MAX_DEFENCE_ROLLS);
		Collections.sort(defenceRolls, Collections.reverseOrder());

		try {
			System.out.println("\nNew Attack");

			while ((defenceRolls.size() > 0) &&
					(attackingTroops.size() > 0) &&
					(defendingTerritory.getMobileTroops().size() > 0)) {
				System.out.println("\n" + attackRolls + " : " + defenceRolls);
				if (attackRolls.get(0) > defenceRolls.get(0)) {
					this.defendingTerritory.getMobileTroops().remove(defendingTerritory.getMobileTroops().get(0));
					System.out.println("Defend Lost +1");
				} else {
					this.attackingTerritory.getMobileTroops().remove(this.attackingTroops.remove(0));
					System.out.println("Attack Lost -1");
				}
				attackRolls.remove(0);
				defenceRolls.remove(0);
			System.out.println("" + attackingTroops.size() + " : " +  defendingTerritory.getMobileTroops().size());
			}

			if (this.attackingTroops.size() == 0) {
				System.out.println("LOST");
				this.won = false;
			}
			if (this.defendingTerritory.getMobileTroops().size() == 0) {
				System.out.println("WON");
				this.won = true;
				for (Troop t : attackingTroops) {
					t.move(defendingTerritory);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	/**
	 * Ends the attack returning any remaining troops to their respective
	 * territories.
	 */
	public void retreat() {
		// TODO
	}

	/**
	 * Returns number of attacking troops.
	 * 
	 * @return int - Number of attackers.
	 */
	public int getAttackers() {
		return this.attackingTroops.size();
	}

	/**
	 * Returns number of defending troops.
	 * 
	 * @return int - Number of defenders.
	 */
	public int getDefenders() {
		return this.defendingTerritory.getMobileTroops().size();
	}

	/**
	 * Returns the defending Territory.
	 * 
	 * @return Territory - the territory that is being attacked.
	 */
	public Territory getTerritory() {
		return this.defendingTerritory;
	}

	/**
	 * Returns if the attack was won
	 * 
	 * @return true - if the attack was won.
	 */
	public Boolean attackDone() {
		return this.won != null;
	}

	public boolean attackWon() {
		return won;
	}

}