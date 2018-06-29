package com.github.xiaoooyu.android

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AndroidManifestWriterTest {
    lateinit var reader: AndroidManifestReader
    lateinit var writer: AndroidManifestWriter
    lateinit var reader2: AndroidManifestReader

    @BeforeEach
    fun setup() {
        val classLoader = javaClass.classLoader
        val testFilePath = classLoader.getResource("AndroidManifest.xml")
        val testFilePath2 = classLoader.getResource("AndroidManifest_xiaomi.xml")

        reader = AndroidManifestReader(testFilePath.file)
        reader2 = AndroidManifestReader(testFilePath2.file)

        writer = AndroidManifestWriter("out/AndroidManifest2.xml")
    }

    @Test
    fun test() {
        reader.read()
        reader2.read()

        val stringPool = reader.stringPool

        writer.stringPool = Array<String>(stringPool.size, {
            stringPool[it].let {
                if (it == "channel_placeholder")
                    "xiaomi"
                else it
            }
        })

        writer.others = reader.others

        writer.write()

        assertEquals(reader2.totalSize, writer.totalSize)
        assertEquals(reader2.stringChunkSize, writer.stringChunkSize)
    }
}