import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidManifestAwareness {

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

    String filePath

    AndroidManifestAwareness(String theFilePath) {
        filePath = theFilePath
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

    void print(byte[] bytes) {
        (0..bytes.length - 1).each{
            if (it % 16 == 0) println ""
            print String.format("%02X ", bytes[it])
        }
    }

    void printAll() {
        byte[] allByte = new File(filePath).readBytes()
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
    }
}
