import java.lang.Math;
//Everything Works Out!!!
public class Plane3D{
	// ax+by+cz = d
	private double a,b,c,d;

	//constructors
	Plane3D(){
		a = b = c = d = 0;
	}


	// given 3 points, we need to get 2 directional vectors from them
	// so we subtract 2 points from the remaining point
	// Directional Vector 1 (dV1) = pTwo - pOne, dV2 = pThree - pOne
	// we then get the cross product, where the values in the x,y,z coordinates will be the respective coefficients a,b,c
	// we will get d from just plugging in a point to our new equation
	
	Plane3D(Point3D pOne,Point3D pTwo,Point3D pThree){
		Vector dV1 = pOne.getVector(pTwo), dV2 = pOne.getVector(pThree);  //Get the 2 directional vectors

		Vector vNormal = dV1.getNormalVector(dV2); //cross product to get the normal vector

		a = vNormal.getX();
		b = vNormal.getY();
		c = vNormal.getZ();
		d = a*pOne.getX()+ b*pOne.getY() + c*pOne.getZ();
		
	}
	
	//
	Plane3D(double a,double b, double c, double d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	/*
	* gets the distance between the point and the plane
	* formula is |a*x+b*y+c*z-d|/sqrt(a^2+b^2+c^2), where a,b,c are coefficients determined by the plane, d is the constant 
	* on the rightside (on the link it is on the leftside, so the sign of d is flipped),
	* and x,y,z  are the cartesian coordiantes of the point we are trying to find the distance to 	
	* formula taken from this link: https://www.cuemath.com/geometry/distance-between-point-and-plane/#:~:text=How%20to%20Find%20the%20Shortest,equation%20of%20the%20given%20plane. 
	*/
	public double getDistance(Point3D point){
		return (Math.abs(a*point.getX()+b*point.getY()+c*point.getZ()-d)/Math.sqrt(Math.pow(a,2)+Math.pow(b,2)+Math.pow(c,2)));
	}


	//personal method
	public String toString(){
		return "Cartesian equation is: "+a+"x+ "+b+"y+ "+c+"z = "+d;
		
	}
	
}