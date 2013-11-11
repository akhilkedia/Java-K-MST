package kMST;

public class ComplexEdge<E> {
	public E edge;
	public double potential; //leftover
	public ComplexEdge(E edge1, double p1){
		edge = edge1;
		potential = p1;
	}
}
