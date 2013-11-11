package kMST;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.graph.AbstractBaseGraph;

//Highly untested. Use it for return types and basic flow.

public class BoringLongGW<E, V> {
	public AbstractBaseGraph<V, E> graph;
	public AbstractBaseGraph<V, E> forest;
	public LinkedList<ComplexEdge<E>> edgelist = new LinkedList<ComplexEdge<E>>();
	public LinkedList<Component<ComplexEdge<E>, V>> componentlist = new LinkedList<Component<ComplexEdge<E>, V>>();
	public double time;
	public HashMap<V, Component<ComplexEdge<E>, V>> verttocomp;

	public double initpotential;
	public int numofvertices;
	public int numofedges;
	public Set<V> vertices;
	public Set<E> edges;
	public LinkedList<Double> time_increments = new LinkedList<Double>();
	public LinkedList<E> edge_orders = new LinkedList<E>();
	public LinkedList<Component<ComplexEdge<E>, V>> black_orders = new LinkedList<Component<ComplexEdge<E>, V>>();
	
	//The main function to call
	public void runGW(){
		boolean flag = true;
		while(flag){
			flag = increasetime();
		}
		prune();
	}
	
	public BoringLongGW(AbstractBaseGraph<V, E> graph1, double p) {
		time = 0;
		initpotential = p;
		graph = graph1;
		vertices = graph.vertexSet();
		numofvertices = vertices.size();
		edges = graph.edgeSet();
		numofedges = edges.size();
		verttocomp = new HashMap<V, Component<ComplexEdge<E>, V>>(numofvertices);
		Iterator<V> iter = vertices.iterator();
		for (int i = 0; i < numofvertices; i++) {
			V vertex = iter.next();
			Component<ComplexEdge<E>, V> comp = new Component<ComplexEdge<E>, V>(vertex, p);
			verttocomp.put(vertex, comp);
			componentlist.add(comp);
		}
		Iterator<E> iter1 = edges.iterator();
		for (int i = 0; i < numofedges; i++) {
			E tempedge = iter1.next();
			ComplexEdge<E> cedge = new ComplexEdge<E>(tempedge, initpotential);
			edgelist.add(cedge);
		}
	}

