import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Breadth-First Search (BFS)
 * 
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public BreadthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main breadth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD

		// explored list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// Queue implementing the Frontier list
		LinkedList<State> queue = new LinkedList<State>();
		State initState = new State(this.maze.getPlayerSquare(),null,0,0);
		queue.push(initState);
		this.maxSizeOfFrontier = 1;
		while(initState.isGoal(maze)){
			return true;
		}
		while (!queue.isEmpty()) {
			// TODO return true if find a solution
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found

			// use queue.pop() to pop the queue.
			// use queue.add(...) to add elements to queue
			
			State curr = queue.pop();
			
			cost = curr.getGValue();
			maxDepthSearched = Math.max(maxDepthSearched, curr.getDepth());
			
			//set the square explored
			explored[curr.getX()][curr.getY()] = true;
			noOfNodesExpanded++;
			if(curr.isGoal(maze)){
				cost = curr.getGValue();
				maxDepthSearched = Math.max(maxDepthSearched, curr.getDepth());
				
				curr = curr.getParent();//don't set the goal to dot
				while(!curr.getSquare().equals(maze.getPlayerSquare())){
					maze.setOneSquare(curr.getSquare(), '.');
					curr = curr.getParent();
				}
				return true;
			}
			
			ArrayList<State> succesor = curr.getSuccessors(explored, maze);
			for(State suc : succesor){
				if(!queue.contains(suc) && !explored[suc.getX()][suc.getY()]){
					
					queue.add(suc);
					
				}
			}
			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, queue.size());
			
		}
		return false;

		// TODO return false if no solution
	}
	
		
}
