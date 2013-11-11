package kMST;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.AbstractBaseGraph;
public class GW<V, E> {/*
	AbstractBaseGraph<V,E> graph;
	int initpotential;
	int numofvertices;
	int numofedges;
	Set<V> vertices;
	Set<E> edges;
	HashMap<V, Integer> verttonum;
	HashMap<Integer, V> numtovert;
	DisjointSets components;
	double[] potentials;
	int[] ys;
	int[] colours;
	
	public void runGW(AbstractBaseGraph<V,E> inputgraph, int initialpotential){
		initialize(inputgraph,initialpotential);
		boolean notoveryet = decreasepotential();
		
	}
	
	public void initialize(AbstractBaseGraph<V,E> inputgraph, int p){
		AbstractBaseGraph<V,E> graph = inputgraph;
		vertices = graph.vertexSet();
		numofvertices = vertices.size();
		edges = graph.edgeSet();
		numofedges = edges.size();
		Iterator<V> iter = vertices.iterator();
		verttonum = new HashMap<V, Integer>(numofvertices);
		numtovert = new HashMap<Integer, V>(numofvertices);
		components = new DisjointSets(numofvertices);
		potentials = new double[numofvertices];
		colours = new int[numofvertices];
		ys = new int[numofvertices];
		initpotential = p;
		for(int i = 0; i<numofvertices; i++){
			V vertex = iter.next();
			verttonum.put(vertex, i);
			numtovert.put(i, vertex);
			potentials[i] = initpotential;
			colours[i] = 0; //0 = initial colour - white, 2 = final colour - black
			ys[i] = 0; //confirm
		}
	}
	
	public boolean decreasepotential(){
		double potential_to_reduce_component = Double.MAX_VALUE;
		double potential_to_reduce_edge = Double.MAX_VALUE;
		int componentroot = 0;
		TreeSet<Integer> componentroots = new TreeSet<Integer>();
		E tightedge = null; //Initialise?
		
		//Checking if some component becomes passive.
		//ideally should be over only components, but doesnt matter.
		for(int i = 0; i < numofvertices;i++){
			int root = components.find(i);
			componentroots.add(root);
			if(potentials[root]<potential_to_reduce_component&&colours[root]!=2){
				potential_to_reduce_component = potentials[root];
				componentroot = root;
			}
		}
		
		//to check if all components are already passive, then terminate.
		if(potential_to_reduce_component == Double.MAX_VALUE){
			return false;
		}
		
		Iterator <E> iter = edges.iterator();
		for(int i = 0; i < numofedges;i++){
			E edge = iter.next();
			V source = graph.getEdgeSource(edge);
			V target = graph.getEdgeTarget(edge);
			int source1 = verttonum.get(source);
			int target1 = verttonum.get(target);
			int root1 = components.find(source1);
			int root2 = components.find(target1);
			double y1 = ys[root1];
			double y2 = ys[root2];
			double temppotential = (graph.getEdgeWeight(edge) -y1 -y2)/2;
			if ((root1!=root2)&&(temppotential<potential_to_reduce_edge)){
				potential_to_reduce_edge = temppotential;
				tightedge = edge;
			}
		}
		
		//check if edge tightens first or something becomes passive.
		//Should I make this into a different function?
		if(potential_to_reduce_component<potential_to_reduce_edge){
			colours[componentroot] = 2;
			Iterator<Integer> iter1 = componentroots.iterator();
			while(iter1.hasNext()){
				int temproot = iter1.next();
				ys[temproot] += potential_to_reduce_component;
				potentials[temproot] -= potential_to_reduce_component;
			}
		}
		else{
			Iterator<Integer> iter1 = componentroots.iterator();
			while(iter1.hasNext()){
				int temproot = iter1.next();
				ys[temproot] += potential_to_reduce_component;
				potentials[temproot] -= potential_to_reduce_component;
				V source = graph.getEdgeSource(tightedge);
				V target = graph.getEdgeTarget(tightedge);
				int source1 = verttonum.get(source);
				int target1 = verttonum.get(target);
				int root1 = components.find(source1);
				int root2 = components.find(target1);
				components.union(root1, root2);
				int newroot = components.find(root1);
				ys[newroot] = ys[root1]+ys[root2]; //What should this be?
				potentials[newroot] = potentials[root1]+potentials[root2];
			}
		}
		
		
		
		return true;
	} */
}
