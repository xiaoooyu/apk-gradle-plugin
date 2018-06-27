package com.github.xiaoooyu.android

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class AndroidManifestWriterTest {

    AndroidManifestReader reader
    AndroidManifestWriter writer
    AndroidManifestReader reader2

    @BeforeEach
    void setup() {
        String testFilePath = getClass().getClassLoader().getResource("AndroidManifest.xml").file
        String testFilePath2 = getClass().getClassLoader().getResource("AndroidManifest_xiaomi.xml").file

        reader = new AndroidManifestReader(testFilePath)
        reader2 = new AndroidManifestReader(testFilePath2)

        writer = new AndroidManifestWriter("out/AndroidManifest2.xml")
    }

    @Test
    void test() {
        reader.read()
        reader2.read()

        writer.stringPool = []
        reader.stringPool.each {
            if (it == 'channel_placeholder') {
                writer.stringPool.add("xiaomi")
            } else {
                writer.stringPool.add(it)
            }
        }
        writer.others = reader.others

        writer.write()

        assertEquals(reader2.totalSize, writer.totalSize)
        assertEquals(reader2.stringChunkSize, writer.stringChunkSize)
    }
}
