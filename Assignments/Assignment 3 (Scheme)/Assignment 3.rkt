#lang scheme


;given sudoko
(define sudoku1 '((2 1 4 3) (4 3 2 1) (1 2 3 4) (3 4 1 2)))
(define sudoku2 '((2 1 4 3) (4 3 2 1) (1 2 3 3) (3 4 1 2)))


;part a: check if there exists the same element after its first appearence in the list (check for duplicates)
(define different (lambda (List) (cond
                                   ((null? List) #t) ;base case, if empty or reach the end
                                   ((member (car List) (cdr List)) #f) ;found a duplicate
                                   (#t (different (cdr List))) ;recursively do the next element in the list
                                   )))
#| test case -> result
(different '(1 2 3 4 5)) -> #t
(different '(1 2 3 4 5 1)) -> #f
(different '(1 3 6 4 8 0)) -> #t
|#


;part b: transpose the given 2D list
(define extract4Columns (lambda (List) (apply map list List)))
#| test case -> result
(extract4Columns sudoku1) -> '((2 4 1 3) (1 3 2 4) (4 2 3 1) (3 1 4 2))
(extract4Columns sudoku2) -> '((2 4 1 3) (1 3 2 4) (4 2 3 1) (3 1 3 2))
|#


;part c: get the first and second pair of each inner list, combine appropriately
;L1 first-pair with L2 first pair , L1 second pair with L2 second pair, L3 first pair with L4 first pair, L3 second pair with L4 second pair


;returns the first and second element of the list
(define get-pair (lambda (List) (if
                                 (or (null? List) (null? (cdr List))) ;first or second element missing
                                 (display "Invalid Input")
                                 (list (car List) (cadr List)))))
#| test case -> result
(get-pair '()) -> Invalid Input  (Empty List)
(get-pair '(1)) -> Invalid Input
(get-pair '(1 2 3)) -> '(1 2)
|#


;get the quadrant pair from 2, assuming both input lists have the same length and are of even length
(define quadrant-pair-list (lambda (List1 List2) (if (null? List1) '()
                                                     (cons
                                                      (append (get-pair List1) (get-pair List2))
                                                      (quadrant-pair-list (cddr List1) (cddr List2))
                                                      ))))
#| test case -> result
(quadrant-pair-list '(1 2 3 4) '(5 6 7 8)) -> '((1 2 5 6) (3 4 7 8))
(quadrant-pair-list '(1 2 3 4 5 6) '(7 8 9 10 11 12)) -> '((1 2 7 8) (3 4 9 10) (5 6 11 12))
|#


;(last step) get each quadrant (this works for more than a 4 by 4 sudoku, and you can't sue me for going overboard)
(define extract4Quadrants (lambda (List) (cond
                                           ((null? List) '()) ;base case: empty list or last list, return null
                                           ((null? (cdr List)) (display "Given List is not a Sudoko"))
                                           (#t (append
                                                (quadrant-pair-list (car List) (cadr List)) ;first pair of lists (innerlist 1 and innerlist 2)
                                                (extract4Quadrants (cddr List)) ;recursively call next pair (skip 2 not 1)
                                                )))))
#| test case -> result
(extract4Quadrants sudoku1) -> '((2 1 4 3) (4 3 2 1) (1 2 3 4) (3 4 1 2))
(extract4Quadrants sudoku2) -> '((2 1 4 3) (4 3 2 1) (1 2 3 4) (3 3 1 2))
|#

;part d: merge all 3 lists
(define merge3 (lambda (List1 List2 List3) (append List1 List2 List3)))
#| test case -> result
(merge3 '(1 3 6) '(5 4) '(1 2 3)) -> (1 3 6 5 4 1 2 3)
|#


;part e:mer ge and check map the different function on them, then check if it has a #f. If it has a #f, it is not a sudoku, otherwise it is

;e.1 and e.2: merge the list and map 'different' on all inner lists of the merged list
(define merge-and-check-each (lambda (List) (map different (merge3 List (extract4Columns List) (extract4Quadrants List)))))
#| test case -> result
(merge-and-check-each sudoku1) -> (#t #t #t #t #t #t #t #t #t #t #t #t)
(merge-and-check-each sudoku2) -> (#t #t #f #t #t #t #t #f #t #t #t #f)
|#

;e.3
;check if all elements are true
;assuming we will not have an initially empty list, and assuming each element is a boolean value
(define check-all-true (lambda (List) (cond
                                        ((null? List) #t) ; we reached the end and there is no false
                                        ((eq? (car List) #f) #f) ;if the list has a false value, return false
                                        (#t (check-all-true (cdr List)))
                                        )))
#| test case -> result
(check-all-true '(#t #t #t #t #t #t #t #t #t #t #t #t)) -> #t
(check-all-true '(#t #t #f #t #t #t #t #f #t #t #t #f)) -> #f
|#


;combining 'merge-and-check-each' with 'check-all-true' to check if the given list is a sudoku (should hypothetically work for bigger than 4 by 4 sudokus)
(define checkSudoku (lambda (List) (check-all-true (merge-and-check-each List))))
#| test case -> result
(checkSudoku sudoku1) -> #t
(checkSudoku sudoku2) -> #f
|#