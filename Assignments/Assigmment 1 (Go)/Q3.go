package main

import (
	"fmt"
	"math/rand"
	"sync"
)

// 3 a) 
func RandomGenerator(multiple int, wg *sync.WaitGroup, stop <-chan bool) <-chan int {
	intStream := make(chan int)
	go func() {
		defer func() { wg.Done() }()
		defer close(intStream)
		for {
			select {
			case <-stop:
				return
			case intStream <- rand.Intn(1000000) * multiple: //get random and multiply by m to make it a multiple of m
			}
		}
	}()
	return intStream
}

// 3 b)
func Multiple(x int, m int) bool {
	return x%m == 0
}

func main() {
	var wg sync.WaitGroup
	stop := make(chan bool)
	m5, m13, m97 := RandomGenerator(5, &wg, stop), RandomGenerator(13, &wg, stop), RandomGenerator(97, &wg, stop)
	var counter5, counter13, counter97 int = 0, 0, 0
	
	for i := 0; i < 100; i++ {
		select {
		case a := <-m5: //if it comes from the multiple of 5 generator, it is already a multiple of 5
			counter5++
			if Multiple(a, 13) { //check if its a multiple of 13
				counter13++
			}
			if Multiple(a, 97) { //check if its a multiple of 97
				counter97++
			}
		case b := <-m13: //same as case a
			counter13++
			if Multiple(b, 5) {
				counter5++
			}
			if Multiple(b, 97) {
				counter97++
			}
		case c := <-m97: //same as first case a and b
			counter97++
			if Multiple(c, 5) {
				counter5++
			}
			if Multiple(c, 13) {
				counter13++
			}
		}
	}
	//print total number of multiples for 5,13,97
	fmt.Println("total number of multiples of 5:", counter5)
	fmt.Println("total number of multiples of 13:", counter13)
	fmt.Println("total number of multiples of 97:", counter97)
}
