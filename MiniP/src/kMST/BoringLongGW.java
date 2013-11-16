package kMST;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleWeightedGraph;
//Highly untested. Use it for return types and basic flow.

public class BoringLongGW<E, V> {
	public SimpleWeightedGraph<V,E> OriginalGraph;
	public SimpleWeightedGraph<V, E> graph;
	public SimpleWeightedGraph<V, E> forest;
	public LinkedList<ComplexEdge<E,V>> edgelist;
	public LinkedList<Component<E, V>> componentlist;
	public double time;
	public HashMap<V, Component<E, V>> verttocomp;

	public double initpotential;
	public int numofvertices;
	public int numofedges;
	public Set<V> vertices;
	public Set<E> edges;
	public LinkedList<Double> time_increments;
	public LinkedList<ComplexEdge<E,V>> edge_orders;
	public LinkedList<Component<E, V>> black_orders;
	
	public Class<E> edgetype;
	
	//The main function to call
	public void runGW(){
		boolean flag = true;
		while(flag){
			flag = increasetime();
		}
	}
	
	public BoringLongGW(SimpleWeightedGraph<V, E> graph1,Class<E> Edgetype, double p) {
		time = 0;
		initpotential = p;
		graph = graph1;
		OriginalGraph = graph1;
		edgetype = Edgetype;
		vertices = graph.vertexSet();
		numofvertices = vertices.size();
		edges = graph.edgeSet();
		numofedges = edges.size();
		verttocomp = new HashMap<V, Component<E, V>>(numofvertices);
		edge_orders = new LinkedList<ComplexEdge<E,V>>();
		black_orders = new LinkedList<Component<E, V>>();
		edgelist = new LinkedList<ComplexEdge<E,V>>();
		componentlist = new LinkedList<Component<E, V>>();
		time_increments = new LinkedList<Double>();
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
	
	//Clear All data for Running GW recursively
	public void ClearAll(SimpleWeightedGraph<V,E> graph1 ,double potential){
		time = 0;
		graph = graph1;
		initpotential = potential;
		vertices = graph.vertexSet();
		numofvertices = vertices.size();
		edges = graph.edgeSet();
		numofedges = edges.size();
		verttocomp = new HashMap<V, Component<E, V>>(numofvertices);
		edge_orders = new LinkedList<ComplexEdge<E,V>>();
		black_orders = new LinkedList<Component<E, V>>();
		edgelist = new LinkedList<ComplexEdge<E,V>>();
		componentlist = new LinkedList<Component<E, V>>();
		time_increments = new LinkedList<Double>();
		Iterator<V> iter = vertices.iterator();
		forest = new SimpleWeightedGraph<V,E>(edgetype);
		for (int i = 0; i < numofvertices; i++) {
			V vertex = iter.next();
			Component<E, V> comp = new Component<E, V>(vertex, potential, time);
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
	
	//Calculate Alpha
	public int Alpha(){
		prune();
		ConnectivityInspector<V,E> ci = new ConnectivityInspector<V,E>(forest);
		List<Set<V>> trees = ci.connectedSets();
		int max = 0;
		for(Set<V> s : trees){
			if(s.size() > max){
				max = s.size();
			}
		}
		return max;
	}
	
	public double QQMaster(double min,double max,int k){
		double epsilon = 0.01;	//Choice of Epsilon is Critical
		//run GW on l+
		ClearAll(OriginalGraph,min + epsilon);
		runGW();
		LinkedList<ComplexEdge<E,V>> edgelistL = edge_orders;
		SimpleWeightedGraph<V,E> forestL = new SimpleWeightedGraph<V,E>(edgetype);
		for(V Vertex: OriginalGraph.vertexSet()){
			forestL.addVertex(Vertex);
		}
		//run GW on r-
		ClearAll(OriginalGraph,max - epsilon);
		runGW();
		LinkedList<ComplexEdge<E,V>> edgelistR = edge_orders;
		SimpleWeightedGraph<V,E> forestR = new SimpleWeightedGraph<V,E>(edgetype);
		for(V Vertex: OriginalGraph.vertexSet()){
			forestR.addVertex(Vertex);
		}
		
		//Find first edge which differs
		ComplexEdge<E,V> edgeL = edgelistL.poll();	//Might be null
		ComplexEdge<E,V> edgeR = edgelistR.poll(); //This cannot be null
		
		int edgeindex = 1;
		//Test for Equality of edges , not very sure about it
		while(edgeL != null && edgeL.edge.equals(edgeR.edge)){
			forestL.addEdge(graph.getEdgeSource(edgeL.edge), graph.getEdgeTarget(edgeL.edge), edgeL.edge);
			forestR.addEdge(graph.getEdgeSource(edgeR.edge), graph.getEdgeTarget(edgeR.edge), edgeR.edge);
			edgeL = edgelistL.poll();
			edgeR = edgelistR.poll();
			edgeindex += 1;
		}
		if(edgeL != null){
			forestL.addEdge(graph.getEdgeSource(edgeL.edge), graph.getEdgeTarget(edgeL.edge), edgeL.edge);
		}
		if(edgeR != null){
			forestR.addEdge(graph.getEdgeSource(edgeR.edge), graph.getEdgeTarget(edgeR.edge), edgeR.edge);
		}
		
		//Alphas required again and again for testing for threshold
		int alpha1;
		int alpha2;
		
		//This is the case when F_l is  a subset of F_r
		if(edgeL == null){
			//Get kink Not Working Properly
			double KinkPotential = max - epsilon - edgeR.getkink();
			
			//We know that kink potential must be between min and max
			assert (KinkPotential <= max && KinkPotential >= min): "Something Wicked Happened";
			
			//Check if kink is threshold
			ClearAll(OriginalGraph,KinkPotential + epsilon);
			runGW();
			alpha1 = Alpha();
			ClearAll(OriginalGraph,KinkPotential - epsilon);
			runGW();
			alpha2 = Alpha();
			if(alpha2 < k && alpha1 >= k){
				return KinkPotential;
			}
			
			//if kink is not the one
			else if(alpha1 < k){
				return QQMaster(KinkPotential,max,k);
			}
			
			else{
				//get i'th edge by running on right forest
				ClearAll(forestR,KinkPotential-epsilon);
				runGW();
				edgeR = edge_orders.get(edgeindex - 1);
				//get threshold not working
				double threshold = KinkPotential - epsilon - edgeR.getthreshhold();
				//threshold must be between min and max
				assert (threshold <= max && threshold >= min): "Something Wicked Happened";

				//check if threshold is the one
				ClearAll(OriginalGraph,threshold + epsilon);
				runGW();
				alpha1 = Alpha();
				ClearAll(OriginalGraph,threshold - epsilon);
				runGW();
				alpha2 = Alpha();
				if(alpha2 < k && alpha1 >= k){
					return threshold;
				}
				
				//if threshold is not the one
				else if(alpha1 < k){
					return QQMaster(threshold,KinkPotential,k);
				}
				
				else{
					return QQMaster(min,threshold,k);
				}
			}			
		}
		//Now both the edges are unequal, and Nasus makes them equal!
		else{
			double[] CriticalPoints = new double[7];
			double[] Timings = new double[4];
			CriticalPoints[0] = max - epsilon - edgeL.getkink();
			Timings[0] = edgeL.getkinktime();
			CriticalPoints[1] = max - epsilon - edgeR.getkink();
			Timings[1] = edgeR.getkinktime();
			
			//Run GW on kink to get threshold
			ClearAll(forestR,CriticalPoints[1] - epsilon);
			runGW();
			double threshold = CriticalPoints[1] - epsilon - edge_orders.get(edgeindex - 1).getthreshhold();
			CriticalPoints[2] = threshold;
			Timings[2] = edge_orders.get(edgeindex - 1).getthresholdtime();
			
			//Run GW to find right side time
			ClearAll(forestL,max-epsilon);
			runGW();
			Timings[3] = edge_orders.get(edgeindex - 1).timeoftight;
			
			double intersection;
			intersection = lineIntersect(min+epsilon,edgeL.timeoftight,CriticalPoints[0],Timings[0],CriticalPoints[2],Timings[2],CriticalPoints[1],Timings[1]);
			if(intersection != -1.0){
				CriticalPoints[3] = intersection;
			}
			
			intersection = lineIntersect(min+epsilon,edgeL.timeoftight,CriticalPoints[0],Timings[0],CriticalPoints[1],Timings[1],max - epsilon,edgeR.timeoftight);
			if(intersection != -1.0){
				CriticalPoints[4] = intersection;
			}
			
			intersection = lineIntersect(CriticalPoints[0],Timings[0],max - epsilon,Timings[3],CriticalPoints[2],Timings[2],CriticalPoints[1],Timings[1]);
			if(intersection != -1.0){
				CriticalPoints[5] = intersection;
			}
			
			intersection = lineIntersect(CriticalPoints[0],Timings[0],max - epsilon,Timings[3],CriticalPoints[1],Timings[1],max - epsilon,edgeR.timeoftight);
			if(intersection != -1.0){
				CriticalPoints[6] = intersection;
			}
			
			Arrays.sort(CriticalPoints);
			for(double candidate : CriticalPoints){
				if(candidate > min && candidate < max){
					ClearAll(OriginalGraph,candidate - epsilon);
					runGW();
					alpha1 = Alpha();
					ClearAll(OriginalGraph,candidate + epsilon);
					runGW();
					alpha2 = Alpha();
					if(alpha1 < k && alpha2 >= k){
						return candidate;
					}
					
					//if threshold is not the one
					else if(alpha2 < k){
						min = candidate;
					}					
					else{
						max = candidate;
					}
				}
			}
			return QQMaster(min,max,k);			
		}
		
		//System.out.println(edgeindex);		
	}
	
	public double lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		  double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		  if (denom == 0.0) { // Lines are parallel.
		     return -1.0;
		  }
		  double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
		  double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
		    if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
		        // Get the intersection point.
		        return (x1 + ua*(x2 - x1));
		    }

		  return -1.0;
		  }
	
	public void Debug(){
		System.out.println(time_increments);
		System.out.println(edge_orders);
	}
}