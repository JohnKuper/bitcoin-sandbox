package com.kaizendeveloper.bitcoinsandbox.util

import java.nio.ByteBuffer

fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(Integer.SIZE / 8).putInt(this).array()

fun Long.toByteArray(): ByteArray = ByteBuffer.allocate(java.lang.Long.SIZE / 8).putLong(this).array()

fun Double.toByteArray(): ByteArray = ByteBuffer.allocate(java.lang.Double.SIZE / 8).putDouble(this).array()
