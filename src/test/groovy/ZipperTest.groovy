import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class ZipperTest {

    @Test
    @DisplayName("Test FileSystem invoked by Java, replace success")
    void testReplaceInJava() {
        def targetApk = new File("out/zipper/debug.apk")
        new File(targetApk.parent).mkdirs()
        def originApk = getClass().getResource("test.apk").path
        Files.copy(Paths.get(originApk), targetApk.toPath(), StandardCopyOption.REPLACE_EXISTING)

        String sourceFilePath = getClass().getResource("AndroidManifest_xiaomi.xml").file
        Zipper.replace(targetApk, sourceFilePath, "AndroidManifest.xml")
    }

    @Test
    void testExtractSingleFile() {
        def oriApk = getClass().getResource("test.apk").path
        Zipper.extract(new File(oriApk), "AndroidManifest.xml", "out/AndroidManifest-extract.xml")
    }
}
