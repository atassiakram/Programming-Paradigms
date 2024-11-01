% Read points from file XYZ file
read_xyz_file(File, Points) :-
    open(File, read, Stream),
    skip_line(Stream), % Skip the first line
    read_xyz_points(Stream, Points),
    close(Stream).

read_xyz_points(Stream, []) :-
    at_end_of_stream(Stream), !.

read_xyz_points(Stream, [Point|Points]) :-
    \+ at_end_of_stream(Stream),
    read_line_to_string(Stream, Line),
    split_string(Line, "\t", "\s\t\n", XYZ),
    maplist(atom_number, XYZ, Point),
    read_xyz_points(Stream, Points).



%get 3 pionts as a list given a list of points
%random3points(Points,Point3).
random3points(Points,[P1,P2,P3]) :-
    random_member(P1,Points), %first random point
    random_member(P2,Points), %second random point
    random_member(P3,Points). %third random point



% plane(Point3,Plane) : given a list of 3 points, this method will
% return the cartesian plane
plane([[X1,Y1,Z1],[X2,Y2,Z2],[X3,Y3,Z3]],[A,B,C,D]) :-
    V1X is X2 - X1, %first vector x
    V1Y is Y2 - Y1, %first vector y
    V1Z is Z2 - Z1, %first vector z
    V2X is X3 - X1,
    V2Y is Y3 - Y1,
    V2Z is Z3 - Z1,
    A is V1Y*V2Z - V2Y*V1Z,
    B is V1Z*V2X - V2Z*V1X,
    C is V1X*V2Y - V2X*V1Y,
    D is A*X1+B*Y1+C*Z1.

% support(Plane,Points,Eps,N): given a plane, list of points,and an
% epsilon, this method will return the number of neighbors to the plane.
support(_,[],_,0).
support(Plane,[H|T],Eps,N) :-
    distance(Plane,H,Distance), %get distance
    support(Plane,T,Eps,Nb4), %get previous support
    (   Distance < Eps -> N is Nb4+1;
    N is Nb4).
% distance(Plane,Point,Distance): givena plane and a point,this
% method will return the distance between a plane and a point
% |ax+by+cz-d|/sqrt(a^2+b^2+c^2)
distance([A,B,C,D],[X,Y,Z],Distance) :-
    Top is A*X+B*Y+C*Z-D,
    Numerator is abs(Top),
    Bottom is A**2+B**2+C**2,
    Denominator is sqrt(Bottom),
    Distance is Numerator/Denominator.


% ransac-number-of-iterations(Confidence,Percentage,N): given a
% confidence and percentage, this method will return the number of
% iterations
% N = log(1-C)/log(1-p^3) = ln(1-C)/ln(1-p^3), rounded upwards
ransac-number-of-iterations(Confidence,Percentage,N):-
    Numerator is log(1-Confidence),
    Denominator is log(1-Percentage**3),
    N is ceiling(Numerator/Denominator).


%testing for plane:
test(plane,1) :- plane([[1,2,3],[4,5,6],[7,8,9]],[0,0,0,0]).
test(plane,2) :-  plane([[1,0,2],[10,9,2],[3,14,20]],[162, -162, 108, 378]).
test(plane,3) :- plane([[1,1,2],[2,3,1],[4,4,4]],[7,-5,-3,-4]).
test(plane,4) :-  plane([[5,4,3],[2,9,10],[5,7,3]],[-21,0,-9,-132]).

%testing for support:
test(support,1) :-  support([1,1,1,0],[[1,0,0],[0,1,0],[0,0,1],[2,0,0]],1,3).
test(support,2) :- support([10,20,30,-10], [[1,2,3],[4,5,6],[7,8,9],[1,1,1],[2,2,2]],5,3).
test(support,3) :- support([1,0,0,0],[[10,2,3],[4.9,5.2,6],[5.8,2,9]],5,1).
test(support,4) :- support([0,0,1,2],[[121,312,2],[0,15,25],[0,1,2],[3,10,15]],5,2).

%testing for ransac-number-of-iterations:
test(ransac-number-of-iterations,1) :- ransac-number-of-iterations(0.99,0.88,5).
test(ransac-number-of-iterations,2) :- ransac-number-of-iterations(0.99,0.5,35).
test(ransac-number-of-iterations,3) :- ransac-number-of-iterations(0.8,0.8,3).
test(ransac-number-of-iterations,4) :- ransac-number-of-iterations(0.7,0.35,28).



%testing for random3points (different tests used).
test(random3points,1) :- %test to see if the all 3 points from the random3points is an actual point from the list
    List = [[1,2,3],[4,5,6],[7,8,9],[10,11,12]],
    random3points(List,[P1,P2,P3]),
    member(P1,List),
    member(P2,List),
    member(P3,List).
test(random3points,2) :-
    List = [[1,2,3],[4,5,6],[7,8,9],[10,11,12,[20,132,58],[19,20,30], %same test as above
    random3points(List,[P1,P2,P3]),
    member(P1,List),
    member(P2,List),
    member(P3,List).
test(random3points,3) :- %making sure that the random3points covers all possible answers
    List = [[10,9,8],[4,5,6]],
    random3points(List,[RandomPoint|_]),
    (   RandomPoint = [10,9,8] ; RandomPoint = [4,5,6]).
test(random3points,4) :- %testing to see that all points exist in the list
   random3points([[1,2,3]],[P1,P2,P3]),
   P1 = P2, P2 = P3,P3 = [1,2,3].





