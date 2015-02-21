//
//  Ragna.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/14/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FrameDataInstance.h"

@interface Ragna : NSObject

@property NSArray* ragnaFrameData;
//@property NSArray* ragnaRevolverAction;
@property NSDictionary* ragnaDRevolverAction;

-(void)loadFrameData;
-(void)loadRevolverAction;


@end
