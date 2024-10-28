%Q1
parent(john, mary).
parent(john, tom).
parent(alice, mary).
parent(mary, ann).
parent(mary, fred).
parent(tom, liz).
male(john).
male(tom).
male(fred).
female(mary).
female(ann).
female(liz).

%1a already done.

%1b
sibling(Sibling1,Sibling2) :- %check if children have the same parent
    parent(ParentOfBoth,Sibling1),parent(ParentOfBoth,Sibling2),!.

%1c
grandparent(Grandparent, Grandchild) :- %check if the child of the grandparent is the parent of the grandchild
    parent(Grandparent,Parent), parent(Parent,Grandchild),!.

%1d
ancestor(Ancestor, Descendant) :- % Base Case: Check the immediate generation
    parent(Ancestor, Descendant),!.
ancestor(Ancestor, Descendant) :-
%Keep checking the previous generation until we find an immediate generational link (as in the base case).
   parent(X, Descendant),
   ancestor(Ancestor, X).
