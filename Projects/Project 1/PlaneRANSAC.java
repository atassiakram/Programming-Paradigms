import java.lang.Math;
import java.util.Iterator;
/*
* The professor stated that we can use one of 2 interpretations to the question. The following code will use the second interpretation:
* "Calculating the three most dominant planes directly from the original point cloud, without removing any points."
*/
public class PlaneRANSAC{

	private PointCloud pc;
	private double eps;
	//constructor
	public PlaneRANSAC(){
		eps = 0;
		pc = new PointCloud();
	}
	public PlaneRANSAC(PointCloud pc){
		this.pc = pc;
		eps = 0;
	}	

	//setter and getter for eps
	public void setEps(double eps){
		this.eps = eps;
	}
	
	public double getEps(){
		return eps;
	}


	//formula taken from the following video
	//https://www.youtube.com/watch?v=dQw4w9WgXcQ
	public int getNumberOfIterations(double confidence,double percentageOfPointsOnPlane){
		return (int) Math.ceil(Math.log(1-confidence)/Math.log(1-Math.pow(percentageOfPointsOnPlane,3)));
	}
	
	//return the amount of neighbors
	private int findNumOfNeighbors(Plane3D plane){
		int counter = 0;
		Iterator<Point3D> iterator = pc.iterator();
		//loop through pointcloud, checl how many points are neighbors to the plane
		while(iterator.hasNext()){
			if(plane.getDistance( iterator.next() )<eps) counter++;
		}
		return counter;
		
	}
	//RANSAC algorithm
	
	public void run(int numberOfIterations,String filename){
		String prename = filename.substring(0,filename.length()-4);
		PointCloud pc1 = getCloud(numberOfIterations), pc2 = getCloud(numberOfIterations), pc3 = getCloud(numberOfIterations);
		pc1.save(prename+"_p"+1+".xyz");
		pc2.save(prename+"_p"+2+".xyz");
		pc3.save(prename+"_p"+3+".xyz");
		
		//for the remaining value, remove all values in other files from the main one
		Iterator<Point3D> iter1 = pc1.iterator(), iter2 = pc2.iterator(),iter3 = pc3.iterator(), iter = pc.iterator();
		Point3D crnt = null ,pOne = iter1.next(),pTwo = iter2.next(),pThree=iter3.next();
		while(iter1.hasNext() || iter2.hasNext() || iter3.hasNext()){
			boolean remove = false;
			crnt = iter.next();
			
			if(crnt == pOne){
				remove = true;
				pOne = iter1.hasNext()? iter1.next() : null;
			}
			if(crnt == pTwo){
				remove = true;
				pTwo = iter2.hasNext()? iter2.next() : null;
			}
			if(crnt == pThree){
				remove = true;
				pThree = iter3.hasNext()? iter3.next() : null;
			}
			if(remove) iter.remove();
			
		}
		pc.save(prename+"_p"+0+".xyz");
		
	}


	private PointCloud getCloud(int numberOfIterations){
		Plane3D dominant = new Plane3D();
		int bestSupport = 0;
			


		//initialize point for later use
		Point3D point;	
		for(int i=0;i<numberOfIterations;i++){
			//create plane and counter , loop through original pointcloud, find the number of points that are in the distance and add that to the counter
			Plane3D tmpPlane = new Plane3D(pc.getPoint(),pc.getPoint(),pc.getPoint());
			int crntSupport= 0;
			Iterator<Point3D> iterator = pc.iterator();

			//loop through pointcloud
			while(iterator.hasNext()){
				point = iterator.next();
				if(tmpPlane.getDistance(point)<eps) crntSupport++;

			}	//went through entire pointcloud
			if(crntSupport > bestSupport) {
				bestSupport = crntSupport;
				dominant = tmpPlane;
			}
		}//found the dominant plane and its support
		
		//now loop through entire pointcloud again to add the neighbors to the new pointcloud
		PointCloud outputCloud = new PointCloud();
		Iterator<Point3D> iterator = pc.iterator();
		
		while(bestSupport>0){ //instead of looping through entire loop, just keep going until u found all neighbors
			point = iterator.next();
			if(dominant.getDistance(point)<eps){
				outputCloud.addPoint(point);
				bestSupport--;
			}
		}
		return outputCloud;
			
		
	}

	public static void main(String[] args){
		//part 1:
		PlaneRANSAC algo1= new PlaneRANSAC(new PointCloud("PointCloud1.xyz"));
		algo1.setEps(0.001);
		int numOfIterations1 = algo1.getNumberOfIterations(0.99,0.8);
		System.out.println("Number of iterations for part 1 is: "+numOfIterations1);
		algo1.run(numOfIterations1,"PointCloud1.xyz");
		
		//part 2:
		PlaneRANSAC algo2= new PlaneRANSAC(new PointCloud("PointCloud2.xyz"));
		algo2.setEps(0.001);
		int numOfIterations2 = algo2.getNumberOfIterations(0.99,0.8);
		System.out.println("Number of iterations for part 2 is: "+numOfIterations2);
		algo2.run(numOfIterations2,"PointCloud2.xyz");
		
		//part 3:
		PlaneRANSAC algo3= new PlaneRANSAC(new PointCloud("PointCloud3.xyz"));
		algo3.setEps(0.001);
		int numOfIterations3 = algo3.getNumberOfIterations(0.99,0.8);
		System.out.println("Number of iterations for part 3 is: "+numOfIterations3);
		algo3.run(numOfIterations3,"PointCloud3.xyz");


	}
	
}