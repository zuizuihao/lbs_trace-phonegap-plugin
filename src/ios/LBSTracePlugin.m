#import "LBSTracePlugin.h"
#import <BaiduTraceSDK/BaiduTraceSDK-Swift.h>
#import <UIKit/UIKit.h>

@implementation LBSTracePlugin {
    BTRACE * traceInstance;
}

/**
 * configure plugin
 * @param {String} serviceId
 */
- (void) configure:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSString * serviceId = [command.arguments objectAtIndex: 0];
        NSLog(@"- LBSTrace configure %@", serviceId);
    }];
}

/**
 * set Interval
 * @param {Number} gatherInterval
 * @param {Number} packInterval
 */
- (void) setInterval:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSString * gatherInterval = [command.arguments objectAtIndex: 0];
        NSString * packInterval = [command.arguments objectAtIndex: 1];
        NSLog(@"- LBSTrace set Interval %@ %@", gatherInterval, packInterval);
    }];
}

/**
 * start Trace
 */
- (void) startTrace:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^ {
        NSLog(@"- LBSTrace start");
    }];
}

/**
 * stop Trace
 */
- (void) stopTrace:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^ {
        NSLog(@"- LBSTrace stop");
    }];
}

/**
 * Query RealtimeLoc
 */
- (void) queryRealtimeLoc:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^ {
        NSLog(@"- LBSTrace queryRealtimeLoc");
    }];
}

/**
 * Query EntityList
 */
- (void) queryEntityList:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^ {
        NSLog(@"- LBSTrace Query EntityList");
    }];
}

/**
 * Query HistoryTrack
 */
- (void) queryHistoryTrack:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^ {
        NSLog(@"- LBSTrace Query HistoryTrack");
    }];
}


@end