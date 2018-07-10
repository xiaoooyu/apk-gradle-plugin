package com.github.xiaoooyu.gradle

import com.github.xiaoooyu.gradle.tasks.ChannelWork
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Action

class ApkPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("apkExtension", ApkExtension::class.java)

        target.getTasks().create("createChannelWorks", ChannelWork::class.java, object : Action<ChannelWork> {
            override fun execute(action: ChannelWork) {
                action.env = extension
            }
        })
    }
}