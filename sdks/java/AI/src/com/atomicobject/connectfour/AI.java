package com.atomicobject.connectfour;

import java.util.*;

public class AI {
	private Random random;
	private int player;
	private float mcTemp;
	private int turnTimeLimit;
	private Map<String, MonteCarloNode> nodeCache;
	private MonteCarloNode lastNode;
	private int[] rolloutMoves;
	private int rollouts;

	public AI() {
		random = new Random();
		mcTemp = 2.0f;
		nodeCache = new HashMap<>();
		lastNode = null;
		rolloutMoves = new int[7];
		rollouts = 0;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public void setTurnTimeLimit(int turnTimeLimit) {
		this.turnTimeLimit = turnTimeLimit;
	}

	public int computeMove(GameState state) {
		MonteCarloNode localRoot = nodeCache.getOrDefault(state.getKey(), new MonteCarloNode(lastNode, state, mcTemp));
		MonteCarloNode[] children = localRoot.getChildren();
//		for (int i = 0; i < children.length; i++) {
//			if
//		}

		mctsExpand(localRoot);
//		localRoot.print(1);
//		System.out.println("--------------------------------------------------------------------------");
		for (int i = 0; i < 50000; i++) {
			mcts(localRoot);
//			localRoot.print(1);
//			System.out.println("--------------------------------------------------------------------------");
		}
		float bestValue = -Float.MAX_VALUE;
		int move = -1;
		for (int i = 0; i < children.length; i++) {
			MonteCarloNode child = children[i];
			if (child != null && child.upperConfidenceBound() > bestValue) {
				bestValue = child.averageValue();
				move = i;
			}
		}
//		for (MonteCarloNode child : children) {
//			String s = child == null ? "null" : String.format("value: %f\t\t ucb: %f\t\t visits: %d", child.averageValue(), child.upperConfidenceBound(), child.getVisits());
//			System.out.println(s);
//		}
//		System.out.println("parent visits: " + localRoot.getVisits());

		GameState chosenState = state.simulate(move);
		lastNode = nodeCache.getOrDefault(chosenState.getKey(), new MonteCarloNode(localRoot, chosenState, mcTemp));
		return move;
	}

	private void mcts(MonteCarloNode current) {
		if (current.isLeaf()) {
			if (current.getVisits() == 0) {
				mctsRollout(current);
			}
			else if (!current.STATE.isTerminal()) {
				MonteCarloNode next = mctsExpand(current);
				mctsRollout(next);
			}
			else {
				float value = current.STATE.evaluate(player);
				current.propagate(value, 1);
			}
		}
		else {
			int bestChild = mctsGetPromisingChild(current);
			mcts(current.getChildren()[bestChild]);
		}
	}

	private int mctsGetPromisingChild(MonteCarloNode parent) {
		float maxUCB = -Float.MAX_VALUE;
		MonteCarloNode[] children = parent.getChildren();
		int promisingChildIndex = -1;

		for (int i = 0; i < children.length; i++) {
			MonteCarloNode child = children[i];
			float ucb;
			if (child == null) {
				if (parent.STATE.getMoveMatrix()[i]) {
					ucb = Float.MAX_VALUE;
					parent.setChild(i, new MonteCarloNode(parent, parent.STATE.simulate(i), mcTemp));
				}
				else {
					continue;
				}
			}
			else {
				ucb = child.upperConfidenceBound();
			}
//			System.out.print("UCB: " + ucb + "\t");
			if (ucb > maxUCB) {
				maxUCB = ucb;
				promisingChildIndex = i;
			}
		}
//		System.out.println();
		return promisingChildIndex;
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
			int tryMove = random.nextInt(7);
			while (!node.STATE.getMoveMatrix()[tryMove]) {
				tryMove = (tryMove + 1) % 7;
			}
			GameState nextState = node.STATE.simulate(tryMove);
			rolloutMoves[tryMove]++;
			MonteCarloNode next = new MonteCarloNode(node, nextState, mcTemp);
			if (!nodeCache.containsKey(nextState.getKey())) {
				nodeCache.put(nextState.getKey(), next);
			}
			node.setChild(tryMove, next);
			mctsRollout(next);
		}
		else {
			float value = node.STATE.evaluate(player);
			node.propagate(value, 1);
			rollouts++;
		}
	}

	public void printData() {
		System.out.println(Arrays.toString(rolloutMoves));
		int tot = rolloutMoves[0] + rolloutMoves[1] + rolloutMoves[2] + rolloutMoves[3] + rolloutMoves[4] + rolloutMoves[5] + rolloutMoves[6];
		System.out.println("total simulated moves: " + tot);
		System.out.println("total rollouts: " + rollouts);
		System.out.println("avg moves per rollout: " + (float) tot / (float) rollouts);
	}
}
