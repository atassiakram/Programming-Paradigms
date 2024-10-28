%Q3
sum_odd_numbers([], 0). %base case: empty list, return 0.


sum_odd_numbers([Head|Tail],OddSum) :- %case 1: current element (Head) is odd.
    1 is Head mod 2,
    sum_odd_numbers(Tail,OddSumb4), %get the previous sum
    OddSum is Head + OddSumb4,!. %add previous sum to current ODD element.


sum_odd_numbers([Head|Tail],OddSum) :- %case 2: current element (Head) is even.
    0 is Head mod 2,
    sum_odd_numbers(Tail,OddSumb4),
    OddSum is OddSumb4,!. %just return the previous sum as is.


% I'm aware I don't have to write the last line if I write I make the
% return of the line before it OddSum, but I prefer readability.
% The below will include the code if I decided to take a shortcut for
% case 2:





/*
 * sum_odd_numbers([Head|Tail],OddSum) :-
 *     0 is Head mod 2,
 *     sum_odd_numbers(Tail,OddSum).
 */


