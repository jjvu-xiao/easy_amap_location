import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

import 'option.dart';

class EasyAmapLocationImpl {
  static const String _CHANNEL_METHOD_LOCATION = "easy_amap_location";
  static const String _CHANNEL_STREAM_LOCATION = "easy_amap_location/event";

  static const MethodChannel _methodChannel = const MethodChannel(_CHANNEL_METHOD_LOCATION);

  static const EventChannel _eventChannel = const EventChannel(_CHANNEL_STREAM_LOCATION);

  static Stream<Map<String, Object>?> _onLocationChanged = _eventChannel
      .receiveBroadcastStream()
      .asBroadcastStream()
      .map<Map<String, Object>?>((element) => element.cast<String, Object>());

  StreamController<Map<String, Object>>? _receiveStream;
  StreamSubscription<Map<String, Object>?>? _subscription;
  String? _pluginKey;

  /// 适配iOS 14定位新特性，只在iOS平台有效
  Future<AMapAccuracyAuthorization> getSystemAccuracyAuthorization() async {
    int? result = -1;
    if (Platform.isIOS) {
      result = await (_methodChannel.invokeMethod(
          "getSystemAccuracyAuthorization", {'pluginKey': _pluginKey}) as FutureOr<int>);
    }
    if (result == 0) {
      return AMapAccuracyAuthorization.AMapAccuracyAuthorizationFullAccuracy;
    } else if (result == 1) {
      return AMapAccuracyAuthorization.AMapAccuracyAuthorizationReducedAccuracy;
    }
    return AMapAccuracyAuthorization.AMapAccuracyAuthorizationInvalid;
  }

  ///初始化
  AmapLocationFlutterPlugin() {
    _pluginKey = DateTime.now().millisecondsSinceEpoch.toString();
  }

  ///开始定位
  void startLocation() {
    _methodChannel.invokeMethod('startLocation', {'pluginKey': _pluginKey});
    return;
  }

  ///停止定位
  void stopLocation() {
    _methodChannel.invokeMethod('stopLocation', {'pluginKey': _pluginKey});
    return;
  }

  ///设置Android和iOS的apikey，建议在weigdet初始化时设置<br>
  ///apiKey的申请请参考高德开放平台官网<br>
  ///Android端: https://lbs.amap.com/api/android-location-sdk/guide/create-project/get-key<br>
  ///iOS端: https://lbs.amap.com/api/ios-location-sdk/guide/create-project/get-key<br>
  ///[androidKey] Android平台的key<br>
  ///[iosKey] ios平台的key<br>
  static void setApiKey(String androidKey, String iosKey) {
    _methodChannel
        .invokeMethod('setApiKey', {'android': androidKey, 'ios': iosKey});
  }

  /// 设置定位参数
  void setLocationOption(AMapLocationOption locationOption) {
    Map option = locationOption.getOptionsMap();
    option['pluginKey'] = _pluginKey;
    _methodChannel.invokeMethod('setLocationOption', option);
  }

  ///销毁定位
  void destroy() {
    _methodChannel.invokeListMethod('destroy', {'pluginKey': _pluginKey});
    if (_subscription != null) {
      _receiveStream!.close();
      _subscription!.cancel();
      _receiveStream = null;
      _subscription = null;
    }
  }

  ///定位结果回调
  Stream<Map<String, Object>> onLocationChanged() {
    if (_receiveStream == null) {
      _receiveStream = StreamController();
      _subscription = _onLocationChanged.listen((Map<String, Object>? event) {
        if (event != null && event['pluginKey'] == _pluginKey) {
          Map<String, Object> newEvent = Map<String, Object>.of(event);
          newEvent.remove('pluginKey');
          _receiveStream!.add(newEvent);
        }
      });
    }
    return _receiveStream!.stream;
  }
}