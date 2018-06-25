import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidManifestReader {

    File inputFile
    InputStream inputStream

    static def MAGIC_NUMBER = 0x00080003
    int totalSize = 0

    static def RESOURCE_ID_CHUNK = 0x00080180

    static def STRING_CHUNK = 0x001C0001
    int stringChunkSize = 0
    int stringCount = 0
    int styleCount = 0
    int unknown = 0
    int stringPoolOffset = 0
    int stylePoolOffset = 0
    int[] stringOffsets
    int[] styleOffsets
    List<String> stringPool

    byte[] others

    InputStream getInputStream() {
        if (inputStream == null) {
            inputStream = new FileInputStream(inputFile)
        }
        return inputStream
    }

    AndroidManifestReader(String theFilePath) {
        inputFile = new File(theFilePath)
    }

    void read() {
//        printAllBytes()

        if (MAGIC_NUMBER != readInt()) {
            return
        }

        totalSize = readInt()

        if (STRING_CHUNK == readInt()) {
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

//            stringPool.each {
//                println it
//            }
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
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(RESOURCE_ID_CHUNK).array())

        byte[] buff = new byte[1024]
        while(getInputStream().available() > 0) {
            int read = getInputStream().read(buff)
            outputStream.write(buff, 0, read)
        }
        others = outputStream.toByteArray()
        print(others)
    }

    void printAllBytes() {
        byte[] allByte = inputFile.readBytes()
        println "Have read ${allByte.length} bytes totally"

        def pos = 0
        byte[] fourBytes = new byte[4]
        allByte.each {
            fourBytes[pos % 4] = allByte[pos]
            pos ++
            if (pos % 4 == 0) {
                print(fourBytes)
                print " "
            }
            if ((pos % 16) == 0) println ""
        }

//
//        ByteBuffer headerByteBuffer = new By
    }

    byte[] read(int len) {
        byte[] readBytes = new byte[len]
        getInputStream().read(readBytes)
        return readBytes
    }

    int toInt(byte[] source) {
        ByteBuffer buffer = ByteBuffer.wrap(source)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        return buffer.getInt()
    }

    short toShort(byte[] source) {
        ByteBuffer buffer = ByteBuffer.wrap(source)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        return buffer.getShort()
    }

    void print(byte[] bytes, boolean reverse = false) {
        def range = !reverse ? (0..bytes.length - 1) : (bytes.length - 1 .. 0)

        range.each{
            if (it % 16 == 0) println ""
            print String.format("%02X ", bytes[it])
        }
    }

    byte[] reverse(byte[] source) {
        int len = source.length
        (0..(len-1)/2).each {
            def exchangeIndex = len - 1 - it
            def tmp = source[it]
            source[it] = source[exchangeIndex]
            source[exchangeIndex] = tmp
        }
        return source
    }
}
