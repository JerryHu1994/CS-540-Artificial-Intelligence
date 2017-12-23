/**
 * A data-structure to keep (state,fValue) pairs. This class will helpful to add
 * states to priority queues. You can add a state with the f() value to a queue
 * by adding (state,f() value) pair to the priority queue.
 * 
 * You do not need to change this class.
 */
public class StateFValuePair implements Comparable<StateFValuePair> {
	private State state;
	private double fValue;

	public StateFValuePair(State state, double fValue) {
		this.state = state;
		this.fValue = fValue;
	}

	public State getState() {
		return state;
	}

	public double getFValue() {
		return fValue;
	}

	/***
	 * @return if this object is equal to the object given
	 */
	
	@Override
	public int compareTo(StateFValuePair o) {
		if (this.fValue > o.fValue)
			return 1;
		else if (this.fValue < o.fValue)
			return -1;

		return 0;
	}
	/***
	 * @return if this object is equal to the object given
	 */
	public boolean equals(Object _that){
		if (_that instanceof StateFValuePair){
			StateFValuePair that = (StateFValuePair)_that;
			return that.state.equals(this.state);
		}
		return false;
	}

}
