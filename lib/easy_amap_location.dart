
import 'dart:async';

import 'package:flutter/services.dart';

class EasyAmapLocation {
  static const MethodChannel _channel =
      const MethodChannel('easy_amap_location');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
