package kMST;

import java.util.HashMap;
import java.util.LinkedList;

public class Component<E, V> {
	public double potential;
	public double dual;
	public boolean haschildren;
	public double timeofformation;
	public double respotenform;
	public double dtimeofform;	//factor for change in time of formation for unit change in initial potential
	public double drespotenform;	//factor for change in residual potential at time of formation for unit change in initial potential
	public double dfinaldual;	//factor for change in final dual for unit change in initial potential - set after it joins
	public Component<E, V> child1, child2;
	public V vertex;
	public ComplexEdge<E,V> edge;
	public LinkedList<ComplexEdge<E,V>> edgelist;
	public LinkedList<V> vertexlist;
	public HashMap<V, Integer> vertexmap;
	public int colour; // 0 = white, 2 = black

	public Component(Component<E, V> Component1, Component<E, V> Component2,
			ComplexEdge<E,V> joiningedge, double time) {
		child1 = Component1;
		child2 = Component2;
		edge = joiningedge;
		haschildren = true;
		joiningedge.timeoftight = time;
		joiningedge.joinedcomp = this; //Is this the correct way to do it?
		dual = 0;
		vertex = null;
		potential = Component1.potential + Component2.potential;
		respotenform = potential;
		colour = 0;
		timeofformation = time;
		edgelist = new LinkedList<ComplexEdge<E,V>>();
		edgelist.addAll(Component1.edgelist);
		edgelist.addAll(Component2.edgelist);
		edgelist.add(joiningedge);
		vertexmap = new HashMap<V,Integer>(Component1.vertexmap.size()+Component2.vertexmap.size());
		vertexmap.putAll(Component1.vertexmap);
		vertexmap.putAll(Component2.vertexmap);
		vertexlist = new LinkedList<V>();
		vertexlist.addAll(Component1.vertexlist);
		vertexlist.addAll(Component2.vertexlist);
		
		//setting all dthings. Recheck.
		dtimeofform-=joiningedge.dsumdual;
		if(child1.colour==0)
			dtimeofform+=child1.dtimeofform;
		else
			dtimeofform-=child1.drespotenform;
		if(child2.colour==0)
			dtimeofform+=child2.dtimeofform;
		else
			dtimeofform-=child2.drespotenform;
		if(child1.colour==0&&child2.colour==0)
			dtimeofform = dtimeofform/2;
		
		//now setting dfinaldual
		if(child1.colour==0)
			child1.dfinaldual = dtimeofform - child1.dtimeofform;
		else
			child1.dfinaldual = child1.drespotenform;
		if(child2.colour==0)
			child2.dfinaldual = dtimeofform - child2.dtimeofform;
		else
			child2.dfinaldual = child2.drespotenform;
		
		//now setting drespotenform
		drespotenform = (child1.drespotenform - child1.dfinaldual) + (child2.drespotenform - child2.dfinaldual);
	}

	public Component(V startingvertex, double startingpotential,double time) {
		potential = startingpotential;
		timeofformation = time;
		respotenform = potential;
		dtimeofform = 0;
		drespotenform = 1;
		dual = 0;
		haschildren = false;
		colour = 0;
		vertex = startingvertex;
		edgelist = new LinkedList<ComplexEdge<E,V>>();
		vertexlist = new LinkedList<V>();
		vertexlist.add(vertex);
		vertexmap = new HashMap<V,Integer>(1);
		vertexmap.put(vertex, 1);
		edge = null;
		child1 = null;
		child2 = null;

	}
}
