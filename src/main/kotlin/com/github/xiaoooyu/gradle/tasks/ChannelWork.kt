package com.github.xiaoooyu.gradle.tasks

import com.github.xiaoooyu.android.AndroidManifestReader
import com.github.xiaoooyu.android.AndroidManifestWriter
import com.github.xiaoooyu.android.extractZip
import com.github.xiaoooyu.android.replaceZip
import com.github.xiaoooyu.gradle.ApkExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.StandardCopyOption

open class ChannelWork : DefaultTask() {
    companion object {
        const val fileName = "AndroidManifest.xml"
    }

    var apks: FileCollection? = null
    var placeHolder: String? = "channel_placeholder"
    var channels: Array<String>? = emptyArray()
    var env: ApkExtension? = null
    var to: String = "build/archives"
    var debug: Boolean = false

    @TaskAction
    fun perform() {
        project.file(to).mkdirs()
        project.file(getTemporaryApkPath()).mkdirs()
        apks?.files?.forEach {
            it?.let {
                batchReplaceString(it, channels)
            }
        }
        project.fileTree(getTemporaryApkPath()).files.forEach { apk ->
            signApk(apk, project.file(to).absolutePath)
        }
    }

    private fun batchReplaceString(apk: File, channels: Array<String>?) {
        val oriFile = project.file("${getTemporaryApkPath()}/AndroidManifest_ori.xml")
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
                    replaceString(apk, it, reader, toReplaceIndex)
                }
            }
        }

        oriFile.delete()
    }

    private fun replaceString(apk: File, channelName: String, reader: AndroidManifestReader, index: Int) {
        val newFile = project.file("${getTemporaryApkPath()}/AndroidManifest_$channelName.xml").apply {
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
        val targetFile = project.file("${getTemporaryApkPath()}/${apk.nameWithoutExtension}-$channelName.${apk.extension}")
        Files.copy(apk.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

        targetFile.replaceZip(newFile.absolutePath, fileName)

        newFile.delete()
    }

    private fun signApk(originApk: File, outputPath: String) {
        val runtime = Runtime.getRuntime()
        val cmdAlign = env?.zipalignExec ?: "zipalign"
        val cmdSign = env?.apksignerExec ?: "apksigner"
        val keyStore = env?.keyStore ?: ""
        val keyAlias = env?.keyAlias ?: ""
        val storePass = env?.storePass ?: ""
        val keyPass = env?.keyPass ?: ""
        val filePath = originApk.absolutePath

        val alignCommand = "$cmdAlign -f 4 $filePath $outputPath/${originApk.name}"
        println("align command: $alignCommand")
        val alignProc = runtime.exec(alignCommand)

        BufferedReader(InputStreamReader(alignProc.errorStream)).forEachLine { if (debug) println("$cmdAlign/E: $it") }
        BufferedReader(InputStreamReader(alignProc.inputStream)).forEachLine { if (debug) println("$cmdAlign/I: $it") }

        val signCommand = "$cmdSign sign --ks $keyStore --ks-key-alias $keyAlias --ks-pass pass:$storePass --key-pass pass:$keyPass $outputPath/${originApk.name}"
        println("signCommand: $signCommand")
        val signProc = runtime.exec(signCommand)

        BufferedReader(InputStreamReader(signProc.errorStream)).forEachLine { if (debug) println("$cmdSign/E: $it") }
        BufferedReader(InputStreamReader(signProc.inputStream)).forEachLine { if (debug) println("$cmdSign/I: $it") }

        originApk.delete()
    }

    private fun getTemporaryApkPath() = "$to/temp"
}
