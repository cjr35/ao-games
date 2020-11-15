package com.atomicobject.connectfour;

import java.util.Arrays;
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
		if (visits == 0) {
			return Float.MAX_VALUE;
		}

		float ucb;
		float avgValue = (float) totalValue / (float) visits;
		float logVpOverV = (float) (Math.log(parent.getVisits()) / (float) visits);
		ucb = (float) (avgValue + (temperature * Math.sqrt(logVpOverV)));

		return ucb;
	}

	public float averageValue() {
//		if (visits == 0) {
//			return Float.MAX_VALUE;
//		}
		return (float) totalValue / (float) visits;
	}

	public void setChild(int move, MonteCarloNode newChild) {
		if (newChild == null) {
			children[move] = newChild;
			return;
		}
		if (children[move] == null && newChild.getVisits() > 0) {
			propagate(newChild.getTotalValue(), newChild.getVisits());
		}
		else if (children[move] != null) {
//			propagate(newChild.getTotalValue() - children[move].getTotalValue(), newChild.getVisits() - children[move].getVisits());
		}
		children[move] = newChild;
	}

	public MonteCarloNode getParent() {
		return parent;
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

	public void print(int plies) {
		System.out.print(plies + ": ");
		System.out.println(this);
		if (plies > 0) {
			plies--;
			for (MonteCarloNode child : children) {
				if (child != null) {
					child.print(plies);
				}
				else {
					System.out.println("null");
				}
			}
		}
	}

	@Override
	public String toString() {
		String out = "STATE:\n\tboard:\n";
		for (int[] row : STATE.getBoard()) {
			out += "\t\t" + Arrays.toString(row) + "\n";
		}
		out += "\tplayer: " + STATE.getPlayer() + "\n";
		out += "\tstate value for player: " + STATE.evaluate(STATE.getPlayer()) + "\n";
		out += "NODE:\n";
		out += "\ttotal visits: " + visits + "\n";
		out += "\ttotal value: " + totalValue + "\n";
		out += "\tparent null: " + (parent == null);
		return out;
	}

}
