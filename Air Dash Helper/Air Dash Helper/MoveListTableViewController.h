//
//  MoveListTableViewController.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  A list of all moves and their frame data
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Character.h"
#import "Ragna.m"

@interface MoveListTableViewController : UITableViewController

//@property Character *cha;
@property NSArray* characterFrameData;
- (IBAction)unwindToMoveList:(UIStoryboardSegue *)segue;

@end
