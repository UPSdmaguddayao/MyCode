//
//  characterController.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 3/22/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MoveListTableViewController.h"

@interface characterController : UINavigationController
@property (strong, nonatomic) NSString *character;
@property MoveListTableViewController* mlt;
@end
