/*
 * Student: Akram Mouhammad Atassi
 * Student ID: 300273157
 */
import java.util.*;
public class NearestNeighbor {
    ArrayList<Point3D> listOfPoints;

    //saves list
    NearestNeighbor(ArrayList<Point3D> listOfPoints){
        this.listOfPoints = listOfPoints;
    }
    //creates stack filled with all neighbors including the point itself
    public Stack<Point3D> rangeQuery(double eps, Point3D P){
        Stack<Point3D> listOfNeighbors = new Stack<>();
        //go through each point
        for(int i=0;i<listOfPoints.size();i++){
            Point3D crntPoint = listOfPoints.get(i);
            //if they are close enough, push the point to the stack
            if(P.Distance(crntPoint)<=eps){
                listOfNeighbors.push(crntPoint);
            }
        }
        return listOfNeighbors;
    }


    
}
