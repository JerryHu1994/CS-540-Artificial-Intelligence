/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 2: Game Playing

  TakeStones.java
  This is the main class that implements functions for Take Stones playing!
  ---------
  	*Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as TakeStones for testing
  		- Not to import any external libraries
  		- Not to include any packages 
	*Notice: To use this file, you should implement 4 methods below.

	@author: TA 
	@date: Feb 2017
*****************************************************************************************/

import java.util.ArrayList;


public class TakeStones {

	final int WIN_SCORE = 100;	// score of max winning game
	final int LOSE_SCORE = -100;// score of max losing game
	final int INFINITY = 1000;	// infinity constant
	
	/** 
    * Class constructor.
    */
	public TakeStones () {};


	/**
	* This method is used to generate a list of successors 
	* @param state This is the current game state
	* @return ArrayList<Integer> This is the list of state's successors
	*/
	public ArrayList<Integer> generate_successors(GameState state) {
		int lastMove = state.get_last_move();	// the last move
		int size = state.get_size();			// game size
		ArrayList<Integer> successors = new ArrayList<Integer>();	// list of successors

		// TODO Add your code here
		//at the first move
		if(state.get_last_move()==-1){
			for(int i = 1;i <= state.get_size()/2;i++){
				if(i%2 == 1){
					successors.add(i);
				}
			}
			return successors;
		}
		//add all the multipliers and factors
		for(int idx = 1;idx <= size; idx++){
			//check if the stone is picked
			if(state.get_stone(idx)){
				if(idx%lastMove==0 || lastMove%idx==0){
					successors.add(idx);
				}
			}
		}
		return successors;
	}


	/**
	* This method is used to evaluate a game state based on 
	* the given heuristic function 
	* @param state This is the current game state
	* @return int This is the static score of given state
	*/
	public int evaluate_state(GameState state) {
		// if stone 1 is still available, score is 0
		if (state.get_stone(1)) 
			return 0;

		int lastMove = state.get_last_move();
		if (1 == lastMove) {
			
			// TODO Add your code here
			int size = generate_successors(state).size();
			return (size%2==0) ? -5 : 5;
		} else if (Helper.is_prime(lastMove)){

			// TODO Add your code here
			int count = 0;
			for(int i : generate_successors(state)){
				if(i%lastMove == 0) count++;
			}
			return (count%2 == 0) ? -7:7;
		} else {
			
			// TODO Add your code here
			int largestP = Helper.get_largest_prime_factor(lastMove);
			int count = 0;
			
			for(int i = largestP;i<=state.get_size();i++){
				if(state.get_stone(i)){
				if(i%largestP==0) count++;
				}
			}
	
			return (count%2 == 0) ? -6 : 6;
			
		}
	}


	/**
	* This method is used to get the best next move from the current state
	* @param state This is the current game state
	* @param depth Current depth of search
	* @param maxPlayer True if player is Max Player; Otherwise, false
	* @return int This is the number indicating chosen stone
	*/
	public int get_next_move(GameState state, int depth, boolean maxPlayer) {
		int move = -1;			// the best next move 
		int alpha = -INFINITY;	// initial value of alpha
		int beta = INFINITY;	// initial value of alpha

		// Getting successors of the given state 
		ArrayList<Integer> successors = generate_successors(state);
		// Check if depth is 0 or it is terminal state 
		if (0 == depth || 0 == successors.size()) {
			state.log();
			Helper.log_alphabeta(alpha, beta);
			return move;
		}

		// TODO Add your code here
		int v = INFINITY; 
		v = (maxPlayer) ? -v : v;
		for(int i=0; i<successors.size();i++){
			//make the move
			int lastM = state.get_last_move();
			state.remove_stone(successors.get(i));
			if(maxPlayer){
				//max player
				int temp = alphabeta(state,depth-1,alpha, beta, !maxPlayer);
				//update the move
				if(v < temp || (v == temp && move > successors.get(i))){
					move = successors.get(i);
				}
				v = Math.max(v, temp);
				if(v >= beta){
					state.set_stone(successors.get(i));
					state.set_last_move(lastM);
					break;
				}
			}else{
				//min player
				int temp = alphabeta(state,depth-1,alpha, beta, !maxPlayer);
				//update the move
				if(v > temp || (v == temp && move > successors.get(i))){
					move = successors.get(i);
				}
				v = Math.min(v, temp);
				if(alpha >= v){
					state.set_stone(successors.get(i));
					state.set_last_move(lastM);
					break;
				}
			}
			//update the alpha and beta
			if(maxPlayer){
				alpha = Math.max(alpha, v);
			}else{
				beta = Math.min(beta, v);
			}
			//restore the move back
			state.set_stone(successors.get(i));
			state.set_last_move(lastM);
		}
		
		// Print state and alpha, beta before return 
		state.log();
		Helper.log_alphabeta(alpha, beta);
		return move;
	}


