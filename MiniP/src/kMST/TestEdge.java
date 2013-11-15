package kMST;

import org.jgrapht.graph.DefaultWeightedEdge;

@SuppressWarnings("serial")
public class TestEdge extends DefaultWeightedEdge{
	public int number;
	public TestEdge(int a){
		number = a;
	}
	public String toString(){
		return (""+number);
	}

}
