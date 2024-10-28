/*
 * Student: Akram Mouhammad Atassi
 * Student ID: 300273157
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class DBScan{
    private ArrayList<Point3D> listOfPoints;
    private int numberOfClusters,noiseCounter;
    private double eps,minPts;

    //constructor 1
    DBScan(ArrayList<Point3D> listOfPoints){ 
        this.listOfPoints = listOfPoints;
    }
    //constructor 2
    DBScan(String filename,String eps,String minPts){
        listOfPoints = DBScan.read(filename);
        setEps(Double.parseDouble(eps));
        setMinPts(Double.parseDouble(minPts));
        findClusters();
        save(filename);
        sortSizeList(clusterSizeList());
        System.out.println("Number of Noise: "+noiseCounter);

    }

    //set eps value
    public double setEps(double eps) {
        this.eps = eps;
        return eps;
    }

    //set minPts value
    public double setMinPts(double minPts) {
        this.minPts = minPts;
        return minPts;
    }

    //returns number of clusters
    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    //returns number of Noise
    public int getNoiseCounter() {
        return noiseCounter;
    }

    //return list of all Points
    public ArrayList<Point3D> getPoints() {
        return listOfPoints;
    }



    /* Goes through each point
     * Gives it a label and RGB value
     * has a cluster counter
     */
    public void findClusters(){
        numberOfClusters = 0;
        noiseCounter =0;
        //go through each point
        for (int i=0;i<listOfPoints.size();i++){
            Point3D crntPoint = listOfPoints.get(i);
            if(crntPoint.isDefined()) continue;    //go to the next point if already defined
            NearestNeighbor nearestNeighbor = new NearestNeighbor(listOfPoints);
            Stack<Point3D> N = nearestNeighbor.rangeQuery(eps, crntPoint);
            if(N.size()<minPts){  //set as Noise, then continue
                crntPoint.setClusterLabel(0);
                noiseCounter++;
                continue; 
            }


            // label is undefined, so make a new cluster for the point
            numberOfClusters++;
            crntPoint.setClusterLabel(numberOfClusters);
            //I already have a stack of neighbors, 
            //no need to push my current stack into another stack of neighbors
            // N =S in pseudocode
            while (!N.isEmpty()){
                Point3D Q = N.pop();


                if(Q.isNoise()) { //replaces noise label to cluster label
                    Q.setClusterLabel(numberOfClusters);
                    noiseCounter--;
                }
                if(Q.isDefined()) continue; //if defined, go next

                //if undefined
                Q.setClusterLabel(numberOfClusters);
                Stack<Point3D> moreNeighbors = nearestNeighbor.rangeQuery(eps, Q);
                //check if point Q has enough neighbors to make its own cluster
                //if so, bring it into this cluster instead
                if (moreNeighbors.size()>=minPts){
                    while(!moreNeighbors.isEmpty()){
                        N.push(moreNeighbors.pop());
                    }
                }
            }

        }





    }

    /* set scanner for specific file
     * skip first line
     * save each line as a Point3D
     */

    public static ArrayList<Point3D> read(String filename){ //successful
        ArrayList<Point3D> listOfPoints = new ArrayList<>();
        File file = new File(filename);
        try {
            Scanner scanner = new Scanner(file);
            scanner.nextLine();                 //skip first line
            /*go through each line
             * split between each commas
             * x is first element in array, y is second, z is third
             * save new Point3D in listOfPoints
             */
            while(scanner.hasNext()){
                String line = scanner.nextLine();
                String[] coords = line.split(",");
                listOfPoints.add(new Point3D(coords[0], coords[1], coords[2]));
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
            e.printStackTrace();
        }
        return listOfPoints;
    }






    /* print out header in new file
     * go through each point
     * print out their details in a new line
     */
    public void save(String filename){
        // follow given naming convention         inputFile_clusters_eps_minPts.csv
        String outPutFileName = filename.substring(0,filename.length()-4)+"_clusters_"+eps+"_"+minPts+"_"+numberOfClusters+".csv";
        File file = new File(outPutFileName);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("x,y,z,C,R,G,B");
            //go through each loop, print out Point3D info
            for (int i=0;i<listOfPoints.size();i++){
                Point3D crnt = listOfPoints.get(i);
                writer.write("\n"+crnt.toString());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //for testing purposes
    public void printAllPoints(){
        for(int i=0;i<listOfPoints.size();i++){
            Point3D crnt = listOfPoints.get(i);
            System.out.println(crnt.getX()+","+crnt.getY()+","+crnt.getZ()+","+crnt.getClusterLabel());

        }
    }
    /*
     * make an array where each element represents the size of cluster i+1
     * example: clusterSize[0] carries the number of points in cluster 1 (0+1)
     */
    public int[] clusterSizeList(){
        int[] clusterSize = new int[numberOfClusters];
        //go through each point
        for (int i=0;i<listOfPoints.size();i++){
            Point3D crnt = listOfPoints.get(i);
            if(crnt.isNoise()) continue;
            clusterSize[crnt.getClusterLabel()-1] ++;
        }
        return clusterSize;
    }


    /*
     * quick sort method with an extra twist
     * create another int[] to represent each cluster (except 0)
     * if we swap 2 clusters, we also swap the exact same elements in the new int[]
     */

    public void sortSizeList(int[] clusterSize){
        //make new array
        int[] clusterNum = new int[numberOfClusters];
        for(int i=0;i<numberOfClusters;i++){
            clusterNum[i] = i+1;
        }
        //to be used to swap 2 elements 
        int tmpSize; 
        int tmpElement;
        for(int i=0;i<numberOfClusters;i++){
            for (int j=i+1;j<numberOfClusters;j++){
                if(clusterSize[j]>clusterSize[i]){
                    //normal quicksort

                    tmpSize = clusterSize[i];
                    tmpElement = clusterNum[i];


                    clusterSize[i] = clusterSize[j];
                    clusterNum[i] = clusterNum[j];
                

                    clusterSize[j] = tmpSize;
                    clusterNum[j] = tmpElement;
                    //special modification
                    //example: swap cluster 1 (represented by clusterNum[0]) with cluster 3 (represented by clusterNum[2])
                    }
            }
            System.out.println("Cluster "+clusterNum[i]+" has "+clusterSize[i]+" points.");
        }
    }




    public static void main(String[] args) {
        DBScan terminalTest = new DBScan(args[0], args[1], args[2]);
    }


    
}
