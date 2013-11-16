package kMST;

import org.jgrapht.graph.SimpleWeightedGraph;

public class Test {
	public static SimpleWeightedGraph<TestVertex,TestEdge> test;
	
	public static void main(String[] args) {
		BuildGraph();
		double inipot = 2.0;
		BoringLongGW<TestEdge,TestVertex> testgw = new BoringLongGW<TestEdge,TestVertex>(test,TestEdge.class, inipot);
		testgw.QQMaster(2.0, 3.0, 4);

	}
	
	public static void BuildGraph(){
		test = new SimpleWeightedGraph<TestVertex,TestEdge>(TestEdge.class);
		TestVertex v1 = new TestVertex(1);
		TestVertex v2 = new TestVertex(2);
		TestVertex v3 = new TestVertex(3);
		TestVertex v4 = new TestVertex(4);
		TestVertex v5 = new TestVertex(5);
		TestVertex v6 = new TestVertex(6);
		TestVertex v7 = new TestVertex(7);
		TestEdge e1 = new TestEdge(1);
		TestEdge e2 = new TestEdge(2);
		TestEdge e3 = new TestEdge(3);
		TestEdge e4= new TestEdge(4);
		TestEdge e5= new TestEdge(5);
		TestEdge e6= new TestEdge(6);
		test.addVertex(v1);
		test.addVertex(v2);
		test.addVertex(v3);
		test.addVertex(v4);
		test.addVertex(v5);
		test.addVertex(v6);
		test.addVertex(v7);
		test.addEdge(v1, v2, e1);
		test.addEdge(v2, v3, e2);
		test.addEdge(v1, v7, e3);
		test.addEdge(v4, v5, e4);
		test.addEdge(v5, v6, e5);
		test.addEdge(v6, v7, e6);
		test.setEdgeWeight(e1, 7.0);
		test.setEdgeWeight(e2, 2.0);
		test.setEdgeWeight(e3, 7.3);
		test.setEdgeWeight(e4, 3.0);
		test.setEdgeWeight(e5, 4.0);
		test.setEdgeWeight(e6, 8.1);
	}

}
