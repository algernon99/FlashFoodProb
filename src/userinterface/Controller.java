package userinterface;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import simulation.Agent;
import simulation.World;
import simulation.World.Summary;

/**
 * The controller object manages a world, providing a means for it to be manipulated and different panels
 * to be kept in sync
 * 
 * @author richard
 *
 */

public class Controller {
	private final String WINDOW_TITLE = "Evolution of communication simulation";

	// We allow direct access to the underlying World and locations of food (which rarely change)
	private World world = new World();

	// Main high-level UI components
	private JFrame masterWindow = new JFrame(WINDOW_TITLE);
	private WorldView worldView = new WorldView(this);
	private RunPanel runPanel = new RunPanel(this);
	private SetupPanel setupPanel = new SetupPanel();
	private StatePanel statePanel = new StatePanel(this);
	private GraphPanel graphPanel = new GraphPanel(this);
	
	// Set up and display a UI
	
	public Controller() {
		setupPanel.configureWorld(world);
		
		masterWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		masterWindow.add(worldView);
		masterWindow.add(runPanel, BorderLayout.SOUTH);
		JTabbedPane switcher = new JTabbedPane();
		switcher.addTab("Setup", setupPanel);
		switcher.addTab("Agents", statePanel);
		switcher.addTab("Graphs", graphPanel);
		masterWindow.add(switcher, BorderLayout.EAST);
		masterWindow.pack();
		masterWindow.setVisible(true);
	}

	// Background job control
	
	private boolean pendingRegeneration = false;  // Set to true if a regeneration is pending at the start of the next batch of runs
	

	private class BatchJob extends Thread {
		private int generations, sweeps;
		private boolean regenerateAtStart, autoRegenerateNextTime;
		private double mutate;
		
		public BatchJob(int gens, int swps, boolean autoRegen, double mut) {
			generations = gens;
			sweeps = swps;
			regenerateAtStart = (gens == 0 && swps == 0) || pendingRegeneration;
			autoRegenerateNextTime = autoRegen;
			mutate = mut;
		}
		
