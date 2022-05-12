package Pugna.Map.Territories;

public class Connection {

	private Territory destination;
	private int weight;

	public Connection(Territory destination, int weight) {
		this.destination = destination;
		this.weight = weight;
	}

	/**
	 * @return Territory return the destination
	 */
	public Territory getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Territory destination) {
		this.destination = destination;
	}

	/**
	 * @return int return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return destination.toString();
	}

}
