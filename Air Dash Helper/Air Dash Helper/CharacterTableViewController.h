//
//  CharacterTableViewController.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 2/5/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CharacterTableViewController : UITableViewController

@property NSString *characterName;

- (IBAction)unwindToCharacters:(UIStoryboardSegue *)segue;

@end
