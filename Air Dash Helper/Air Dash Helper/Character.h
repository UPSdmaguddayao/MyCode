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

@interface Character : NSObject

@property NSArray* frameData;
@property NSDictionary* revolverAction;

-(void)load;
-(void)loadFrameData;
-(void)loadRevolverAction;
-(NSDictionary*)returnRevolverAction;
-(NSArray*)returnFrameData;
@end
