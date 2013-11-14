package kMST;

import java.util.HashMap;
import java.util.LinkedList;

public class Component<E, V> {
	public double potential;
	public double dual;
	public boolean haschildren;
	public double timeofformation;
	public double respotenform;
	public double child1dual;
	public double child2dual;
	public Component<E, V> child1, child2;
	public V vertex;
	public ComplexEdge<E> edge;
	public LinkedList<ComplexEdge<E>> edgelist;
	public LinkedList<V> vertexlist;
	public HashMap<V, Integer> vertexmap;
	public int colour; // 0 = white, 2 = black

	public Component(Component<E, V> Component1, Component<E, V> Component2,
			ComplexEdge<E> joiningedge, double time) {
		child1 = Component1;
		child2 = Component2;
		edge = joiningedge;
		haschildren = true;
		joiningedge.timeoftight = time;
		dual = 0;
		vertex = null;
		potential = Component1.potential + Component2.potential;
		child1dual = Component1.dual;
		child2dual = Component2.dual;
		respotenform = potential;
		colour = 0;
		timeofformation = time;
		edgelist = new LinkedList<ComplexEdge<E>>();
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

	public Component(V startingvertex, double startingpotential,double time) {
		potential = startingpotential;
		timeofformation = time;
		respotenform = potential;
		child1dual = 0;
		child2dual = 0;
		dual = 0;
		haschildren = false;
		colour = 0;
		vertex = startingvertex;
		edgelist = new LinkedList<ComplexEdge<E>>();
		vertexlist = new LinkedList<V>();
		vertexlist.add(vertex);
		vertexmap = new HashMap<V,Integer>(1);
		vertexmap.put(vertex, 1);
		edge = null;
		child1 = null;
		child2 = null;

	}
}
