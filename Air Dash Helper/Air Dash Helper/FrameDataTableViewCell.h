//
//  FrameDataTableViewCell.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/19/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//  Helps display frame data shown on the MoveListTableViewController
//


#import <UIKit/UIKit.h>
#import "FrameDataInstance.h"

@interface FrameDataTableViewCell : UITableViewCell <UITableViewDelegate, UITableViewDataSource>
{
    UITableView *_horizontalTableView;
    //NSArray *_moveData;
    FrameDataInstance *_moveData;
}

@property (nonatomic, retain) UITableView *horizontalTableView;
//@property (nonatomic, retain) NSArray *moveData;
@property (nonatomic, retain) FrameDataInstance *moveData;

//-(void)setMove:(FrameDataInstance *)move;

@end
