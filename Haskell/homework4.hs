--DJ Maguddayao
--2/6/14
--Homework 4

--1--

data Color = Red | Green | Blue | Yellow | Purple | Brown
             deriving (Eq, Enum, Show, Bounded)

-- We also need a structure for associating a color with an item.  Note 
-- that this type is parameterized, and can associate colors with items
-- of ANY type.

data Binding a = CB a Color
                 deriving (Eq, Show)

--2--

--Edge a means that it takes the type of a.  Can put in two different a's--
data Edge a = E a a
				deriving (Eq, Show)

--3--

--Create a type Graph which is a list of Edges

type Graph a = [Edge a]

--4--
--helper methods for grabbing stuff
getVariable (CB b color) = b;
getColor (CB b color) = color; 

colorOf :: Eq a => a -> [Binding a] -> Maybe Color
colorOf a bindList = if finder == [] then Nothing else Just (getColor (head finder))
	where finder = filter (\n -> getVariable n == a) bindList

--5--

--If two nodes clash (have the same color in an edge), returns true, else false
nodesClash :: Eq a => [Binding a] -> Edge a -> Bool
nodesClash xs (E x y)
	|finder x == [] = False
	|finder y == [] = False
	|getColor(head (finder x)) == getColor(head (finder y)) = True
	|otherwise = False
	where finder item = filter(\n ->getVariable n == item) xs

--6--

--helpers for grabbing parts of an edge
getLeftNode (E x _) = x
getRightNode (E _ y) = y

--helper for removing repeat items
removeRepeats [] = []
removeRepeats [x] = [x]
removeRepeats (x:xs) = x :  removeRepeats (filter (/= x) xs)

uniqueNodes :: Eq a => Graph a -> [a]
uniqueNodes x = removeRepeats nodeList
	where nodeList = [getLeftNode n| n<-x ]++ [getRightNode n | n<- x]

--7--

noClashes :: Eq a => [Binding a] -> Graph a -> Bool
noClashes bindLst edgeLst = if (filter (nodesClash bindLst) edgeLst) == [] then True else False
--filters out nonclashing nodes (only clashing nodes left).   As a result, if it's empty, none of the nodes clashed

--8--

setToMinimum a = CB a (minBound :: Color)
setAllToMinimum nodeList = map setToMinimum nodeList
--creates a list of Bindings of all color of the smallest

assignColors :: Eq a => [a] -> Graph a -> [Binding a]
assignColors nodeList edgeList = assignColours minList edgeList
			where minList = setAllToMinimum nodeList

--try doing recursion ouside of assignColors
incrementBind (CB a color) = CB a (succ color)

assignColours coloredList [] = coloredList --base case, no edges left to check
assignColours coloredList edgeList = if noClashes coloredList [head edgeList] then assignColours coloredList (tail edgeList)
 else assignColours (incrementedNodes ++ restOfList) removeConnection
	where 
		connectedEdgeList = [n | n <- edgeList, getLeftNode n == getLeftNode (head edgeList)] ++ [n | n <- edgeList, getRightNode n == getLeftNode (head edgeList)]--list of items that are connected
		edgesNeededToIncrement = [n | n<-connectedEdgeList, nodesClash coloredList n] --edges that violate the relationship
		listOfNodesConnected =  filter (/= (getLeftNode (head edgeList))) (uniqueNodes edgesNeededToIncrement) --every node that is connected to the first one except the first one
		nodesToIncrement = [n | n <- coloredList, (checkIfElem (getVariable n) listOfNodesConnected)==True]
		incrementedNodes = map incrementBind nodesToIncrement
		restOfList = [n | n <- coloredList, (checkIfElem (getVariable n) listOfNodesConnected) == False]--(filter (\x -> getVariable x /= getLeftNode ((head (edgeList))) ) coloredList) --remove the rest of the list not in nodesToIncrement
		removeConnection = [n| n <- edgeList, getLeftNode n /= getLeftNode (head edgeList), getRightNode n /= getLeftNode (head edgeList)] --list of unconnected items

checkIfElem _ [] = False
checkIfElem element (x:xs) = if x == element then True else checkIfElem element xs
--instead of incrementing the first node and ignoring the rest, try incrementing everything it's connected to 


--9--

colorGraph edgeList = assignColors (uniqueNodes edgeList) edgeList