		@Override
		public void run() {
			boolean finishedNormally = false;
			if(world != null) {
				if(regenerateAtStart) {
					// Get statistics just before the regeneration
					Summary summary = world.getSummary();
					world.regenerateAgents(mutate);
					world.shuffleFood();
					// Invalidate snapshot and notify statistics
					synchronized(Controller.this) {
						stats.add(summary);
						snapshot = null;
					}
				}
				for(int g=0; g<generations && !isInterrupted(); g++) {
					for(int s=0; s<sweeps && !isInterrupted(); s++) {
						// We don't allow a job to be interrupted mid-sweep
						world.sweep();
						// Invalidate snapshot
						synchronized(Controller.this) { snapshot = null; }
					}
					if(!isInterrupted()) {
						if(g<generations-1) {
							// Get statistics just before the regeneration
							Summary summary = world.getSummary();
							world.regenerateAgents(mutate);
							world.shuffleFood();
							// Invalidate snapshot and notify statistics
							synchronized(Controller.this) {
								stats.add(summary);
								snapshot = null;
							}
						}
						else finishedNormally = true;
					}
				}
			}
			// Signal listening objects that batch job has finished
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						jobFinished();
					}
			});
			synchronized(Controller.this) {
				runningJob = null;
				pendingRegeneration = (finishedNormally && autoRegenerateNextTime);
				Controller.this.notifyAll();
			}
		}
	}

	private BatchJob runningJob = null;
	
	// Start/stop protocol:  the Controller object is locked to ensure jobs are only started when existing jobs have stopped
			
	/*
	 * Private method to start a job: use one of the run... methods to start a particular job
	 */
	private void startJob(BatchJob job) {
		synchronized(this) {
			if(runningJob != null) return;
			job.start();
			runningJob = job;
		}
		// Signal any UI objects who might be interested in a job starting
		runPanel.jobStarted();
		worldView.jobStarted();
		statePanel.jobStarted();
		graphPanel.jobStarted();
	}
	
	private void jobFinished() {
		// Signal any UI objects who might be interested in a job finishing
		worldView.jobFinished();
		runPanel.jobFinished();
		statePanel.jobFinished();
		graphPanel.jobFinished();

	}

	/**
	 * Stop any running job started with a run... method; the job will finish at some time in the future unless 'wait' is specified as true in which case it waits for the job to actually finish
	 * 
	 * @param wait return only once the job has actually finished; note that any listeners may not get their events until Swing picks it up later
	 */
	public synchronized void stopJob(boolean wait) {
		if(runningJob == null) return;
		runningJob.interrupt();
		if(wait) {
			try {
				this.wait();
			} catch(InterruptedException e) { /* Return silently */ }
		}
	}
	
	private boolean viewsNotified = false;
		
	/** Reset the state of the world using the values in the SetupPanel, and notify all views of any changes **/
	public void Reset() {
		// Stop any running background job, and wait for it to have finished
		stopJob(true);
		
		// Reconfigure the world using the setup panel
		setupPanel.configureWorld(world);
		
		// Clear the snapshot, and prevent autoregeneration
		snapshot = null;
		pendingRegeneration = false;
		stats.clear();
		
		if(!viewsNotified) {
			viewsNotified = true;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					worldView.repaint();
					statePanel.worldUpdated();
					graphPanel.worldUpdated();
					viewsNotified = false;
				}
			});
		}

	}

	

	/**
	 * Run some sweeps of the dynamics: no regeneration is done here
	 * @param shaded whether lights are shaded
	 */
	public void runSweep(int sweeps) {
		startJob(new BatchJob(1, sweeps, false, 0.0));
	}
	
	/**
	 * Regenerate, as long as some update has occurred since the last regeneration
	 * 
	 * @param mutate mutation probability per agent in the regeneration
	 * @param simpleton mutate behaviour sets en-block, rather than individually
	 */
	public void runRegeneration(double mutate) {
		startJob(new BatchJob(0, 0, false, mutate));
	}
	
	/**
	 * Run multiple sweeps and possibly generations, regenerating with the specified mutation rate between generations.
	 * Notice the final regeneration is not done, so you can examine the state of the system before regeneration; you
	 * can always call <code>regenerate</code> if you want to examine the state afterwards as well.  This regeneration
	 * will be done automatically at the start of the next call to <code>runGenerations</code> if you don't do it 
	 * manually.
	 * 
	 * So the call runGenerations(2, 100, 0.05) is equivalent to
	 * runSweep(100); regenerate(0.05); runSweep(100);
	 * and the next call to runGenerations will do a regenerate at whatever mutation rate is specified at the following call
	 * before performing any sweeps (unless regenerate has been called beforehand)
	 * 
	 * @param generations number of generations to run for
	 * @param sweeps number of sweeps to perform per generation
	 * @param mutate mutation probability per agent between generations
	 * @param simpleton mutate behaviour sets en-block, rather than individually
	 * 
	 */
	public void runGenerations(int generations, int sweeps, double mutate) {
		startJob(new BatchJob(generations, sweeps, true, mutate));		
	}
	
	//  OBTAINING SNAPSHOTS
	
	// A compact representation of the state of the system for easy plotting
	public class Site {
		boolean food = false; // true if food on this site
		int agents = 0;   // number of agents on this site
		int lit = 0;     // number of agents with their lights on
	}
	
	private Site[][] snapshot = null;
	
	/**
	 * Discover if any earlier snapshot obtained is invalid
	 * 
	 * @return true if a call to getSnapshot() will give a new snapshot of the system
	 */
	public synchronized boolean isSnapshotInvalid() {
		return snapshot == null;
	}
	
	/**
	 * Quickly obtain the state of the system in a manner that is suitable for plotting
	 * 
	 * @return an array of site statuses
	 */
	public synchronized Site[][] getSnapshot() {
		if(snapshot == null) {
			// Create snapshot
			snapshot = new Site[world.getSizeX()][world.getSizeY()];
			for(int x=0; x<world.getSizeX(); x++) {
				for(int y=0; y<world.getSizeY(); y++) {
					snapshot[x][y] = new Site();
					snapshot[x][y].food = world.isFoodAt(x, y);
				}
			}
			for(Agent a : world.getAgents()) {
				snapshot[a.getX()][a.getY()].agents++;
				if(a.isLightOn()) snapshot[a.getX()][a.getY()].lit++;
			}
		}
		return snapshot;
	}
	
	// We might also want to get a stable list of agents while a simulation is running
	
	/**
	 * Obtain an array of agents in the current state; this array is an independent copy of what is in the World so that the contents may be displayed
	 * even as a simulation is running
	 * 
	 * @return array of agent data
	 */
	public synchronized Agent[] getAgentsStable() {
		Agent[] ags = new Agent[world.getAgents().size()];
		for(int i=0; i<ags.length; i++) ags[i] = (Agent)world.getAgents().get(i).clone();
		return ags;
	}
	
	//  TIME-DEPENDENT STATISTICS

	// Capture the current statistical state of the system
		
	private List<Summary> stats = new ArrayList<Summary>();
	
	/**
	 * Obtain a set of per-generation statistics since the last call to getStatistics(), or when the simulation was last reset
	 * 
	 * @return array of Statistics objects containing generation-by-generation data for processing
	 */
	public synchronized Summary[] getStatistics() {
		if(stats.size() == 0) return null;
		Summary[] astats = stats.toArray(new Summary[stats.size()]);
		stats.clear();
		return astats;
	}

	// Allow different UI objects to communicate with each other about an active agent; note, this is likely a cloned agent obtained from
	// a call to getAgentsStable, so may not actually correspond with any agent in the current simulation state
	
	private Agent activeAgent = null;

	public void setActiveAgent(Agent a) {
		activeAgent = a;
		if(activeAgent != null) {
			worldView.setActiveSite(a.getX(), a.getY());
		}
		else {
			worldView.clearActiveSite();
		}
		statePanel.activeAgentChanged();
	}
	
	public Agent getActiveAgent() {
		return activeAgent;
	}
	
	public JFrame getFrame() {
		return masterWindow;
	}

	
}
