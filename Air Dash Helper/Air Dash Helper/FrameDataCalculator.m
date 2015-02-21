//
//  FrameDataCalculator.h
//  Air Dash Helper
//
//  Class that helps calculate the timing of frame data.  Everything will be stored in an array
//  Created by DJ Maguddayao on 2/19/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "FrameDataCalculator.h"

@implementation FrameDataCalculator

-(void)createDisplay: (NSMutableArray*)moveList characterName:(NSString*)characterName
{
    //allocate new table for frame timings of both
    
    //allocate framedata movelist
    
    //go through the array one by one (loop) and calculate the frame data
    
    //3 extra loops: one for startup, one for active, one for recovery.  Grab all three values at once.  After one frame of recovery, start the blockstun loop while at it simulatenously.Something like
    
    //while moveList isn't empty, grab startup, active, recovery, and blockstun
        //while startup != 0, add S to attackFrameData if block = 0 add blank else block-- add block
    
        //check the cancels of each move.  If the move after this one is in it, make active frame 1 and recovery zero.  Else, put it at the end of recovery (loop)
    
        //add A to attackFrameData.  Active = active-- if block = 0 add blank else block--;  block = blockdata <--this ensures that the first active frame is read and calculates blockstun after it hits
        //while active !=0 add A to attackFrameData.  active--; if block = 0 add blank else block--;
        //while recovery !=0 add R to recovery.  r--; if block = 0 add blank else block--;
}

-(void)returnDisplay //make this a return, but don't know how to create the display yet
{
    
}
@end
