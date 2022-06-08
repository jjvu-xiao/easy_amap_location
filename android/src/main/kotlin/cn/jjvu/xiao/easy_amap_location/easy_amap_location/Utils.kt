package cn.jjvu.xiao.easy_amap_location.easy_amap_location

import android.text.TextUtils
import com.amap.api.location.AMapLocation
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun buildLocationResultMap(location: AMapLocation?): MutableMap<String, Any> {
        val result: MutableMap<String, Any> = LinkedHashMap()
        result["callbackTime"] = formatUTC(System.currentTimeMillis(), null)
        if (null != location) {
            if (location.errorCode == AMapLocation.LOCATION_SUCCESS) {
                result["locationTime"] = formatUTC(location.time, null)
                result["locationType"] = location.locationType
                result["latitude"] = location.latitude
                result["longitude"] = location.longitude
                result["accuracy"] = location.accuracy
                result["altitude"] = location.altitude
                result["bearing"] = location.bearing
                result["speed"] = location.speed
                result["country"] = location.country
                result["province"] = location.province
                result["city"] = location.city
                result["district"] = location.district
                result["street"] = location.street
                result["streetNumber"] = location.streetNum
                result["cityCode"] = location.cityCode
                result["adCode"] = location.adCode
                result["address"] = location.address
                result["description"] = location.description
            } else {
                result["errorCode"] = location.errorCode
                result["errorInfo"] = location.errorInfo + "#" + location.locationDetail
            }
        } else {
            result["errorCode"] = -1
            result["errorInfo"] = "location is null"
        }
        return result
    }

    /**
     * 格式化时间
     *
     * @param time
     * @param strPattern
     * @return
     */
    fun formatUTC(time: Long, strPattern: String?): String {
        var strPattern = strPattern
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss"
        }
        var sdf: SimpleDateFormat? = null
        try {
            sdf = SimpleDateFormat(strPattern, Locale.CHINA)
            sdf.applyPattern(strPattern)
        } catch (e: Throwable) {
        }
        return if (sdf == null) "NULL" else sdf.format(time)
    }
}