package simulation;


import java.util.Random;

/**
 * The idea here is to do new, improved stochastic agents
 *
 * @author richard
 *
 */


public class Agent implements Cloneable {

	// Lighting contrast: contrast in [-1,1]
	private double contrast = 0.0;
	// Lighting brightness: brightness in [0,1].
	private double brightness = 0.5;
	// Actual probability of lighting when on food is 0.5*brightness*(1+contrast); when off food is 0.5*brightness*(1-contrast)
	// Hence 0 contrast = equally likely to be lit up when on or off food; brightness is overall probability of being lit in a random environment

	// Motion directedness: directed in [0,1]
	private double directedness = 0.0;
	private boolean reversed = false; // Whether the tendency is to move *away* from the light; this is equivalent to d in [-1,1] as stated in the paper
	// Motion rate: rate in [0,1]
	private double motility = 1.0;
	// Probability of moving in any direction is rate; probability that it is towards food is directed

	// Efficiency at consuming food, derived from some cost associated with signalling and responses
	private double efficiency = 1.0;

	// World in which this agent is embedded
	private World world = null;

	// Position of the agent
	private int posx = 0, posy = 0;

	// How much food the agent has consumed
	private int food = 0;

	// Is the light currently showing?
	private boolean light = false;

	// A species id, useful in those cases where we can trace to a common ancestor
	private int speciesId;


	//  METHODS FOR OBTAINING CURRENT STATE
	public int getX() {
		return posx;
	}

	public int getY() {
		return posy;
	}

	public boolean isOnFood() {
		return world.isFoodAt(posx, posy);
	}

	public int getFoodConsumed() {
		return food;
	}

	public void setFoodConsumed(int f) {
		food = f;
	}

	public double getFitness() {
		return food * efficiency;
	}

	public boolean isLightOn() {
		return light;
	}

	public double getEfficiency() {
		return efficiency;
	}

	public int getSpeciesId() {
		return speciesId;
	}


	//  METHODS FOR MANIPULATING THE CURRENT STATE

	/**
	 * Generate a response to a particular light signal: agent will move, set its light and consume food.
	 *
	 * If this method received dx=dy=0, this is to be treated as a random direction
	 *
	 * @param dx x displacement of nearest visible light
	 * @param dy y displacement of nearest visible light
	 * @param rng noise source
	 *
	 */
	public void respond(int dx, int dy, Random rng) {
		// Decide whether we will move: this is set up so that we will *always* move with probability 'rate'
		if(rng.nextDouble() < motility) {

			// Generate a movement direction
			if(!(dx == 0 && dy == 0) && (directedness == 1.0 || rng.nextDouble() < directedness)) {
				// Systematic towards a light
				if(reversed) {
					if(dx < 0) dx = 1;
					else if (dx > 0) dx = -1;
					if(dy < 0) dy = 1;
					else if (dy > 0) dy = -1;
				}
				else {
					if(dx < 0) dx = -1;
					else if (dx > 0) dx = 1;
					if(dy < 0) dy = -1;
					else if (dy > 0) dy = 1;
				}
			}
			else {
				// Random non-zero displacement
				dx = dy = 0;
				while (dx == 0 && dy == 0) {
					dx = rng.nextInt(3)-1; dy = rng.nextInt(3)-1;
				}
			}

			posx = (posx + dx + world.getSizeX()) % world.getSizeX();
			posy = (posy + dy + world.getSizeY()) % world.getSizeY();
		}

		if(isOnFood()) food++;

		// Generate a lighting condition
		if(isOnFood()) {
			light = rng.nextDouble() < 0.5 * brightness * (1.0 + contrast);
		} else {
			light = rng.nextDouble() < 0.5 * brightness * (1.0 - contrast);
		}

	}

	/**
	 * Set the species
	 *
	 * @param sid new species
	 */
	public void setSpeciesId(int sid) {
		this.speciesId = sid;
	}

	//  AGENT BEHAVIOURS

	/**
	 * How much benefit is obtained per unit of food; we assume that any deviation from purely random behaviour incurs a cost
	 */
	private void calculateEfficiency() {
		efficiency = Math.exp( - world.getContrastWeight() * Math.abs(contrast) - world.getBrightnessWeight() * brightness
				- world.getMotilityWeight() * motility - world.getDirectednessWeight() * directedness );
		assert !Double.isInfinite(efficiency);
		assert !Double.isNaN(efficiency);
	}


	/**
	 * Randomly change one of the genotype values to a number distributed uniformly in [-1,1)
	 *
	 * @param rng noise source
	 */
	public void mutate(Random rng) {
		// This goes against the efficiency grain
		int r = rng.nextInt((world.contrastIsMutable() ? 1 : 0) + (world.brightnessIsMutable() ? 1 : 0) + (world.motilityIsMutable() ? 1 : 0) + (world.directednessIsMutable() ? 1 : 0));
		if(r == 0 && world.contrastIsMutable()) {
			contrast =  2.0*rng.nextDouble() - 1.0;
		} else if (--r == 0 && world.brightnessIsMutable()) {
			brightness = rng.nextDouble();
		} else if (--r == 0 && world.motilityIsMutable()) {
			motility = rng.nextDouble();
		} else {
			assert r == 0 && world.directednessIsMutable();
			directedness = rng.nextDouble();
			reversed = rng.nextBoolean();
		}
		calculateEfficiency();
	}



	/**
	 * Obtain a measure of the contrast
	 *
	 * @return a number in [-1,1]; +ve (-ve) is (dis)honest; modulus is strength
	 */
	public double getContrast() {
		return contrast;
	}

	/**
	 * Obtain a measure of the brightness
	 *
	 * @return a number in [0,1]; strength
	 */
	public double getBrightness() {
		return brightness;
	}

	/**
	 * Obtain a measure of the motility
	 *
	 * @return a number in [0,1]; strength
	 */
	public double getMotility() {
		return motility;
	}

	/**
	 * Obtain a measure of the directedness
	 *
	 * @return a number in [-1,1]; strength (negative means away from the nearest light)
	 */
	public double getDirectedness() {
		return reversed ? -directedness : directedness;
	}


	//  METHODS FOR CREATING AN AGENT

	/**
	 * Create an agent with a known position in a specified world onfood status, and some set of behaviours
	 * @param w world
	 * @param x initial x position
	 * @param y initial y position
	 * @param contrast initial contrast (in [-1,1])
	 * @param brightness initial brightness (in [0,1])
	 * @param motility initial motility (in [0,1])
	 * @param directedness initial directedness (in [-1,1])
	 *
	 */
	public Agent(World w, int x, int y, double contrast, double brightness, double motility, double directedness) {
		world = w; posx = x; posy = y;

		this.contrast = contrast;
		this.brightness = brightness;
		this.motility = motility;
		this.directedness = Math.abs(directedness);
		this.reversed = directedness < 0;

		calculateEfficiency();
	}


	/**
	 * Create an agent with a known position in the specified world and mapping copied from another agent
	 *
	 * @param w world
	 * @param x initial x position
	 * @param y initial y position
	 * @param parent agent to copy mapping from
	 */
	public Agent(World w, int x, int y, Agent parent) {
		world = w; posx = x; posy = y;
		this.contrast = parent.contrast;
		this.brightness = parent.brightness;
		this.directedness = parent.directedness;
		this.reversed = parent.reversed;
		this.motility = parent.motility;
		this.efficiency = parent.efficiency;
		this.light = parent.light;
		this.speciesId = parent.speciesId;
	}

	/** Clone an agent
	 *
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			return null;
		}
	}



}
