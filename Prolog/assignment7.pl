
/*
1)
does floras mother have any other children?

mother(M,flora),parent(M,C), C\=flora.
*/


/*
2)
To find all the great uncles, Id use great_uncle(GU,brad) and follow the list of GU....but apparently
great_uncle(GU,brad) returns false
*/

/*
This works, but we already have a brother operation in family.pl
great_uncle(GUncle, Person) :- grandparent(Gran,Person),parent(GP,Gran), parent(GP,GUncle), GUncle\=Gran, male(GUncle). 
*/

great_uncle(GUncle, Person):- grandparent(Gran,Person), brother(GUncle,Gran).

/*
3)
Full brother would share both parents.  So, find out if you share the same mother and father.

To find all the full brothers in this database, you will use full_brother(F,P).
*/

full_brother(Full, Person) :- mother(M, Full), mother(M, Person), father(F, Person), father(F,Full), male(Full), Full\=Person.

/*
4)
*/

/*					___________________________________________________________
					|  brother(brad,trevor)   				                   |
					|__________________________________________________________|
					/				|					|					  \
			____________	____________________	_______________________   ______________
			|male(brad)|	|parent(Parent,brad)|   |parent(Parent,trevor)|  |brad\= trevor|
			|__________|	|___________________|	|_____________________|	 |_____________|
									|		 Parent = durkee		|				|
									|								|			   true
							 _____________________          ______________________
							|father(durkee,brad)|			|father(durkee,trevor)|
							|___________________|			|_____________________|
										  |								  |
										true							true

*/


edge(a,b).
edge(b,c).
edge(b,d).
edge(c,e).
edge(d,e).
edge(e,f).
edge(g,h).

edge(f,a).


connected(X,Y) :- edge(X,Y).
connected(X,Y) :- edge(X,Mid), connected(Mid,Y).


natural(0).
natural(s(X)) :- natural(X).

%% Addition over natural numbers

%%s(0) :- natural(0).
%%s(X) :- s(X-1).

p(0,X,X) :- natural(X).
p(s(X),Y,s(Z)) :- p(X,Y,Z).

times(0,X,0) :- natural(X).
times(s(X),Y,Z) :- times(X,Y,XY), p(Y,XY,Z).

fac(0,s(0)).
fac(s(N),F) :- fac(N,NFac), times(NFac,s(N),F).

less_than(0,s(N)) :- natural(N).
less_than(s(X),s(Y)) :- less_than(X,Y).

mod(N,D,N) :- less_than(N,D). /*the solution is N when D is larger than N*/
mod(N,D,Rem):- p(M,D,N), mod(M,D,Rem). /*subtracts the denominator from the numerator and keep going until we end up with the basecase.  Utilizes the addition relation to do this*/


mod_dec(N,D,Rem) :- times(D,C,DC), p(DC,Rem,N), less_than(Rem,D). /*Don't use this one...it's sadness incarnate.  However, it's very declaritive (doesn't use recursion.  Don't worry about how the machine executes.  No pattern)*/

/*to abbort, hit up arrow and type abort*/

% elem(Item,List) is true if Item is an element of List

elem(Item, [Item| __ ]).  %%It's true if it's at the front of the list
elem(Item, [_ | Tail]) :- elem(Item,Tail).

/*
Acts like member(Item, List).
*/


len([],0).
len([Head|Tail] , L) :- len(Tail, X), plus(X,1,L).
/*len(List,L) :- l*/

sum([],0).
sum([Head|Tail], S) :- sum(Tail,TailSum), plus(Head,TailSum,S).

/*
More of lists
*/
append2([],L,L).
append2([X|Xs],L,[X|Tail]) :- append2(Xs,L,Tail). %Places X first and puts a tail at the end.

prefix(Pre,List) :- append(Pre,_,List). %finds if the Pre is the beginning part(s) of List
suffix(Suf,List) :- append(_,Suf,List). %finds if Suf is the last part(s) of List

sublist(Sub,List) :- append(Pre,Sub,Front), append(Front,Back,List). %finds the different sublists of a List.  First append takes off things from the sub list, second append finds finds the sub list

sorting(L,Sorted) :- permutation(L,Sorted),ordered(Sorted).

ordered([]).
ordered([X]).
ordered([X,Y|Rest]) :- X =< Y, ordered([Y|Rest]).

permutation(Xs, [Y|Ys]) :- select(Y,Xs, ShorterXs), permutation(ShorterXs,Ys).
permutation([],[]).

select(X,[X|Tail], Tail). %easiest to grab is the head.
select(X,[Y|Tail],[Y|TailWithoutX]) :- select(X,Tail,TailWithoutX).
