//
//  MoveDataTableViewController.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 4/26/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "MoveDataTableViewController.h"

@interface MoveDataTableViewController ()

@end

@implementation MoveDataTableViewController

- (void)setFrameData:(NSArray *)frameData
{
    _fData = frameData;
    //Adjust the data to be displayed
    //go through the frameData loop
    _displayData = [[NSMutableArray alloc] init];
    NSArray *revo = [_fData objectAtIndex:10];
    NSString *sValue = [[NSString alloc] init];
    for (int i = 0; i< _fData .count - 1 + revo.count;)
    {
        NSLog(@"Did this: %d",i);
        if (i == 0)
        {
            //NSString *notation =[frameData objectAtIndex:i];
            sValue = [NSString stringWithFormat:@"Move Name: %@",[_fData  objectAtIndex:i]] ;
        }
        else if (i == 1)
        {
            NSArray *startup = [[NSArray alloc] init];
            startup = [_fData  objectAtIndex:i];
            if (startup.count < 2)
            {
                sValue = [NSString stringWithFormat:@"Startup: %@",[startup objectAtIndex:0]] ;
            }
            else
            {
                sValue = [NSString stringWithFormat:@"Startup: %@+%@",[startup objectAtIndex:0],[startup objectAtIndex:1]] ;
            }
            startup = nil;
        }
        else if (i ==2)
        {
            NSArray *active = [[NSArray alloc] init];
            active = [_fData  objectAtIndex:i];
            NSMutableString *temp = [NSMutableString stringWithFormat:@""];
            for (int x = 0; x < active.count;)
            {
                if(x%2 == 0)
                {
                    [temp appendFormat:@"%@ ",[active objectAtIndex:x]];
                }
                else
                {
                    [temp appendFormat:@"(%@) ",[active objectAtIndex:x]];
                }
                x += 1;
            }
            sValue = [NSString stringWithFormat:@"Active: %@",temp] ;
            active = nil;
            temp = nil;
        }
        else if (i == 3)
        {
            NSArray *recovery = [[NSArray alloc] init];
            recovery = [_fData  objectAtIndex:i];
            BOOL *check = [[recovery objectAtIndex:0] boolValue];
            if (check) //if check is true, then we add a T.  Else false
            {
                
                sValue = [NSString stringWithFormat:@"Recovery: %@ T",[recovery objectAtIndex:1]] ;
            }
            else
            {
                if (recovery.count <3)
                {
                   sValue = [NSString stringWithFormat:@"Recovery: %@",[recovery objectAtIndex:1]] ;
                }
                else
                {
                    sValue = [NSString stringWithFormat:@"Recovery: %@ + %@ L",[recovery objectAtIndex:1],[recovery objectAtIndex:2]] ;
                }
            }
            recovery = nil;
        }
        else if (i == 4)
        {
            sValue = [NSString stringWithFormat:@"Blockstun: %@",[_fData  objectAtIndex:i]] ;
        }
        else if (i ==5)
        {
            sValue = [NSString stringWithFormat:@"Frame Advantage: %@",[_fData  objectAtIndex:i]] ;
        }
        else if (i == 6)
        {
            NSArray *invuln = [[NSArray alloc] init];
            invuln = [_fData  objectAtIndex:i];
            if(invuln.count < 2)
            {
                sValue = [NSString stringWithFormat:@"Invuln: None"] ;
            }
            else
            {
                sValue = [NSString stringWithFormat:@"Invuln: %@ - %@ %@",[invuln objectAtIndex:0],[invuln objectAtIndex:1],[invuln objectAtIndex:2]] ;
            }
            invuln = nil;
        }
        else if (i == 7)
        {
            sValue = [NSString stringWithFormat:@"Attribute: %@",[_fData  objectAtIndex:i]] ;
        }
        else if (i ==8)
        {
            sValue = [NSString stringWithFormat:@"Block: %@",[_fData  objectAtIndex:i]] ;
        }
        else if (i == 9)
        {
            sValue = [NSString stringWithFormat:@"Note: %@",[_fData  objectAtIndex:i]] ;
        }
        else if (i == 10)
        {
            sValue = [NSString stringWithFormat:@"Revolver Action: %@",[revo objectAtIndex:(i-10)]] ;
        }
        else
        {
            sValue = [NSString stringWithFormat:@"%@",[revo objectAtIndex:(i-10)]] ;
        }
        [_displayData addObject:sValue];
        i +=1;
    }

}

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return _displayData.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"mDataCell" forIndexPath:indexPath];
    
    // Configure the cell...
    NSString *txt =[_displayData objectAtIndex:indexPath.row];
    //NSLog(txt);
    cell.textLabel.text = txt;
    if (indexPath.row == 9) //notes section is usually the longest
    {
        cell.textLabel.font = [UIFont systemFontOfSize:12.0];
    }
    else
    {
    cell.textLabel.font = [UIFont systemFontOfSize:16.0];
    }
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
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
