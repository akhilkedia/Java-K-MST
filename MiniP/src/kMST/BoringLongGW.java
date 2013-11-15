package kMST;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.graph.SimpleWeightedGraph;

//Highly untested. Use it for return types and basic flow.

public class BoringLongGW<E, V> {
	public SimpleWeightedGraph<V, E> graph;
	public SimpleWeightedGraph<V, E> forest;
	public LinkedList<ComplexEdge<E,V>> edgelist = new LinkedList<ComplexEdge<E,V>>();
	public LinkedList<Component<E, V>> componentlist = new LinkedList<Component<E, V>>();
	public double time;
	public HashMap<V, Component<E, V>> verttocomp;

	public double initpotential;
	public int numofvertices;
	public int numofedges;
	public Set<V> vertices;
	public Set<E> edges;
	public LinkedList<Double> time_increments = new LinkedList<Double>();
	public LinkedList<ComplexEdge<E,V>> edge_orders = new LinkedList<ComplexEdge<E,V>>();
	public LinkedList<Component<E, V>> black_orders = new LinkedList<Component<E, V>>();
	
	//The main function to call
	public void runGW(){
		boolean flag = true;
		while(flag){
			flag = increasetime();
		}
		prune();
	}
	
	public BoringLongGW(SimpleWeightedGraph<V, E> graph1,Class<E> Edgetype, double p) {
		time = 0;
		initpotential = p;
		graph = graph1;
		vertices = graph.vertexSet();
		numofvertices = vertices.size();
		edges = graph.edgeSet();
		numofedges = edges.size();
		verttocomp = new HashMap<V, Component<E, V>>(numofvertices);
		Iterator<V> iter = vertices.iterator();
		forest = new SimpleWeightedGraph<V,E>(Edgetype);
		for (int i = 0; i < numofvertices; i++) {
			V vertex = iter.next();
			Component<E, V> comp = new Component<E, V>(vertex, p, time);
			verttocomp.put(vertex, comp);
			componentlist.add(comp);
			forest.addVertex(vertex);
		}
		Iterator<E> iter1 = edges.iterator();
		for (int i = 0; i < numofedges; i++) {
			E tempedge = iter1.next();
			ComplexEdge<E,V> cedge = new ComplexEdge<E,V>(tempedge, graph.getEdgeWeight(tempedge));
			edgelist.add(cedge);
		}
	}

	public boolean increasetime() {
		double edgetime = Double.MAX_VALUE;
		ComplexEdge<E,V> minedge = null; // i need to change the null, right?

		// find min edge
		Iterator<ComplexEdge<E,V>> iter = edgelist.listIterator();
		while (iter.hasNext()) {
			ComplexEdge<E,V> cedge = iter.next();
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
		Component<E, V> mincomponent = null; // i need to change
															// the null, right?
		boolean allpassive = true;

		// find min component
		Iterator<Component<E, V>> iter1 = componentlist.listIterator();
		while (iter1.hasNext()) {
			Component<E, V> tempcomp = iter1.next();
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


			Iterator<ComplexEdge<E,V>> iter3 = edgelist.listIterator();
			while (iter3.hasNext()) {
				ComplexEdge<E,V> cedge = iter3.next();
				V vertex1 = graph.getEdgeSource(cedge.edge);
				V vertex2 = graph.getEdgeTarget(cedge.edge);
				int colour1 = verttocomp.get(vertex1).colour;
				int colour2 = verttocomp.get(vertex2).colour;
				if (colour1 == 0 && colour2 == 0)
					cedge.potential -= 2 * componenttime;
				else if (colour1 == 0 || colour2 == 0)
					cedge.potential -= componenttime;
			}
			

			Iterator<Component<E, V>> iter2 = componentlist.listIterator();
			while (iter2.hasNext()) {
				Component<E, V> tempcomp = iter2.next();
				if (tempcomp.colour == 0) {
					tempcomp.potential -= componenttime;
					tempcomp.dual += componenttime;
				}
			}
			

			mincomponent.colour = 2;
			black_orders.addLast(mincomponent);
		}
		// else edge tightens
		else {
			time += edgetime;
			time_increments.addLast(time);

			Component<E, V> comp1 = verttocomp.get(graph.getEdgeSource(minedge.edge));
			Component<E, V> comp2 = verttocomp.get(graph.getEdgeTarget(minedge.edge));
			edge_orders.addLast(minedge);
			forest.addEdge(graph.getEdgeSource(minedge.edge), graph.getEdgeTarget(minedge.edge), minedge.edge);

			// remove the two components
			Iterator<Component<E, V>> iter2 = componentlist.listIterator();
			while (iter2.hasNext()) {
				Component<E, V> tempcomp = iter2.next();
				if (tempcomp.colour == 0) {
					tempcomp.potential -= edgetime;
					tempcomp.dual += edgetime;
				}
				if (tempcomp == comp1 || tempcomp == comp2) {
					iter2.remove();
				}
			}

			Component<E, V> joinedcomp = new Component<E, V>(comp1, comp2, minedge, time);
			// remove edges between these components
			Iterator<ComplexEdge<E,V>> iter3 = edgelist.listIterator();
			while (iter3.hasNext()) {
				ComplexEdge<E,V> cedge = iter3.next();
				V vertex1 = graph.getEdgeSource(cedge.edge);
				V vertex2 = graph.getEdgeTarget(cedge.edge);
				int colour1 = verttocomp.get(vertex1).colour;
				int colour2 = verttocomp.get(vertex2).colour;
				if (colour1 == 0 && colour2 == 0)
					cedge.potential -= 2 * edgetime;
				else if (colour1 == 0 || colour2 == 0)
					cedge.potential -= edgetime;
				//will make dfinaldual for the joiningedge equal to zero.
				if(verttocomp.get(vertex1) == comp1||verttocomp.get(vertex2) == comp1)
					cedge.dsumdual += comp1.dfinaldual;
				if(verttocomp.get(vertex1) == comp2||verttocomp.get(vertex2) == comp2)
					cedge.dsumdual += comp2.dfinaldual;
				
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
				Component<E, V> vcomp = verttocomp.get(vertex);
				if (vcomp == comp1 || vcomp == comp2) {
					verttocomp.put(vertex, joinedcomp);
				}
			}

		}
		return true;
	}

	public void prune(){
		Iterator<Component<E, V>> citer = black_orders.descendingIterator();
		while(citer.hasNext()){
			Component<E, V> comp = citer.next();
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
	
	public void Debug(){
		System.out.println(time_increments);
		System.out.println(edge_orders);
	}
}