	public boolean increasetime() {
		double edgetime = Double.MAX_VALUE;
		ComplexEdge<E> minedge = null; // i need to change the null, right?

		// find min edge
		Iterator<ComplexEdge<E>> iter = edgelist.listIterator();
		while (iter.hasNext()) {
			ComplexEdge<E> cedge = iter.next();
			V vertex1 = graph.getEdgeSource(cedge.edge);
			V vertex2 = graph.getEdgeTarget(cedge.edge);
			int colour1 = verttocomp.get(vertex1).colour;
			int colour2 = verttocomp.get(vertex2).colour;
			double temptime = Double.MAX_VALUE;
			if (colour1 == 0 && colour2 == 0)
				temptime = cedge.potential / 2;
			else if (colour1 == 0 || colour2 == 0)
				temptime = cedge.potential;
			if (temptime < edgetime) {
				minedge = cedge;
				edgetime = temptime;
			}
		}

		double componenttime = Double.MAX_VALUE;
		Component<ComplexEdge<E>, V> mincomponent = null; // i need to change
															// the null, right?
		boolean allpassive = true;

		// find min component
		Iterator<Component<ComplexEdge<E>, V>> iter1 = componentlist.listIterator();
		while (iter1.hasNext()) {
			Component<ComplexEdge<E>, V> tempcomp = iter1.next();
			if (tempcomp.colour == 0)
				allpassive = false;
			if (tempcomp.colour == 0 && tempcomp.potential < componenttime) {
				componenttime = tempcomp.potential;
				mincomponent = tempcomp;
			}
		}

		// if all are passive, terminate.
		if (allpassive == true) {
			return false;
		}

		// if component gets passive first
		if (componenttime < edgetime) {
			time += componenttime;
			time_increments.addLast(time);

			mincomponent.colour = 2;
			black_orders.addLast(mincomponent);

			Iterator<ComplexEdge<E>> iter3 = edgelist.listIterator();
			while (iter3.hasNext()) {
				ComplexEdge<E> cedge = iter.next();
				V vertex1 = graph.getEdgeSource(cedge.edge);
				V vertex2 = graph.getEdgeTarget(cedge.edge);
				int colour1 = verttocomp.get(vertex1).colour;
				int colour2 = verttocomp.get(vertex2).colour;
				if (colour1 == 0 && colour2 == 0)
					cedge.potential -= 2 * componenttime;
				else if (colour1 == 0 || colour2 == 0)
					cedge.potential -= componenttime;
			}

			Iterator<Component<ComplexEdge<E>, V>> iter2 = componentlist.listIterator();
			while (iter2.hasNext()) {
				Component<ComplexEdge<E>, V> tempcomp = iter2.next();
				if (tempcomp.colour == 0) {
					tempcomp.potential -= componenttime;
					tempcomp.dual += componenttime;
				}
			}
		}
		// else edge tightens
		else {
			time += edgetime;
			time_increments.addLast(time);

			Component<ComplexEdge<E>, V> comp1 = verttocomp.get(graph.getEdgeSource(minedge.edge));
			Component<ComplexEdge<E>, V> comp2 = verttocomp.get(graph.getEdgeTarget(minedge.edge));
			Component<ComplexEdge<E>, V> joinedcomp = new Component<ComplexEdge<E>, V>(comp1, comp2, minedge);
			edge_orders.addLast(minedge.edge);
			forest.addEdge(graph.getEdgeSource(minedge.edge), graph.getEdgeTarget(minedge.edge), minedge.edge);

			// remove the two components
			Iterator<Component<ComplexEdge<E>, V>> iter2 = componentlist.listIterator();
			while (iter2.hasNext()) {
				Component<ComplexEdge<E>, V> tempcomp = iter2.next();
				if (tempcomp.colour == 0) {
					tempcomp.potential -= edgetime;
					tempcomp.dual += edgetime;
				}
				if (tempcomp == comp1 || tempcomp == comp2) {
					iter2.remove();
				}
			}

			// remove edges between these components
			Iterator<ComplexEdge<E>> iter3 = edgelist.listIterator();
			while (iter3.hasNext()) {
				ComplexEdge<E> cedge = iter.next();
				V vertex1 = graph.getEdgeSource(cedge.edge);
				V vertex2 = graph.getEdgeTarget(cedge.edge);
				int colour1 = verttocomp.get(vertex1).colour;
				int colour2 = verttocomp.get(vertex2).colour;
				if (colour1 == 0 && colour2 == 0)
					cedge.potential -= 2 * edgetime;
				else if (colour1 == 0 || colour2 == 0)
					cedge.potential -= edgetime;
				if ((verttocomp.get(vertex1) == comp1 && verttocomp.get(vertex2) == comp2)
						|| (verttocomp.get(vertex2) == comp1 && verttocomp.get(vertex1) == comp2)) {
					iter3.remove();
				}
			}

			// change the vertexmapping
			componentlist.add(joinedcomp);
			Iterator<V> iter4 = vertices.iterator();
			for (int i = 0; i < numofvertices; i++) {
				V vertex = iter4.next();
				Component<ComplexEdge<E>, V> vcomp = verttocomp.get(vertex);
				if (vcomp == comp1 || vcomp == comp2) {
					verttocomp.put(vertex, joinedcomp);
				}
			}

		}
		return true;
	}

	public void prune(){
		Iterator<Component<ComplexEdge<E>, V>> citer = black_orders.descendingIterator();
		while(citer.hasNext()){
			Component<ComplexEdge<E>, V> comp = citer.next();
			int outgoingedgecount = 0;
			E badedge = null; //check
			Iterator <V> viter = comp.vertexlist.listIterator();
			while(viter.hasNext()){
				V vert = viter.next();
				Set<E> edgeset = forest.edgesOf(vert);
				Iterator<E> eiter = edgeset.iterator();
				while(eiter.hasNext()){
					E edge1 = eiter.next();
					V svert = forest.getEdgeSource(edge1);
					V tvert = forest.getEdgeTarget(edge1);
					if(svert == vert){
						if(!comp.vertexmap.containsKey(tvert)){
							outgoingedgecount++;
							badedge = edge1;
						}
					}
					else{
						if(!comp.vertexmap.containsKey(svert)){
							outgoingedgecount++;
							badedge = edge1;
						}
					}
				}
				
			}
			if (outgoingedgecount==1){
				forest.removeEdge(badedge);
			}
		}
	}
}