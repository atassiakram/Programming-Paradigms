package main

import (
	"bufio"
	"fmt"
	"log"
	"math"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"time"
)

type Point3D struct {
	x, y, z float64
}

type Plane3D struct {
	A, B, C, D float64
}

type Plane3DwSupport struct {
	Plane3D
	SupportSize int
}

// computes the distance between 2 points
func (p1 *Point3D) GetDistance(p2 *Point3D) float64 {
	return math.Sqrt(math.Pow(p1.x-p2.x, 2) + math.Pow(p1.y-p2.y, 2) + math.Pow(p1.z-p2.z, 2))
}

//computes the plane defined by a set of 3 points
/*
*	v1 = (p2.x-p1.x, p2.y-p1.y, p2.z-p1.z), v2 = (p3.x-p1.x, p3.y-p1.y, p3.z-p1.z)
*	get the normal vector by doing the cross product of p1 and p2
*	| i    j    k    |
*	| v1.x v1.y v1.z |
*	| v2.x v2.y v2.z |
*	->	( (v1.y * v2.z - v1.z * v2.y) , - (v1.x * v2.z - v1.z * v2.x) , (v1.x * v2.y - v1.y * v2.x) )
*   the x,y,z coords are the coefficients of a,b,c, and d is just the result of plugging a point into our equation
 */
func GetPlane(points []Point3D) Plane3D {
	p1, p2, p3 := points[0], points[1], points[2]

	//get the vectors
	v1x, v1y, v1z := p2.x-p1.x, p2.y-p1.y, p2.z-p1.z
	v2x, v2y, v2z := p3.x-p1.x, p3.y-p1.y, p3.z-p1.z

	//cross product
	var a, b, c float64 = v1y*v2z - v1z*v2y, v1z*v2x - v1x*v2z, v1x*v2y - v1y*v2x

	//plug the p1 in the equation to get d
	var d float64 = a*p1.x + b*p1.y + c*p1.z

	//return them as a Plane3D
	return Plane3D{a, b, c, d}

}

// computes the number of required RANSAC iterations
// k= log( 1 - C ) / log( 1- p^3)
func GetNumberOfIterations(confidence float64, percentageOfPointsOnPlane float64) int {
	return int(math.Ceil(math.Log(1-confidence) / math.Log(1-math.Pow(percentageOfPointsOnPlane, 3))))
}

// reads and XYZ file and return a slice of strings
func ReadXYZ(filename string) (points []Point3D) {
	file, err := os.Open(filename) //open file in read mode
	if err != nil {
		log.Fatalf("error is %v", err)
	}

	defer file.Close()

	scanner := bufio.NewScanner(file) //create scanner
	scanner.Split(bufio.ScanLines)    //set scanner to read by lines

	scanner.Scan()       //skip first line
	for scanner.Scan() { //scan each line
		pointString := scanner.Text()             //take the line as a string
		pointSlice := strings.Fields(pointString) //split it
		p1, err1 := strconv.ParseFloat(pointSlice[0], 64)
		p2, err2 := strconv.ParseFloat(pointSlice[1], 64)
		p3, err3 := strconv.ParseFloat(pointSlice[2], 64)

		//error handling
		if err1 != nil || err2 != nil || err3 != nil {
			log.Fatal("xyz file contains a non-number")
		}
		//append the new point to the slice of points
		points = append(points, Point3D{p1, p2, p3})

	}
	return points
}

// save the poitns to a file
func saveXYZ(filename string, points []Point3D) {
	file, err := os.Create(filename)
	if err != nil {
		if err != nil {
			log.Fatalf("error is %v", err)
		}
	}
	defer file.Close()

	writer := bufio.NewWriter(file)
	fmt.Fprintln(writer, "x y z")
	for _, v := range points {
		fmt.Fprintf(writer, "%v,%v,%v\n", v.x, v.y, v.z)
	}
	writer.Flush()

}

// computes distance between plane and point
func (plane Plane3D) getDistance(point Point3D) float64 {
	return math.Abs(plane.A*point.x+plane.B*point.y+plane.C*point.z-plane.D) / math.Sqrt(math.Pow(plane.A, 2)+math.Pow(plane.B, 2)+math.Pow(plane.C, 2))
}

// computes the support of a plane in a set of points
func GetSupport(plane Plane3D, points []Point3D, eps float64) Plane3DwSupport {
	var support int = 0
	for _, point := range points {
		if plane.getDistance(point) < eps {
			support++
		}
	}
	return Plane3DwSupport{plane, support}
}

// extracts the points that supports the given plane
// and returns them as a slice of points
func GetSupportingPoints(plane Plane3D, points []Point3D, eps float64) []Point3D {
	output := make([]Point3D, 0)
	for _, point := range points {
		if plane.getDistance(point) < eps {
			output = append(output, point)
		}
	}
	return output
}

