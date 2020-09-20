import java.util.Random;

import simulation.Agent;
import simulation.FoodInitialiser;
import simulation.World;

/**
 * This bodges the initial food of the agent as a function of its brightness so as to match that arising naturally from motility
 * 
 * @author richard
 *
 */

public class BrightnessBodgeInitialiser extends FoodInitialiser {
	
	private Random rng;
	private double rho, alpha, beta;
	private int T;
	
	public BrightnessBodgeInitialiser(Random rng, double rho, double alpha, double beta, int T) {
		this.rng = rng;
		this.rho = rho;
		this.alpha = alpha;
		this.beta = beta;
		this.T = T;
	}

	@Override
	public void assignFood(Agent agent) {
		double brightness = agent.getBrightness();
		if(brightness == 0.0) {
			// Generate from bimodal distribution: generate T with probability rho, and 0 otherwise
			agent.setFoodConsumed(rng.nextDouble() < rho ? T : 0);
		} else {
			double p0 = alpha * Math.exp(-beta * brightness);
			// Generate from delta at 0 (w.p p0) + exponential distribution with mean rho * t / (1-p0) otherwise
			agent.setFoodConsumed(rng.nextDouble() < p0 ? 0 : (int)( (rho * T)/(p0 - 1.0) * Math.log(1.0-rng.nextDouble()) ) ) ;
		}
	}
	
	
	public static void main(String args[]) {
		// Test this for the value of brightness specified at the command line, and the parameters used in the simulation
		double brightness = 0.0;
		if(args.length > 0) {
			try {
				brightness = Double.parseDouble(args[0]);
			}
			catch(Exception e) { }
		}
		System.out.println("# brightness = " + brightness);
		
		Agent agent = new Agent( new World(),0,0,0.0,brightness,0.0,0.0);
		BrightnessBodgeInitialiser bbi = new BrightnessBodgeInitialiser(World.getRNG(), (double)10/(double)(51*51), 0.96848, 1.9094, 1000);
		
		for(int n=0; n<200000; n++) {
			bbi.assignFood(agent);
			System.out.println(agent.getFoodConsumed());
		}
		
	}
}
