//
//  CharacterList.h
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 3/17/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Character.h"

@interface CharacterList : NSObject

//@property NSMutableArray* listOfCharacters; //holds a list of characters
//@property NSDictionary* characterData; //holds a list of character instances that relate to a character

-(NSMutableArray*) returnCharaList;
-(id<Character>) returnCharacter;

@end
