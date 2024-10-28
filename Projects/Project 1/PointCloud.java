import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;
import java.io.File;
import java.io.PrintWriter;

//Everything works out!!
public class PointCloud{
	private ArrayList<Point3D> pointCloudList; 
	
	//constructors
	
	//empty constructor
	PointCloud(){
		pointCloudList = new ArrayList<Point3D>();
	}

	//constructor given filename
	PointCloud(String filename){
		try{
			//initialize BufferedReader and ArrayList
			BufferedReader scanner = new BufferedReader(new FileReader(filename));
			pointCloudList = new ArrayList<Point3D>();
			//skip x   y   z
			scanner.readLine();
			//read each line, turn it into an array of strings, x = array[0] as double, y= array[1] as double , z= array[2] as double
			String line;
			String[] xyz;
			while((line = scanner.readLine())!=null){
				xyz = line.split("\\s+");

				pointCloudList.add(new Point3D(Double.parseDouble(xyz[0]),Double.parseDouble(xyz[1]),Double.parseDouble(xyz[2])));
				

			}
			scanner.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}	

	// add a point to the pointCloudList
	public void addPoint(Point3D pt){
		pointCloudList.add(pt);
	}

	// return a random Point3D from the PointCloud
	public Point3D getPoint(){
		int index = (int) java.lang.Math.floor(java.lang.Math.random()*pointCloudList.size());
		return pointCloudList.get(index);
	}
	// write the pointcloud to another file
	public void save(String filename){
		try{
			PrintWriter pw = new PrintWriter(new File(filename));
			pw.println("x	y	z");
			for(Point3D point : pointCloudList){
				pw.println(point.toString());
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();	
		}
		
	}

	
	public Iterator<Point3D> iterator(){
		return pointCloudList.iterator();
	}
	//for testing purposes, please ignore
	public static void main(String[] args){
		PointCloud pc = new PointCloud("PointCloud1.xyz");
		for(int i=0;i<10;i++){
			Point3D p = pc.getPoint();
			System.out.println(p.getX()+","+p.getY()+","+p.getZ());
		}
		pc.save("SomeFile.xyz");
	}
	
}