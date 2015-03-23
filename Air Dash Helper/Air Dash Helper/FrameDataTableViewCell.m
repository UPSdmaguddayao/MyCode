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

#pragma mark - Table View Data Source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.moveData count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"fdCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    /* if (cell == nil)
     {
     cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier] autorelease];
     }*/
    
    cell.textLabel.text = @"The title of the cell in the table within the table :O";
    
    return cell;
}

/*#pragma mark - Memory Management
 
 - (void)dealloc
 {
 self.horizontalTableView = nil;
 self.articles = nil;
 
 //  [super dealloc];
 }*/
@end