	/**
	* This method is used to implement alpha-beta pruning for both 2 players
	* @param state This is the current game state
	* @param depth Current depth of search
	* @param alpha Current Alpha value
	* @param beta Current Beta value
	* 
	* @param maxPlayer True if player is Max Player; Otherwise, false
	* @return int This is the number indicating score of the best next move
	*/
	public int alphabeta(GameState state, int depth, int alpha, int beta, boolean maxPlayer) {
		int v = INFINITY; // score of the best next move

		// TODO Add your code here
		if(depth==0){
			ArrayList<Integer> successors = generate_successors(state);
			if(successors.size()==0){
				v = maxPlayer ? LOSE_SCORE:WIN_SCORE;
			}else{
			v = evaluate_state(state);
		}
		}else{
		
		v = (maxPlayer) ? -v : v;
		ArrayList<Integer> successors = generate_successors(state);
		if(successors.size()==0){
			v = maxPlayer ? LOSE_SCORE:WIN_SCORE;
		}
		for(int i=0; i<successors.size();i++){
			//make the move
			int lastM = state.get_last_move();
			state.remove_stone(successors.get(i));
			if(maxPlayer){
				//maxplayer
				v = Math.max(v, alphabeta(state,depth-1,alpha, beta, !maxPlayer));
				if(v >= beta){
					state.set_stone(successors.get(i));
					state.set_last_move(lastM);
					break;
				}
				alpha = Math.max(alpha, v);
			}else{
				//minplayer
				v = Math.min(v, alphabeta(state,depth-1,alpha, beta, !maxPlayer));
				if(alpha >= v){
					state.set_stone(successors.get(i));
					state.set_last_move(lastM);
					break;	
				}
				beta = Math.min(beta, v);
			}
			//restore the move back
			state.set_stone(successors.get(i));
			state.set_last_move(lastM);
		}
		}
		// Print state and alpha, beta before return 
		state.log();
		Helper.log_alphabeta(alpha, beta);
		return v;	
	}


	/**
	* This is the main method which makes use of addNum method.
	* @param args A sequence of integer numbers, including the number of stones,
	* the number of taken stones, a list of taken stone and search depth
	* @return Nothing.
	* @exception IOException On input error.
	* @see IOException
	*/
	public static void main (String[] args) {
		try {
			// Read input from command line
			int n = Integer.parseInt(args[0]);		// the number of stones
			int nTaken = Integer.parseInt(args[1]);	// the number of taken stones
			
			// Initialize the game state
			GameState state = new GameState(n);		// game state
			int stone;
			for (int i = 0; i < nTaken; i++) {
				stone = Integer.parseInt(args[i + 2]);
				state.remove_stone(stone);
			}

			int depth = Integer.parseInt(args[nTaken + 2]);	// search depth
			// Process for depth being 0
			if (0 == depth)
				depth = n + 1;

			TakeStones player = new TakeStones();	// TakeStones Object
			boolean maxPlayer = (0 == (nTaken % 2));// Detect current player
			
			// Get next move
			int move = player.get_next_move(state, depth, maxPlayer);	
			// Remove the chosen stone out of the board
			state.remove_stone(move);

			// Print Solution 
			System.out.println("NEXT MOVE");
			state.log();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Invalid input");
		}
	}
}