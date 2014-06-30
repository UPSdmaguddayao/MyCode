/**********************************************************************
 *  README.txt                                                   
 *  CS315 - Basic OpenGL
 **********************************************************************/

/**********************************************************************
* What is your name?
***********************************************************************/

DJ Maguddayao



/**********************************************************************
* What does your program do? Include a brief description!
***********************************************************************/

This program draws a 2-Dimensional MultiColored Hexagon that can be moved
around by a user dragging along the screen from any point



/**********************************************************************
* Briefly explain the strategy you used to get the colors of the
* Hexagon's component triangles to line up correctly.
***********************************************************************/

To line the colors up correctly, I first made sure I can change colors.
After doing that, I figured I can make any combinations of the colors I have with
two triangles that I can rotate around.  After that, it became a matter of fitting which
rotations and which triangles I needed.  

Fitting the triangles used rotation and figuring out
(relative to what is 'up' at the time) how many places to move based on the original's
height and how much I needed to move horizontally (usually only 0.5f).  Triangle 1 seems compliant
and didn't need extra math (just translate then rotate), but Triangle 2 needed to be rotated and 
fixed from there.



/**********************************************************************
* For applying movement, did you transform the view matrix or the model
* matrix? Explain your decision.
***********************************************************************/

I transformed the view.  It was mostly because I didn't know how to manipulate my matrix
for the vertices due to not being fully consistant and the fact that the orientation of the Triangle's
directions change when you rotate it. So changing the view was much simpler.



/**********************************************************************
* Is there anything I need to know about building and running your app?
***********************************************************************/

This one was much easier than the last coding assignment.  Reused code a little bit, but 
most of that came from the google tutorial (where you told us to check how to do movement), and
code for buffers (which I used the code for vertices and applied to colors instead)



/**********************************************************************
* Did you add any extensions to the assignment that I should be aware 
* of?
***********************************************************************/

Nope



/**********************************************************************
* Approximately how many hours did it take you to complete assignment?
***********************************************************************/

<Replace this with your response!>


/**********************************************************************
 * Describe any serious problems you encountered in this assignment.
 * What was hard, or what should we warn students about in the future?
 * How can I make this assignment better?
 **********************************************************************/

7 hours of actual work  Was much easier when looking through the tutorial online
along with knowing that we can create a buffer for colors.


/**********************************************************************
 * List any other comments (about the assignment or your submission) 
 * here. Feel free to provide any feedback on what you learned from 
 * doing the assignment, whether you enjoyed doing it, etc.
 **********************************************************************/

I didn't hate this assignment, unlike the last one



