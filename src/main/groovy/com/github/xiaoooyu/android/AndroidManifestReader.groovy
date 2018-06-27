package com.github.xiaoooyu.android

import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidManifestReader extends AndroidManifestAwareness {

    InputStream inputStream

    InputStream getInputStream() {
        if (inputStream == null) {
            inputStream = new FileInputStream(filePath)
        }
        return inputStream
    }

    AndroidManifestReader(String theFilePath) {
        super(theFilePath)
    }

    void read() {
        if (AndroidManifestAwareness.MAGIC_NUMBER != readInt()) {
            return
        }

        totalSize = readInt()

        if (AndroidManifestAwareness.STRING_CHUNK == readInt()) {
            stringChunkSize = readInt()
            stringCount = readInt()          // String Count
            styleCount = readInt()           // Style Count
            unknown = readInt()              // Unknown
            stringPoolOffset = readInt()     // String Pool Offset
            stylePoolOffset = readInt()      // Style Pool Offset

            if (stringCount != 0) {
                stringOffsets = new int[stringCount]
                (0..stringCount-1).each {
                    stringOffsets[it] = readInt()
                }
            }

            if (styleCount != 0) {
                styleOffsets = new int[styleCount]
                (0..styleCount-1).each {
                    styleOffsets[it] = readInt()
                }
            }

            stringPool = new ArrayList<String>()
            while(stringPool.size() < stringCount) {
                def toAdd = readString()
                stringPool.add(toAdd)
            }
        }

        others = readOthers()
    }

    String readString() {
        short len = readShort()                 // get string length
        def bytes = read(len * 2 + 2)           // utf-16, 2 byte per char, plus 0x0000 for termination

        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        bb.limit(len * 2)

        return bb.asCharBuffer().toString()
    }

    int readInt() {
        return toInt(read(4))
    }

    int readShort() {
        return toShort(read(2))
    }

    byte[] readOthers() {
        while(readInt() == 0x0) {

        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4 + getInputStream().available())
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(AndroidManifestAwareness.RESOURCE_ID_CHUNK).array())

        byte[] buff = new byte[1024]
        while(getInputStream().available() > 0) {
            int read = getInputStream().read(buff)
            outputStream.write(buff, 0, read)
        }
        others = outputStream.toByteArray()
    }

    byte[] read(int len) {
        byte[] readBytes = new byte[len]
        getInputStream().read(readBytes)
        return readBytes
    }
}
