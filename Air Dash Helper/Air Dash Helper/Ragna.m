//
//  Ragna.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/14/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "Ragna.h"

@implementation Ragna

-(void)loadFrameData{
    
    FrameDataInstance *nA = [[FrameDataInstance alloc] init];
    nA.notation = @"5A";
    nA.moveName = @"5A";
    nA.startUp = 5;
    NSArray *activeFrames = [NSArray arrayWithObjects: @3,nil];
    nA.active = activeFrames;
    NSArray *recoveryFrames = [NSArray arrayWithObjects: @9,nil];
    nA.recovery = recoveryFrames;
    nA.blockStun = 11;
    nA.frameAdv = 0;
    nA.invuln = nil;
    nA.attribute = @"B";
    nA.block = @"All";
    
    FrameDataInstance *nB = [[FrameDataInstance alloc] init];
    nB.notation = @"5B";
    nB.moveName = @"5B";
    nB.startUp = 8;
    NSArray *nBactiveFrames = [NSArray arrayWithObjects: @8,nil];
    nB.active = nBactiveFrames;
    NSArray *nBrecoveryFrames = [NSArray arrayWithObjects: @16,nil];
    nB.recovery = nBrecoveryFrames;
    nB.blockStun = 16;
    nB.frameAdv = -7;
    nB.invuln = nil;
    nB.attribute = @"B";
    nB.block = @"HL";
    
    self.ragnaFrameData = [NSArray arrayWithObjects:nA,nB, nil]; //work with this for now.  Figure out how to make it more efficient at loading later.
    
}

-(void)loadRevolverAction{

}

@end

