package com.example.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ZipFileUtil {

    public static boolean unzip(String zipFilePath, String destDirectory) {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        // 使用ZipFile类解压
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // 如果条目不是目录，则解压文件
                    try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                         FileOutputStream fos = new FileOutputStream(filePath)) {
                        byte[] bytesIn = new byte[4096];
                        int read;
                        while ((read = bis.read(bytesIn)) != -1) {
                            fos.write(bytesIn, 0, read);
                        }
                    }
                } else {
                    // 如果条目是目录，则创建目录
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean zipFile(String destDirectory, String zipFile) {
        // 文件夹路径（要压缩的文件夹）
        Path folderToZip = Paths.get(destDirectory); // 替换为你的文件夹路径
        // ZIP文件路径
        Path zipFilePath = Paths.get(zipFile);
        // 创建ZIP文件输出流
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            // 遍历文件夹中的每个文件和子文件夹
            Files.walkFileTree(folderToZip, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // 获取文件相对于要压缩的文件夹的路径
                    Path targetFile = folderToZip.relativize(file);
                    // 创建ZIP条目（即文件或文件夹）
                    zipOut.putNextEntry(new ZipEntry(targetFile.toString()));
                    // 读取文件内容并写入ZIP输出流
                    Files.copy(file, zipOut);
                    // 关闭当前ZIP条目
                    zipOut.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // 对于每个目录，也创建一个ZIP条目（不写入内容，只表示目录存在）
                    Path targetDir = folderToZip.relativize(dir);
                    zipOut.putNextEntry(new ZipEntry(targetDir.toString() + "/"));
                    zipOut.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("文件夹已成功压缩到 " + zipFilePath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
