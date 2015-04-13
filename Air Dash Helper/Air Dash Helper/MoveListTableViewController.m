//
//  MoveListTableViewController.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "MoveListTableViewController.h"
#import "CharacterTableViewController.h"
#import "RevolverActionTableViewController.h"
#import "ControlVariables.h"
#import "FrameDataInstance.h"



@interface MoveListTableViewController ()
@end


@implementation MoveListTableViewController
@synthesize character;
@synthesize moves;


- (void)setCharacterName:(NSString *)characterName
{
    character = characterName;
    NSLog(@"Setting the character as %@",character);
}

- (void)setMoveList:(NSDictionary *)moveList
{
    moves = moveList;
    NSLog(@"Setting move list");
}
- (void)viewDidLoad {
    [super viewDidLoad];
    //NSLog(@"Test, using Ragna Data");
    //character name and movelist should be set.  Now try getting move data.
    NSDictionary *ground =[moves objectForKey:@"Ground"];
    NSDictionary *air = [moves objectForKey:@"Air"];
    NSDictionary *spec = [moves objectForKey:@"Special"];
    NSDictionary *sup = [moves objectForKey:@"Super"];
    NSDictionary *ast = [moves objectForKey:@"Astral"];
    _moveNotation = [[NSMutableArray alloc] init];
    _moveData = [[NSMutableArray alloc] init];
    //run this in a loop to create two arrays.  First one is a list of moves.  Second one is move data
    NSArray *stand= [[[ground objectForKey:@"5"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    
    for (int i = 0; i< stand.count;)
    {
        NSString *notation =[stand objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"5"] objectForKey:notation]];
        i +=1;
    }
    NSLog(@"%lu",(unsigned long)_moveNotation.count);
    NSArray *crouch = [[[ground objectForKey:@"2"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< crouch.count;)
    {
        NSString *notation =[crouch objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"2"] objectForKey:notation]];
        i +=1;
    }
    NSLog(@"%lu",(unsigned long)_moveNotation.count);
    NSArray *forward = [[[ground objectForKey:@"6"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< forward.count;)
    {
        NSString *notation =[forward objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"6"] objectForKey:notation]];
        i +=1;
    }
    NSArray *other = [[[ground objectForKey:@"Other"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< other.count;)
    {
        NSString *notation =[other objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"Other"] objectForKey:notation]];
        i +=1;
    }
    NSArray *aerial = [[air allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< aerial.count;)
    {
        NSString *notation =[aerial objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[air objectForKey:notation]];
        i +=1;
    }
    NSArray *specials =[[spec allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< specials.count;)
    {
        NSString *notation =[specials objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[spec objectForKey:notation]];
        i +=1;
    }
    NSArray *supers =[[sup allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< supers.count;)
    {
        NSString *notation =[supers objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[sup objectForKey:notation]];
        i +=1;
    }
    NSArray *astral = [[ast allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< astral.count;)
    {
        NSString *notation =[astral objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[ast objectForKey:notation]];
        i +=1;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)unwindToMoveList:(UIStoryboardSegue *)segue
{
    [self viewDidLoad];

}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1; //each of these will be a table, so only need one
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return _moveNotation.count; //prototype stuff for testing.  We'll make it from what the array soon
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"MoveListCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
  //  FrameDataInstance *fd = [_characterFrameData objectAtIndex:indexPath.row];
    NSString *move =[_moveNotation objectAtIndex:indexPath.row];
    NSArray *moveD = [_moveData objectAtIndex:indexPath.row];

    NSString *string = [NSString stringWithFormat:@"%@ \t\t\t %@ \t\t\t %@", move,[moveD objectAtIndex:1],[moveD objectAtIndex:5]];
    cell.textLabel.text = string;
    
    return cell;

}


#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
    NSLog(@"Time to go to Revolver Action");
    //NSString *character = send.textLabel.text;
    if([[segue identifier] isEqualToString:@"RevolverAction"])
    {
        NSLog(@"The sender is %@",self.character);
        // Get the new view controller using [segue destinationViewController]
        
        //MoveListTableViewController *mlt = [segue destinationViewController];
        UINavigationController *nc = [segue destinationViewController];
        RevolverActionTableViewController *ra = (RevolverActionTableViewController*)([nc viewControllers][0]);
        [ra setCharacterName:self.character];
        // Pass the selected object to the new view controller.
    }
    else
    {
        NSLog(@"Going back to CharacterTable");
        return;
    }

    //RevolverAction
}


@end
