package com.github.xiaoooyu.android

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun File.extractZip(source: String, target: String) {
    FileSystems.newFileSystem(this.toPath(), null).use {
        val srcPath = it.getPath(source)
        Files.copy(srcPath, Paths.get(target), StandardCopyOption.REPLACE_EXISTING)
    }
}

fun File.replaceZip(source: String, target: String) {
    FileSystems.newFileSystem(this.toPath(), null).use {
        val targetPath = it.getPath(target)
        Files.copy(Paths.get(source), targetPath, StandardCopyOption.REPLACE_EXISTING)
    }
}