/*
 * Student: Akram Mouhammad Atassi
 * Student ID: 300273157
 */

public class Point3D{
    private double x,y,z;
    private int clusterLabel=-1; //undefined at -1, Noise at 0
    private double R,G,B;


    //convert from string to stored double
    Point3D(String x, String y, String z){
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
        this.z = Double.parseDouble(z);
    }
    


    //getters for x,y and z
    public double getX() {
        return x;
    }public double getY() {
        return y;
    }public double getZ() {
        return z;
    }


    //getters and setters for clusterLabel
    public int getClusterLabel() {
        return clusterLabel;
    }

    //when u set the label, also set the RGB colors
    public void setClusterLabel(int clusterLabel) {
         
        double coefficient = Math.sqrt(clusterLabel);
        R = 1* coefficient %1;
        G = 2* coefficient %1;
        B = 3* coefficient %1;
        this.clusterLabel = clusterLabel;
    }



    //when we get Noise
    public void setNoise(){
        clusterLabel = 0;
        R=G=B=0;
    }
    //method to tell if label is Noise
    public boolean isNoise(){
        return clusterLabel == 0;
    }


    //method to tell if label is defined
    public boolean isDefined(){
        return clusterLabel != -1;
    }


    //get R,G,B
    public double getR() {
        return R;
    }public double getG() {
        return G;
    }public double getB() {
        return B;
    }
    //string of RGB in list
    public String toString(){
        return x+","+y+","+z+","+clusterLabel+","+R+","+G+","+B;
    }

    //euclidian distance formula 
    // sqrt ( (X1-X2)^2+(Y1-Y2)^2+(Z1-Z2)^2  )
    public double Distance(Point3D inputPoint3d){
        return Math.sqrt(Math.pow(x-inputPoint3d.getX(), 2) + Math.pow(y-inputPoint3d.getY(), 2) + Math.pow(z-inputPoint3d.getZ(),2));

    }
}