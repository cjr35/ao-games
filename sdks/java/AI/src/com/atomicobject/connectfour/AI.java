package com.atomicobject.connectfour;

import java.util.*;

public class AI {
	private Random random;
	private int player;
	private float mcTemp;
	private int turnTimeLimit;
	private Map<String, MonteCarloNode> nodeCache;
	private MonteCarloNode lastNode;

	public AI() {
		random = new Random();
		mcTemp = 1.5f;
		nodeCache = new HashMap<>();
		lastNode = null;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public void setTurnTimeLimit(int turnTimeLimit) {
		this.turnTimeLimit = turnTimeLimit;
	}

	public int computeMove(GameState state) {
		MonteCarloNode localRoot = nodeCache.getOrDefault(state.getKey(), new MonteCarloNode(lastNode, state, mcTemp));
		mcts(localRoot);
		int move = mctsGetBestChild(localRoot);

		GameState chosenState = state.simulate(move);
		lastNode = nodeCache.getOrDefault(chosenState.getKey(), new MonteCarloNode(localRoot, chosenState, mcTemp));
		return move;
	}

	private void mcts(MonteCarloNode current) {
		if (current.isLeaf()) {
			if (current.getVisits() == 0) {
				mctsRollout(current);
			}
			else {
				MonteCarloNode next = mctsExpand(current);
				mctsRollout(next);
			}
		}
		else {
			int bestChild = mctsGetBestChild(current);
			mcts(current.getChildren()[bestChild]);
		}
	}

	private int mctsGetBestChild(MonteCarloNode parent) {
		float maxUCB = Float.MIN_VALUE;
		MonteCarloNode[] children = parent.getChildren();
		int bestChildIndex = -1;

		for (int i = 0; i < children.length; i++) {
			MonteCarloNode child = children[i];
			float ucb = child.upperConfidenceBound();
			if (ucb > maxUCB) {
				maxUCB = ucb;
				bestChildIndex = i;
			}
		}

		return bestChildIndex;
	}

	private MonteCarloNode mctsExpand(MonteCarloNode node) {
		boolean[] moveMatrix = node.STATE.getMoveMatrix();
		GameState childState;
		MonteCarloNode nextChild = null;
		for (int i = 0; i < moveMatrix.length; i++) {
			if (moveMatrix[i]) {
				childState = node.STATE.simulate(i);
				String childKey = childState.getKey();
				if (!nodeCache.containsKey(childKey)) {
					nextChild = new MonteCarloNode(node, childState, mcTemp);
					node.setChild(i, nextChild);
					nodeCache.put(childKey, nextChild);
				}
				else {
					node.setChild(i, nodeCache.get(childKey));
				}
			}
			else {
				node.setChild(i, null);
			}
		}
		return nextChild;
	}

	private void mctsRollout(MonteCarloNode node) {
		if (!node.STATE.isTerminal()) {
			GameState nextState;
			try {
				nextState = node.STATE.simulate(random.nextInt(7));
			}
			catch (IllegalArgumentException iax) {
				mctsRollout(node);
				return;
			}
			MonteCarloNode next = new MonteCarloNode(node, nextState, mcTemp);
			nodeCache.putIfAbsent(nextState.getKey(), next);
			mctsRollout(next);
		}
		else {
			node.propagate(10, 1);
		}
	}
}
