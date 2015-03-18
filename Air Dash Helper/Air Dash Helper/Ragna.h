//
//  Ragna.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/14/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FrameDataInstance.h"
#import "Character.h"

@interface Ragna : NSObject <Character>

#warning Make sure that these can be passed generically to another method
@property NSArray* frameData; //apparently, the protocol also counts for holding these properties.
//@property NSArray* ragnaRevolverAction;
@property NSDictionary* revolverAction; //still need to declare this though.

//-(void)loadFrameData;
//-(void)loadRevolverAction;
//every method is held by by the character class

@end
