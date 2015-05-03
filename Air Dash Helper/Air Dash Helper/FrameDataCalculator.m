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
static NSString *START = @"S";
static NSString *BLOCK = @"B";
static NSString *ACT = @"A";
static NSString *RECOVER = @"R";
static NSString *SPACE = @"Sp";
NSDictionary *characterFrameData;

-(void)createDisplay: (NSMutableArray*)moveList characterName:(NSString*)characterName
{
    //allocate new table for frame timings of both
    _attackFrameTiming = [[NSMutableArray alloc] init];
    _defendFrameTiming = [[NSMutableArray alloc] init];
    //allocate framedata movelist
    
    NSArray *su = [[NSArray alloc] init]; //startup array
    NSArray *act = [[NSArray alloc] init]; //active frame array
    NSArray *rec = [[NSArray alloc] init]; //recovery frame
    NSArray *move = [[NSArray alloc] init]; //move array that is pointed to
    NSArray *revolver =[[NSArray alloc] init]; //revolver action of the move
    int *blockstun = 0;
    int *startup = 0;
    int *active = 0;
    int *recovery = 0; //initialize the three variables to be used.  The arrays will be translated into ints
    int *actCheck = 0;
    NSMutableArray *holder = [[NSMutableArray alloc] initWithCapacity:2];  //holds two strings at any time.
    
    //go through the array one by one (loop) and calculate the frame data
    NSString *move1;
    NSString *move2;
    for(int i = 0; i < moveList.count;)
    {
        move1 = [moveList objectAtIndex:i];
        if(i < moveList.count -1) //avoid array out of bounds error
        {
            move2 = [moveList objectAtIndex:i+1];
        }
        
        [holder insertObject:move1 atIndex:0];
        
        if(i < moveList.count-1)
        {
            [holder insertObject:move2 atIndex:1]; //most moves are self cancellable on block other than 5A and 2A, so this should be safe
        }
    
    //3 extra loops: one for startup, one for active, one for recovery.  Grab all three values at once.  After one frame of recovery, start the blockstun loop while at it simulatenously.
    //while moveList isn't empty, grab startup, active, recovery, and blockstun
       
        
#warning Make a first check:  if recovery is a T.  Then you figure out the remaining recovery from that if the next move is part of revolver action, that's fine.  It will just turn into zero
        
#warning Figure out how to deal with T (which is projectiles) later.  It actually makes no sense when you do the math
        move = [characterFrameData objectForKey:move1];
        su = [move objectAtIndex:1];
        for(int x = 0; x< su.count;)
        {
            startup = startup + (int)[su objectAtIndex:i]; //grabs the object at i to add
        }
        
        act = [move objectAtIndex:2]; //find the active frames.  Active frames alternates odds
        rec = [move objectAtIndex:3]; //calculate the recovery first just in case.
        
        //if rec[0] is true, then calculate the recovery frames
        
        revolver = [move objectAtIndex:10]; //for checking if the next move is in the list or not
        while(su > 0)
        {
            [_attackFrameTiming addObject:START]; //adds startup timing
            if(blockstun == 0)
            {
                [_defendFrameTiming addObject:SPACE];
            }
            else
            {
                blockstun -=1; //reduce blockstun time
                [_defendFrameTiming addObject:BLOCK];
            }
            startup -=1;//reduce startup timer
            
            if(su == 0) //this is so we don't escape the loop early without checking active frames
            {
                if(actCheck == (act.count -1)) //check if at end of active array
                {
                    //if next move in revolver action
                        //active = 1;
                        //recovery = 0;
                }
                else //not at the end of active array.
                {
                    active = (int)[act objectAtIndex:actCheck];
                    actCheck +=1;//go one farther in the actCheck

                }
                [_attackFrameTiming addObject:ACT]; //remove the first active frame
                active -=1;
                if(blockstun == 0) //iterates the first frame of active frame as if it didn't hit yet
                {
                    [_defendFrameTiming addObject:SPACE];
                }
                else
                {
                    blockstun -=1;
                    [_defendFrameTiming addObject:BLOCK]; //remove the first active frame timing
                }
                blockstun = (int)[move objectAtIndex:4]; //set the blockstun.  Acts as a reset if it has been set already.
                //now iterate through active frames
                while(active > 0)
                {
#warning WORK ON THIS PLEASE.
                }
            }
            
        }
  /*      for(int x = 0; x < act.count;)
        {
            if(x == (act.count-1))//end of the active frame array
            {
                //check if move 2 is in revolver action
                if([revolver containsObject:move2])
                {
                    addToAct = 1;
                    [active addObject:addToAct];
                    recovery = 0;
                }
            }
        }*/
        
        
        
    //while startup != 0, add S to attackFrameData if block = 0 add blank else block-- add block
    
    //check the cancels of each move.  If the move after this one is in it, make active frame 1 and recovery zero.  This will be utilizing grabbing from a . dictionaryElse, put it at the end of recovery (loop)
    //example: if character_name.revolveraction[@"5A"].contains(nextMove)
    
    //add A to attackFrameData.  Active = active-- if block = 0 add blank else block--;  block = blockdata <--this ensures that the first active frame is read and calculates blockstun after it hits
    //while active !=0 add A to attackFrameData.  active--; if block = 0 add blank else block--;
    //while recovery !=0 add R to recovery.  r--; if block = 0 add blank else block--;
        i += 1; //go to next move
    }
}

#warning Make this return something
-(void)returnDisplay //make this a return, but don't know how to create the display yet
{
    
}
@end
