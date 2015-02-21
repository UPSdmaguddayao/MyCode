//
//  MoveListTableViewController.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "MoveListTableViewController.h"


@interface MoveListTableViewController ()

@end

@implementation MoveListTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
#warning Figure out how to keep it that a character instance is passed to both this and MoveListTableViewController.  Figure it out with how segues to the CharacterFrameDataTabController works
#warning "Not using other characters yet"
   /* self.cha = (Character*)[ [Ragna alloc] init]; //test with Ragna first.  Then finetune this for other characterss
    [self.cha loadFrameData];
    self.characterFrameData = self.cha.frameData;*/
    
    // create a background image for the cell:
    /*
    UIImageView *hdr = [[[UIImageView alloc] initWithFrame:CGRectZero] autorelease];
    [hdr setBackgroundColor:[UIColor clearColor]];
    [hdr setImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"hdr" ofType:@"png"]]];
    [self setHeaderView:hdr];
    [hdr release];
    
    UIImageView *footer = [[[UIImageView alloc] initWithFrame:CGRectZero] autorelease];
    [footer setBackgroundColor:[UIColor clearColor]];
    [footer setImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"footer" ofType:@"png"]]];
    [self setFooterView:footer];
    [footer release]; */
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)unwindToMoveList:(UIStoryboardSegue *)segue
{
    
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 10;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.characterFrameData.count;
}

#warning Make this have columns and stuff
/*
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"FrameDataCell" forIndexPath:indexPath];
    
    if( cell == nil ){
        UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"FrameDataCell"];
    }
    
    // Configure the cell...
    //grabbed item from the array
    FrameDataInstance* fdi = [self.characterFrameData objectAtIndex:indexPath];
        cell.textLabel.text = (NSString*)[players valueForKey:@"PlayerFullName"]
    
    return cell;
}*/


/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
