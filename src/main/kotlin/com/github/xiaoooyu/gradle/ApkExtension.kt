package com.github.xiaoooyu.gradle

open class ApkExtension(
        var zipalignExec: String,
        var apksignerExec: String) {
    constructor() : this("", "")
}
