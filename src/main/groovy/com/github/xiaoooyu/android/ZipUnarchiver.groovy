package com.github.xiaoooyu.android

import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipUnarchiver {

    ZipUnarchiver() {
    }

    def unzip(String zipFilePath, String unarchivePath) {
        def zip = new ZipFile(new File(zipFilePath))
        zip.entries().each{
            if (!it.isDirectory()){
                def fOut = new File(unarchivePath+ File.separator + it.name)
                //create output dir if not exists
                new File(fOut.parent).mkdirs()
                def fos = new FileOutputStream(fOut)
                //println "name:${it.name}, size:${it.size}"
                def buf = new byte[it.size]
                def len = zip.getInputStream(it).read(buf) //println zip.getInputStream(it).text
                fos.write(buf, 0, len)
                fos.close()
            }
        }
        zip.close()
    }

    def replace(String theZipFilePath, String filePath, String toReplaceFilePath) {
        Path theFilePath = Paths.get(filePath)

        Path zipFilePath = Paths.get(theZipFilePath)
        FileSystem fs
        try {
            fs = FileSystems.newFileSystem(zipFilePath, null)
            Path pathInsideZip = fs.getPath(toReplaceFilePath)
            Files.copy(theFilePath, pathInsideZip, StandardCopyOption.REPLACE_EXISTING)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    def zip(String zipFilePath, String sourceDir) {
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zipFilePath))
        Path root = Paths.get(sourceDir)

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

                String pathInZip = (root.relativize(path).normalize() as String).replaceAll("\\\\", '/')
                addEntry(pathInZip, path)
                return FileVisitResult.CONTINUE
            }

            private void addEntry(String pathInZip, Path path) {
                zipFile.putNextEntry(new ZipEntry(pathInZip))
                if (path.toFile().isFile()) {
                    path.withInputStream { fileStream ->
                        zipFile << fileStream
                    }
                }
                zipFile.closeEntry()
            }
        })
        zipFile.close()
    }
}