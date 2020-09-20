package simulation;

/**
 * Class to provide an initial amount of food to an agent when it is created
 * 
 * @author richard
 *
 */

public class FoodInitialiser {
	/**
	 * The default initialiser simply assigns no food; subclasses can replace this, e.g., to sample from a distribution
	 * @param agent agent to assign food to
	 */
	public void assignFood(Agent agent) {
		agent.setFoodConsumed(0);
	}
}
