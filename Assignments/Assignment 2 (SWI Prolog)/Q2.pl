%Q2
pet(fido, dog, 3).
pet(spot, dog, 5).
pet(mittens, cat, 2).
pet(tweety, bird, 1).
male(fido).
male(spot).
female(mittens).


%2a already done
%2b: Assuming each name is unique.
species(Species,Count) :-
    findall(Name,pet(Name,Species,_),ListOfPets), %get a list of every name with the given species
    length(ListOfPets,Count). %count the length of the list
% 2d: Including Minimum and Maximum age (so if MinAge is 2 and MaxAge is
% 4, this program will find all animals with ages 2,3, and 4.
age_range(MinAge,MaxAge,Count):-
    findall(Age,(pet(_,_,Age),Age>=MinAge,Age=<MaxAge),ListOfAges), %put all animals in the age range in a list
    length(ListOfAges,Count). %count the length of the list
