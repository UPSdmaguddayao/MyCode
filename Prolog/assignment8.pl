:- dynamic fac/2.

/*
DJ Maguddayao
4/23/14
*/
/*
natural(0).
natural(s(N)) :- natural(N).
*/
/*
1)
*/
nat_int(0,0).
nat_int(s(N),I) :- nat_int(N,X), plus(X,1,I).

/*
2)
*/


ancestor_path(Ancestor, Person, [Ancestor,AChild|Rest]) :- ancestor(Ancestor,Person), parent(Ancestor,AChild), ancestor_path(AChild,Person,[AChild|Rest]).
ancestor_path(Person,Person,[Person]).


/*
3) Find out how to parse backwards
*/

numnames([nine,hundred],900).
numnames([eight,hundred],800).
numnames([seven,hundred],700).
numnames([six,hundred],600).
numnames([five,hundred],500).
numnames([four,hundred],400).
numnames([three,hundred],300).
numnames([two,hundred],200).
numnames([one,hundred],100).
numnames([ninety],90).
numnames([eighty],80).
numnames([seventy],70).
numnames([sixty],60).
numnames([fifty],50).
numnames([fourty],40).
numnames([thirty],30).
numnames([twenty],20).
numnames([ten],10).
numnames([nine],9).
numnames([eight],8).
numnames([seven],7).
numnames([six],6).
numnames([five],5).
numnames([four],4).
numnames([three],3).
numnames([two],2).
numnames([one],1).
numnames([],0).

%%This works, but it cant read backwards for some reason

numnames([X,hundred,and|Rest], Number) :- numnames([X,hundred],Hun), numnames(Rest,Numeral), plus(Hun,Numeral,Number).

numnames([X,Y], Number) :- numnames([X],Tens), numnames([Y],Ones), plus(Tens,Ones,Number). %%pattern matches two digits


%negation used to be not(), but now is \+

male(x) :- \+ female(x).  %However, it wont find females, it will find females and then negates it :<

set_contains(Item, Set) :- member(Item, Set).
add_item(Item, [Item|Set]) :- \+ member(Item,Set). %[Item|Set] will be the result returned


natural(X) :- write(X), nl, fail. %acts like PrintLine, but it helps.  Fail will always return false, but the first two parts are undoable at that point.
natural(0).
natural(s(N)) :- natural(N).

s(X,Y) :- q(X,Y).
s(0,0).

q(X,Y) :- i(X), !, j(Y).  %addded a cut.... (!)

i(1).
i(2).

j(1).
j(2).
j(3).

/*
s(A,B).
A = B, B = 1 
A = 1,
B = 2 
A = 1,
B = 3 
A = B, B = 0.
Doesn't even explore the i(2) case because it cuts the branch off
*/


new_fact(F) :- 
	\+ clause(F,true),
	write('remembering '), write(F), nl,
	asserta(F).
new_fact(F). %helps for remembering new facts.  Makes it self learn and do things

fac(0,1).
fac(N,NFac):-
	N>0,
	N1 is N - 1,
	fac(N1,N1Fac),
	NFac is N * N1Fac, 
	new_fact(fac(N,NFac)).