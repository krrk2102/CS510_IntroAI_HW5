package cs380.othello;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/*
 * A class used for regular Monte Carlo Tree Search for Othello.
 */
public class OthelloMCTSPlayer_ShangqiWu extends OthelloPlayer {
	
	final int player_number;
	final int iteration_boundary;
	// This list stores MCTS tree nodes for every search. Class MCTSNode is defined in MCTSNode.java
	List<MCTSNode> NodeInTree = new ArrayList<MCTSNode>();
	
	/*
	 * Constructor of this player, player number and iteration boundary should be required.
	 */
	public OthelloMCTSPlayer_ShangqiWu(int _player_number, int _iteration_boundary) {
		if (_player_number == 0 || _player_number == 1) player_number = _player_number;
		else throw new IllegalArgumentException("Player number for OthelloMinimaxPlayer constructor should be either 0 or 1.\n");
		if (_iteration_boundary > 0) iteration_boundary = _iteration_boundary;
		else throw new IllegalArgumentException("Iteration boundasy has to be a positive integer.\n");
		NodeInTree.clear();
	}
	
	/*
	 * The method inherited from OthelloPlayer.
	 */
	public OthelloMove getMove(OthelloState state){
		NodeInTree.clear();
		return MonteCarloTreeSearch(state);
	}
	
	/*
	 * Entry of Monte Carlo Tree Search algorithm. 
	 */
	private OthelloMove MonteCarloTreeSearch(OthelloState _state) {
		// Create root node for this tree. 
		MCTSNode rootNode = new MCTSNode(_state);
		// If this tree has no available moves, quit the search. 
		if (rootNode.isTerminalNode()) return null;
		// Add root into the tree list.
		NodeInTree.add(rootNode);
		// Execute iterations. 
		for (int i = 0; i < iteration_boundary; i++) {
			// node = treePolicy(root node), to generate a new node of the tree.
			Integer nodeNumber = treePolicy(rootNode.getCurrentNumber());
			// defaultPolicy function continues the state randomly and return a final score.
			// backup function for average score calculation. 
			if (nodeNumber != null) backup(nodeNumber, defaultPolicy(nodeNumber));
		}
		// Return the move lead to the child has the highest average score. 
		return NodeInTree.get(bestChild(rootNode.getCurrentNumber())).getLastMove();
	}
	
	/*
	 * The method to generate new node for search. 
	 */
	private Integer treePolicy(Integer _parentNodeNumber) {
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
				// This integer will be 0~9.
				Integer prob = new Integer(new Random().nextInt(10));
				// 10% times are supposed to go into if, while 90% times are supposed to go into else.
				// 10% times it will choose a random child, and recurse treePolicy(newRandomChild) for another time.
				if (prob == 9) return treePolicy(NodeInTree.get(_parentNodeNumber).getRandomChild());
				// 90% times it will execute bestChild(node), and recurse treePolicy for another time.
				else return treePolicy(bestChild(_parentNodeNumber));
			}
		}
	}
	
	/*
	 * The defaultPolicy is to continue to play the game randomly until the game is over, then returns the final score. 
	 */
	private Integer defaultPolicy(Integer _newNodeNumber) {
		// Use 2 random players, and a copy of state and then continue the game.
		OthelloRandomPlayer[] player = {new OthelloRandomPlayer(), new OthelloRandomPlayer()};
		OthelloState currentState = NodeInTree.get(_newNodeNumber).getCurrentState();
		// The same procedure as 2 random player.
		while (!currentState.gameOver()) currentState = currentState.applyMoveCloning(player[currentState.nextPlayerToMove].getMove(currentState));
		// Return the final score.
		return new Integer(currentState.score());
	}
	
	/*
	 * The method to update average score for each node, also for its parent nodes, recursively.
	 */
	private void backup(Integer _oldNodeNumber, Integer _score) {
		NodeInTree.get(_oldNodeNumber).update(_score);
		if (NodeInTree.get(_oldNodeNumber).hasParent()) backup(NodeInTree.get(_oldNodeNumber).getParentNumber(), _score);
	}
	
	/*
	 * The method to choose best children. 
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
