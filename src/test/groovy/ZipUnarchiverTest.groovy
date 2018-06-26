import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class ZipUnarchiverTest {

    ZipUnarchiver unarchiver

    @BeforeEach
    void setUp() {
        unarchiver = new ZipUnarchiver()
    }

    @Test
    void test() {
        unarchiver.unzip(getClass().getResource("test.apk").file, "out/unzip/")
    }

    @Test
    @DisplayName("Test unzip apk, replace, then zip, but file size is smaller than before")
    void testUnzipAndZip() {
        unarchiver.unzip(getClass().getResource("test.apk").file, "out/unzip/")

        OutputStream output = new FileOutputStream("out/unzip/AndroidManifest.xml")
        Files.copy(Paths.get(getClass().getResource("AndroidManifest_xiaomi.xml").path), output)

        unarchiver.zip("out/test_zip.zip", "out/unzip")
    }

    @Test
    @DisplayName("invoke FileSystem in Groovy will fail by casting. Still don't know the cause.")
    void testReplaceInGroovy() {
        String targetZip = "/Users/xiaoooyu/workspace/me/apk-gradle-plugin/out/unzip/test.apk"

        OutputStream output = new FileOutputStream(targetZip)
        Files.copy(Paths.get(getClass().getResource("test.apk").path), output)

        String sourceFilePath = getClass().getResource("AndroidManifest_xiaomi.xml").file

        unarchiver.replace(targetZip, sourceFilePath, "AndroidManifest.xml")
    }

    void copyFile(String sourcePath, String targetPath) {
        OutputStream output = new FileOutputStream(targetPath)
        Files.copy(Paths.get(sourcePath), output)
    }
}
