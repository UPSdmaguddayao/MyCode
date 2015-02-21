//
//  RevolverActionTableViewController.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Character.h"
#import "Ragna.m"

@interface RevolverActionTableViewController : UITableViewController

#warning Find out which of these works better
@property NSArray *listOfMoves;
@property NSDictionary *moveList;
@property Character *cha;

@end
