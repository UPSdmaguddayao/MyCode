--DJ Maguddayao
--CS291
--2.4.2014


--construct a list of first quadrant

--Question 1
averageDist lst = sum distance / fromIntegral (length distance) --constructs a list of viable distances and averages them
	where distance = [sqrt (a^2 + b^2) | (a,b)<- lst, a >0, b>0]


--Question 2 
sameOutput :: Integral a => (a -> a) -> (a -> a) -> a -> a -> Bool --construct a list from a -> b and checks if the two transformed lists are similar
sameOutput m1 m2 a b = if ([m1 n | n <-[a..b]] == [m2 n | n<-[a..b]]) then True else False

--Question 3--
largestDiff [] = error "not enough items" --needs 2 items at least
largestDiff [x] = error "not enough items"
largestDiff [x,y] = abs (x-y) --base case
largestDiff (x:y:xs)
	|abs (x-y) > bigDiff = abs (x-y)
	|otherwise = bigDiff
	where bigDiff = largestDiff (y:xs)

safeSqrt = sqrt . abs

