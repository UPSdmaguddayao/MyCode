--DJ Maguddayao
--CS291
--1.31.2014

--1--

-- squares an input by multiplying it by itself
square n = n*n;

--2--

--uses the square function and finding the square root.  Helps get rid of a '-'
absolute n = sqrt (square n);

--3--

-- Finds if the first two numbers add up to the last one.

sumTo x y z =  x + y == z;

--4--

--Findest the largest square...yeah.  Oh, if the first number is bigger than the second one, then it displays an error

largestSquare x y = if x > y then error "First argument must be <= the second" else max (square x) (square y);

--5--

--Pretty...uhh, straight forward.  Has three inputs:  two tuples and a boolean.  Output is a tuple.

leftForTrueRightForFalse (x,y) (w,z) bool = if bool == True then (x,y) else (w,z);