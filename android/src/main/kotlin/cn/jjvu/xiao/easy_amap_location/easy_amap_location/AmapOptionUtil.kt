package cn.jjvu.xiao.easy_amap_location.easy_amap_location

import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClientOption

class AmapOptionUtil {

    companion object {

        fun mode(szMode: String) : AMapLocationClientOption.AMapLocationMode {
            return AMapLocationClientOption.AMapLocationMode.valueOf(szMode)
        }

    }

    private fun resultToMap(a: AMapLocation?): Map<*, *>? {
        val map: MutableMap<*, *> = HashMap<Any?, Any?>()
        if (a != null) {
            if (a.errorCode != 0) {
                //错误信息
                map["description"] = a.errorInfo
                map["success"] = false
            } else {
                map["success"] = true
                map["accuracy"] = a.accuracy
                map["altitude"] = a.altitude
                map["speed"] = a.speed
                map["timestamp"] = a.time.toDouble() / 1000
                map["latitude"] = a.latitude
                map["longitude"] = a.longitude
                map["locationType"] = a.locationType
                map["provider"] = a.provider
                map["formattedAddress"] = a.address
                map["country"] = a.country
                map["province"] = a.province
                map["city"] = a.city
                map["district"] = a.district
                map["citycode"] = a.cityCode
                map["adcode"] = a.adCode
                map["street"] = a.street
                map["number"] = a.streetNum
                map["POIName"] = a.poiName
                map["AOIName"] = a.aoiName
            }
            map["code"] = a.errorCode
            Log.d(
                com.jzoom.amaplocation.AmapLocationPlugin.TAG,
                "定位获取结果:" + a.latitude + " code：" + a.errorCode + " 省:" + a.province
            )
        }
        return map
    }

}