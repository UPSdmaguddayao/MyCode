//
//  Ragna.m
//  Air Dash Helper
//
//  Stores all the data for Ragna the Bloodedge
//  Created by DJ Maguddayao on 2/14/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "Ragna.h"

@implementation Ragna

-(void)load{
    [self loadFrameData];
    [self loadRevolverAction];
}
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
    
    /*NSString *notation = @"5A";
    NSString *moveName = @"5A";
    NSInteger *startUp = 5;
    NSArray *activeFrames = [NSArray arrayWithObjects: @3,nil];
    NSArray *recoveryFrames = [NSArray arrayWithObjects: @9,nil];
    NSInteger *blockStun = 11;
    NSInteger *frameAdv = 0;
    NSArray *invuln = [NSArray arrayWithObjects: @"no", nil];
    NSString *attribute = @"B";
    NSString *block = @"All";
    NSArray *nA = [NSArray arrayWithObjects: @"5A",
                   @"5A",
                   @5,
                   [NSArray arrayWithObjects: @3,nil],
                   [NSArray arrayWithObjects: @9,nil],
                   @11,
                   @0,
                   "@no",
                   @"B",
                   @"All", nil];*/
    /*NSArray *nA = [NSArray arrayWithObjects: notation, moveName, startUp, activeFrames,recoveryFrames, blockStun, frameAdv, invuln, attribute, block, nil];*/
    
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
    nB.block = @"HL";/*
    
    NSArray *nB = [NSArray arrayWithObjects: @"5B",
                   @"5B",
                   8,
                   [NSArray arrayWithObjects: @8,nil],
                   [NSArray arrayWithObjects: @16,nil],
                   16,
                   -7,
                   "@no",
                   @"B",
                   @"HL", nil];
    */
    
    self.frameData = [NSArray arrayWithObjects:nA,nB, nil]; //work with this for now.  Figure out how to make it more efficient at loading later.
    
}

-(void)loadRevolverAction{
    self.revolverAction = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSArray arrayWithObjects:@"5A", @"2A", @"6A", @"5B", @"2B", @"6B", @"5C", @"2C", @"6C",@"3C",@"5D", @"2D",@"6D",@"Throw",@"Jump", @"Special", @"Overdrive", nil], @"5A",
                           nil];
    
    
    NSString * result = [[self.revolverAction valueForKey:@"description"] componentsJoinedByString:@", "];
    NSLog(@"The revolver action of the move %@",result); //use this for testing how it displays
    
    
}

-(NSArray*)returnFrameData{
    return self.frameData;
}

-(NSDictionary*)returnRevolverAction{
    return self.revolverAction;
}

@end