package cn.jjvu.xiao.easy_amap_location.easy_amap_location

import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import io.flutter.plugin.common.EventChannel.EventSink


class AmapLocationImpl(
    private val mContext: Context,
    private val mPluginKey: String,
    private val mEventSink: EventSink?
) :
    AMapLocationListener {
    private var locationOption: AMapLocationClientOption? = AMapLocationClientOption()
    private var locationClient: AMapLocationClient? = null

    /**
     * 开始定位
     */
    fun startLocation() {
        if (null == locationClient) {
            locationClient = AMapLocationClient(mContext)
        }
        if (null != locationOption) {
            locationClient!!.setLocationOption(locationOption)
        }
        locationClient!!.setLocationListener(this)
        locationClient!!.startLocation()
    }

    /**
     * 停止定位
     */
    fun stopLocation() {
        if (null != locationClient) {
            locationClient!!.stopLocation()
            locationClient!!.onDestroy()
            locationClient = null
        }
    }

    fun destroy() {
        if (null != locationClient) {
            locationClient!!.onDestroy()
            locationClient = null
        }
    }

    /**
     * 定位回调
     *
     * @param location
     */
    override fun onLocationChanged(location: AMapLocation) {
        if (null == mEventSink) {
            return
        }
        val result: MutableMap<String, Any> = Utils.buildLocationResultMap(location).toMutableMap()
        result["pluginKey"] = mPluginKey
        mEventSink.success(result)
    }

    /**
     * 设置定位参数
     *
     * @param optionMap
     */
    fun setLocationOption(optionMap: Map<*, *>) {
        if (null == locationOption) {
            locationOption = AMapLocationClientOption()
        }
        if (optionMap.containsKey("locationInterval")) {
            locationOption!!.interval = (optionMap["locationInterval"] as Int?)!!.toLong()
        }
        if (optionMap.containsKey("needAddress")) {
            locationOption!!.isNeedAddress = optionMap["needAddress"] as Boolean
        }
        if (optionMap.containsKey("locationMode")) {
            try {
                locationOption!!.locationMode =
                    AMapLocationClientOption.AMapLocationMode.values()[optionMap["locationMode"] as Int]
            } catch (e: Throwable) {
            }
        }
        if (optionMap.containsKey("geoLanguage")) {
            locationOption!!.geoLanguage =
                AMapLocationClientOption.GeoLanguage.values()[optionMap["geoLanguage"] as Int]
        }
        if (optionMap.containsKey("onceLocation")) {
            locationOption!!.isOnceLocation = optionMap["onceLocation"] as Boolean
        }
        if (null != locationClient) {
            locationClient!!.setLocationOption(locationOption)
        }
    }

    init {
        if (null == locationClient) {
            locationClient = AMapLocationClient(mContext)
        }
    }
}