#import "EasyAmapLocationPlugin.h"
#if __has_include(<easy_amap_location/easy_amap_location-Swift.h>)
#import <easy_amap_location/easy_amap_location-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "easy_amap_location-Swift.h"
#endif

@implementation EasyAmapLocationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftEasyAmapLocationPlugin registerWithRegistrar:registrar];
}
@end
