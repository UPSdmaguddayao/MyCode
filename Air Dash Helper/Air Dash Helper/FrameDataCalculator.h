//
//  FrameDataCalculator.h
//  Air Dash Helper
//
//  Class that helps calculate the timing of frame data.  Everything will be stored in an array
//  Created by DJ Maguddayao on 2/19/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FrameDataCalculator : NSObject

@property NSMutableArray *attackFrameTiming; //two tables.  One for the attacker, and one for the defender
@property NSMutableArray *defendFrameTiming;

-(void)createDisplay: (NSMutableArray*)moveList characterName:(NSString*)characterName;
#warning Fix this second part before continuing
-(void)returnDisplay; //make this a return, but don't know how to create the display yet
@end
