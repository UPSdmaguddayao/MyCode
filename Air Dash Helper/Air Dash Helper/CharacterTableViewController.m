//
//  CharacterTableViewController.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "CharacterTableViewController.h"
#import "Character.h"

@interface CharacterTableViewController ()

@property NSMutableArray *listOfCharacters;

@end

@implementation CharacterTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];

 self.listOfCharacters= [ [NSMutableArray alloc] init]; //initializes the array on load
    [self loadInitialData]; //initializes the data
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)loadInitialData{
    self.listOfCharacters = [NSMutableArray arrayWithObjects:
     @"Amane Nishiki",
     @"Arakune",
     @"Azrael",
     @"Bang Shishigami",
     @"Bullet",
     @"Carl Clover",
     @"Hakumen",
     @"Hazama",
     @"Iron Tager",
     @"Izayoi",
     @"Jin Kisaragi",
     @"Kagura Mutsuki",
     @"Kokonoe",
     @"Litchi Faye Ling",
     @"Makoto Nanaya",
     @"Noel Vermillion",
     @"Platinum the Trinity",
     @"Rachel Alucard",
     @"Ragna the Bloodedge",
     @"Relius Clover",
     @"Taokaka",
     @"Tsubaki Yayoi",
     @"Valkenhayn R. Hellsing",
     @"Yuuki Terumi",
     @"μ-12",
                             @"ν-13",nil ];
    
    
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)unwindToCharacters:(UIStoryboardSegue *)segue
{
    
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return [self.listOfCharacters count];
}

//Creates the display of the table
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CharacterListCell" forIndexPath:indexPath];
    
    // Configure the cell...
    NSString *characterName = [self.listOfCharacters objectAtIndex:indexPath.row];
    cell.textLabel.text = characterName; //places it in the cell
    
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
/*
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath: (NSIndexPath *)indexPath{
    //Figure out how to make this move in a segue with passing the string of the character selected
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
    ToDoItem *tappedItem = [self.toDoItems objectAtIndex:indexPath.row];
    tappedItem.completed = !tappedItem.completed;
    [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
}*/

@end
