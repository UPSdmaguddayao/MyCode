//
//  Ragna.h
//  Air Dash Helper.  Helped with testing what classes are allowed or not.
//
//  Created by DJ Maguddayao on 2/14/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Character.h"

@interface Ragna:NSObject

@property NSArray* frameData;
@property NSDictionary* revolverAction;

-(void)load;
-(void)loadFrameData;
-(void)loadRevolverAction;
-(NSDictionary*)returnRevolverAction;
-(NSArray*)returnFrameData;
@end
