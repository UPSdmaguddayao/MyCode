//
//  Character.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/20/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//  All characters should have the implementation
//

#import <Foundation/Foundation.h>
#import "FrameDataInstance.h"

@protocol Character <NSObject>

@required
@property NSArray* frameData; //holds the frame data instance of any character to be used by another class
@property NSDictionary* revolverAction; //hold the revolver action of the character

-(void)load;
-(void)loadFrameData;
-(void)loadRevolverAction;
-(NSDictionary*)returnRevolverAction;
-(NSArray*)returnFrameData;

@end
