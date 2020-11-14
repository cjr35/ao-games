package com.atomicobject.connectfour;

import java.util.List;

public class MonteCarloNode {
	private MonteCarloNode parent;
	private List<MonteCarloNode> children;
	private int totalValue;
	private int visits;
}
