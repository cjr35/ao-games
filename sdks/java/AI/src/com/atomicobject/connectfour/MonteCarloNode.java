package com.atomicobject.connectfour;

import java.util.List;

public class MonteCarloNode {
	private MonteCarloNode parent;
	private List<MonteCarloNode> children;
	private double temperature;
	private int totalValue;
	private int visits;
	private int zobristKey;

	public MonteCarloNode(MonteCarloNode parent, double temperature) {
		this.parent = parent;
		this.temperature = temperature;
		totalValue = 0;
		visits = 0;
	}

	public int getVisits() {
		return visits;
	}

	public double upperConfidenceBound() {
		double ucb;
		double avgValue = totalValue / (double) visits;

		ucb = avgValue + (temperature * Math.sqrt(Math.log(parent.getVisits()) / visits));

		return ucb;
	}

}
