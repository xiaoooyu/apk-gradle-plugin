package com.github.xiaoooyu.android

import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidManifestWriter(filePath: String) : AndroidManifestAwareness(filePath) {

//    var _outputStream: OutputStream? = null
//    val outputStream: OutputStream
//        get() {
//            if (_outputStream == null) {
//                _outputStream = FileOutputStream(filePath)
//            }
//            return _outputStream ?: throw AssertionError("output stream init failed")
//        }

    fun write() {
        val stringChunk = buildStringChunk()
        totalSize = 8 + stringChunkSize + others.size

        FileOutputStream(filePath).use {
            writeInt(MAGIC_NUMBER, it)
            writeInt(totalSize, it)
            write(stringChunk, it)
            write(others, it)
        }
    }

    fun buildStringChunk(): ByteArray {
        stringCount = stringPool.size

        val stringPoolStream = ByteArrayOutputStream()
        val stringOffsetBuffer = ByteBuffer.allocate(stringCount * 4).order(ByteOrder.LITTLE_ENDIAN)
        var offset = 0
        (0 until stringCount).forEach {
            val str = stringPool.get(it)
            stringOffsetBuffer.putInt(offset)
            stringPoolStream.write(buildStringBytes(str))

            offset += str.length * 2 + 4
        }

        val stringOffsetBytes = stringOffsetBuffer.array()
        val stringPoolBytes = stringPoolStream.toByteArray()
        stringPoolOffset = (7 * 4) + stringOffsetBytes.size
        stringChunkSize = (7 * 4) + stringOffsetBytes.size + stringPoolBytes.size
        val alignByteLen = if ((stringChunkSize % 4) == 0) 0 else 4 - (stringChunkSize % 4)
        stringChunkSize += alignByteLen

        return ByteBuffer.allocate(stringChunkSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(STRING_CHUNK)
            putInt(stringChunkSize)
            putInt(stringCount)
            putInt(styleCount)
            putInt(unknown)
            putInt(stringPoolOffset)
            putInt(stylePoolOffset)

            put(stringOffsetBytes)
            put(stringPoolBytes)

            (0 until alignByteLen).forEach {
                put(0.toByte())
            }
        }.array()
    }

    fun buildStringBytes(str: String): ByteArray {
        val bb = ByteBuffer.allocate(str.length * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        val nestBb = ByteBuffer.allocate(str.length * 2).order(ByteOrder.LITTLE_ENDIAN)
        val nestCb = nestBb.asCharBuffer()
        nestCb.put(str)

        bb.putShort(str.length.toShort())
        bb.put(nestBb.array())
        bb.putShort(0.toShort())
        return bb.array()
    }

    fun writeInt(value: Int, outputStream: OutputStream) {
        val bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value)
        write(bb.array(), outputStream)
    }

    fun writeShort(value: Short, outputStream: OutputStream) {
        val bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value)
        write(bb.array(), outputStream)
    }

    fun write(value: ByteArray, stream: OutputStream) {
        stream.write(value)
    }
}