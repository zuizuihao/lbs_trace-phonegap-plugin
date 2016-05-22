//
//  PushTalkPlugin.h
//  PushTalk
//
//  Created by zhangqinghe on 13-12-13.
//
//

#import <Cordova/CDV.h>

@interface LBSTracePlugin : CDVPlugin{
  
}

- (void) configure:(CDVInvokedUrlCommand*)command;
- (void) setInterval:(CDVInvokedUrlCommand*)command;
- (void) startTrace:(CDVInvokedUrlCommand*)command;
- (void) stopTrace:(CDVInvokedUrlCommand*)command;
- (void) queryRealtimeLoc:(CDVInvokedUrlCommand*)command;
- (void) queryEntityList:(CDVInvokedUrlCommand*)command;
- (void) queryHistoryTrack:(CDVInvokedUrlCommand*)command;

@end
