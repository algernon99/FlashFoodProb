package simulation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class World {
	
	// Threshold of species abundance that must be exceeded for a dominance threshold to be exceeded
	private final static double DOMINANCE_THRESHOLD = 0.75;

	// By default, use the standard Java random number generator for all random numbers
	private static Random rng = new Random();

	/**
	 * Call this method at the start of your simulation if you wish to generate random numbers from a
	 * generator other than the Java standard generator
	 * 
	 * @param r instance of random number generator to use in a simulation
	 */
	public static void setRNG(Random r) {
		World.rng = r;
	}
	
	public static Random getRNG() {
		return rng;
	}

	
	// Dimensions of this world; agents can occupy the positions x=0..sizex-1, y=0..sizey-1
	private int sizex=0, sizey=0;
	
	// Costs to the agents in this world associated with developing different behaviours
	private double contrastWeight = 0.0, brightnessWeight = 0.0, motilityWeight = 0.0, directednessWeight = 0.0 ;
	
	// Mutability of these behaviours in this world
	private boolean contrastIsMutable = false, brightnessIsMutable = false, motilityIsMutable = false, directednessIsMutable = false;
	
	// Food initialisation routine - use the default, unless changed with a call to setFoodInitialiser()
	private FoodInitialiser finit = new FoodInitialiser();
	
	// Number of generations that have elapsed
	private int generations = 0;

	// 2d array indicating if food is present at a location
	private boolean food[][] = new boolean[sizex][sizey];
	private int sitesWithFood = 0;

	// List of agents
	List<Agent> agents = new ArrayList<Agent>();

	// Total number of species that have ever been created
	private int maxSpeciesID;
	
	// Representative of the dominant species
	Agent dominator = null;


	/**
	 * Set the size of the world - any food or agents outside the new bounds are removed
	 * 
	 * @param x desired x size
	 * @param y desired y size
	 */
	public void setSize(int x, int y) {
		boolean newFood[][] = new boolean[x][y];
		sitesWithFood = 0;
		int minx = sizex < x ? sizex : x;
		int miny = sizey < y ? sizey : y;
		for(int i=0;i<minx;i++) {
			for(int j=0;j<miny;j++) {
				newFood[i][j] = food[i][j];
				if(newFood[i][j]) sitesWithFood++;
			}
		}
		Iterator<Agent> ags = agents.iterator();
		while(ags.hasNext()) {
			Agent a = ags.next();
			if(a.getX() >= x || a.getY() >= y) ags.remove();
		}
		sizex = x; sizey = y; food = newFood;
	}

	/**
	 * Read off the horizontal size of the world
	 * 
	 * @return x dimension of the world
	 */
	public int getSizeX() {
		return sizex;
	}

	/**
	 * Read off the vertical size of the world
	 * 
	 * @return y dimension of the world
	 */
	public int getSizeY() {
		return sizey;
	}

	/**
	 * Measure of the fitness cost associated with displaying contrast
	 * 
	 * @return contrast weight (\ge 0)
	 */
	public double getContrastWeight() {
		return contrastWeight;
	}
	
	/**
	 * Set the contrast weight
	 * 
	 * @param contrastWeight new contrast weight (\ge 0)
	 */
	public void setContrastWeight(double contrastWeight) {
		this.contrastWeight = contrastWeight;
	}

	/**
	 * Measure of the fitness cost associated with displaying brightness
	 * 
	 * @return brightness weight (\ge 0)
	 */
	public double getBrightnessWeight() {
		return brightnessWeight;
	}

	/**
	 * Set the brightness weight
	 * 
	 * @param contrastWeight new brightness weight (\ge 0)
	 */
	public void setBrightnessWeight(double brightnessWeight) {
		this.brightnessWeight = brightnessWeight;
	}

	/**
	 * Measure of the fitness cost associated with displaying motility
	 * 
	 * @return motility weight (\ge 0)
	 */
	public double getMotilityWeight() {
		return motilityWeight;
	}

	/**
	 * Set the motility weight
	 * 
	 * @param motilityWeight new motility weight (\ge 0)
	 */
	public void setMotilityWeight(double motilityWeight) {
		this.motilityWeight = motilityWeight;
	}
	
	/**
	 * Measure of the fitness cost associated with displaying directedness
	 * 
	 * @return directedness weight (\ge 0)
	 */
	public double getDirectednessWeight() {
		return directednessWeight;
	}

	/**
	 * Set the directedness weight
	 * 
	 * @param directednessWeight new directedness weight (\ge 0)
	 */
	public void setDirectednessWeight(double directednessWeight) {
		this.directednessWeight = directednessWeight;
	}
	
	/**
	 * Mutability of various agent characteristics
	 * 
	 */
	public boolean contrastIsMutable() {
		return contrastIsMutable;
	}
	
	public void setContrastMutability(boolean mutability) {
		contrastIsMutable = mutability;
	}
	
	public boolean brightnessIsMutable() {
		return brightnessIsMutable;
	}

	public void setBrightnessMutability(boolean mutability) {
		brightnessIsMutable = mutability;
	}

	public boolean motilityIsMutable() {
		return motilityIsMutable;
	}
	
	public void setMotilityMutability(boolean mutability) {
		motilityIsMutable = mutability;
	}

	public boolean directednessIsMutable() {
		return directednessIsMutable;
	}
	
	public void setDirectednessMutability(boolean mutability) {
		directednessIsMutable = mutability;
	}
	
	public void setFoodInitialiser(FoodInitialiser fi) {
		finit = fi;
	}
	
	/**
	 * Create a random assignment of food on n sites of the lattice
	 * 
	 * @param n number of lattice sites to occupy with food
	 */
	public void assignFood(int n) {
		if(n>sizex*sizey) n = sizex*sizey;
		sitesWithFood = n;
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				food[x][y]=false;
			}
		}
		while(n>0) {
			// Choose a random position
			int x = rng.nextInt(sizex), y = rng.nextInt(sizey);
			// Place food at this site if it is not already there
			if(!food[x][y]) {
				food[x][y] = true;
				n--;
			}
		}
	}
	
	/**
	 * Shuffle the locations of food, whilst keeping the number of sites containing food constant
	 */
	public void shuffleFood() {
		assignFood(sitesWithFood);
	}

	/**
	 * Find out if food is at a given location
	 * 
	 * @param x x coordinate of location to query
	 * @param y y coordinate of location to query
	 * @return true if specified location contains food
	 */
	public boolean isFoodAt(int x, int y) {
		return food[x][y];
	}	

	/**
	 * Create a set of agents, according to one of the allowed initial conditions.
	 * Agents are initially arranged randomly within the world
	 * 
	 * @param nagents number of agents
	 * @param cue cueing behaviour
	 * @param meanContrast behaviour
	 * @param startTogether whether all agents start on the same site
	 */
	public void assignAgents(int nagents, double brightness, double contrast, double motility, double directedness) {
		// Restore a clean slate
		agents.clear();
		generations =  0;
				
		// Add agents one by one, each of the same species
		for(int i=0; i<nagents; i++) {
			int x = rng.nextInt(sizex), y = rng.nextInt(sizey);
			Agent agent = new Agent(this, x, y, contrast, brightness, motility, directedness);
			finit.assignFood(agent);
			agent.setSpeciesId(0);
			agents.add(agent);	
		}
		dominator = agents.get(0);
		
	}
	
	/**
	 * Get a list of agents - do not modify the objects returned
	 * 
	 * @return list of agents
	 */
	public List<Agent> getAgents() {
		return agents;
	}

	/**
	 * Choose an agent at random, set up its input, get the output and act accordingly. Do this on average once per agent.
	 * 
	 * The agent consumes food if the site it occupies before the site contains food
	 */
	public void sweep() {
		int ags = agents.size();
		int hsx = sizex/2, hsy = sizey/2;
		for(int i=0; i<ags; i++) {
			Agent a = agents.get(rng.nextInt(ags));

			// Find position of nearest light source (if we don't find one, dx and dy will end up as zero, which will be treated as "no light source" in the input)
			int dx = 0, dy = 0;

			for(Agent b : agents) {
				// Skip agents whose lights are off
				if(!b.isLightOn()) continue;
				// Do not monitor our own light state, or those of different species
				if(b == a || a.getSpeciesId() != b.getSpeciesId()) continue;

				// Use Euclidean distance metric combined with the minimum image convention
				// (i.e., find the shortest distance from agent a to any of the periodic images of b)
				int abdx = b.getX() - a.getX();
				int abdy = b.getY() - a.getY();

				// Ignore lights on the same site
				if(abdx == 0 && abdy == 0) continue;

				// Apply minimum image convention
				if(abdx > hsx) abdx -= sizex;
				else if(abdx < - hsx) abdx += sizex;

				if(abdy > hsy) abdy -= sizey;
				else if(abdy < - hsy) abdy += sizey;

				if((dx ==0 && dy == 0) || abdx * abdx + abdy * abdy < dx*dx + dy*dy) {
					dx = abdx;
					dy = abdy;
				}
			}

			a.respond(dx, dy, rng);
		}

	}


	/**
	 * Create a new generation of agents (same population size as the previous one)
	 * with the probability of having offspring being proportional to the amount of food consumed (+1 to avoid singularities)
	 * 
	 * @param agentMutate probability of a mutation in any given birth event
	 */
	public void regenerateAgents(double agentMutate) {
		// Assign a score to each existing agent
		int popSize = agents.size();
		double score[] = new double[popSize];

		score[0] = agents.get(0).getFitness();
		
		for(int i=1; i<popSize; i++) {
			score[i] = score[i-1] + agents.get(i).getFitness();
		}

		List<Agent> newAgents = new ArrayList<Agent>();
		Map<Integer,Integer> sad = new HashMap<Integer, Integer>();
		Agent domagent = null;
		int threshold = (int)(DOMINANCE_THRESHOLD * popSize);
		
		// Zero all the entries of the map
		for(Integer key : sad.keySet()) {
			sad.put(key, 0);
		}

		// Generate a random sample of the same size as previously
		for(int i=0; i<popSize; i++) {

			// Choose a position for the next child to go
			int x = rng.nextInt(sizex);
			int y = rng.nextInt(sizey);
			
			// Choose a child; randomly if no food was consumed, weighted according to consumption otherwise
			Agent child = null;
			if(score[popSize-1] == 0.0) child = new Agent(this, x, y, agents.get(rng.nextInt(popSize)));
			else {			
				double choose = score[popSize-1] * rng.nextDouble();
				for(int j=0; j<popSize; j++) {
					if(choose < score[j]) {
						child = new Agent(this, x, y, agents.get(j));
						break;
					}
				}
				// NB: not the most efficient way to do this
			}

			// Change one of the agent's behaviour parameters with the specified probability, and make this a member of a new species
			// Keep track of species abundances and the dominant species as we do this
			if(rng.nextDouble() < agentMutate) {
				child.mutate(rng);
				child.setSpeciesId(++maxSpeciesID);
			} 

			finit.assignFood(child);
			newAgents.add(child);

			// Keep track of species abundances
			Integer sid = child.getSpeciesId();
			Integer put = sad.containsKey(sid) ? sad.get(sid)+1 : 1;
			sad.put(sid, put);
			if(put >= threshold) domagent = child;		
		}
				
		// If a new species has exceeded the threshold, we update its representative
		// If a new species has exceeded the threshold, we update its representative
		if(domagent != null && domagent.getSpeciesId() != dominator.getSpeciesId()) {
			dominator = domagent;
		}

		agents = newAgents;
		++generations;
		
	}
	

		
	/**
	 * Get number of generations since the agents were last initialised
	 * 
	 * @return number of generations
	 */
	public int getGenerations() {
		return generations;
	}

	/**
	 * A class for holding a summary of the system
	 */
	public static class Summary implements Cloneable {
		public double meanContrast, meanBrightness, meanMotility, meanDirectedness;
		public double domContrast, domBrightness, domMotility, domDirectedness;
		
		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch(CloneNotSupportedException e) {
				return null;
			}
		}
	}

	/**
	 * Obtain a summary object for the system
	 * 
	 * @return
	 */
	public Summary getSummary() {
		Summary sum = new Summary();
		double con = 0, bri = 0, mot = 0, dir = 0;
		for(Agent a : agents) {
			con += a.getContrast();
			bri += a.getBrightness();
			mot += a.getMotility(); 
			dir += a.getDirectedness();
		}
		sum.meanContrast = (double)con / (double)agents.size();
		sum.meanBrightness = (double)bri / (double)agents.size();
		sum.meanMotility = (double)mot / (double)agents.size();
		sum.meanDirectedness = (double)dir / (double)agents.size();
		
		sum.domContrast = dominator.getContrast();
		sum.domBrightness = dominator.getBrightness();
		sum.domMotility = dominator.getMotility();
		sum.domDirectedness = dominator.getDirectedness();

		return sum;
	}
	
	/**
	 * Find the species that dominates the system
	 * 
	 * @return the above-threshold species
	 */
	public Agent getDominator() {
		return dominator;
	}

	
}
