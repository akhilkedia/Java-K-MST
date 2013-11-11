package kMST;

import java.util.HashMap;
import java.util.LinkedList;

public class Component<CE, V> {
	public double potential;
	public double dual;
	public boolean haschildren;
	public Component<CE, V> child1, child2;
	public V vertex;
	public CE edge;
	public LinkedList<CE> edgelist;
	public LinkedList<V> vertexlist;
	public HashMap<V, Integer> vertexmap;
	public int colour; // 0 = white, 2 = black

	public Component(Component<CE, V> Component1, Component<CE, V> Component2,
			CE joiningedge) {
		child1 = Component1;
		child2 = Component2;
		edge = joiningedge;
		haschildren = true;
		dual = 0;
		vertex = null;
		potential = Component1.potential + Component2.potential;
		colour = 0;
		edgelist = new LinkedList<CE>();
		edgelist.addAll(Component1.edgelist);
		edgelist.addAll(Component2.edgelist);
		edgelist.add(joiningedge);
		vertexmap = new HashMap<V,Integer>(Component1.vertexmap.size()+Component2.vertexmap.size());
		vertexmap.putAll(Component1.vertexmap);
		vertexmap.putAll(Component2.vertexmap);
		vertexlist = new LinkedList<V>();
		vertexlist.addAll(Component1.vertexlist);
		vertexlist.addAll(Component2.vertexlist);
	}

	public Component(V startingvertex, double startingpotential) {
		potential = startingpotential;
		dual = 0;
		haschildren = false;
		colour = 0;
		vertex = startingvertex;
		edgelist = new LinkedList<CE>();
		vertexlist = new LinkedList<V>();
		vertexlist.add(vertex);
		vertexmap = new HashMap<V,Integer>(1);
		vertexmap.put(vertex, 1);
		edge = null;
		child1 = null;
		child2 = null;

	}
}