// creates a new slice of points in which all points
// belonging to the plane have been removed
func RemovePlane(plane Plane3D, points []Point3D, eps float64) []Point3D {
	output := make([]Point3D, 0)
	for _, point := range points {
		if plane.getDistance(point) >= eps {
			output = append(output, point)
		}
	}
	return output
}

//pipeline begins !!!!

// keep generating points
func RandomPointGenerator(Points []Point3D) <-chan Point3D {
	//make a channel
	outputChan := make(chan Point3D)
	//get a random number
	rand.Seed(time.Now().UnixNano())

	go func() {
		for {
			outputChan <- Points[rand.Intn(len(Points))]
		}
	}()
	return outputChan
}
func tripletOfPointsGenerator(inputChan <-chan Point3D) <-chan [3]Point3D {
	outputChan := make(chan [3]Point3D)
	go func() {
		for {
			first, second, third := <-inputChan, <-inputChan, <-inputChan
			outputChan <- [3]Point3D{first, second, third}
		}
	}()
	return outputChan

}

func takeN(inputChan <-chan [3]Point3D, numberOfIterations int) <-chan [3]Point3D {
	outputChan := make(chan [3]Point3D)
	go func() {
		defer close(outputChan)
		for i := 0; i < numberOfIterations; i++ {
			outputChan <- <-inputChan
		}

	}()
	return outputChan
}

func planeEstimator(inputChan <-chan [3]Point3D) <-chan Plane3D {
	outputChan := make(chan Plane3D)
	go func() {
		defer close(outputChan)
		for triplet := range inputChan {
			outputChan <- GetPlane(triplet[:])
		}
	}()
	return outputChan
}

func supportingPointEstimator(inputChan <-chan Plane3D, points []Point3D, eps float64) <-chan Plane3DwSupport {
	outputChan := make(chan Plane3DwSupport)
	go func() {
		defer close(outputChan)
		plane := <-inputChan
		outputChan <- GetSupport(plane, points, eps)
	}()
	return outputChan
}

func fanIn(inputChan []<-chan Plane3DwSupport) <-chan Plane3DwSupport {
	outputChan := make(chan Plane3DwSupport)
	go func(){
		defer close(outputChan)
		for _,v:=range inputChan{
			outputChan<- <-v
		} 
	}()
	return outputChan
}


func dominantPlaneIdentifier(inputChan <-chan Plane3DwSupport,dominantPlane *Plane3DwSupport){
	*dominantPlane = Plane3DwSupport{}
	for i:=range inputChan{
		if i.SupportSize > dominantPlane.SupportSize{
			*dominantPlane = i
		}
	}
}
func main() {
	if len(os.Args) != 5 {
		fmt.Println("Usage: go planeRANSAC <filename> <confidence> <percentage> <eps>")
		os.Exit(1)
	}
	//file input
	filename := os.Args[1]
	filenameWithoutSuffix := strings.TrimSuffix(filename,".xyz")

	//numerical inputs
	confidence, _ := strconv.ParseFloat(os.Args[2], 64)
	percentage, _ := strconv.ParseFloat(os.Args[3], 64)
	eps, _ := strconv.ParseFloat(os.Args[4], 64)

	//get the slice and number of iterations
	slice := ReadXYZ(filename)
	numberOfIterations := GetNumberOfIterations(confidence, percentage)

	//function that does entire ransac algorithm concurrently
	findDominantPlane := func(iterationNum int){
		
		var dominantPlane Plane3DwSupport
	
		randomPointOutput := RandomPointGenerator(slice)

		tripletOutput := tripletOfPointsGenerator(randomPointOutput)

		takeNOutput := takeN(tripletOutput, numberOfIterations)


		//stop the 2 generators from going to infinity
		randomPointOutput = nil
		tripletOutput = nil
		//keep going

		planeEstimatorOutput := planeEstimator(takeNOutput)

		//slice to store all channels from the support point estimator
		var arrayOfChan []<-chan Plane3DwSupport

		for i:=0;i<numberOfIterations;i++{
			element := supportingPointEstimator(planeEstimatorOutput,slice,eps)
			arrayOfChan = append(arrayOfChan,element)
		}

		
		fanInOutput := fanIn(arrayOfChan)

		dominantPlaneIdentifier(fanInOutput,&dominantPlane)	//we found the dominant plane

		//save the dominant plane

		dominantPlaneFileName := fmt.Sprintf("%s_p%d.xyz",filenameWithoutSuffix,iterationNum)
		dominantPlaneSupportingPoints := GetSupportingPoints(dominantPlane.Plane3D,slice,eps)
		saveXYZ(dominantPlaneFileName,dominantPlaneSupportingPoints)

		//remove the points from the original slice
		slice = RemovePlane(dominantPlane.Plane3D,slice,eps)

	}

	//repeat function 3 times
	for i:=1;i<=3;i++{
		findDominantPlane(i)
	}
	//print remaining points
	remainingPointsFileName := fmt.Sprintf("%s_p0.xyz",filenameWithoutSuffix)
	saveXYZ(remainingPointsFileName,slice)

}
