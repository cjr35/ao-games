package com.atomicobject.connectfour;

import java.util.Collections;
import java.util.List;

public class MonteCarloNode {
	private MonteCarloNode parent;
	private MonteCarloNode[] children;
	private float temperature;
	private int totalValue;
	private int visits;
	public final GameState STATE;

	public MonteCarloNode(MonteCarloNode parent, GameState state, float temperature) {
		this.parent = parent;
		this.STATE = state;
		this.temperature = temperature;
		children = new MonteCarloNode[7];
		totalValue = 0;
		visits = 0;
	}

	public int getVisits() {
		return visits;
	}

	/**
	 * Compute the Upper Confidence Bound 1 of a node
	 */
	public float upperConfidenceBound() {
		float ucb;
		float avgValue = totalValue / (float) visits;

		try {
			ucb = (float) (avgValue + (temperature * Math.sqrt(Math.log(parent.getVisits()) / visits)));
		}
		catch (ArithmeticException ax) {
			return Float.MAX_VALUE;
		}

		return ucb;
	}

	public void setChild(int move, MonteCarloNode child) {
		if (children[move] == null) {
			propagate(child.getTotalValue(), child.getVisits());
		}
		children[move] = child;
	}

	private int getTotalValue() {
		return totalValue;
	}

	public boolean isLeaf() {
		for (MonteCarloNode child : children) {
			if (child != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Propagate value from a leaf node back up to the root of the tree
	 * @param value the value of the game state at the leaf node
	 */
	public void propagate(int value, int visits) {
		totalValue += value;
		this.visits += visits;
		if (parent != null) {
			parent.propagate(value, visits);
		}
	}

	public MonteCarloNode[] getChildren() {
		return children;
	}

}
