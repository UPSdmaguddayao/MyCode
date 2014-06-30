/*
	DJ Maguddayao
	May 7th, 2014
	Homework Number 9
*/

%colors in this list (from homework 4).
color(red).
color(green).
color(blue).
color(yellow).
color(purple).
color(brown).

states([edge(wa, or), edge(or, ca), edge(wa, id), edge(or, id), edge(ca, nv), edge(id, nv),
edge(or, nv), edge(id, mt), edge(id, wy), edge(id, ut), edge(nv, ut), edge(nv, az),
edge(ca, az), edge(ut, az), edge(mt, wy), edge(wy, ut), edge(wy, co), edge(co, ut),
edge(co, nm), edge(nm, az), edge(mt, nd), edge(mt, sd), edge(nd, mn), edge(nd, sd),
edge(sd, wy), edge(sd, ne), edge(ne, co), edge(sd, mn), edge(sd, ia), edge(ne, ia),
edge(ne, ks), edge(ne, mo), edge(nm, tx), edge(nm, ok), edge(tx, ok), edge(co, ok),
edge(co, ks), edge(ks, ok), edge(ks, mo), edge(ok, ar), edge(ar, la), edge(ar, tx),
edge(mo, ar), edge(ia, mo), edge(mn, ia)]). %list of states

cb(X,Y) :- color(Y). %color bind

edge(X,Y). %%<-- needed to declare that edge is actually a thing in this

colorGraph([],[]). %basecase for this part

colorGraph([edge(X,Y)|OtherEdges], [cb(X,ColorOne), cb(Y,ColorTwo)|OtherColors]) :- color(ColorOne), color(ColorTwo), color(ColorOne) \= color(ColorTwo), colorGraph(OtherEdges, OtherColors, [cb(X,ColorOne),cb(Y,ColorTwo)], [edge(X,Y)|OtherEdges]). %tries to find out what the other colors actually are.  Uses a helper method.

colorGraph([],[],_, _). %basecase for this part.  If there are no edges left, there are no "other colors" left.
colorGraph([edge(X,Y)|OtherEdges], OtherColors, ColoredNodes, EdgeList) :- member(cb(X,ColorOne), ColoredNodes), member(cb(Y,ColorTwo), ColoredNodes), colorGraph(OtherEdges, OtherColors, ColoredNodes, EdgeList). %case where both edges are colored already
colorGraph([edge(X,Y)|OtherEdges], [cb(X,ColorOne)|OtherColors], ColoredNodes, EdgeList) :- color(ColorOne), color(ColorTwo), member(cb(Y,ColorTwo), ColoredNodes), \+ member(cb(X,ColorOne), ColoredNodes), colorHelper(X, ColorOne, ColoredNodes, EdgeList, []), colorGraph(OtherEdges, OtherColors, [cb(X,ColorOne)|ColoredNodes], EdgeList). %case where Y is colored, but not X.
colorGraph([edge(X,Y)|OtherEdges], [cb(Y,ColorTwo)|OtherColors], ColoredNodes, EdgeList) :- color(ColorOne), color(ColorTwo), member(cb(X,ColorOne), ColoredNodes), \+ member(cb(Y,ColorTwo), ColoredNodes), colorHelper(Y, ColorTwo, ColoredNodes, EdgeList, []), colorGraph(OtherEdges, OtherColors, [cb(Y,ColorTwo)|ColoredNodes], EdgeList). %case where X is colored, but not Y.
colorGraph([edge(X,Y)|OtherEdges], [cb(X,ColorOne), cb(Y,ColorTwo)|OtherColors], ColoredNodes, EdgeList) :- color(ColorOne), color(ColorTwo), \+ member(cb(X,ColorOne), ColoredNodes), \+member(cb(Y,ColorTwo), colorHelper(X, ColorOne, ColoredNodes, EdgeList, []), colorHelper(Y, ColorTwo, ColoredNodes, EdgeList), ColoredNodes), ColorOne \= ColorTwo, colorGraph(OtherEdges,OtherColors,[cb(X,ColorOne),cb(Y,ColorTwo)|ColoredNodes], EdgeList). %case where neither X nor Y are colored.  Still needs to loop in the color helper to avoid conflicts with future items or items that are already colored.

%recursively find a color that fits.  Once theres no matching nodes, 

colorHelper(X, ColorX, ColoredNodes, EdgeList, NoColorList) :- print(X), print(NoColorList), nl, fail. %print method for helping
colorHelper(X, ColorX, ColoredNodes, EdgeList, NoColorList) :- \+ member(edge(X,Y), EdgeList), \+ member(edge(Y,X), EdgeList), \+ member(color(ColorX), NoColorList). %stops looping.
colorHelper(X, ColorX, ColoredNodes, EdgeList, NoColorList) :- member(edge(X,Y), EdgeList), member(cb(Y,ColorTwo), ColoredNodes), color(ColorX), color(ColorTwo), color(ColorX) \= color(ColorTwo), \+ member(color(ColorX), NoColorList), delete(EdgeList, edge(X,Y), NewList), append(NoColorList, [color(ColorTwo)], AppendList), colorHelper(X, ColorX, ColoredNodes, NewList, AppendList). %case where Edge(X,Y) exists (aka it connected to something) and Ys color is known.
colorHelper(X, ColorX, ColoredNodes, EdgeList, NoColorList) :- member(edge(Y,X), EdgeList), member(cb(Y,ColorTwo), ColoredNodes), color(ColorX), color(ColorTwo), color(ColorX) \= color(ColorTwo), \+ member(color(ColorX), NoColorList), delete(EdgeList, edge(Y,X), NewList), append(NoColorList, [color(ColorTwo)], AppendList), colorHelper(X, ColorX, ColoredNodes, NewList, AppendList). %case where Edge(Y,X) exists (aka it connected to something) and Ys color is known.
colorHelper(X, ColorX, ColoredNodes, EdgeList, NoColorList) :- member(edge(X,Y), EdgeList), \+ member(cb(Y,ColorTwo), ColoredNodes), color(ColorX), delete(EdgeList, edge(X,Y), NewList), \+ member(color(ColorX), NoColorList), colorHelper(X, ColorX, ColoredNodes, NewList, NoColorList). %case where Edge is found, but Y isnt colored.  So just give X a color and continue looping
colorHelper(X, ColorX, ColoredNodes, EdgeList, NoColorList) :- member(edge(Y,X), EdgeList), \+ member(cb(Y,ColorTwo), ColoredNodes), color(ColorX), delete(EdgeList, edge(Y,X), NewList), \+ member(color(ColorX), NoColorList), colorHelper(X, ColorX, ColoredNodes, NewList, NoColorList). %case where Edge is found, but Y isnt colored.  So just give X a color and continue looping

%only problem with this program is that when you hit ";", it doesnt work well.  Does well with the first case though.