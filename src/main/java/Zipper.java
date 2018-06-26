
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Zipper {

    static void replace(File theZipFile, String filePath, String toReplaceFilePath) {
        Path theFilePath = Paths.get(filePath);

        Path zipFilePath = theZipFile.toPath();

        try (FileSystem fs = FileSystems.newFileSystem(zipFilePath, null)){
            Path pathInsideZip = fs.getPath(toReplaceFilePath);
            Files.copy(theFilePath, pathInsideZip, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void extract(File theZip, String pathInZip, String pathToPut) {
        try (FileSystem fs = FileSystems.newFileSystem(theZip.toPath(), null)){
            Path srcPath = fs.getPath(pathInZip);
            Files.copy(srcPath, Paths.get(pathToPut), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
