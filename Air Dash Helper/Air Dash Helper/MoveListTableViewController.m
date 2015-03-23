//
//  MoveListTableViewController.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "MoveListTableViewController.h"
#import "CharacterTableViewController.h"


@interface MoveListTableViewController ()
@end


@implementation MoveListTableViewController
@synthesize characterName;


- (void)setCharacter:(NSString *)character
{
    characterName = character;
    NSLog(@"Set character is @%",characterName);
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)unwindToMoveList:(UIStoryboardSegue *)segue
{
    /*CharacterTableViewController *source = [segue sourceViewController];
    character = source.characterName;
    
    NSLog(@"Was given the character %@",character);*/

}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
#warning Potentially incomplete method implementation.
    // Return the number of sections.
    return 1; //each of these will be a table, so only need one
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
#warning Incomplete method implementation.
    // Return the number of rows in the section.
    return 1; //prototype stuff for testing.  We'll make it from what the array is soon
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"MoveListCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    cell.textLabel.text = @"Vertical Table Rows on iPhone";
    
    return cell;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
