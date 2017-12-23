import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD

		// explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// ...
		
		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
		
		// TODO initialize the root state and add
		// to frontier list
		// ...
		State initialState = new State(this.maze.getPlayerSquare(),null,0,0);
		frontier.add(new StateFValuePair(initialState,getHeuristic(initialState,maze)));
		maxSizeOfFrontier = 1;
		while (!frontier.isEmpty()) {
			// TODO return true if a solution has been found
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found

			// use frontier.poll() to extract the minimum stateFValuePair.
			// use frontier.add(...) to add stateFValue pairs
			StateFValuePair curr = frontier.poll();
			State currState = curr.getState();
			cost = currState.getGValue();
			maxDepthSearched = Math.max(maxDepthSearched, currState.getDepth());
			noOfNodesExpanded++;
			
			//goal test
			if(currState.isGoal(maze)){
				//retrieve the parent
				cost = currState.getGValue();
				maxDepthSearched = Math.max(maxDepthSearched, currState.getDepth());
				currState = currState.getParent();
				while(!currState.getSquare().equals(this.maze.getPlayerSquare())){
					maze.setOneSquare(currState.getSquare(), '.');
					currState = currState.getParent();
				}
				return true;
			}
			explored[currState.getX()][currState.getY()] = true;
			
			for(State next : currState.getSuccessors(explored, maze)){
				
				double fValue = (double)next.getGValue() + getHeuristic(next, maze);
				StateFValuePair newP =new StateFValuePair(next,fValue);
				if(explored[next.getX()][next.getY()]) continue;
				if(!frontier.contains(newP)){
					frontier.add(newP);
					continue;
				}
				
				for(StateFValuePair p : frontier){
					//check if the next state is already in the frontier
					//decrease the F value if needed
					
					if(next.equals(p.getState())){
						if(p.getFValue() > fValue){
							frontier.remove(p);
							frontier.add(newP);
							break;
						}
					}
				}
				//if not added above, add the state here
			}
			this.maxSizeOfFrontier = Math.max(this.maxSizeOfFrontier, frontier.size());
			
		}
		return false;

		// TODO return false if no solution
	}
	
	/**
	 * Returns the heuristic value of the input state
	 * 
	 */
	private double getHeuristic(State s,Maze maze){
		return Math.sqrt(Math.pow(s.getX()-maze.getGoalSquare().X, 2)+
				Math.pow(s.getY()-maze.getGoalSquare().Y, 2));
		
	}
	

}
