//
//  FDCell.m
//  Air Dash Helper
//
//  Created by DJ Maguddayao on 3/31/15.
//  Copyright (c) 2015 DJ Maguddayao. All rights reserved.
//

#import "FDCell.h"
#import "ControlVariables.h"

@implementation FDCell

@synthesize itemLabel = _itemLabel;


- (NSString *)reuseIdentifier
{
    return @"FDCell";
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    //if ((self = [super initWithFrame:frame]))
    
    /*self.thumbnail = [[[UIImageView alloc] initWithFrame:CGRectMake(kArticleCellHorizontalInnerPadding, kArticleCellVerticalInnerPadding, kCellWidth - kArticleCellHorizontalInnerPadding * 2, kCellHeight - kArticleCellVerticalInnerPadding * 2)] autorelease];
    self.thumbnail.opaque = YES;
    
    [self.contentView addSubview:self.thumbnail];*/
    
   // self.itemLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, self.thumbnail.frame.size.height * 0.632, self.thumbnail.frame.size.width, self.thumbnail.frame.size.height * 0.37)];
    
    self.itemLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, kCellHeight * 0.632, kCellWidth, kCellHeight * 0.37)];
    self.itemLabel.opaque = YES;
    self.itemLabel.backgroundColor = [UIColor colorWithRed:0 green:0.4745098 blue:0.29019808 alpha:0.9];
    self.itemLabel.textColor = [UIColor whiteColor];
    self.itemLabel.font = [UIFont boldSystemFontOfSize:11];
    self.itemLabel.numberOfLines = 2;
    //[self.thumbnail addSubview:self.titleLabel];
    
    self.backgroundColor = [UIColor colorWithRed:0 green:0.40784314 blue:0.21568627 alpha:1.0];
//    self.selectedBackgroundView = [[[UIView alloc] initWithFrame:self.thumbnail.frame] autorelease];
    self.selectedBackgroundView.backgroundColor = kHorizontalTableSelectedBackgroundColor;
    
    self.transform = CGAffineTransformMakeRotation(M_PI * 0.5);
    
    return self;
}

@end
