package com.github.xiaoooyu.android

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidManifestReader(filePath: String) : AndroidManifestAwareness(filePath) {
    var _inputStream : InputStream? = null
    val inputStream: InputStream
        get() {
            if (_inputStream == null) {
                _inputStream = FileInputStream(filePath)
            }
            return _inputStream ?: throw AssertionError("input stream init failed")
        }

    fun read() {
        if(MAGIC_NUMBER != readInt()) {
            return
        }

        totalSize = readInt()

        if (STRING_CHUNK == readInt()) {
            stringChunkSize = readInt()
            stringCount = readInt()
            styleCount = readInt()
            unknown = readInt()
            stringPoolOffset = readInt()
            stylePoolOffset = readInt()

            stringOffsets = Array(stringCount, { readInt() })
            styleOffsets = Array(styleCount, { readInt() })

            stringPool = Array(stringCount, { readString() })
        }

        others = readOthers()
    }

    fun read(len: Int): ByteArray =
        ByteArray(len).apply { inputStream.read(this) }

    fun readInt() = toInt(read(4))

    fun readShort() = toShort(read(2))

    fun readString(): String {
        val len = readShort()
        val array = read(len * 2 + 2)
        return ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN).let {
            it.limit(len * 2)
            it.asCharBuffer()
        }.toString()
    }

    fun readOthers(): ByteArray {
        while (0x00 == readInt()) { }

        val outputStream = ByteArrayOutputStream(4 + inputStream.available())
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(RESOURCE_ID_CHUNK).array())

        val buff = ByteArray(1024)
        while (inputStream.available() > 0) {
            val hasRead = inputStream.read(buff)
            outputStream.write(buff, 0, hasRead)
        }

        return outputStream.toByteArray()
    }
}