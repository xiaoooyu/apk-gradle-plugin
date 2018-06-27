package com.github.xiaoooyu.android

import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidManifestWriter extends AndroidManifestAwareness {

    OutputStream outputStream

    AndroidManifestWriter(String theFilePath) {
        super(theFilePath)
    }

    OutputStream getOutputStream() {
        if (outputStream == null) {
            outputStream = new FileOutputStream(filePath)
        }
        return outputStream
    }

    void write() {

        byte[] stringChunk = buildStringChunk()

        writeInt(AndroidManifestAwareness.MAGIC_NUMBER, getOutputStream())
        totalSize = 8 + stringChunkSize + others.length
        writeInt(totalSize, getOutputStream())
        write(stringChunk, getOutputStream())
        write(others, getOutputStream())
    }

    def buildStringChunk() {
        stringCount = stringPool.size()

        def stringPoolStream = new ByteArrayOutputStream()
        def stringOffsetBuffer = ByteBuffer.allocate(stringCount * 4).order(ByteOrder.LITTLE_ENDIAN)
        def offset = 0
        (0..<stringCount).each {
            def str = stringPool.get(it)
            stringOffsetBuffer.putInt(offset)
            stringPoolStream.write(buildStringBytes(str))

            offset += str.length() * 2 + 4
        }

        byte[] stringOffsetBytes = stringOffsetBuffer.array()
        byte[] stringPoolBytes = stringPoolStream.toByteArray()
        stringPoolOffset = (7 * 4) + stringOffsetBytes.length
        stringChunkSize = (7 * 4) + stringOffsetBytes.length + stringPoolBytes.length
        int alignByteLength = (stringChunkSize % 4) == 0 ? 0 : 4 - (stringChunkSize % 4)
        stringChunkSize += alignByteLength


        def chunkBuffer = ByteBuffer.allocate(stringChunkSize).order(ByteOrder.LITTLE_ENDIAN)
        chunkBuffer.with {
            putInt(AndroidManifestAwareness.STRING_CHUNK)
            putInt(stringChunkSize)
            putInt(stringCount)
            putInt(styleCount)
            putInt(unknown)
            putInt(stringPoolOffset)
            putInt(stylePoolOffset)

            put(stringOffsetBytes)
            put(stringPoolBytes)

            (0..<alignByteLength).each {
                put((byte)0x00)
            }
        }
        return chunkBuffer.array()
    }

    void writeInt(int theInt, OutputStream stream) {
        ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
        bb.putInt(theInt)
        write(bb.array(), stream)
    }

    void writeShort(short theShort, OutputStream stream) {
        ByteBuffer bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
        bb.putShort(theShort)
        write(bb.array(), stream)
    }

    byte[] buildStringBytes(String theString) {
        def bb = ByteBuffer.allocate(theString.length() * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        def bb2 = ByteBuffer.allocate(theString.length() * 2).order(ByteOrder.LITTLE_ENDIAN)
        def cb2 = bb2.asCharBuffer()
        cb2.put(theString)

        bb.putShort(theString.length().shortValue())
        bb.put(bb2.array())
        bb.putShort((short)0)
        return bb.array()
    }

    void write(byte[] bytes, OutputStream stream) {
        stream.write(bytes)
    }
}
