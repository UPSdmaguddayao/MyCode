//
//  FrameDataInstance.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/14/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FrameDataInstance : NSObject

@property NSString *notation;  //String representing the motion and button needed to perform the move
@property NSString *moveName; //Official move name.  Mostly used for specials
@property int startUp;  //Start up the move
@property NSArray *active; //Active Frames of the moves.  If it is multiple hitting, odd part of the array represent the start up of the next hits
@property NSArray *recovery; //recovery after the move finishes
@property int blockStun; //amount of blockstun this move create
@property int frameAdv; //frame advantage of the move if not cancelled into anything
@property NSArray *invuln; //if there is a value, lists it out. Otherwise, place a 0 here.
@property NSString *attribute; //Body, Head, Leg, or Projectile?
//@property NSArray *attribute; //if we need this for multiple hitting moves, then fine.  Use this instead.  Otherwise, don't use this
@property NSString *block; //how do you need to block this?  High?  Low?  Mid?  Barrier?  All?  Can't at all?  Different types:  H, L, HL (air barrier), B (barrier), All , UNBL (unblockable)


@end
