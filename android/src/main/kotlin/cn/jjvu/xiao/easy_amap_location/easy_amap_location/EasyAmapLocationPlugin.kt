package cn.jjvu.xiao.easy_amap_location.easy_amap_location

import android.os.Handler
import android.util.Log
import androidx.annotation.NonNull
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** EasyAmapLocationPlugin */
class EasyAmapLocationPlugin: FlutterPlugin, MethodCallHandler, AMapLocationListener, EventChannel.StreamHandler {

  private lateinit var channel : MethodChannel

  private lateinit var option: AMapLocationClientOption

  private lateinit var locationClient: AMapLocationClient

  private lateinit var activity: FlutterActivity

  private var isLocation: Boolean = false

  private lateinit var eventChannel: EventChannel

  var eventSink: EventChannel.EventSink? = null

  private val TAG = "easy_amap_location"

  //备份至
  private var onceLocation = false

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "easy_amap_location")
    channel.setMethodCallHandler(this)
    locationClient = AMapLocationClient(flutterPluginBinding.applicationContext)
    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "easy_amap_location/event")
    eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        Log.d("${TAG}A", "EventChannel onListen called")
        Handler().postDelayed({
          eventSink?.success("Android")
        }, 500)
      }

      override fun onCancel(arguments: Any?) {
        Log.w("${TAG}A" , "EventChannel onCancel called")
      }
    })
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method) {
      "getLocation" -> {
        result.success(getLocation(result))
      }
      "setApi" -> {

      }
      "startLocation" -> {

      }
      "stopLocation" -> {

      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private fun getLocation(result: Result): Boolean {
    option.isOnceLocation = true
    val listener = AMapLocationListener { aMapLocation -> //恢复原来的值
      result.success(aMapLocation)
      stopLocation()
    }
    startLocation(listener)
    return true
  }

  private fun startLocation(listener: AMapLocationListener): Boolean {
    synchronized(this) {
      locationClient.setLocationListener(listener)
      locationClient.startLocation()
      isLocation = true
      return true
    }
  }

  private fun stopLocation(): Boolean {
    synchronized(this) {
      locationClient.stopLocation()
      isLocation = false
      return true
    }
  }

  override fun onLocationChanged(location: AMapLocation?) {
    eventSink?.success(location)
  }

  fun initOption(params: Map<String, Any?>) {
    option = AMapLocationClientOption()
    option.locationMode = AmapOptionUtil.mode(params["mode"] as String)
    locationClient.setLocationOption(option)
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    TODO("Not yet implemented")
  }

  override fun onCancel(arguments: Any?) {
    TODO("Not yet implemented")
  }
}
