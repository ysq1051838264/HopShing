package com.example.hopshing.ble

import android.graphics.RectF

/**
 * Created by hdr on 15/9/23.
 */
class ScanDevice {
    var mac: String = ""
    var scaleType: Int = 0
    var deviceType: Int = 0
    var scaleName: String = ""
    var alias: String? = null
    var needReadInternalModel: Boolean = false
    var hasBind: Boolean = false
    var rssi: Int = 0

    val x: Float
        get() = rect.left

    val y: Float
        get() = rect.top

    val rect = RectF()

}
