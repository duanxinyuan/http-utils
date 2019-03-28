package com.dxy.library.network.http.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author duanxinyuan
 * 2018/8/24 11:53
 */
@Slf4j
public class FileUtils {

    /**
     * 创建文件
     */
    public static void createFile(String localPath) {
        // 本地文件的地址
        File localFile = new File(localPath);
        if (localFile.isDirectory()) {
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
        } else {
            String localPathDir = localPath.substring(0, localPath.lastIndexOf(File.separator));
            File localPathDirFile = new File(localPathDir);
            if (!localPathDirFile.exists()) {
                localPathDirFile.mkdirs();
            }
            if (!localFile.exists()) {
                try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    log.error("file create error, path: {}", localPath, e);
                }
            }
        }
    }
}
