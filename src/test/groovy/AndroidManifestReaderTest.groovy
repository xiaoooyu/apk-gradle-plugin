import java.nio.ByteBuffer
import java.nio.ByteOrder

import static org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AndroidManifestReaderTest {

    AndroidManifestReader reader

    @BeforeEach
    void setup() {
        String testFilePath = getClass().getResource("AndroidManifest.xml").file
        reader = new AndroidManifestReader(testFilePath)
    }

    @Test
    void test() {
        reader.read()

        // total file size
        assertEquals(0x012e7c, reader.getTotalSize())

        // string chunk size
        assertEquals(0x938c, reader.getStringChunkSize())

        // string count
        assertEquals(0x019d, reader.getStringCount())

        // style count
        assertEquals(0x00, reader.getStyleCount())

        // unknown
        assertEquals(0x00, reader.getUnknown())

        // string pool offset
        assertEquals(0x0690, reader.getStringPoolOffset())

        // style pool offset
        assertEquals(0x00, reader.getStylePoolOffset())

        // calculate string pool offset
        assertEquals(reader.getStringPoolOffset(), reader.getStringCount() * 4 + (4 * 7))

        // string offsets' number
        assertEquals(reader.getStringCount(), reader.getStringOffsets().length)


        assertEquals(0x00, reader.getStringOffsets()[0])
        assertEquals(0x0E, reader.getStringOffsets()[1])
    }

    @Test
    void test2() {
        byte[] bytes = [0x74, 0x00, 0x68, 0x00, 0x65, 0x00, 0x6d, 0x00, 0x65, 0x00]
        println bytes

        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        println bb.asCharBuffer().toString()
//        CharBuffer cb = Charset.forName("UTF-16").decode(bb)
//        println cb.toString()


//        def str = new String(reader.reverse(bytes), Charset.forName("UTF-16"))
//        print str

        String theme = "theme"
        bb = ByteBuffer.allocate(theme.length() * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        def bb2 = ByteBuffer.allocate(theme.length() * 2).order(ByteOrder.LITTLE_ENDIAN)
        def cb2 = bb2.asCharBuffer()
        cb2.put(theme)

        bb.putShort(theme.length().shortValue())
        bb.put(bb2.array())
        bb.putShort((short)0)
        println bb.array()
    }

    @Test
    void test3() {
        (0..<0).each {
            println('a')
        }
    }
}
