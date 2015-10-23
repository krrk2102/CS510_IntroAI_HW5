package cs380.othello;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/*
 * This is the Tournament Monte Carlo Tree Search player, which uses a time boundary to terminate instead of using iteration boundary.
 */
public class OthelloMCTSPlayerTournament_ShangqiWu extends OthelloPlayer {
	
	final int player_number;
	final long time_boundary;
	long end_time;
	List<MCTSNode> NodeInTree = new ArrayList<MCTSNode>();
	
	/*
	 * The constructor with time boundary rather than iteration boundary.
	 */
	public OthelloMCTSPlayerTournament_ShangqiWu(int _player_number, long _time_boundary) {
		if (_player_number == 0 || _player_number == 1) player_number = _player_number;
		else throw new IllegalArgumentException("Player number for OthelloMinimaxPlayer constructor should be either 0 or 1.\n");
		if (_time_boundary > 0) time_boundary = _time_boundary;
		else throw new IllegalArgumentException("Time boundasy has to be a positive integer.\n");
		NodeInTree.clear();
		end_time = 0;
	}
	
	/*
	 * Inherited getMove() method.
	 */
	public OthelloMove getMove(OthelloState state){
		// Clear the tree after each search.
		NodeInTree.clear();
		// Calculate when the program should come to an end.
		end_time = System.currentTimeMillis() + time_boundary;
		return MonteCarloTreeSearch(state);
	}
	
	/*
	 * Entry for Monte Carlo Tree Search. 
	 */
	private OthelloMove MonteCarloTreeSearch(OthelloState _state) {
		// Create root node.
		MCTSNode rootNode = new MCTSNode(_state);
		// If it has no available moves, it returns null. 
		if (rootNode.isTerminalNode()) return null;
		NodeInTree.add(rootNode);
		// Instead of using for statement for iterations, it checks time boundary after each loop. 
		do {
			// Generate new nodes and perform random play. Update average score to relevant nodes.
			Integer nodeNumber = treePolicy(rootNode.currentNodeNumber);
			if (nodeNumber != null) backup(nodeNumber, defaultPolicy(nodeNumber));
		} while (System.currentTimeMillis() < end_time);
		// Return the best move.
		return NodeInTree.get(bestChild(rootNode.getCurrentNumber())).getLastMove();
	}
	
	/*
	 * The method to generate new nodes and add it to the tree, returns it at last. 
	 */
	private Integer treePolicy(Integer _parentNodeNumber) {
		if (System.currentTimeMillis() < end_time) {
			// If the node is a terminal node, return this.
			if (NodeInTree.get(_parentNodeNumber).isTerminalNode()) return _parentNodeNumber;
			else {
				// If this is not a terminal node.
				if (NodeInTree.get(_parentNodeNumber).hasAvailableChildren()) {
					// And if it has children that not in the tree, generates a new child and add it to the tree. 
					NodeInTree.add(NodeInTree.get(_parentNodeNumber).getNextNode());
					NodeInTree.get(NodeInTree.size()-1).updateCurrentNumber(NodeInTree.size()-1);
					NodeInTree.get(_parentNodeNumber).addChildNumber(NodeInTree.size()-1);
					return NodeInTree.size()-1;
				} else {
					// And its all children are in the tree.
					Integer prob = new Integer(new Random().nextInt(10));
					// 10% times are supposed to go into if, while 90% times are supposed to go into else. 
					// 10% times it will choose a random child, and return treePolicy(newRandomChild). 
					if (prob == 9) return treePolicy(NodeInTree.get(_parentNodeNumber).getRandomChild());
					// 90% times it will execute bestChild(node), and recurse treePolicy.
					else return treePolicy(bestChild(_parentNodeNumber));
				}
			}
		// If time is up, quit this function. 
		} else return null;
	}
	
	/*
	 * Perform random play. It will return a score either when game is over or time is up.
	 */
	private Integer defaultPolicy(Integer _newNodeNumber) {
		OthelloRandomPlayer[] player = {new OthelloRandomPlayer(), new OthelloRandomPlayer()};
		OthelloState currentState = NodeInTree.get(_newNodeNumber).getCurrentState();
		while (!currentState.gameOver() && System.currentTimeMillis() < end_time) currentState = currentState.applyMoveCloning(player[currentState.nextPlayerToMove].getMove(currentState));
		return new Integer(currentState.score());
	}
	
	/*
	 * Updating average score for relevant nodes. This method is atomic and should not be disturbed for time up.
	 */
	private void backup(Integer _oldNodeNumber, Integer _score) {
		NodeInTree.get(_oldNodeNumber).update(_score);
		if (NodeInTree.get(_oldNodeNumber).hasParent()) backup(NodeInTree.get(_oldNodeNumber).getParentNumber(), _score);
	}
	
	/*
	 * Choosing best child that has highest average score. This method is also atomic and should not be disturbed for time up.
	 */
	private Integer bestChild(Integer _rootNumber) {
		// Get a list of all children of the input node.
		List<Integer> Children = NodeInTree.get(_rootNumber).getChildren();
		// If it has no children, return null.
		if (Children.size() == 0) return null;
		// If this is player1, it returns a children number has a max average score.
		if (player_number == 0) {
			float bestScore = Integer.MIN_VALUE;
			int bestChildrenNumber = 0;
			for (int i = 0; i < Children.size(); i++) 
				if (NodeInTree.get(Children.get(i)).getScore() > bestScore) {
					bestScore = NodeInTree.get(Children.get(i)).getScore();
					bestChildrenNumber = i;
				}
			return Children.get(bestChildrenNumber);
		} else {
			// If this is player2, it returns a children number has a min average score.
			float bestScore = Integer.MAX_VALUE;
			int bestChildrenNumber = 0;
			for (int i = 0; i < Children.size(); i++) 
				if (NodeInTree.get(Children.get(i)).getScore() < bestScore) {
					bestScore = NodeInTree.get(Children.get(i)).getScore();
					bestChildrenNumber = i;
				}
			return Children.get(bestChildrenNumber);
		}
	}

}
