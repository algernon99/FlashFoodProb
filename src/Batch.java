
import simulation.Agent;
import simulation.World;


public class Batch {
	
	// Basic setup
	private static final int size = 51;
	private static final int agents = 200;
	private static int foodSites = 10;
	
	// Cost used for all behaviours	
	private static double costScale = 0.25;
	
	// Time to run for from specified initial condition
	private static int sweepsPerGeneration = 1000;
	private static int generations = 1000;
	
	// Evolution
	private static double mutate = 0.01;
	
	
	// Obtain the transition times from specified initial condition
	// Put B M C or D in the string to turn the behaviour on; add a * to lock it on
	public static void doBCMD(String args[]) {
		World w = new World();
		w.setSize(size, size);
		w.setBrightnessMutability(true);
		w.setContrastMutability(true);
		w.setMotilityMutability(true);
		w.setDirectednessMutability(true);
		w.assignFood(foodSites);
		
		// Grok the initial condition
		String userIC = "";
		for(String t : args) userIC+=t.toLowerCase();
		
		double brightness = 0.0, contrast = 0.0, motility = 0.0, directedness = 0.0;
		boolean bodge = false;
		
		String parseIC = "";
		
		for(int i=0; i < userIC.length(); i++) {
			if(userIC.charAt(i) == 'b') {
				brightness = 1.0;
				parseIC += "B";
				if(i+1 < userIC.length() && userIC.charAt(i+1) == 'x') {
					w.setBrightnessMutability(false);
					parseIC += "x";
					i++;
				}
			}
			else if(userIC.charAt(i) == 'c') {
				contrast = 1.0;
				parseIC += "C";
				if(i+1 < userIC.length() && userIC.charAt(i+1) == 'x') {
					w.setContrastMutability(false);
					parseIC += "x";
					i++;
				}
			}
			else if(userIC.charAt(i) == 'm') {
				motility = 1.0;
				parseIC += "M";
				if(i+1 < userIC.length() && userIC.charAt(i+1) == 'x') {
					w.setMotilityMutability(false);
					parseIC += "x";
					i++;
				}
			}
			else if(userIC.charAt(i) == 'd') {
				directedness = 1.0;
				parseIC += "D";
				if(i+1 < userIC.length() && userIC.charAt(i+1) == 'x') {
					w.setDirectednessMutability(false);
					parseIC += "x";
					i++;
				}
			}
			else if(userIC.charAt(i) == 'z') {
				costScale = 0.0;
			}
			else if(userIC.charAt(i) == 'f') {
				bodge = true;
			}
		}

		w.setBrightnessWeight(costScale);
		w.setContrastWeight(costScale);
		w.setMotilityWeight(costScale);
		w.setDirectednessWeight(costScale);
		
		if(bodge) {
			w.setFoodInitialiser(new BrightnessBodgeInitialiser(World.getRNG(), (double)foodSites/(double)(size*size), 0.96848, 1.9094, generations));
		}
		
		w.assignAgents(agents, brightness, contrast, motility, directedness);
		Agent dominator = w.getDominator();

		System.out.println("# ic=" + parseIC + " cost=" + costScale + " bodge=" + bodge + " sweeps="+sweepsPerGeneration + " generations="+generations );
		System.out.println("0\t" + dominator.getBrightness() + "\t" + dominator.getContrast() + "\t" + dominator.getMotility() + "\t" + dominator.getDirectedness() );
		
		for(int g=0; g<generations; g++) {
			w.shuffleFood();
			for(int t=0; t<sweepsPerGeneration; t++) {
				w.sweep();
			}
			w.regenerateAgents(mutate);
			// See if the dominant species has changed
			if(w.getDominator() != dominator) {
				dominator = w.getDominator();
				System.out.println((g+1) + "\t" + dominator.getBrightness() + "\t" + dominator.getContrast() + "\t" + dominator.getMotility() + "\t" + dominator.getDirectedness() );			
			}
		}
	}
	
	public static void doGetFoodBenefit(String args[]) {
		World w = new World();
		w.setSize(size, size);
		w.setBrightnessMutability(true);
		w.setContrastMutability(true);
		w.setMotilityMutability(true);
		w.setDirectednessMutability(true);
		w.assignFood(foodSites);
				
		double brightness = 0.0, contrast = 0.0, motility = 0.0, directedness = 0.0;

		if(args.length >= 1) {
			try {
				motility = Double.parseDouble(args[0]);
			}
			catch(NumberFormatException e) { /* Use default value */ }
		}
		
		System.out.println("# motility = " + motility + " sweeps="+sweepsPerGeneration + " generations="+generations );
		
		for(int g=0; g<generations; g++) {
			w.assignAgents(agents, brightness, contrast, motility, directedness);
			w.shuffleFood();
			for(int t=0; t<sweepsPerGeneration; t++) {
				w.sweep();
			}
			for(Agent a : w.getAgents()) {
				System.out.println(a.getFoodConsumed());
			}
		}
		
	}
	
	public static void main(String args[]) {
		doBCMD(args);
		//doGetFoodBenefit(args);
	} 
}
