--DJ Maguddayao
--2/14/14

--1--
--Psuedo trapezoid.  Takes the as long as the area is above the y axis, it'll work

areaTrap deltaX y1 y2 = deltaX*(min y1 y2) + 0.5*deltaX *(abs(y1-y2));

--Make a rectangle
areaRect deltaX y1 y2 = deltaX*(max y1 y2); 

sum' = foldl (+) 0 --starts from the left.  Adds everything starting with value 0

--2--
findY intervalX x1 x2 fx = map fx [x1, x1 +intervalX..x2]; --transforms x values into y values

--area uses an area pattern fn and iterates through a list of y values
area _ _ [] = error "Not enough to calculate"
area _ _ [y] = error "Not enough to calculate"
area fn intervalX  [y1,y2] = (fn intervalX y1 y2)
area fn intervalX  (y1:y2:ys)  = (fn intervalX y1 y2) + (area fn intervalX (y2:ys))

--area' fn intervalX yList = foldl1 (\x y-> (psuedoArea x y))   yList
--	where psuedoArea = fn intervalX  --failed attempt at trying to use foldr/l to avoid recursion
--area' fn intervalX lst = [(fn intervalX y1 y2) | (y1:y2:yx) <- lst ]

integrate fn intervalX x1 x2 fx = area fn intervalX (map fx xValues) + area fn (x2 -  last xValues) [last xValues, fx x2]  --adds in the last missing area
	where  xValues  = [x1, x1+ intervalX..x2] --helps create the set of y values from the x values.  area does the rest


	--integrate fn intervalX x1 x2 fx
	--Map the inputs to fn (fx x1) and chop off the last one
--3--

trapInt = integrate areaTrap 0.1 --adds in the first two arguements for integrate.  The rest is implied


--4--


--creates a list of lists


createSepLists :: Eq t => [t] -> [[t]]
createSepLists [] = []
createSepLists [x] = [[x]];
createSepLists (x:xs) = [filter (==x) (x:xs) ] ++ createSepLists (filter (/=x) xs) --creates a list of lists.  Recursion sadly


--Finds the largest list of the list of lists


findLargestList lst= foldr1 (\x y -> if (length x) > (length y) then x else y) lst --folds and finds the largest item

mostFrequent [] = error "Not defined on empty lists"
mostFrequent [x] = x
mostFrequent (x:xs) =  head (findLargestList (createSepLists(x:xs))) ---there will only be the largest list left


--assumes word as the last argument.  Replaces the old word with new word if "word" is the same as old
replace old new = \word -> if (word==old) then new else word

--Takes a replace function of something else to see if 
extend repFn old new =
	\word -> if (word == old) then new else (repFn word)





--simplest possible way to add new types to Haskell: Give a new name to an existing type

data Size = Small | Medium | Large

data Shape = Circle Float Float Float | Rectangle Float Float Float Float  

type Name = [Char]

type Score = Int --synonyms uses "type"

data Kind = Assignment | Exam | Quiz
			deriving (Eq, Ord, Show)

data GradeEntry = Entry Name Kind Score
				deriving (Eq,Ord,Show)

getName (Entry name _ _ ) = name

data IntList = Nil | LNode Int IntList
	deriving (Eq,Show) --building a list.  Nodes take an int, and another node it's pointing to (can be Nil for null)

length' Nil = 0
length' (LNode _ rest) = 1 + length' rest

head' (LNode int _) = int
tail' (LNode _ rest) = rest

--Can have polymorphic types too -- things that can hold items of various types

data AnyList a = Empty | ALNode a (AnyList a)
	deriving (Eq,Show)




data Edit = Copy | Delete | Insert Char | Change Char | Kill --data type for Edits
	deriving (Eq,Show)

--Transform returns a list of edits that will turn on string into another.  (Cheapest set of edits)

transform [] [] = [] --turning empty string into the empty string
transform s [] = [Kill]
transform [] s = map Insert s
transform (a:as) (b:bs) 
	|a == b = Copy : transform as bs
	|otherwise = cheapest [ Delete  : transform as (b:bs), --removes a and only has as left
							Insert b : transform (a:as) bs, --inserts b, removes b from b:bs
							Change b : transform as bs ] --changes a into b, removes a and b from remainder

cost seq = length (filter(/=Copy) seq) --finds instances of not Copy in the rest of the sequence and sees which is the longer of the sequences
--cost = length . filter (/=Copy)

cheapest = foldr1 (\s1 s2 -> if cost s1 < cost s2 then s1 else s2)

