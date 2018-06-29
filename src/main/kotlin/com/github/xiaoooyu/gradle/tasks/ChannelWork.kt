package com.github.xiaoooyu.gradle.tasks

import com.github.xiaoooyu.android.AndroidManifestReader
import com.github.xiaoooyu.android.AndroidManifestWriter
import com.github.xiaoooyu.android.*
import com.github.xiaoooyu.gradle.ApkExtension

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

open class ChannelWork: DefaultTask() {
    var apks: FileCollection? = null
    var placeHolder: String? = "channel_placeholder"
    var channels: Array<String>? = emptyArray()
    var to: String = "build/archives"
    var env: ApkExtension? = null

    companion object {
        const val fileName = "AndroidManifest.xml"
    }

    @TaskAction
    fun perform() {
        to.let {
            project.file(to).mkdirs()
        }

        apks?.files?.forEach {
            it?.let {
                packMultiple(it, channels)
            }
        }
    }

    fun packMultiple(apk: File, channels: Array<String>?) {
        val oriFile = project.file("${to}/AndroidManifest_ori.xml")
        apk.extractZip(fileName, oriFile.absolutePath)

        val reader = AndroidManifestReader(oriFile.absolutePath)
        reader.read()

        var toReplaceIndex = -1
        placeHolder?.let {
            for (i in reader.stringPool.indices) {
                if (reader.stringPool[i] == it) {
                    toReplaceIndex = i
                    break
                }
            }
        }

        if (toReplaceIndex >= 0) {
            channels?.forEach {
                it.apply {
                    packSingle(apk, it, reader, toReplaceIndex)
                }
            }
        }

        oriFile.delete()
    }

    fun packSingle(apk: File, channelName: String, reader: AndroidManifestReader, index: Int) {
        val newFile = project.file("${to}/AndroidManifest_${channelName}.xml").apply {
            File(parent).mkdirs()
        }

        placeHolder?.let {
            val writer = AndroidManifestWriter(newFile.absolutePath)
            writer.others = reader.others
            reader.stringPool[index] = channelName
            writer.stringPool = reader.stringPool
            writer.write()
        }

        // copy template apk file
        val targetFile = project.file("${to}/${apk.nameWithoutExtension}-${channelName}.${apk.extension}")
        Files.copy(apk.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)


        targetFile.replaceZip(newFile.absolutePath, fileName)

        newFile.delete()
    }
}