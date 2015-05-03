//
//  MoveListTableViewController.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Character.h"

@interface MoveListTableViewController : UITableViewController

@property (strong, nonatomic) NSString *character; //gets this string passed to it so it can get a character
@property (strong, nonatomic) NSDictionary* moves;
@property NSMutableArray* moveNotation;
@property NSMutableArray* moveData;
@property NSArray* sendMove;


- (IBAction)unwindToMoveList:(UIStoryboardSegue *)segue;
-(void)setCharacterName:(NSString *)characterName;
-(void)setMoveList:(NSDictionary *)moveList;


@end
