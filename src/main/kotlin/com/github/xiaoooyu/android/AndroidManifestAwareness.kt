package com.github.xiaoooyu.android

import java.nio.ByteBuffer
import java.nio.ByteOrder

open class AndroidManifestAwareness(
        val filePath: String,
        var totalSize: Int = 0,
        var stringChunkSize: Int = 0,
        var stringCount: Int = 0,
        var styleCount: Int = 0,
        var unknown: Int = 0,
        var stringPoolOffset: Int = 0,
        var stylePoolOffset: Int = 0,
        var stringOffsets: Array<Int> = emptyArray(),
        var styleOffsets: Array<Int> = emptyArray(),
        var stringPool: Array<String> = emptyArray(),
        var others: ByteArray = ByteArray(0)

) {

    companion object {
        const val MAGIC_NUMBER = 0x00080003
        const val RESOURCE_ID_CHUNK = 0x00080180
        const val STRING_CHUNK = 0x001C0001
    }

    fun toInt(source: ByteArray): Int =
            ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).int

    fun toShort(source: ByteArray): Short =
            ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).short

    fun print(source: ByteArray) {
        (0 until source.size).forEach {
            if (it % 16 == 0) println()
            print(String.format("%02X ", source[it]))
        }
    }


}