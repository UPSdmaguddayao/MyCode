//
//  FrameDataTableViewCell.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/19/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//  Helps display frame data shown on the MoveListTableViewController
//

#import "FrameDataTableViewCell.h"
#import "ControlVariables.h"
#import "FDCell.h"
#import "FrameDataInstance.h"

@implementation FrameDataTableViewCell

@synthesize horizontalTableView = _horizontalTableView;
@synthesize moveData = _moveData;

//Ignore this for now
/*- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}*/
- (void)setMove:(FrameDataInstance *)move
{
    _moveData = move;
}

#pragma mark - Table View Data Source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    //return [self.moveData count];
    return 10; //each frame data instance should be 10
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"Setting up horizontal table");
    static NSString *cellIdentifier = @"FDCell";
    
    //UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    FDCell *cell = (FDCell *)[tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    /* if (cell == nil)
     {
     cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier] autorelease];
     }*/
    
    //NSArray *currentMove = [self.moveData objectAtIndex:indexPath.row];
    //FrameDataInstance *currentMove = [self.moveData objectAtIndex:indexPath.row];
    if (indexPath.row == 0)
    {
        cell.itemLabel.text = @"name filler";
       // cell.itemLabel.text = _moveData.moveName;
    }
    else{
        cell.itemLabel.text = @"filler";
    }
    
    //cell.itemLabel.text = currentMove.moveName;
    //cell.textLabel.text = @"The title of the cell in the table within the table :O";
    
    return cell;
}

#warning Finish this part of the tutorial
- (NSString *) reuseIdentifier
{
    return @"FDCell";
}

- (id)initWithFrame:(CGRect)frame
{
    NSLog(@"Initializing");
    if ((self = [super initWithFrame:frame])) //reuse identifier for our cell, then we move on to initWithFrame starting with the usual call to our super class’ initWithFrame and assign it to self
    {
        self.horizontalTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, kCellHeight, kTableLength)]; //When creating the table view you will see we pass a new CGRect with four values. The first two (0, 0) mean that we want to position our table at the top left corner of the containing view, the second values are for the table’s width and height respectively.
        
        //[[[UITableView alloc] initWithFrame:CGRectMake(0, 0, kCellHeight, kTableLength)] autorelease];
        self.horizontalTableView.showsVerticalScrollIndicator = NO;
        self.horizontalTableView.showsHorizontalScrollIndicator = NO;
        //After creating the table we turn off the vertical and horizontal scroll indicators, we don’t want those to appear as they will be on top of our beautiful custom cells.
        
        
        self.horizontalTableView.transform = CGAffineTransformMakeRotation(-M_PI * 0.5); //turns view counter clockwise
        [self.horizontalTableView setFrame:CGRectMake(kRowHorizontalPadding * 0.5, kRowVerticalPadding * 0.5, kTableLength - kRowHorizontalPadding, kCellHeight)]; //After rotating our table we set the table view’s frame again.
        
        self.horizontalTableView.rowHeight = kCellWidth;
        self.horizontalTableView.backgroundColor = kHorizontalTableBackgroundColor; //Next up we set the height of our table view’s rows, but since our table is now rotated and appears horizontally, the row height is actually the width of our cell now, which is why we use the kCellWidth constant.
        
        self.horizontalTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        self.horizontalTableView.separatorColor = [UIColor clearColor];
        
        self.horizontalTableView.dataSource = self;
        self.horizontalTableView.delegate = self;
        [self addSubview:self.horizontalTableView];
        //Finally, we set ourselves as the table’s data source and delegate, and add the horizontalTableView we just created and customized as a subview of the cell.
    }
    NSLog (@"Making a view");
    return self;
}

#pragma mark - Memory Management

/* - (void)dealloc
 {
 self.horizontalTableView = nil;
 self.moveData = nil;
 
   [super dealloc];
 }*/
@end
