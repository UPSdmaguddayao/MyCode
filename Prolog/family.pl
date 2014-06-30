%
% Start with some facts drawn from my family tree
%

father(david, holly).
father(david, heather).
father(durkee, brad).
father(durkee, trevor).
father(leverett, durkee).
father(leverett, elmo).
father(brad, charlie).
father(brad, flora).
father(reuben, virginia).
father(reuben, dorothy).

mother(nancy, holly).
mother(nancy, heather).
mother(mary, brad).
mother(mary, trevor).
mother(holly, charlie).
mother(holly, flora).
mother(virginia, durkee).
mother(virginia, elmo).

male(david).
male(durkee).
male(leverett).
male(brad).
male(charlie).
male(trevor).
male(elmo).
male(reuben).

female(nancy).
female(mary).
female(virginia).
female(dorothy).
female(holly).
female(flora).
female(heather).

% 
% Rules describing the parent relation
%

parent(Mom, Person) :- mother(Mom, Person).
parent(Dad, Person) :- father(Dad, Person).

grandmother(Granny, Person) :- mother(Granny, Parent), parent(Parent, Person).

son(Dad,Son) :- father(Dad,Son), male(Son).

grandparent(Gran,Person) :- parent(Gran,Middle), parent(Middle,Person).

/*
grandparent(G,P) :- father(G,M), father(M,P).
grandparent(G,P) :- father(G,M), mother(M,P).
grandparent(G,P) :- mother(G,M), mother(M,P).
grandparent(G,P) :- mother(G,M), father(M,P).
*/

% Y'all forced me to use "Rent" for the parent variable name.

brother(Bro, Person) :- 
  male(Bro), parent(Rent, Bro), parent(Rent, Person), Bro \= Person.


/*
ancestor(A,P) :- parent(A,P).
ancestor(A,P) :- grandparent(A,P).
ancestor(A,P) :- parent(A,GP), grandparent(GP,P).
...
*/

ancestor(A,P) :- parent(A,P).
ancestor(A,P) :- parent(A,Middle), ancestor(Middle,P).




