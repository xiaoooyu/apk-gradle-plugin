package com.github.xiaoooyu.android

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

internal class FileExTest {

    lateinit var targetFile: File

    @BeforeEach
    fun setup() {
        targetFile = File("out/filex/test.apk").apply {
            File(this.parent).mkdirs()
        }
        val resource = javaClass.classLoader.getResource("test.apk")
        Files.copy(Paths.get(resource.toURI()), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)


    }

    @AfterEach
    fun teardown() {
        targetFile.delete()
    }

    @Test
    @DisplayName("Test FileSystem invoked by Java, replace success")
    fun testReplace() {
        targetFile.replaceZip(javaClass.classLoader.getResource("AndroidManifest_xiaomi.xml").file,
                "AndroidManifest.xml")
    }

    @Test
    @DisplayName("Test extract certain file from a zip formatted")
    fun testExtract() {
        targetFile.extractZip("AndroidManifest.xml", "out/AndroidManifest-extract.xml")
    }
}
