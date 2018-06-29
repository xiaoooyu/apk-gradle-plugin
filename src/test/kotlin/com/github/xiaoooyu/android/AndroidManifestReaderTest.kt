package com.github.xiaoooyu.android

import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal class AndroidManifestReaderTest {

    lateinit var reader: AndroidManifestReader

    @BeforeEach
    fun setUp() {
        val testFile = javaClass.classLoader.getResource("AndroidManifest.xml")
        reader = AndroidManifestReader(testFile.file)
    }

    @Test
    fun test() {
        reader.read()

        assertEquals(0x012e7c, reader.totalSize)
        assertEquals(0x0938c, reader.stringChunkSize)
        assertEquals(0x019d, reader.stringCount)
        assertEquals(0x00, reader.styleCount)
        assertEquals(0x00, reader.unknown)
        assertEquals(0x0690, reader.stringPoolOffset)
        assertEquals(0x00, reader.stylePoolOffset)

        assertEquals(reader.stringPoolOffset, reader.stringCount * 4 + (4 * 7))
        assertEquals(reader.stringCount, reader.stringOffsets.size)

        assertEquals(0x00, reader.stringOffsets[0])
        assertEquals(0x0E, reader.stringOffsets[1])
    }

    @Test
    fun test2() {
        val bytes = byteArrayOf(0x74, 0x00, 0x68, 0x00, 0x65, 0x00, 0x6d, 0x00, 0x65, 0x00)
        bytes.forEach(::print)
        println()

        var bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        println(bb.asCharBuffer())

        val theme = "theme"
        bb = ByteBuffer.allocate(theme.length * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        val bb2 = ByteBuffer.allocate(theme.length * 2).order(ByteOrder.LITTLE_ENDIAN)
        val cb2 = bb2.asCharBuffer()
        cb2.put(theme)

        bb.putShort(theme.length.toShort())
        bb.put(bb2.array())
        bb.putShort(0.toShort())
        bb.array().forEach(::print)
    }
}