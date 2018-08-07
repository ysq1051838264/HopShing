package com.example.hopshing.util

import java.util.*

/**
 * Created by ysq on 16/7/31.
 */
abstract class CommandSender() {
    abstract fun send(value: ByteArray)

    open fun send(serviceUUID: UUID, characterUUID: UUID, value: ByteArray) {
        send(value)
    }
}