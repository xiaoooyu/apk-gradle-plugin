package com.github.xiaoooyu.gradle

open class ApkExtension {
    var zipalignExec: String = "zipalign"
    var apksignerExec: String = "apksigner"
    var keyStore: String = ""
    var keyAlias: String = ""
    var storePass: String = ""
    var keyPass: String = ""
}