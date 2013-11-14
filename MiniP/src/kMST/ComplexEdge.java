package kMST;

public class ComplexEdge<E> {
	public E edge;
	public double potential; //leftover
	public double timeoftight;
	public ComplexEdge(E edge1, double p1){
		edge = edge1;
		potential = p1;
		timeoftight = -1;
	}
	public String toString(){
		String s = "("+timeoftight+" "+edge.toString()+")";
		return s;
	}
}
