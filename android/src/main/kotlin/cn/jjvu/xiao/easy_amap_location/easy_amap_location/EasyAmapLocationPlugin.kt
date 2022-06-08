package cn.jjvu.xiao.easy_amap_location.easy_amap_location

import android.content.Context
import android.text.TextUtils
import androidx.annotation.NonNull
import com.amap.api.location.AMapLocationClient
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.concurrent.ConcurrentHashMap

/** EasyAmapLocationPlugin */
class EasyAmapLocationPlugin: FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {

  companion object {
    private val CHANNEL_NAME = "easy_amap_location"
    private val EVENT_NAME = "easy_amap_location/event"
  }

  private lateinit var context: Context

  var eventSink: EventChannel.EventSink? = null

  private var locationClientMap: MutableMap<String, AmapLocationImpl> = ConcurrentHashMap<String, AmapLocationImpl>(8)

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    val args: Map<*, *> = call.arguments as Map<*, *>
    when (call.method) {
      "setApiKey" -> {
        setApiKey(args)
      }
      "setLocationOption" -> {
        setLocationOption(args)
      }
      "startLocation" -> {
        startLocation(args)
      }
      "stopLocation" -> {
        stopLocation(args)
      }
      "destroy" -> {
        destroy(args)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    eventSink = events
  }

  override fun onCancel(arguments: Any?) {
    locationClientMap.forEach{
      it.value.stopLocation()
    }
  }

  fun startLocation(argsMap: Map<*, *>) {
    val client = getLocationClientImp(argsMap)
    client?.startLocation()
  }

  fun stopLocation(argsMap: Map<*, *>) {
    val client = getLocationClientImp(argsMap)
    client?.stopLocation()
  }


  fun destroy(argsMap: Map<*, *>) {
    val client = getLocationClientImp(argsMap)
    client?.destroy()
    locationClientMap.remove(getPluginKeyFromArgs(argsMap))
  }

  fun setApiKey(apiKeyMap: Map<*, *>?) {
    if (null != apiKeyMap) {
      if (apiKeyMap.containsKey("android")
        && !TextUtils.isEmpty(apiKeyMap["android"] as String?)
      ) {
        AMapLocationClient.setApiKey(apiKeyMap["android"] as String?)
      }
    }
  }

  fun setLocationOption(argsMap: Map<*, *>) {
    val client = getLocationClientImp(argsMap)
    client?.setLocationOption(argsMap)
  }

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    if (null == context) {
      context = binding.applicationContext
      val channel = MethodChannel(binding.binaryMessenger, CHANNEL_NAME)
      channel.setMethodCallHandler(this)
      val eventChannel = EventChannel(binding.binaryMessenger, EVENT_NAME)
      eventChannel.setStreamHandler(this)
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    locationClientMap.forEach {
      it.value.destroy()
    }
  }

  private fun getLocationClientImp(argsMap: Map<*, *>): AmapLocationImpl? {
    if (null == locationClientMap) {
      locationClientMap = ConcurrentHashMap(8)
    }
    val pluginKey = getPluginKeyFromArgs(argsMap)
    if (TextUtils.isEmpty(pluginKey)) {
      return null
    }
    if (!locationClientMap.containsKey(pluginKey)) {
      val locationClientImp = AmapLocationImpl(
        context,
        pluginKey!!,
        eventSink
      )
      locationClientMap[pluginKey] = locationClientImp
    }
    return locationClientMap[pluginKey]
  }

  private fun getPluginKeyFromArgs(argsMap: Map<*, *>?): String? {
    var pluginKey: String? = null
    try {
      if (null != argsMap) {
        pluginKey = argsMap["pluginKey"] as String?
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
    return pluginKey
  }

}
