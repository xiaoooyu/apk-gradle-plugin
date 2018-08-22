package com.github.xiaoooyu.gradle

import com.github.xiaoooyu.gradle.tasks.ChannelWork
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApkPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("apkExtension", ApkExtension::class.java)
        target.tasks.create("channelWork", ChannelWork::class.java).apply {
            env = extension
        }
    }
}