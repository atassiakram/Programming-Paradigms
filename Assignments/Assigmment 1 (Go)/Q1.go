package main

import (
	"fmt"
	"math"
)
type Point struct {
	x float64
	y float64
}
//1 a)
func MidPoint(p1,p2 Point){
	//calculate midpoint 
	midPoint := Point{(p1.x + p2.x )/2, (p1.y + p2.y)/2}
	//calculate magnitude
	var magnitude float64 =math.Sqrt(math.Pow((p1.x-p2.x),2) + math.Pow((p1.y - p2.y),2))
	//print out the outcomes
	fmt.Printf("Points: (%v,%v) (%v,%v)\nMidpoint: (%v,%v)\nLength: %.2f\n\n",p1.x,p1.y,p2.x,p2.y,midPoint.x,midPoint.y,magnitude)
}



//for the numOfCombinations method 
func factorial(num int) int{
	var product int = 1
	for i:=1;i<=num ;i++{
		product *= i
	}
	return product
}

//return the number of combinations for a list of any size
func numOfCombinations(listSize,comboSize int)int{
	return factorial(listSize)/(factorial(listSize-comboSize)*factorial(comboSize))
}

func main() {
	points := []Point{{8., 1.},{3., 2.},{7., 4.},{6., 3.}}
	fmt.Printf("point= %v\n", points[2])

	//my code starts here
	fmt.Println("\nMy Code:\n")
	quit := make(chan bool) 

	//create a concurrent MidPoint Method
	cMidPoint := func(p1,p2 Point){
		MidPoint(p1,p2)
		quit<-false
	}

	//run MidPoint concurrently for all combinations
	func(){
		for i:=0;i<len(points);i++{
			for j := i+1 ;j<len(points) ; j++{
				go cMidPoint(points[i],points[j])
			}
		}
	}()

	// terminate when all combinations are done 
	for i:=0; i<numOfCombinations( len(points) , 2) ;i++{
		<-quit
	}
	
}