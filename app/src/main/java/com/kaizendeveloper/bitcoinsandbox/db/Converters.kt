package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.TypeConverter
import com.kaizendeveloper.bitcoinsandbox.util.ByteArrayWrapper
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import java.security.PublicKey


class Converters {

    @TypeConverter
    fun encodePublicKey(key: PublicKey): ByteArray = key.encoded

    @TypeConverter
    fun decodePublicKey(input: ByteArray): PublicKey = Cipher.decodePublicKey(input)

    @TypeConverter
    fun fromByteArrayWrapper(wrapper: ByteArrayWrapper): ByteArray = wrapper.data

    @TypeConverter
    fun toByteArrayWrapper(input: ByteArray): ByteArrayWrapper = input.wrap()
}