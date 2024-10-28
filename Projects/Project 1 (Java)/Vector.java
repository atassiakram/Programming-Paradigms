public class Vector{
	private double x,y,z;

	//constructors
	Vector(){
		x = y = z = 0;
	}
	Vector(double x,double y,double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//getters
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getZ(){
		return z;
	}
	
	/*return the normal vector via crossproduct
	* let a,b,c be the current vector, and d,e,f be the input vector
	*	| i j k |
	*	| a b c |
	*	| d e f |
	* -> (b*f - c*e)i - (a*f - c*d)j + (  a*e - b*d)k
	* -> (bf-ce, cd-af, ae-bd)
	*/
	Vector getNormalVector(Vector input){
		return new Vector(
			y*input.getZ() - z*input.getY(),
			z*input.getX() - x*input.getZ(),
			x*input.getY() - y*input.getX()
		); 
	}
	//personal method for testing 
	public String toString(){
		return "Vector ("+x+", " + y + ", "+ z+")";
	}
}