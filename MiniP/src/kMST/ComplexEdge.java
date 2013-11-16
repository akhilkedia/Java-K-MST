package kMST;

public class ComplexEdge<E,V> {
	public E edge;
	public double potential; //leftover
	public double timeoftight;
	public double dsumdual; //factor for change in dual falling on the edge for unit change in initial potential
	Component<E, V> joinedcomp;
	public ComplexEdge(E edge1, double p1){
		edge = edge1;
		potential = p1;
		timeoftight = -1;
	}
	public String toString(){
		String s = "("+timeoftight+" "+getkink()+" "+edge.toString()+")";
		return s;
	}
	//no idea if this is correct - And might have crazy lots of division by zero errors.
	public double getkink(){
		double potenchange1 = 0;
		double potenchange2 = 0;
		if(joinedcomp.child1.colour==0&&joinedcomp.child2.colour==0){
			potenchange1 = -(joinedcomp.child1.dual-joinedcomp.child1.respotenform)/(joinedcomp.child1.dfinaldual-joinedcomp.child1.drespotenform);
			potenchange2 = -(joinedcomp.child2.dual-joinedcomp.child2.respotenform)/(joinedcomp.child2.dfinaldual-joinedcomp.child2.drespotenform);
			if(potenchange1>potenchange2)
				return potenchange1;
			else
				return potenchange2;
		}
		else if(joinedcomp.child1.colour==2){
			potenchange1 = -(joinedcomp.timeofformation-joinedcomp.child1.timeofformation-joinedcomp.child1.dual)/(joinedcomp.dtimeofform-joinedcomp.child1.dtimeofform-joinedcomp.child1.dfinaldual);
			return potenchange1;
		}
		else{
			potenchange2 = -(joinedcomp.timeofformation-joinedcomp.child2.timeofformation-joinedcomp.child2.dual)/(joinedcomp.dtimeofform-joinedcomp.child2.dtimeofform-joinedcomp.child2.dfinaldual);
			return potenchange2;
		}
	}
	public double getkinktime(){
		return (joinedcomp.timeofformation+(joinedcomp.dtimeofform*getkink()));
	}
	//only gives correct answer is one of them is black.
	public double getthreshhold(){
		double potenchange1 = 0;
		potenchange1 = -joinedcomp.respotenform/joinedcomp.drespotenform;
		return potenchange1;
	}
	public double getthreshholdtime(){
		return (joinedcomp.timeofformation+(joinedcomp.dtimeofform*getthreshhold()));
	}
}
