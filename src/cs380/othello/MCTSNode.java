package cs380.othello;

import java.util.List;
import java.util.LinkedList;
import java.util.Random;

/*
 * This is a wrap of a single node in Monte Carlo Tree Search.
 */
public class MCTSNode {
	
	final OthelloState current;
	// Where are parent and children and itself in the tree.
	Integer currentNodeNumber;
	Integer parentNodeNumber;
	List<Integer> childrenNodeNumbers = new LinkedList<Integer>();
	// Moves leads to this node.
	List<OthelloMove> MoveRecord = new LinkedList<OthelloMove>();
	// Available moves at this node.
	List<OthelloMove> AvailableMoves = new LinkedList<OthelloMove>();
	Integer VisitedTimes;
	float AvgScore;
	// If it has available moves.
	final boolean Terminal;
	
	// Constructor for root node. 
	public MCTSNode(OthelloState _state) {
		current = _state;
		parentNodeNumber = null;
		currentNodeNumber = 0;
		MoveRecord.clear();
		AvailableMoves = current.generateMoves();
		childrenNodeNumbers.clear();
		if (AvailableMoves.size() == 0) Terminal = true;
		else Terminal = false;
		VisitedTimes = 0;
		AvgScore = 0;
	}
	
	// Constructor for a derived node from another node. 
	public MCTSNode(MCTSNode _oldNode, OthelloMove _lastMove, Integer _previousNumber, Integer _newNumber) {
		// A new derived state, and inherit all move records. Its parent is this node derived form.
		current = _oldNode.current.applyMoveCloning(_lastMove);
		currentNodeNumber = _newNumber;
		parentNodeNumber = _previousNumber;
		MoveRecord.clear();
		MoveRecord.addAll(_oldNode.MoveRecord);
		MoveRecord.add(_lastMove);
		AvailableMoves = current.generateMoves();
		childrenNodeNumbers.clear();
		if (AvailableMoves.size() == 0) Terminal = true;	
		else Terminal = false;
		VisitedTimes = 0;
		AvgScore = 0;
	}
	
	// Update visited times and average score, called in backup function.
	public void update(Integer _score) {
		AvgScore = (AvgScore * VisitedTimes) + _score;
		VisitedTimes++;
		AvgScore = AvgScore / VisitedTimes;
	}
	
	// Return if this is a terminal node.
	public boolean isTerminalNode() {
		return Terminal;
	}
	
	// Randomly select a child and return its number.
	public Integer getRandomChild() {
		if (childrenNodeNumbers.size() == 0) return null;
		return new Integer(new Random().nextInt(childrenNodeNumbers.size()));
	}
	
	// Add children number record, if it has appeared, quit adding.
	public void addChildNumber(Integer _i) {
		if (_i <= currentNodeNumber) throw new IllegalArgumentException("Children node number must be greater than its own node number.\n");
		for (int i = 0; i < childrenNodeNumbers.size(); i++) 
			if (_i == childrenNodeNumbers.get(i)) return;
		childrenNodeNumbers.add(_i);
	}
	
	// Generate a new node from available move, remove the move just performed.
	public MCTSNode getNextNode() {
		if (AvailableMoves.size() > 0) {
			OthelloMove move = AvailableMoves.get(0);
			AvailableMoves.remove(0);
			return new MCTSNode(this, move, this.getCurrentNumber(), 0);
		} else return null;
	}
	
	// Update current position number in the tree.
	public void updateCurrentNumber(Integer _num) {
		if (_num > parentNodeNumber) currentNodeNumber = _num;
		else throw new IllegalArgumentException("Current number must be greater than its parent number.\n");
	}
	
	// Return the list of children.
	public List<Integer> getChildren() {
		return childrenNodeNumbers;
	}
	
	// Returns its average score.
	public float getScore() {
		return AvgScore;
	}
	
	// Return a copy of current state.
	public OthelloState getCurrentState() {
		return current.clone();
	}
	
	public Integer getCurrentNumber() {
		return new Integer(currentNodeNumber);
	}
	
	// Return current position.
	public Integer getParentNumber() {
		return new Integer(parentNodeNumber);
	}
	
	// Return a boolean to show if it is a root node.
	public boolean hasParent() {
		return parentNodeNumber != null;
	}
	
	// Return the last move that lead to current node.
	public OthelloMove getLastMove() {
		return MoveRecord.get(MoveRecord.size()-1);
	}
	
	// To show if it has children that not returned and added in the tree. 
	public boolean hasAvailableChildren() {
		return AvailableMoves.size() > 0;
	}
	
}
