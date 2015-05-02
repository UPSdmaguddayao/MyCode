//
//  MoveListTableViewController.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Character.h"
#import "Ragna.h"

@interface MoveListTableViewController : UITableViewController

//@property Character *cha; //sadly this doesn't work well :<
@property Ragna* rag;
@property (strong, nonatomic) NSString *character; //gets this string passed to it so it can get a character
@property (strong, nonatomic) NSDictionary* moves;
@property NSMutableArray* moveNotation;
@property NSMutableArray* moveData;
@property NSArray* sendMove;

#warning For now, just use a bunch of if statements and use every character list there is.  Work on optimizing this option later

- (IBAction)unwindToMoveList:(UIStoryboardSegue *)segue;
-(void)setCharacterName:(NSString *)characterName;
-(void)setMoveList:(NSDictionary *)moveList;


@end
