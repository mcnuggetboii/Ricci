package src.guiVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Worker extends Thread {

	private final SharedContext context;
	private List<Body> threadBalls;
	private final int start;
	private final int lastIndex;
	private final double dt = 0.1;

	/*
	 * name : thread name 
	 * context: shared context 
	 * start: index of the first ball assigned to the thread (included)
	 * lastIndex: index of the last ball assigned to the thread (not included)
	 */
	public Worker(final String name, final SharedContext context, final int start, final int lastlIndex) {
		super(name);
		this.context = context;
		this.start = start;
		this.lastIndex = lastlIndex;
		this.threadBalls = new ArrayList<Body>(context.getBallList().subList(start, lastIndex));
	}

	public void run() {
		while (!context.getStop()) {

			//each cycle move the balls according to its speed
			updateInternalPos();
			context.waitNonConcurrentCalc();			
			
			//check and solve collisions assigned to the thread
			checkAndSolveCollisions();
			context.waitNonConcurrentCalc();

			//check and solve boundary collisions
			solveBoundaryCollision();
			context.waitNonConcurrentCalc();
			context.hitBarrier();
		}
	}
	
	//method to move each assigned ball accordingly to its velocity
	private void updateInternalPos() {
		
		for(int i = 0; i < threadBalls.size(); i++) {
			threadBalls.get(i).updatePos(dt);
		}
		
		context.lockUpdateSem();
		updateGlobalList();
		context.releaseUpdateSem();
	}
	
	//method to check and solve collisions relative to balls assigned to the thread
	private void checkAndSolveCollisions() {
		
		//instantiate a copy of the global ball list
		List<Body> tmp = context.getBallList();
    	
    	//for each assigned ball
	    for (int i = start; i < lastIndex - 1; i++) {	    	
	    	Body b1 = tmp.get(i);
	    	
	    	//for each subsequent ball
	        for (int j = i+1; j < tmp.size(); j++) {        	
	        	Body b2 = tmp.get(j);
	        	
	            if (b1.collideWith(b2)) {     
	            	synchronized(b1) {
	            		synchronized(b2) {
	    	            	Body.solveCollision(b1, b2);	            			
	            		}
	            	}	            	
	            }
	        }
        }
	}
	
	
	//check and directly update local collisions
	private void solveBoundaryCollision() {
		for(int i = 0; i < threadBalls.size(); i++) {
			threadBalls.get(i).checkAndSolveBoundaryCollision(SharedContext.getBounds());
		}
		context.lockUpdateSem();
		updateGlobalList();
		context.releaseUpdateSem();
	}

	//set the global list to match our local
	private void updateGlobalList() {
		int k = start;
		for (int m = 0; m < threadBalls.size(); m++) {
			context.updateBallList(threadBalls.get(m), k++);
		}
	}
}
