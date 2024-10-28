#lang scheme



(define (readXYZ fileIn)
 (let ((sL (map (lambda s (string-split (car s)))
 (cdr (file->lines fileIn)))))
 (map (lambda (L)
 (map (lambda (s)
 (if (eqv? (string->number s) #f)
 s
(string->number s))) L)) sL)))


;given a list of points, return 3 random points in a list
(define three-random-points (lambda (Ps) (let
                                             ((P1 (list-ref Ps (random (length Ps))))  ;get a random point 
                                              (P2 (list-ref Ps (random (length Ps))))  ;get another random point
                                              (P3 (list-ref Ps (random (length Ps))))) ;get a final random point
                                           (list P1 P2 P3) ;return a list with the 3 points
                                           )))

(define plane (lambda (P1 P2 P3)
                              (let
                                     ;initialize variables
                                     (
                                      ;get V1
                                      (X1 (- (car P2)   (car P1)))    ;get X of first vector
                                      (Y1 (- (cadr P2)  (cadr P1)))   ;get Y of first vector
                                      (Z1 (- (caddr P2) (caddr P1)))  ;get Z of first vector
                                      ;get V2
                                      (X2 (- (car P3)   (car P1)))    ;get X of second vector
                                      (Y2 (- (cadr P3)  (cadr P1)))   ;get Y of second vector
                                      (Z2 (- (caddr P3) (caddr P1)))  ;get Z of second vector
                                      ) ;end of initialization
                                
                                   (let ;second round of initializing variables (a b c) so we can get d
                                       (
                                        (a (- (* Y1 Z2) (* Y2 Z1)))
                                        (b (- (* Z1 X2) (* Z2 X1)))
                                        (c (- (* X1 Y2) (* Y1 X2)))
                                        ) ;end of initialization
                                     (list a b c (+ (* a (car P1)) (* b (cadr P1)) (* c (caddr P1))))
                                     ))))
;test cases:
;(plane '(1 2 3) '(4 5 6) '(7 8 9)) -> (0 0 0 0)
;(plane '(1 2 5) '(5 3 4) '(6 9 2)) -> (4 7 23 133)
;(plane '(3 2 1) '(10 8 2) '(4 9 10)) -> (47 -62 43 60)

;using the formula |ax+by+cz-d|/sqrt(a^2+b^2+c^2)
(define distance (lambda (Plane Point) (let
                                           (
                                            ;initialize plane values
                                            (a (car Plane))
                                            (b (cadr Plane))
                                            (c (caddr Plane))
                                            (d (cadddr Plane))
                                            ;initialize point values
                                            (x (car Point))
                                            (y (cadr Point))
                                            (z (caddr Point))
                                            ) ;end of initialization
                                         (/
                                          (abs (+ (* a x) (* b y) (* c z) (- d))) ; |ax+by+cz-d|
                                          (sqrt (+ (* a a) (* b b) (* c c))) ;sqrt(a^2+b^2+c^2)
                                          ) ;|ax+by+cz-d|/sqrt(a^2+b^2+c^2)
                                         )))
;test cases:
;(distance '(1 2 3 4) '(0 10 20)) -> 20.311854385344255
;(distance '(1 1 1 0) '(1 0 0)) -> 0.5773502691896258
;(distance '(1 1 1 0) '(0 1 0)) -> 0.5773502691896258
;(distance '(1 1 1 0) '(0 0 1)) -> 0.5773502691896258
;(distance '(10 -2 3 5) '(12 13 169)) -> 56.06696375566239


;given a plane, points, and eps, return the support count and the plane as a pair
(define support (lambda (plane points eps)
                  (list (count plane points eps) plane) ;return the support count and the plane as a pair
                  ))
;given a plane, points, and eps, return the support count
(define count (lambda (plane points eps)
                (if (null? points) 0 ;base case
                    (let ;initialize the distance from plane to the first point in the list
                        ((Distance (distance plane (car points))))
                      
                      (if (< Distance eps) (+ 1 (count plane (cdr points) eps)) ;if top element is a neighbor, add 1 to the counter
                          (count plane (cdr points) eps) ;keep counter as is
                          )
                      )
                    )
                ))
;given a list of points, number of iteration, eps, and an initial best support, return the best support
;initially set bestSupport to '(0 (0 0 0 0)) 
(define dominant-plane-BTS (lambda (Ps k eps bestSupport)
                             (if (or (null? Ps) (<= k 0)) bestSupport ;if given an empty list or (base case) k=0, return best support
                                 (let
                                     ((randomPoints (three-random-points Ps)) ;initialize three random points to get a plane
                                      )
                                   (let ((currentPlane (plane (car randomPoints) (cadr randomPoints) (caddr randomPoints)))) ;initialize plane
                                     (let ((currentSupport (support currentPlane Ps eps))) ;get current support
                                       
                                       (if (> (car currentSupport) (car bestSupport)) ;if current support is bigger than best support, 
                                           (dominant-plane-BTS Ps (- k 1) eps currentSupport) ;set bestsupport to currentsupport
                                           (dominant-plane-BTS Ps (- k 1) eps bestSupport) ;otherwise, keep bestsupport as is.
                                           )))))))
;given a list of points, number of iterations, and eps, return the best support
(define dominantPlane (lambda (Ps k eps) (dominant-plane-BTS Ps k eps '(0 (0 0 0 0)))))

;test cases for (dominant-plane (readXYZ "Point_Cloud_1_No_Road_Reduced.xyz") 5 0.0005):
;(19 (-40.97547624093104 1.5263044754602808 -0.08496438891180169 -473.5096603035303))
;(17 (-4.225064661618184 0.19672091371789385 0.06145894840855548 -48.78096521110075))
;(7 (68.45667610528386 -11.273618247219519 643.473244600967 155.69989230581072))
;(7 (3.7695122753856953 4.887251715724188 -104.42793193046286 -30.846558165532716))
;(7 (2.3732333524755527 -8.20543614766367 28.203429806406557 3.8356092607342767))
;(8 (8.713162017420586 -3.996791435265541 276.8957244090566 106.90121169312644))
;(6 (10.202310051029192 -4.9459658249885905 -41.531534023717825 41.97076565312269))
;(8 (-10.951911518024746 -0.15287927817927222 70.21476453178681 0.09465361251755133))
;(9 (-21.5362561677855 45.15400377737848 -343.5440461608685 -151.4315165966308))
                          
; The equation for the ransac number of iteration is :
;log(1-C)/log(1-p^n) = log base (1-p^n) of 1-C, we only need to find 1 dominant plane, so n=1
;if we get a decimal, we need to round up

(define ransacNumberOfIteration (lambda (confidence percentage)
                                  (ceiling (log (- 1 confidence) (- 1 (expt percentage 3)) ;log base (1-p^n) of 1-C
                                       ))))
;test cases:
;(ransacNumberOfIteration 0.99 0.88) -> 5.0

;given a filename, confidence, percentage, and eps, computes the number of iterations and returns the best support
;using the ransac algorithm
(define planeRANSAC (lambda (filename confidence percentage eps)
                      (let
                          ((Ps (readXYZ filename)) (k (ransacNumberOfIteration confidence percentage)))
                        (dominantPlane Ps k eps)
                        )))
                                           
;perform the planeransac algorithm n times                         
(define planeRANSAC-ntimes (lambda (filename confidence percentage eps n) (if
                                                                         (= n 0) '()
                                                                         (append
                                                                          (list (planeRANSAC filename confidence percentage eps))
                                                                          (planeRANSAC-ntimes filename confidence percentage eps (- n 1))
                                                                          ))))
;uses:
;(planeRANSAC-ntimes "Point_Cloud_1_No_Road_Reduced.xyz" 0.99 0.88 0.0005 10)
;(planeRANSAC-ntimes "Point_Cloud_2_No_Road_Reduced.xyz" 0.99 0.88 0.0005 10)
;(planeRANSAC-ntimes "Point_Cloud_3_No_Road_Reduced.xyz" 0.99 0.88 0.0005 10)

                                       
                                       
                                       
                                   
                                      
                                      





                                              