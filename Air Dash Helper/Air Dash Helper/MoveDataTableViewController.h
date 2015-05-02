//
//  MoveDataTableViewController.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 4/26/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MoveDataTableViewController : UITableViewController

@property NSMutableArray* displayData;
@property NSArray* fData;

-(void)setFrameData:(NSArray *)frameData;
@end
