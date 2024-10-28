package main

import "fmt"
func fct(line []float64) {
	for _,v:= range line {
		fmt.Printf("%f, ", v)
	}
}

func fct2(matrix [][]float64) {
	matrix[2][0]= 12345.6
}

//2 a)
func sort(tab []float64){
	quickSort(tab,0,len(tab)-1)
}
func quickSort(slice []float64,start,end int){
	if start>=end{
		return
	}
	middle := partition(slice,start,end)

	quickSort(slice,start,middle-1)
	quickSort(slice,middle+1,end)
}

func partition(slice []float64,start,end int)int{
	partition := slice[end]
	var i int= start-1
	for j:=start;j<end;j++{
		if slice[j]<partition{
			i++
			slice[i],slice[j] = slice[j],slice[i]
		}
	}
	i++
	slice[i],slice[end] = slice[end],slice[i]
	return i
}

//2 b)
//only for square matrices 
func squareTranspose(tab [][]float64){
	for i:=0;i<len(tab[0]);i++{ //get a row
		for j:=0 ; j<i;j++{      //go through each row until you hit the dignol (0,0),(1,1),(2,2),...,(n,n)
			//swap tab[i][j] and tab[j][i]
			tab[i][j],tab[j][i] = tab[j][i],tab[i][j]
		}
	}
	
}

//more generalized
func transpose(tab *[][]float64) {
	*tab = returnTranspose(*tab)
}	
//returns transposed matrix
func returnTranspose(tab [][]float64) [][]float64{
	outcome := make([][]float64,len(tab[0])) //set length of outer slice of transpose to length of inner slice of the input
	for i:=0;i<len(outcome);i++{
		outcome[i] = make([]float64,len(tab)) //set length of inner slice of transpose to length of outer slice of the input
		for j:=0;j<len(outcome[i]);j++{
			outcome[i][j] = tab[j][i]
		}
	}
	return outcome
}


//2 c)
func sortRows(tab [][]float64){
	done := make(chan int)
	for i:=0;i<len(tab);i++{
		go cSort(tab[i],done)
	}
	for i:=0;i<len(tab);i++{
		<-done
	}
}
func cSort(row []float64, done chan <- int){
	sort(row)
	done <- 1
}


// 2 d)
func main() {
	//input 1

	// array := [][]float64{{7.1, 2.3, 1.1},
	// {4.3, 5.6, 6.8},
	// {2.3, 2.7, 3.5},
	// {4.5, 8.1, 6.6}}

	//input 2
	array	 := [][]float64{{1.1, 7.3, 3.2, 0.3, 3.1},
		{4.3, 5.6, 1.8, 5.3, 3.1},
		{1.3, 2.7, 3.5, 9.3, 1.1},
		{7.5, 5.1, 0.6, 2.3, 3.9}}
	//print (a)
	fmt.Println("Before:")
	for _,v := range(array){
		fmt.Println(v)
	}

	//sort rows (b)
	sortRows(array)
	
	//trasnpose (c)
	transpose(&array)

	//sort rows (d)
	sortRows(array)

	//transpose (e)
	transpose(&array)

	//print result (f)
	fmt.Println("\n\nAfter:\n")
	for _,v := range(array){
		fmt.Println(v)
	}
}
	