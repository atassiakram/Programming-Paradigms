//Everything works out!!!
public class Point3D{
	//cartesian coordinates
	private double x,y,z;

	//Constructors
	Point3D(){
		x = y = z = 0;
	}

	Point3D(double x,double y,double z){
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


	// will calculate the vector between another point
	// returns input point - this point as vector
	public Vector getVector(Point3D p){
		return new Vector( p.getX() - x, p.getY() - y, p.getZ() - z);
	}


	//Prints out the vector
	public String toString(){
		return x+"	" + y + "	"+ z;
	}
	//personal experiment, please ignore
	public static void main(String[] args){
		Point3D p = new Point3D(10,10,10), p2 = new Point3D(10,11,10), p3 = new Point3D(11,10,10);
		Vector v1 = p.getVector(p2), v2 = p.getVector(p3), normalV = v2.getNormalVector(v1);
		System.out.println(v1.toString());	
		System.out.println(v2.toString());	
		System.out.println(normalV.toString());	
	}
}