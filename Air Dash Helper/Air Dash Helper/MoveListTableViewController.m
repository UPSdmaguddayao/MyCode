//
//  MoveListTableViewController.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "MoveListTableViewController.h"
#import "CharacterTableViewController.h"
#import "MoveDataTableViewController.h"
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
    //NSLog(@"Setting the character as %@",character);
}

- (void)setMoveList:(NSDictionary *)moveList
{
    moves = moveList;
    //NSLog(@"Setting move list");
}
- (void)viewDidLoad {
    [super viewDidLoad];
    NSDictionary *ground =[moves objectForKey:@"Ground"];  //grabs the seperate dictionaries of one character's moves in the pList
    NSDictionary *air = [moves objectForKey:@"Air"];
    NSDictionary *spec = [moves objectForKey:@"Special"];
    NSDictionary *sup = [moves objectForKey:@"Super"];
    NSDictionary *ast = [moves objectForKey:@"Astral"];
    _moveNotation = [[NSMutableArray alloc] init];
    _moveData = [[NSMutableArray alloc] init];
    //run this in a loop to create two arrays.  First one is a list of moves.  Second one is move data
    NSArray *stand= [[[ground objectForKey:@"5"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    
    for (int i = 0; i< stand.count;) //grabs from standing neutral ground moves
    {
        NSString *notation =[stand objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"5"] objectForKey:notation]];
        i +=1;
    }
   // NSLog(@"%lu",(unsigned long)_moveNotation.count);
    NSArray *crouch = [[[ground objectForKey:@"2"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< crouch.count;) //crouching ground moves
    {
        NSString *notation =[crouch objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"2"] objectForKey:notation]];
        i +=1;
    }
   // NSLog(@"%lu",(unsigned long)_moveNotation.count);
    NSArray *forward = [[[ground objectForKey:@"6"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< forward.count;) //standing tilt moves
    {
        NSString *notation =[forward objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"6"] objectForKey:notation]];
        i +=1;
    }
    
    NSArray *drive = [[[ground objectForKey:@"Drive"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< drive.count;) //drive moves (normals using the D button except j.D)
    {
        NSString *notation =[drive objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"Drive"] objectForKey:notation]];
        i +=1;
    }
    NSArray *other = [[[ground objectForKey:@"Other"] allKeys]sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< other.count;) //special "command" normals such as 3C and jump cancels
    {
        NSString *notation =[other objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[[ground objectForKey:@"Other"] objectForKey:notation]];
        i +=1;
    }
    NSArray *aerial = [[air allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< aerial.count;) //aerial moves.  Certain characters have j.2C along with a j.C
    {
        NSString *notation =[aerial objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[air objectForKey:notation]];
        i +=1;
    }
    NSArray *specials =[[spec allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< specials.count;) //special moves
    {
        NSString *notation =[specials objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[spec objectForKey:notation]];
        i +=1;
    }
    NSArray *supers =[[sup allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< supers.count;) //super moves
    {
        NSString *notation =[supers objectAtIndex:i];
        [_moveNotation addObject:notation];
        [_moveData addObject:[sup objectForKey:notation]];
        i +=1;
    }
    NSArray *astral = [[ast allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    for (int i = 0; i< astral.count;) //instant kill moves
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
    [self viewDidLoad]; //reload the lists
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

-(NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    return [NSString stringWithFormat:@"Move Notation      Startup         Adv Block"];
}

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 45;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"MoveListCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    //  FrameDataInstance *fd = [_characterFrameData objectAtIndex:indexPath.row];
    NSMutableString *move =[NSMutableString stringWithFormat:@"%@",[_moveNotation objectAtIndex:indexPath.row]] ;
    NSString *fakemove = [_moveNotation objectAtIndex:indexPath.row];
    //[[fakemove replaceOccurrencesOfString:@"[" withString:@"" options:NSAnchoredSearch range:NSMakeRange(0, [fakemove length])];
    fakemove= [fakemove stringByReplacingOccurrencesOfString:@"[" withString:@""];
    //[fakemove replaceOccurrencesOfString:@"]" withString:@"" options:NSAnchoredSearch range:NSMakeRange(0, [fakemove length])];
    fakemove= [fakemove stringByReplacingOccurrencesOfString:@"]" withString:@""];
    //[fakemove replaceOccurrencesOfString:@"." withString:@"" options:NSAnchoredSearch range:NSMakeRange(0, [fakemove length])];
    fakemove= [fakemove stringByReplacingOccurrencesOfString:@"." withString:@""];
    fakemove= [fakemove stringByReplacingOccurrencesOfString:@"j" withString:@"J"];
    //NSString *f = [NSString stringWithFormat: @"Fake move length of %@ is %lu", move, (unsigned long)move.length];
    int removedChara = (int)move.length - (int)fakemove.length;
    int moveStringL = (14-(int)fakemove.length)*2+removedChara;
    for(int i = 0; i < moveStringL;)
    {
        [move appendString:@" "];
    //    [realmove appendString:@" "];
        i++;
    }
    /*while (move.length < 14)
     {
     [move appendString:@" "];
     }*/
    NSArray *moveD = [_moveData objectAtIndex:indexPath.row];
    
    //format the String Array from Startup in [moveD objectAtIndex:1]
    NSMutableString *blah;
    
    NSArray *startup = [moveD objectAtIndex:1];
    if (startup.count <2)
    {
        blah = [NSMutableString stringWithFormat:@"%@",[startup objectAtIndex:0]];
        while (blah.length < 10)
        {
            [blah appendString:@"  "];
        }
    }
    else
    {
        blah = [NSMutableString stringWithFormat:@"%@+%@",[startup objectAtIndex:0],[startup objectAtIndex:1]];
        while (blah.length < 10)
        {
            [blah appendString:@"  "];
        }
    }
    
    //format the String of Frame Advantage
    
    NSString *string = [NSString stringWithFormat:@"%@  %@          %@", move,blah,[moveD objectAtIndex:5]];
    cell.textLabel.text = string;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
    
}


#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
       UITableViewCell *send = (UITableViewCell*)sender; //need to set it like this in order to send the string correctly
    if([[segue identifier] isEqualToString:@"moveDataTable"])
    {
        NSLog(@"Time to go to move data");
        NSString *notation = [[NSString alloc] init];
        notation = send.textLabel.text;
        notation = [notation substringWithRange:NSMakeRange(0, 14)];
        notation= [notation stringByReplacingOccurrencesOfString:@" " withString:@""];
        
        NSLog(@"The move name is %@",notation);
        
        //NSInteger *temp= [_moveNotation indexOfObject:notation];
        NSArray *moveData = [_moveData objectAtIndex:[_moveNotation indexOfObject:notation]];
        // Get the new view controller using [segue destinationViewController]
        //MoveListTableViewController *mlt = [segue destinationViewController];
        UINavigationController *nc = [segue destinationViewController];
        MoveDataTableViewController *md = (MoveDataTableViewController*)([nc viewControllers][0]);
        [md setFrameData:moveData];
        // Pass the selected object to the new view controller.
    }
    else
    {
        NSLog(@"Going back to CharacterTable");
        _moveData = nil;
        _moveNotation = nil;
        return;
    }

    //RevolverAction
}


@end
